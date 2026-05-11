import ast.GUIVisitor;
import ast.PrintVisitor;
import ast.Program;

public class Main {
    public static void main(String[] args) {
        // 1. Caminho do arquivo de teste
        String path = "src/codes/test4_invalid_command.txt";

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
            System.out.println("RESULTADO: Sucesso! O código é sintaticamente correto.");
            
            // 5. Visualização da AST no terminal (Visitor existente)
            PrintVisitor printer = new PrintVisitor();
            printer.print(ast);

            // 6. Visualização da AST em Janela Gráfica (Novo Visitor)
            System.out.println("\n---> Abrindo janela de visualização da AST...");
            GUIVisitor gui = new GUIVisitor();
            gui.show(ast);

        } catch (RuntimeException e) {
            // Captura erros lançados pelo accept() ou parseSingleCommand()
            System.err.println("\n---------------------------------------");
            System.err.println("RESULTADO: Erro Sintático detectado!");
            System.err.println("Detalhes: " + e.getMessage());
        } catch (Exception e) {
            // Captura outros erros (ex: arquivo não encontrado)
            System.err.println("\nErro inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }
}