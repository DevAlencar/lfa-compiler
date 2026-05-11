import enums.Kind;

public class Parser {
    private Token currentToken;
    private final Scanner scanner;

    public Parser(Scanner scanner){
        this.scanner = scanner;
        currentToken = scanner.scan();
    }

    private void accept(byte expected) {
        if (currentToken.kind == expected) {
            acceptIt();
        } else {
            throw new RuntimeException("Erro Sintático: Esperado " + Kind.fromByte(expected) + " mas encontrado " + Kind.fromByte(currentToken.kind) + " na linha: " + currentToken.currentLine + " coluna: " + currentToken.currentColumn);
        }
    }

    private void acceptIt() {
        currentToken = scanner.scan();
    }

    // 1. Program ::= single-Command
    public void parseProgram() {
        parseSingleCommand();
        if (currentToken.kind != Kind.EOT.getValue()) {
            throw new RuntimeException("Erro: Tokens extras após o fim do programa.");
        }
        System.out.println("Compilação sintática concluída com sucesso!");
    }

    // 2. Command ::= single-Command Command-Tail
    private void parseCommand() {
        parseSingleCommand();
        parseCommandTail();
    }

    // 3. Command-Tail ::= ; single-Command Command-Tail | epsilon
    private void parseCommandTail() {
        if (currentToken.kind == Kind.SEMICOLON.getValue()) {
            acceptIt();
            parseSingleCommand();
            parseCommandTail();
        }
        // Epsilon: não faz nada
    }

    // 4. single-Command
    private void parseSingleCommand() {
        if(currentToken.kind == Kind.IF.getByteValue()) {
            acceptIt();
            parseExpression();
            accept(Kind.THEN.getByteValue());
            parseSingleCommand();
            accept(Kind.ELSE.getByteValue());
            parseSingleCommand();
            return;
        }else if(currentToken.kind == Kind.WHILE.getByteValue()){
            acceptIt();
            parseExpression();
            accept(Kind.DO.getByteValue());
            parseSingleCommand();
            return;
        }else if(currentToken.kind == Kind.LET.getByteValue()) {
            acceptIt();
            parseDeclaration();
            accept(Kind.IN.getByteValue());
            parseSingleCommand();
            return;
        }else if(currentToken.kind == Kind.BEGIN.getByteValue()) {
            acceptIt();
            parseCommand();
            accept(Kind.END.getByteValue());
            return;
        }else if(currentToken.kind == Kind.IDENTIFIER.getByteValue()) {
            acceptIt();
            parseCommandRest();
            return;
        }
            throw new RuntimeException("Erro: Início de comando inválido " + currentToken.kind);
    }

    // 5. Command-Rest ::= := Expression | ( Expression )
    private void parseCommandRest() {
        if (currentToken.kind == Kind.BECOMES.getByteValue()) {
            acceptIt();
            parseExpression();
        } else if (currentToken.kind == Kind.LPAREN.getByteValue()) {
            acceptIt();
            parseExpression();
            accept(Kind.RPAREN.getByteValue());
        } else {
            throw new RuntimeException("Erro: Esperado ':=' ou '(' após identificador.");
        }
    }

    // 6. Expression ::= primary-Expression Expression-Tail
    private void parseExpression() {
        parsePrimaryExpression();
        parseExpressionTail();
    }

    // 7. Expression-Tail ::= Operator primary-Expression Expression-Tail | epsilon
    private void parseExpressionTail() {
        if (currentToken.kind == Kind.OPERATOR.getByteValue()) {
            acceptIt();
            parsePrimaryExpression();
            parseExpressionTail();
        }
    }

    // 8. primary-Expression
    private void parsePrimaryExpression() {
        if(currentToken.kind == Kind.INTLITERAL.getByteValue()) {
            acceptIt();
            return;
        }else if(currentToken.kind == Kind.IDENTIFIER.getByteValue()) {
            acceptIt();
            return;
        }else if(currentToken.kind == Kind.OPERATOR.getByteValue()) {
            acceptIt();
            parsePrimaryExpression();
            return;
        }else if(currentToken.kind == Kind.LPAREN.getByteValue()) {
            acceptIt();
            parseExpression();
            accept(Kind.RPAREN.getByteValue());
            return;
        }
        throw new RuntimeException("Erro: Expressão primária inválida.");
    }

    // 9. Declaration ::= single-Declaration Declaration-Tail
    private void parseDeclaration() {
        parseSingleDeclaration();
        parseDeclarationTail();
    }

    // 10. Declaration-Tail ::= ; single-Declaration Declaration-Tail | epsilon
    private void parseDeclarationTail() {
        if (currentToken.kind == Kind.SEMICOLON.getByteValue()) {
            acceptIt();
            parseSingleDeclaration();
            parseDeclarationTail();
        }
    }

    // 11. single-Declaration ::= const Identifier ~ Expression | var Identifier : Type-denoter
    private void parseSingleDeclaration() {
        if (currentToken.kind == Kind.CONST.getByteValue()) {
            acceptIt();
            accept(Kind.IDENTIFIER.getByteValue());
            accept(Kind.IS.getByteValue());
            parseExpression();
        } else if (currentToken.kind == Kind.VAR.getByteValue()) {
            acceptIt();
            accept(Kind.IDENTIFIER.getByteValue());
            accept(Kind.COLON.getByteValue());
            parseTypeDenoter();
        }
    }

    // 12. Type-denoter ::= Identifier
    private void parseTypeDenoter() {
        accept(Kind.IDENTIFIER.getByteValue());
    }
}