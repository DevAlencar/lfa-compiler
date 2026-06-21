package ast;

public class Decompiler implements Visitor {

    private final StringBuilder output = new StringBuilder();
    private int indentLevel = 0;
    private boolean skipSemicolonOnNextFinish = false;

    public String decompile(Program program) {
        output.setLength(0);
        indentLevel = 0;
        program.visit(this);
        return output.toString().trim() + "\n";
    }

    private void appendIndent() {
        for (int i = 0; i < indentLevel; i++) {
            output.append("    ");
        }
    }

    private void appendLine(String text) {
        appendIndent();
        output.append(text);
        output.append('\n');
    }

    private void finishStatement() {
        if (skipSemicolonOnNextFinish) {
            output.append('\n');
            skipSemicolonOnNextFinish = false;
        } else {
            output.append(";\n");
        }
    }

    private static boolean isEmptyCommand(Command command) {
        return command instanceof Command.EmptyCommand;
    }

    private static boolean isMultiStatementBlock(Command command) {
        if (command instanceof Command.SequentialCommand sequential) {
            return !isEmptyCommand(sequential.C2);
        }
        return false;
    }

    private static int operatorPrecedence(String operator) {
        switch (operator) {
            case "or":
                return 1;
            case "+":
            case "-":
                return 2;
            case "and":
                return 3;
            case "*":
            case "/":
                return 4;
            case "<":
            case ">":
            case "=":
                return 0;
            default:
                return 5;
        }
    }

    @Override
    public void visitProgram(Program ast) {
        output.append("program ");
        if (ast.name != null) {
            ast.name.visit(this);
        }
        output.append(";\n");

        if (ast.D != null) {
            ast.D.visit(this);
        }

        appendLine("begin");
        indentLevel++;
        if (ast.C != null) {
            ast.C.visit(this);
        }
        indentLevel--;
        appendLine("end.");
    }

    @Override
    public void visitAssignCommand(Command.AssignCommand ast) {
        appendIndent();
        ast.V.visit(this);
        output.append(" := ");
        ast.E.visit(this);
        finishStatement();
    }

    @Override
    public void visitCallCommand(Command.CallCommand ast) {
        appendIndent();
        ast.I.visit(this);
        output.append('(');
        ast.E.visit(this);
        output.append(')');
        finishStatement();
    }

    @Override
    public void visitSequentialCommand(Command.SequentialCommand ast) {
        if (ast.C1 != null) {
            ast.C1.visit(this);
        }
        if (ast.C2 != null && !isEmptyCommand(ast.C2)) {
            ast.C2.visit(this);
        }
    }

    @Override
    public void visitIfCommand(Command.IfCommand ast) {
        appendIndent();
        output.append("if ");
        ast.E.visit(this);
        output.append(" then\n");

        boolean hasElse = ast.C2 != null && !isEmptyCommand(ast.C2);
        if (hasElse && ast.C1 instanceof Command.AssignCommand) {
            skipSemicolonOnNextFinish = true;
        }

        indentLevel++;
        if (isMultiStatementBlock(ast.C1)) {
            appendBlock(ast.C1);
        } else if (ast.C1 != null) {
            ast.C1.visit(this);
        }
        indentLevel--;
        skipSemicolonOnNextFinish = false;

        if (hasElse) {
            appendLine("else");
            indentLevel++;
            if (isMultiStatementBlock(ast.C2)) {
                appendBlock(ast.C2);
            } else {
                ast.C2.visit(this);
            }
            indentLevel--;
        }
    }

    @Override
    public void visitWhileCommand(Command.WhileCommand ast) {
        appendIndent();
        output.append("while ");
        ast.E.visit(this);
        output.append(" do\n");
        indentLevel++;
        if (isMultiStatementBlock(ast.C)) {
            appendBlock(ast.C);
        } else if (ast.C != null) {
            ast.C.visit(this);
        }
        indentLevel--;
    }

    @Override
    public void visitLetCommand(Command.LetCommand ast) {
        appendLine("let");
        indentLevel++;
        if (ast.D != null) {
            ast.D.visit(this);
        }
        indentLevel--;
        appendLine("in");
        indentLevel++;
        if (ast.C != null) {
            ast.C.visit(this);
        }
        indentLevel--;
    }

