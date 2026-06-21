import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import ast.Encoder;
import ast.GUIVisitor;
import ast.PrintVisitor;
import ast.Program;
import ast.TypeChecker;

public class Main {
    public static void main(String[] args) {
        String path = "src/codes/nivaldo.txt";
        if (args.length > 0) {
            path = args[0];
        }

        System.out.println("Iniciando Compilação...");
        System.out.println("Arquivo: " + path);
        System.out.println("---------------------------------------\n");

        try {
            Scanner scanner = new Scanner(path);
            Parser parser = new Parser(scanner);

            // 1. Fase Sintática (Gera a AST)
            Program ast = parser.parseProgram();
            System.out.println("\n---------------------------------------");
            System.out.println("SINTÁTICO: Sucesso! O código é sintaticamente correto.");

            // 2. Fase Semântica (Valida Tipos e Escopo)
            TypeChecker checker = new TypeChecker();
            checker.check(ast);
            System.out.println("SEMÂNTICO: Sucesso! Verificação de tipos concluída.");
            System.out.println("---------------------------------------");

            // 3. Fase de Geração de Código (Onde o resultado é gerado)
            System.out.println("---> Gerando código para a TAM (Triangle Abstract Machine)...");
            Encoder generator = new Encoder();
            generator.visitProgram(ast); // Faz o visitor varrer a AST gerando o código

            String generatedCode = generator.getGeneratedCode();
            System.out.println("\n======= CÓDIGO OBJETO TAM GERADO =======");
            System.out.println(generatedCode);
            System.out.println("========================================");

            saveObjectFile(path, generatedCode);

            // 4. Visualizadores Visuais (Opcional, rodam no final)
            PrintVisitor printer = new PrintVisitor();
            printer.print(ast);

            GUIVisitor gui = new GUIVisitor();
            gui.show(ast);

        } catch (RuntimeException e) {
            System.err.println("\n---------------------------------------");
            System.err.println("RESULTADO: Erro detectado durante a compilação!");
            System.err.println("Detalhes: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("\nErro inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void saveObjectFile(String sourcePath, String content) {
        try {
            File sourceFile = new File(sourcePath);
            String fileName = sourceFile.getName();
            File objDir = new File("obj");

            if (!objDir.exists()) {
                objDir.mkdirs();
            }

            File outputFile = new File(objDir, fileName);
            Files.write(outputFile.toPath(), content.getBytes());

            System.out.println("\nARQUIVO OBJETO GERADO: " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Erro ao salvar o arquivo objeto: " + e.getMessage());
        }
    }
}