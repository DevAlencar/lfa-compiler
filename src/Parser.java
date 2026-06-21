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

    private Terminal.FloatLiteral parseFloatLiteral() {
        if (currentToken.kind == Kind.FLOATLITERAL.getByteValue()) {
            Terminal.FloatLiteral fl = new Terminal.FloatLiteral(currentToken.spelling);
            acceptIt();
            return fl;
        } else {
            throw new RuntimeException("Erro: Literal float esperado.");
        }
    }

    public Program parseProgram() {
        accept(Kind.PROGRAM.getByteValue());
        Terminal.Identifier id = parseIdentifier();
        accept(Kind.SEMICOLON.getByteValue());
        Declaration d = parseDeclaracoes();
        Command c = parseComandoComposto();
        accept(Kind.DOT.getByteValue());
        accept(Kind.EOT.getByteValue());
        System.out.println("Compilação sintática concluída com sucesso!");
        return new Program(id, d, c);
    }

    private Declaration parseDeclaracoes() {
        if (currentToken.kind == Kind.VAR.getByteValue()) {
            Declaration d1 = parseDeclaracao();
            accept(Kind.SEMICOLON.getByteValue());
            Declaration d2 = parseDeclaracoes();
            return new Declaration.SequentialDeclaration(d1, d2);
        }
        return new Declaration.EmptyDeclaration();
    }

    private Declaration parseDeclaracao() {
        return parseDeclaracaoDeVariavel();
    }

    private Declaration parseDeclaracaoDeVariavel() {
        accept(Kind.VAR.getByteValue());
        Terminal.Identifier id = parseIdentifier();
        accept(Kind.COLON.getByteValue());
        TypeDenoter t = parseTipo();
        return new Declaration.VarDeclaration(id, t);
    }

    private TypeDenoter parseTipo() {
        return parseTipoSimples();
    }

    private TypeDenoter parseTipoSimples() {
        if (currentToken.kind == Kind.INTEGER.getByteValue()) {
            Terminal.Identifier id = new Terminal.Identifier(currentToken.spelling);
            acceptIt();
            return new TypeDenoter.SimpleTypeDenoter(id);
        } else if (currentToken.kind == Kind.BOOLEAN.getByteValue()) {
            Terminal.Identifier id = new Terminal.Identifier(currentToken.spelling);
            acceptIt();
            return new TypeDenoter.SimpleTypeDenoter(id);
        } else {
            throw new RuntimeException("Erro: Tipo esperado (integer ou boolean).");
        }
    }

    private Command parseComandoComposto() {
        accept(Kind.BEGIN.getByteValue());
        Command c = parseListaDeComandos();
        accept(Kind.END.getByteValue());
        return c;
    }

    private Command parseListaDeComandos() {
        if (currentToken.kind == Kind.IDENTIFIER.getByteValue() ||
            currentToken.kind == Kind.IF.getByteValue() ||
            currentToken.kind == Kind.WHILE.getByteValue() ||
            currentToken.kind == Kind.BEGIN.getByteValue()) {
            Command c1 = parseComando();
            accept(Kind.SEMICOLON.getByteValue());
            Command c2 = parseListaDeComandos();
            return new Command.SequentialCommand(c1, c2);
        }
        return new Command.EmptyCommand();
    }

    private Command parseComando() {
        if (currentToken.kind == Kind.IDENTIFIER.getByteValue()) {
            return parseAtribuicao();
        } else if (currentToken.kind == Kind.IF.getByteValue()) {
            return parseCondicional();
        } else if (currentToken.kind == Kind.WHILE.getByteValue()) {
            return parseIterativo();
        } else if (currentToken.kind == Kind.BEGIN.getByteValue()) {
            return parseComandoComposto();
        }
        throw new RuntimeException("Erro: Comando inválido. Linha: " + currentToken.currentLine);
    }

    private Command parseAtribuicao() {
        Terminal.Identifier id = parseIdentifier();
        accept(Kind.BECOMES.getByteValue());
        Expression e = parseExpression();
        return new Command.AssignCommand(new Vname.SimpleVname(id), e);
    }

    private Command parseCondicional() {
        accept(Kind.IF.getByteValue());
        Expression e = parseExpression();
        accept(Kind.THEN.getByteValue());
        Command c1 = parseComando();
        Command c2 = parseRestoCondicional();
        return new Command.IfCommand(e, c1, c2);
    }

    private Command parseRestoCondicional() {
        if (currentToken.kind == Kind.ELSE.getByteValue()) {
            acceptIt();
            return parseComando();
        }
        return new Command.EmptyCommand();
    }

    private Command parseIterativo() {
        accept(Kind.WHILE.getByteValue());
        Expression e = parseExpression();
        accept(Kind.DO.getByteValue());
        Command c = parseComando();
        return new Command.WhileCommand(e, c);
    }

    private Expression parseExpression() {
        Expression e = parseExpressaoSimples();
        return parseRestoExpressao(e);
    }

    private boolean isOpRel(byte kind) {
        return kind == Kind.OPERATOR.getByteValue() && 
               (currentToken.spelling.equals("<") || currentToken.spelling.equals(">") || currentToken.spelling.equals("="));
    }

    private Expression parseRestoExpressao(Expression e1) {
        if (isOpRel(currentToken.kind)) {
            Terminal.Operator op = parseOperator();
            Expression e2 = parseExpressaoSimples();
            return new Expression.BinaryExpression(e1, op, e2);
        }
        return e1;
    }

    private Expression parseExpressaoSimples() {
        Expression e = parseTermo();
        return parseRestoExprSimples(e);
    }

    private boolean isOpAd(byte kind) {
        if (kind == Kind.OPERATOR.getByteValue()) {
            return currentToken.spelling.equals("+") || currentToken.spelling.equals("-");
        }
        return kind == Kind.OR.getByteValue();
    }

    private Expression parseRestoExprSimples(Expression e1) {
        if (isOpAd(currentToken.kind)) {
            Terminal.Operator op;
            if (currentToken.kind == Kind.OR.getByteValue()) {
                op = new Terminal.Operator(currentToken.spelling);
                acceptIt();
            } else {
                op = parseOperator();
            }
            Expression e2 = parseTermo();
            return parseRestoExprSimples(new Expression.BinaryExpression(e1, op, e2));
        }
        return e1;
    }

    private Expression parseTermo() {
        Expression e = parseFator();
        return parseRestoTermo(e);
    }

    private boolean isOpMul(byte kind) {
        if (kind == Kind.OPERATOR.getByteValue()) {
            return currentToken.spelling.equals("*") || currentToken.spelling.equals("/");
        }
        return kind == Kind.AND.getByteValue();
    }

    private Expression parseRestoTermo(Expression e1) {
        if (isOpMul(currentToken.kind)) {
            Terminal.Operator op;
            if (currentToken.kind == Kind.AND.getByteValue()) {
                op = new Terminal.Operator(currentToken.spelling);
                acceptIt();
            } else {
                op = parseOperator();
            }
            Expression e2 = parseFator();
            return parseRestoTermo(new Expression.BinaryExpression(e1, op, e2));
        }
        return e1;
    }

    private Expression parseFator() {
        if (currentToken.kind == Kind.IDENTIFIER.getByteValue()) {
            Terminal.Identifier id = parseIdentifier();
            return new Expression.VnameExpression(new Vname.SimpleVname(id));
        } else if (currentToken.kind == Kind.LPAREN.getByteValue()) {
            acceptIt();
            Expression e = parseExpression();
            accept(Kind.RPAREN.getByteValue());
            return e;
        } else {
            return parseLiteral();
        }
    }

    private Expression parseLiteral() {
        if (currentToken.kind == Kind.BOOLLITERAL.getByteValue()) {
            return new Expression.BoolLiteralExpression(parseBooleanLiteral());
        } else if (currentToken.kind == Kind.INTLITERAL.getByteValue()) {
            return new Expression.IntLiteralExpression(parseIntegerLiteral());
        } else if (currentToken.kind == Kind.FLOATLITERAL.getByteValue()) {
            return new Expression.FloatLiteralExpression(parseFloatLiteral());
        }
        throw new RuntimeException("Erro: Literal esperado. Linha: " + currentToken.currentLine);
    }
}