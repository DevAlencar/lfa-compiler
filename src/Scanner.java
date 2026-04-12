import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Scanner {
    private BufferedReader reader;
    private char currentChar;
    private byte currentKind;
    private StringBuilder currentSpelling;

    private int currentLine = 1;
    private int currentCharPosition = 0;
    private static final char EOT = '\u0000';

    public Scanner(String filePath) {
        try {
            this.reader = new BufferedReader(new FileReader(filePath));
            this.currentSpelling = new StringBuilder();
            this.advance();
        } catch (IOException e) {
            System.err.println("Erro ao abrir o arquivo: " + e.getMessage());
        }
    }

    private void advance() {
        try {
            if (currentChar == '\n') {
                currentLine++;
                currentCharPosition = 0;
            }

            int data = reader.read();
            if (data == -1) {
                currentChar = EOT;
            } else {
                currentChar = (char) data;
                currentCharPosition++;
            }
        } catch (IOException e) {
            currentChar = EOT;
        }
    }

    private void take(char expectedChar) {
        if (currentChar == expectedChar) {
            currentSpelling.append(currentChar);
            this.advance();
        } else {
            System.err.printf("Erro Léxico na linha %d, coluna %d: Esperado '%s' mas encontrado '%s'%n",
                    currentLine, currentCharPosition, expectedChar,
                    (currentChar == EOT ? "EOF" : currentChar));
        }
    }

    private void takeIt() {
        currentSpelling.append(currentChar);
        this.advance();
    }

    private boolean isDigit(char c) {
        return Character.isDigit(c);
    }

    private boolean isLetter(char c) {
        return Character.isLetter(c);
    }

    private boolean isGraphic(char c) {
        return !(this.isDigit(c) || this.isLetter(c));
    }
}