package ast;

public abstract class TypeDenoter extends AST {
    public static class SimpleTypeDenoter extends TypeDenoter {
        public Terminal.Identifier I;

        public SimpleTypeDenoter(Terminal.Identifier i) {
            this.I = i;
        }

        @Override
        public void visit(Visitor v) {
            v.visitSimpleTypeDenoter(this);
        }
    }
}