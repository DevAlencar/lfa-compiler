import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import enums.Kind;

public class Scanner {
    private BufferedReader reader;
    private char currentChar;
    private byte currentKind;
    private StringBuilder currentSpelling;

    int currentLine = 1;
    int currentCharPosition = 0;
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

    public Token scan() {
        while (currentChar == '!' || currentChar == ' ' || currentChar == '\n'){
            this.scanSeparator();
        }
        
        int startColumn = this.currentCharPosition;
		int startLine = this.currentLine;
        
        currentSpelling = new StringBuilder("");
        currentKind = this.scanToken();
        return new Token(currentKind, currentSpelling.toString(), startLine, startColumn);
    }

    private void scanSeparator() {
        switch (currentChar){
            case '!': {
                this.takeIt();
                while (this.isGraphic(currentChar)){
                    takeIt();
                };
                take('\n');
                break;
            }
            case ' ': case '\n':
                takeIt();
                break;
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
        return !(c == '\n');
    }

    private byte scanToken() {
        if (isLetter(currentChar)) {
            while (isLetter(currentChar) || isDigit(currentChar))
                takeIt();
            return (byte) Kind.IDENTIFIER.getValue();
        }

        if (isDigit(currentChar)) {
            takeIt();
            while (isDigit(currentChar))
                takeIt();
            return (byte) Kind.INTLITERAL.getValue();
        }

        switch (currentChar) {
            case '+': case '-': case '*': case '/': case '<': case '>': case '=':
                takeIt();
                return (byte) Kind.OPERATOR.getValue();
            case ';':
                takeIt();
                return (byte) Kind.SEMICOLON.getValue();
            case ':':
                takeIt();
                if (currentChar == '=') {
                    takeIt();
                    return (byte) Kind.BECOMES.getValue();
                } else {
                    return (byte) Kind.COLON.getValue();
                }
            case '~':
                takeIt();
                return (byte) Kind.IS.getValue();
            case '(':
                takeIt();
                return (byte) Kind.LPAREN.getValue();
            case ')':
                takeIt();
                return (byte) Kind.RPAREN.getValue();
            case EOT:
                return (byte) Kind.EOT.getValue();
            default:
                throw new RuntimeException("Erro léxico: caractere inesperado '" + currentChar + "'");
        }
    }
}