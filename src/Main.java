import enums.Kind;
// Importe aqui seu Scanner, Parser e Token conforme a estrutura de pacotes

public class Main {
    public static void main(String[] args) {
        // 1. Caminho do arquivo de teste
        String path = "/home/arthur/Documents/lfa-compiler/src/codes/test1.txt";

        System.out.println("Iniciando Compilação...");
        System.out.println("Arquivo: " + path);
        System.out.println("---------------------------------------\n");

        try {
            // 2. Inicializa o Scanner (Léxico)
            Scanner scanner = new Scanner(path);

            // 3. Inicializa o Parser (Sintático) passando o scanner
            // O construtor do Parser já chamará scanner.scan() para pegar o primeiro token
            Parser parser = new Parser(scanner);

            // 4. Chama o axioma (regra inicial) da gramática
            parser.parseProgram();

            System.out.println("\n---------------------------------------");
            System.out.println("RESULTADO: Sucesso! O código é sintaticamente correto.");

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