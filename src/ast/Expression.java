package ast;

public abstract class Expression extends AST {
    public static class VnameExpression extends Expression {
        public Vname V;

        public VnameExpression(Vname v) {
            this.V = v;
        }

        @Override
        public void visit(Visitor v) {
            v.visitVnameExpression(this);
        }
    }

    public static class IntLiteralExpression extends Expression {
        public Terminal.IntegerLiteral IL;

        public IntLiteralExpression(Terminal.IntegerLiteral il) {
            this.IL = il;
        }

        @Override
        public void visit(Visitor v) {
            v.visitIntLiteralExpression(this);
        }
    }

    public static class BoolLiteralExpression extends Expression {
        public Terminal.BooleanLiteral BL;

        public BoolLiteralExpression(Terminal.BooleanLiteral bl) {
            this.BL = bl;
        }

        @Override
        public void visit(Visitor v) {
            v.visitBoolLiteralExpression(this);
        }
    }

    public static class UnaryExpression extends Expression {
        public Terminal.Operator O;
        public Expression E;

        public UnaryExpression(Terminal.Operator o, Expression e) {
            this.O = o;
            this.E = e;
        }

        @Override
        public void visit(Visitor v) {
            v.visitUnaryExpression(this);
        }
    }

    public static class BinaryExpression extends Expression {
        public Expression E1, E2;
        public Terminal.Operator O;

        public BinaryExpression(Expression e1, Terminal.Operator o, Expression e2) {
            this.E1 = e1;
            this.O = o;
            this.E2 = e2;
        }

        @Override
        public void visit(Visitor v) {
            v.visitBinaryExpression(this);
        }
    }

    public static class EmptyExpression extends Expression {
        @Override
        public void visit(Visitor v) {
            v.visitEmptyExpression(this);
        }
    }

    public static class FloatLiteralExpression extends Expression {
        public Terminal.FloatLiteral FL;

        public FloatLiteralExpression(Terminal.FloatLiteral fl) {
            this.FL = fl;
        }

        @Override
        public void visit(Visitor v) {
            v.visitFloatLiteralExpression(this);
        }
    }
}