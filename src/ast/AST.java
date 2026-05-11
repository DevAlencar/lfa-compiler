package ast;

public abstract class AST {
    public abstract void visit(Visitor v);
}