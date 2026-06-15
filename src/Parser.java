import ast.Command;
import ast.Declaration;
import ast.Expression;
import ast.Program;
import ast.Terminal;
import ast.TypeDenoter;
import ast.Vname;
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
            throw new RuntimeException(
                "Erro Sintático: Esperado " + Kind.fromByte(expected) 
                + " mas encontrado " + Kind.fromByte(currentToken.kind) 
                + " na linha: " + currentToken.currentLine
                + " coluna: " + currentToken.currentColumn
            );
        }
    }

    private void acceptIt() {
        currentToken = scanner.scan();
    }

    // Terminais
    private Terminal.Identifier parseIdentifier() {
        if (currentToken.kind == Kind.IDENTIFIER.getByteValue()) {
            Terminal.Identifier i = new Terminal.Identifier(currentToken.spelling);
            acceptIt();
            return i;
        } else {
            throw new RuntimeException(
                "Erro: Identificador esperado."
                + " Linha: " + currentToken.currentLine
                + " Coluna: " + currentToken.currentColumn
            );
        }
    }

    private Terminal.IntegerLiteral parseIntegerLiteral() {
        if (currentToken.kind == Kind.INTLITERAL.getByteValue()) {
            Terminal.IntegerLiteral il = new Terminal.IntegerLiteral(currentToken.spelling);
            acceptIt();
            return il;
        } else {
            throw new RuntimeException(
                "Erro: Literal inteiro esperado."
                + " Linha: " + currentToken.currentLine
                + " Coluna: " + currentToken.currentColumn
            );
        }
    }

    private Terminal.BooleanLiteral parseBooleanLiteral() {
        if (currentToken.kind == Kind.BOOLLITERAL.getByteValue()) {
            Terminal.BooleanLiteral bl = new Terminal.BooleanLiteral(currentToken.spelling);
            acceptIt();
            return bl;
        } else {
            throw new RuntimeException(
                "Erro: Literal booleano esperado."
                + " Linha: " + currentToken.currentLine
                + " Coluna: " + currentToken.currentColumn
            );
        }
    }

    private Terminal.Operator parseOperator() {
        if (currentToken.kind == Kind.OPERATOR.getByteValue()) {
            Terminal.Operator o = new Terminal.Operator(currentToken.spelling);
            acceptIt();
            return o;
        } else {
            throw new RuntimeException(
                "Erro: Operador esperado."
                + " Linha: " + currentToken.currentLine
                + " Coluna: " + currentToken.currentColumn
            );
        }
    }

    // 1. Program ::= single-Command
    public Program parseProgram() {
        Command c = parseSingleCommand();
        if (currentToken.kind != Kind.EOT.getValue()) {
            throw new RuntimeException(
                "Erro: Tokens extras após o fim do programa."
            );
        }
        System.out.println("Compilação sintática concluída com sucesso!");
        return new Program(c);
    }

    // 2. Command ::= single-Command Command-Tail
    private Command parseCommand() {
        Command c1 = parseSingleCommand();
        return parseCommandTail(c1);
    }

    // 3. Command-Tail ::= ; single-Command Command-Tail | epsilon
    private Command parseCommandTail(Command c1) {
        if (currentToken.kind == Kind.SEMICOLON.getValue()) {
            acceptIt();
            Command c2 = parseSingleCommand();
            return parseCommandTail(new Command.SequentialCommand(c1, c2));
        }
        return c1;
    }

    // 4. single-Command
    private Command parseSingleCommand() {
        if(currentToken.kind == Kind.IF.getByteValue()) {
            acceptIt();
            Expression e = parseExpression();
            accept(Kind.THEN.getByteValue());
            Command c1 = parseSingleCommand();
            accept(Kind.ELSE.getByteValue());
            Command c2 = parseSingleCommand();
            return new Command.IfCommand(e, c1, c2);
        }else if(currentToken.kind == Kind.WHILE.getByteValue()){
            acceptIt();
            Expression e = parseExpression();
            accept(Kind.DO.getByteValue());
            Command c = parseSingleCommand();
            return new Command.WhileCommand(e, c);
        }else if(currentToken.kind == Kind.LET.getByteValue()) {
            acceptIt();
            Declaration d = parseDeclaration();
            accept(Kind.IN.getByteValue());
            Command c = parseSingleCommand();
            return new Command.LetCommand(d, c);
        }else if(currentToken.kind == Kind.BEGIN.getByteValue()) {
            acceptIt();
            Command c = parseCommand();
            accept(Kind.END.getByteValue());
            return c;
        }else if(currentToken.kind == Kind.IDENTIFIER.getByteValue()) {
            Terminal.Identifier i = parseIdentifier();
            return parseCommandRest(i);
        }
        throw new RuntimeException(
            "Erro: Início de comando inválido " + currentToken.kind
            + " Linha: " + currentToken.currentLine
            + " Coluna: " + currentToken.currentColumn
        );
    }

    // 5. Command-Rest ::= := Expression | ( Expression )
    private Command parseCommandRest(Terminal.Identifier i) {
        if (currentToken.kind == Kind.BECOMES.getByteValue()) {
            acceptIt();
            Expression e = parseExpression();
            return new Command.AssignCommand(new Vname.SimpleVname(i), e);
        } else if (currentToken.kind == Kind.LPAREN.getByteValue()) {
            acceptIt();
            Expression e = parseExpression();
            accept(Kind.RPAREN.getByteValue());
            return new Command.CallCommand(i, e);
        } else {
            throw new RuntimeException(
                "Erro: Esperado ':=' ou '(' após identificador."
                + " Linha: " + currentToken.currentLine
                + " Coluna: " + currentToken.currentColumn
            );
        }
    }

    // 6. Expression ::= primary-Expression Expression-Tail
    private Expression parseExpression() {
        Expression e1 = parsePrimaryExpression();
        return parseExpressionTail(e1);
    }

    // 7. Expression-Tail ::= Operator primary-Expression Expression-Tail | epsilon
    private Expression parseExpressionTail(Expression e1) {
        if (currentToken.kind == Kind.OPERATOR.getByteValue()) {
            Terminal.Operator o = parseOperator();
            Expression e2 = parsePrimaryExpression();
            return parseExpressionTail(new Expression.BinaryExpression(e1, o, e2));
        }
        return e1;
    }

    // 8. primary-Expression
    private Expression parsePrimaryExpression() {
        if(currentToken.kind == Kind.INTLITERAL.getByteValue()) {
            Terminal.IntegerLiteral il = parseIntegerLiteral();
            return new Expression.IntLiteralExpression(il);
        }else if(currentToken.kind == Kind.IDENTIFIER.getByteValue()) {
            Terminal.Identifier i = parseIdentifier();
            return new Expression.VnameExpression(new Vname.SimpleVname(i));
        }else if(currentToken.kind == Kind.OPERATOR.getByteValue()) {
            Terminal.Operator o = parseOperator();
            Expression e = parsePrimaryExpression();
            return new Expression.UnaryExpression(o, e);
        }else if(currentToken.kind == Kind.LPAREN.getByteValue()) {
            acceptIt();
            Expression e = parseExpression();
            accept(Kind.RPAREN.getByteValue());
            return e;
        }else if (currentToken.kind == Kind.BOOLLITERAL.getByteValue()) {
            Terminal.BooleanLiteral bl = parseBooleanLiteral();
            return new Expression.BoolLiteralExpression(bl);
        }
        throw new RuntimeException(
            "Erro: Expressão primária inválida."
            + " Linha: " + currentToken.currentLine
            + " Coluna: " + currentToken.currentColumn
        );
    }

    // 9. Declaration ::= single-Declaration Declaration-Tail
    private Declaration parseDeclaration() {
        Declaration d1 = parseSingleDeclaration();
        return parseDeclarationTail(d1);
    }

    // 10. Declaration-Tail ::= ; single-Declaration Declaration-Tail | epsilon
    private Declaration parseDeclarationTail(Declaration d1) {
        if (currentToken.kind == Kind.SEMICOLON.getByteValue()) {
            acceptIt();
            Declaration d2 = parseSingleDeclaration();
            return parseDeclarationTail(new Declaration.SequentialDeclaration(d1, d2));
        }
        return d1;
    }

    // 11. single-Declaration ::= const Identifier ~ Expression | var Identifier : Type-denoter
    private Declaration parseSingleDeclaration() {
        if (currentToken.kind == Kind.CONST.getByteValue()) {
            acceptIt();
            Terminal.Identifier i = parseIdentifier();
            accept(Kind.IS.getByteValue());
            Expression e = parseExpression();
            return new Declaration.ConstDeclaration(i, e);
        } else if (currentToken.kind == Kind.VAR.getByteValue()) {
            acceptIt();
            Terminal.Identifier i = parseIdentifier();
            accept(Kind.COLON.getByteValue());
            TypeDenoter t = parseTypeDenoter();
            return new Declaration.VarDeclaration(i, t);
        }
        throw new RuntimeException(
            "Erro: Declaração inválida."
            + " Linha: " + currentToken.currentLine
            + " Coluna: " + currentToken.currentColumn
        );
    }

    // 12. Type-denoter ::= Identifier
    private TypeDenoter parseTypeDenoter() {
        Terminal.Identifier i = parseIdentifier();
        return new TypeDenoter.SimpleTypeDenoter(i);
    }
}