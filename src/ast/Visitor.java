package ast;

public interface Visitor {
    // Program
    void visitProgram(Program ast);

    // Commands
    void visitAssignCommand(Command.AssignCommand ast);
    void visitCallCommand(Command.CallCommand ast);
    void visitSequentialCommand(Command.SequentialCommand ast);
    void visitIfCommand(Command.IfCommand ast);
    void visitWhileCommand(Command.WhileCommand ast);
    void visitLetCommand(Command.LetCommand ast);
    void visitEmptyCommand(Command.EmptyCommand ast);

    // Expressions
    void visitVnameExpression(Expression.VnameExpression ast);
    void visitIntLiteralExpression(Expression.IntLiteralExpression ast);
    void visitBoolLiteralExpression(Expression.BoolLiteralExpression ast);
    void visitUnaryExpression(Expression.UnaryExpression ast);
    void visitBinaryExpression(Expression.BinaryExpression ast);
    void visitEmptyExpression(Expression.EmptyExpression ast);
    void visitFloatLiteralExpression(Expression.FloatLiteralExpression ast);

    // Declarations
    void visitSequentialDeclaration(Declaration.SequentialDeclaration ast);
    void visitConstDeclaration(Declaration.ConstDeclaration ast);
    void visitVarDeclaration(Declaration.VarDeclaration ast);
    void visitEmptyDeclaration(Declaration.EmptyDeclaration ast);

    // Vnames
    void visitSimpleVname(Vname.SimpleVname ast);

    // TypeDenoters
    void visitSimpleTypeDenoter(TypeDenoter.SimpleTypeDenoter ast);

    // Terminals
    void visitIdentifier(Terminal.Identifier ast);
    void visitIntegerLiteral(Terminal.IntegerLiteral ast);
    void visitBooleanLiteral(Terminal.BooleanLiteral ast);
    void visitOperator(Terminal.Operator ast);
    void visitFloatLiteral(Terminal.FloatLiteral ast);
}