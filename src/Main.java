import ast.GUIVisitor;
import ast.PrintVisitor;
import ast.Program;
import ast.TypeChecker; // Garanta que importou o novo TypeChecker

public class Main {
    public static void main(String[] args) {
        // 1. Caminho do arquivo de teste
        String path = "src/codes/nivaldo.txt";

        System.out.println("Iniciando Compilação...");
        System.out.println("Arquivo: " + path);
        System.out.println("---------------------------------------\n");

        try {
            // 2. Inicializa o Scanner (Léxico)
            Scanner scanner = new Scanner(path);

            // 3. Inicializa o Parser (Sintático) passando o scanner
            Parser parser = new Parser(scanner);

            // 4. Chama o axioma e captura a AST
            Program ast = parser.parseProgram();

            System.out.println("\n---------------------------------------");
            System.out.println("SINTÁTICO: Sucesso! O código é sintaticamente correto.");

            // 5. Executa o Analisador Semântico (TypeChecker)
            // Se houver variáveis não declaradas ou tipos errados, a execução para aqui
            TypeChecker checker = new TypeChecker();
            checker.check(ast);
            System.out.println("SEMÂNTICO: Sucesso! Verificação de tipos concluída.");
            System.out.println("---------------------------------------");

            // 6. Visualização da AST no terminal
            PrintVisitor printer = new PrintVisitor();
            printer.print(ast);

            // 7. Visualização da AST em Janela Gráfica
            System.out.println("\n---> Abrindo janela de visualização da AST...");
            GUIVisitor gui = new GUIVisitor();
            gui.show(ast);

        } catch (RuntimeException e) {
            // Captura erros lançados pelo Parser (Sintáticos) ou pelo TypeChecker (Semânticos)
            System.err.println("\n---------------------------------------");
            System.err.println("RESULTADO: Erro detectado durante a compilação!");
            System.err.println("Detalhes: " + e.getMessage());
        } catch (Exception e) {
            // Captura outros erros (ex: arquivo não encontrado)
            System.err.println("\nErro inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }
}