    @Override
    public void visitEmptyCommand(Command.EmptyCommand ast) {
        // fim da lista de comandos
    }

    private void appendBlock(Command command) {
        appendLine("begin");
        indentLevel++;
        command.visit(this);
        indentLevel--;
        appendLine("end");
    }

    @Override
    public void visitVnameExpression(Expression.VnameExpression ast) {
        ast.V.visit(this);
    }

    @Override
    public void visitIntLiteralExpression(Expression.IntLiteralExpression ast) {
        ast.IL.visit(this);
    }

    @Override
    public void visitBoolLiteralExpression(Expression.BoolLiteralExpression ast) {
        ast.BL.visit(this);
    }

    @Override
    public void visitFloatLiteralExpression(Expression.FloatLiteralExpression ast) {
        ast.FL.visit(this);
    }

    @Override
    public void visitUnaryExpression(Expression.UnaryExpression ast) {
        ast.O.visit(this);
        appendExpression(ast.E, operatorPrecedence(ast.O.spelling) + 1);
    }

    @Override
    public void visitBinaryExpression(Expression.BinaryExpression ast) {
        appendExpression(ast, 0);
    }

    @Override
    public void visitEmptyExpression(Expression.EmptyExpression ast) {
        output.append("<empty>");
    }

    private void appendExpression(Expression expression, int parentPrecedence) {
        if (expression instanceof Expression.BinaryExpression binary) {
            int precedence = operatorPrecedence(binary.O.spelling);
            if (precedence < parentPrecedence) {
                output.append('(');
            }
            appendExpression(binary.E1, precedence);
            output.append(' ').append(binary.O.spelling).append(' ');
            appendExpression(binary.E2, precedence + 1);
            if (precedence < parentPrecedence) {
                output.append(')');
            }
            return;
        }

        if (expression instanceof Expression.UnaryExpression unary) {
            if (0 < parentPrecedence) {
                output.append('(');
            }
            unary.O.visit(this);
            appendExpression(unary.E, operatorPrecedence(unary.O.spelling) + 1);
            if (0 < parentPrecedence) {
                output.append(')');
            }
            return;
        }

        if (needsParentheses(expression, parentPrecedence)) {
            output.append('(');
            expression.visit(this);
            output.append(')');
        } else {
            expression.visit(this);
        }
    }

    private boolean needsParentheses(Expression expression, int parentPrecedence) {
        return parentPrecedence > 0 && expression instanceof Expression.BinaryExpression;
    }

    @Override
    public void visitSequentialDeclaration(Declaration.SequentialDeclaration ast) {
        if (ast.D1 != null) {
            ast.D1.visit(this);
        }
        if (ast.D2 != null && !(ast.D2 instanceof Declaration.EmptyDeclaration)) {
            ast.D2.visit(this);
        }
    }

    @Override
    public void visitConstDeclaration(Declaration.ConstDeclaration ast) {
        appendIndent();
        output.append("const ");
        ast.I.visit(this);
        output.append(" ~ ");
        ast.E.visit(this);
        output.append(";\n");
    }

    @Override
    public void visitVarDeclaration(Declaration.VarDeclaration ast) {
        appendIndent();
        output.append("var ");
        ast.I.visit(this);
        output.append(" : ");
        ast.T.visit(this);
        output.append(";\n");
    }

    @Override
    public void visitEmptyDeclaration(Declaration.EmptyDeclaration ast) {
        // sem declarações
    }

    @Override
    public void visitSimpleVname(Vname.SimpleVname ast) {
        ast.I.visit(this);
    }

    @Override
    public void visitSimpleTypeDenoter(TypeDenoter.SimpleTypeDenoter ast) {
        ast.I.visit(this);
    }

    @Override
    public void visitIdentifier(Terminal.Identifier ast) {
        output.append(ast.spelling);
    }

    @Override
    public void visitIntegerLiteral(Terminal.IntegerLiteral ast) {
        output.append(ast.spelling);
    }

    @Override
    public void visitBooleanLiteral(Terminal.BooleanLiteral ast) {
        output.append(ast.spelling);
    }

    @Override
    public void visitOperator(Terminal.Operator ast) {
        output.append(ast.spelling);
    }

    @Override
    public void visitFloatLiteral(Terminal.FloatLiteral ast) {
        output.append(ast.spelling);
    }
}
