package ast;

public abstract class Command extends AST {
    public static class AssignCommand extends Command {
        public Vname V;
        public Expression E;

        public AssignCommand(Vname v, Expression e) {
            this.V = v;
            this.E = e;
        }

        @Override
        public void visit(Visitor v) {
            v.visitAssignCommand(this);
        }
    }

    public static class CallCommand extends Command {
        public Terminal.Identifier I;
        public Expression E;

        public CallCommand(Terminal.Identifier i, Expression e) {
            this.I = i;
            this.E = e;
        }

        @Override
        public void visit(Visitor v) {
            v.visitCallCommand(this);
        }
    }

    public static class SequentialCommand extends Command {
        public Command C1, C2;

        public SequentialCommand(Command c1, Command c2) {
            this.C1 = c1;
            this.C2 = c2;
        }

        @Override
        public void visit(Visitor v) {
            v.visitSequentialCommand(this);
        }
    }

    public static class IfCommand extends Command {
        public Expression E;
        public Command C1, C2;

        public IfCommand(Expression e, Command c1, Command c2) {
            this.E = e;
            this.C1 = c1;
            this.C2 = c2;
        }

        @Override
        public void visit(Visitor v) {
            v.visitIfCommand(this);
        }
    }

    public static class WhileCommand extends Command {
        public Expression E;
        public Command C;

        public WhileCommand(Expression e, Command c) {
            this.E = e;
            this.C = c;
        }

        @Override
        public void visit(Visitor v) {
            v.visitWhileCommand(this);
        }
    }

    public static class LetCommand extends Command {
        public Declaration D;
        public Command C;

        public LetCommand(Declaration d, Command c) {
            this.D = d;
            this.C = c;
        }

        @Override
        public void visit(Visitor v) {
            v.visitLetCommand(this);
        }
    }

    public static class EmptyCommand extends Command {
        @Override
        public void visit(Visitor v) {
            v.visitEmptyCommand(this);
        }
    }
}