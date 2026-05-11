package ast;

public abstract class Terminal extends AST {
    public String spelling;

    public Terminal(String spelling) {
        this.spelling = spelling;
    }

    public static class Identifier extends Terminal {
        public Identifier(String spelling) {
            super(spelling);
        }

        @Override
        public void visit(Visitor v) {
            v.visitIdentifier(this);
        }
    }

    public static class IntegerLiteral extends Terminal {
        public IntegerLiteral(String spelling) {
            super(spelling);
        }

        @Override
        public void visit(Visitor v) {
            v.visitIntegerLiteral(this);
        }
    }

    public static class Operator extends Terminal {
        public Operator(String spelling) {
            super(spelling);
        }

        @Override
        public void visit(Visitor v) {
            v.visitOperator(this);
        }
    }
}