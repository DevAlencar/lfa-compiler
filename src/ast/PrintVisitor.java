package ast;

public class PrintVisitor implements Visitor {
    private int i = 0;

    private void indent() {
        for (int j = 0; j < i; j++) {
            System.out.print("  ");
        }
    }

    public void print(Program p) {
        System.out.println("---> Iniciando impressao da arvore");
        p.visit(this);
    }

    @Override
    public void visitProgram(Program ast) {
        indent();
        System.out.println("Program");
        i++;
        if (ast.C != null) ast.C.visit(this);
        i--;
    }

    @Override
    public void visitAssignCommand(Command.AssignCommand ast) {
        indent();
        System.out.println("AssignCommand");
        i++;
        ast.V.visit(this);
        ast.E.visit(this);
        i--;
    }

    @Override
    public void visitCallCommand(Command.CallCommand ast) {
        indent();
        System.out.println("CallCommand");
        i++;
        ast.I.visit(this);
        ast.E.visit(this);
        i--;
    }

    @Override
    public void visitSequentialCommand(Command.SequentialCommand ast) {
        indent();
        System.out.println("SequentialCommand");
        i++;
        ast.C1.visit(this);
        ast.C2.visit(this);
        i--;
    }

    @Override
    public void visitIfCommand(Command.IfCommand ast) {
        indent();
        System.out.println("IfCommand");
        i++;
        ast.E.visit(this);
        ast.C1.visit(this);
        ast.C2.visit(this);
        i--;
    }

    @Override
    public void visitWhileCommand(Command.WhileCommand ast) {
        indent();
        System.out.println("WhileCommand");
        i++;
        ast.E.visit(this);
        ast.C.visit(this);
        i--;
    }

    @Override
    public void visitLetCommand(Command.LetCommand ast) {
        indent();
        System.out.println("LetCommand");
        i++;
        ast.D.visit(this);
        ast.C.visit(this);
        i--;
    }

    @Override
    public void visitEmptyCommand(Command.EmptyCommand ast) {
        indent();
        System.out.println("EmptyCommand");
    }

    @Override
    public void visitVnameExpression(Expression.VnameExpression ast) {
        indent();
        System.out.println("VnameExpression");
        i++;
        ast.V.visit(this);
        i--;
    }

    @Override
    public void visitIntLiteralExpression(Expression.IntLiteralExpression ast) {
        indent();
        System.out.println("IntLiteralExpression");
        i++;
        ast.IL.visit(this);
        i--;
    }

    @Override
    public void visitUnaryExpression(Expression.UnaryExpression ast) {
        indent();
        System.out.println("UnaryExpression");
        i++;
        ast.O.visit(this);
        ast.E.visit(this);
        i--;
    }

    @Override
    public void visitBinaryExpression(Expression.BinaryExpression ast) {
        indent();
        System.out.println("BinaryExpression");
        i++;
        ast.E1.visit(this);
        ast.O.visit(this);
        ast.E2.visit(this);
        i--;
    }

    @Override
    public void visitEmptyExpression(Expression.EmptyExpression ast) {
        indent();
        System.out.println("EmptyExpression");
    }

    @Override
    public void visitSequentialDeclaration(Declaration.SequentialDeclaration ast) {
        indent();
        System.out.println("SequentialDeclaration");
        i++;
        ast.D1.visit(this);
        ast.D2.visit(this);
        i--;
    }

    @Override
    public void visitConstDeclaration(Declaration.ConstDeclaration ast) {
        indent();
        System.out.println("ConstDeclaration");
        i++;
        ast.I.visit(this);
        ast.E.visit(this);
        i--;
    }

    @Override
    public void visitVarDeclaration(Declaration.VarDeclaration ast) {
        indent();
        System.out.println("VarDeclaration");
        i++;
        ast.I.visit(this);
        ast.T.visit(this);
        i--;
    }

    @Override
    public void visitEmptyDeclaration(Declaration.EmptyDeclaration ast) {
        indent();
        System.out.println("EmptyDeclaration");
    }

    @Override
    public void visitSimpleVname(Vname.SimpleVname ast) {
        indent();
        System.out.println("SimpleVname");
        i++;
        ast.I.visit(this);
        i--;
    }

    @Override
    public void visitSimpleTypeDenoter(TypeDenoter.SimpleTypeDenoter ast) {
        indent();
        System.out.println("SimpleTypeDenoter");
        i++;
        ast.I.visit(this);
        i--;
    }

    @Override
    public void visitIdentifier(Terminal.Identifier ast) {
        indent();
        System.out.println("Identifier(" + ast.spelling + ")");
    }

    @Override
    public void visitIntegerLiteral(Terminal.IntegerLiteral ast) {
        indent();
        System.out.println("IntegerLiteral(" + ast.spelling + ")");
    }

    @Override
    public void visitOperator(Terminal.Operator ast) {
        indent();
        System.out.println("Operator(" + ast.spelling + ")");
    }
}