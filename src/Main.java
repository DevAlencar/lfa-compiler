import enums.Kind;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner("/home/arthur/Documents/lfa-compiler/src/codes/test1.txt");
        System.out.println("Iniciando Análise Léxica...\n");
        Token token;
        try {
            do {
                token = scanner.scan();
                System.out.printf("Char: %d | Linha: %d | Kind: %-12s (%2d) | Spelling: \"%s\"%n",
                        scanner.currentCharPosition,
                        scanner.currentLine,
                        getKindName(token.kind),
                        token.kind,
                        token.spelling);

            } while (token.kind != (byte) Kind.EOT.getValue());
            System.out.println("\nAnálise finalizada com sucesso!");
        } catch (Exception e) {
            System.err.println("\nErro durante a execução: " + e.getMessage());
        }
    }

    // Método auxiliar apenas para deixar o print bonito no console
    private static String getKindName(byte kindValue) {
        for (Kind k : Kind.values()) {
            if (k.getValue() == kindValue) return k.name();
        }
        return "UNKNOWN";
    }
}