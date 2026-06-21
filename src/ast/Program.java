package ast;

public class Program extends AST {
    public Terminal.Identifier name;
    public Declaration D;
    public Command C;

    public Program(Terminal.Identifier name, Declaration d, Command c) {
        this.name = name;
        this.D = d;
        this.C = c;
    }

    @Override
    public void visit(Visitor v) {
        v.visitProgram(this);
    }
}