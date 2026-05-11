package ast;

public abstract class Declaration extends AST {
    public static class SequentialDeclaration extends Declaration {
        public Declaration D1, D2;

        public SequentialDeclaration(Declaration d1, Declaration d2) {
            this.D1 = d1;
            this.D2 = d2;
        }

        @Override
        public void visit(Visitor v) {
            v.visitSequentialDeclaration(this);
        }
    }

    public static class ConstDeclaration extends Declaration {
        public Terminal.Identifier I;
        public Expression E;

        public ConstDeclaration(Terminal.Identifier i, Expression e) {
            this.I = i;
            this.E = e;
        }

        @Override
        public void visit(Visitor v) {
            v.visitConstDeclaration(this);
        }
    }

    public static class VarDeclaration extends Declaration {
        public Terminal.Identifier I;
        public TypeDenoter T;

        public VarDeclaration(Terminal.Identifier i, TypeDenoter t) {
            this.I = i;
            this.T = t;
        }

        @Override
        public void visit(Visitor v) {
            v.visitVarDeclaration(this);
        }
    }

    public static class EmptyDeclaration extends Declaration {
        @Override
        public void visit(Visitor v) {
            v.visitEmptyDeclaration(this);
        }
    }
}