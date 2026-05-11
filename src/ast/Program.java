package ast;

public class Program extends AST {
    public Command C;

    public Program(Command c) {
        this.C = c;
    }

    @Override
    public void visit(Visitor v) {
        v.visitProgram(this);
    }
}