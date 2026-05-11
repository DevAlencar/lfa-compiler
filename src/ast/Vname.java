package ast;

public abstract class Vname extends AST {
    public static class SimpleVname extends Vname {
        public Terminal.Identifier I;

        public SimpleVname(Terminal.Identifier i) {
            this.I = i;
        }

        @Override
        public void visit(Visitor v) {
            v.visitSimpleVname(this);
        }
    }
}