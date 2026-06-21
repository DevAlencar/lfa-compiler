package ast;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

public class GUIVisitor implements Visitor {
    private DefaultMutableTreeNode currentNode;
    private DefaultMutableTreeNode rootNode;

    public void show(Program p) {
        // Inicializa a construção da árvore Swing
        p.visit(this);

        // Cria a JTree
        JTree tree = new JTree(rootNode);
        
        // Expande todas as linhas
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }

        // Configura a janela
        JFrame frame = new JFrame("Visualizador da AST");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new JScrollPane(tree), BorderLayout.CENTER);
        frame.setSize(500, 700);
        frame.setLocationRelativeTo(null); // Centraliza na tela
        frame.setVisible(true);
    }

    private void addNode(String name, Runnable visitChildren) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(name);
        
        if (currentNode == null) {
            rootNode = node;
        } else {
            currentNode.add(node);
        }
        
        DefaultMutableTreeNode previousNode = currentNode;
        currentNode = node;
        
        // Visita os filhos, que serão adicionados ao nó atual
        if (visitChildren != null) {
            visitChildren.run();
        }
        
        // Retorna o ponteiro para o nó pai
        currentNode = previousNode;
    }

    @Override
    public void visitProgram(Program ast) {
        addNode("Program", () -> {
            if (ast.name != null) ast.name.visit(this);
            if (ast.D != null) ast.D.visit(this);
            if (ast.C != null) ast.C.visit(this);
        });
    }

    @Override
    public void visitAssignCommand(Command.AssignCommand ast) {
        addNode("AssignCommand", () -> {
            if (ast.V != null) ast.V.visit(this);
            if (ast.E != null) ast.E.visit(this);
        });
    }

    @Override
    public void visitCallCommand(Command.CallCommand ast) {
        addNode("CallCommand", () -> {
            if (ast.I != null) ast.I.visit(this);
            if (ast.E != null) ast.E.visit(this);
        });
    }

    @Override
    public void visitSequentialCommand(Command.SequentialCommand ast) {
        addNode("SequentialCommand", () -> {
            if (ast.C1 != null) ast.C1.visit(this);
            if (ast.C2 != null) ast.C2.visit(this);
        });
    }

    @Override
    public void visitIfCommand(Command.IfCommand ast) {
        addNode("IfCommand", () -> {
            if (ast.E != null) ast.E.visit(this);
            if (ast.C1 != null) ast.C1.visit(this);
            if (ast.C2 != null) ast.C2.visit(this);
        });
    }

    @Override
    public void visitWhileCommand(Command.WhileCommand ast) {
        addNode("WhileCommand", () -> {
            if (ast.E != null) ast.E.visit(this);
            if (ast.C != null) ast.C.visit(this);
        });
    }

    @Override
    public void visitLetCommand(Command.LetCommand ast) {
        addNode("LetCommand", () -> {
            if (ast.D != null) ast.D.visit(this);
            if (ast.C != null) ast.C.visit(this);
        });
    }

    @Override
    public void visitEmptyCommand(Command.EmptyCommand ast) {
        addNode("EmptyCommand", null);
    }

    @Override
    public void visitVnameExpression(Expression.VnameExpression ast) {
        addNode("VnameExpression", () -> {
            if (ast.V != null) ast.V.visit(this);
        });
    }

    @Override
    public void visitIntLiteralExpression(Expression.IntLiteralExpression ast) {
        addNode("IntLiteralExpression", () -> {
            if (ast.IL != null) ast.IL.visit(this);
        });
    }

    @Override
    public void visitBoolLiteralExpression(Expression.BoolLiteralExpression ast) {
        addNode("BoolLiteralExpression", () -> {
            if (ast.BL != null) ast.BL.visit(this);
        });
    }

    @Override
    public void visitUnaryExpression(Expression.UnaryExpression ast) {
        addNode("UnaryExpression", () -> {
            if (ast.O != null) ast.O.visit(this);
            if (ast.E != null) ast.E.visit(this);
        });
    }

    @Override
    public void visitBinaryExpression(Expression.BinaryExpression ast) {
        addNode("BinaryExpression", () -> {
            if (ast.E1 != null) ast.E1.visit(this);
            if (ast.O != null) ast.O.visit(this);
            if (ast.E2 != null) ast.E2.visit(this);
        });
    }

    @Override
    public void visitEmptyExpression(Expression.EmptyExpression ast) {
        addNode("EmptyExpression", null);
    }

    @Override
    public void visitSequentialDeclaration(Declaration.SequentialDeclaration ast) {
        addNode("SequentialDeclaration", () -> {
            if (ast.D1 != null) ast.D1.visit(this);
            if (ast.D2 != null) ast.D2.visit(this);
        });
    }

    @Override
    public void visitConstDeclaration(Declaration.ConstDeclaration ast) {
        addNode("ConstDeclaration", () -> {
            if (ast.I != null) ast.I.visit(this);
            if (ast.E != null) ast.E.visit(this);
        });
    }

    @Override
    public void visitVarDeclaration(Declaration.VarDeclaration ast) {
        addNode("VarDeclaration", () -> {
            if (ast.I != null) ast.I.visit(this);
            if (ast.T != null) ast.T.visit(this);
        });
    }

    @Override
    public void visitEmptyDeclaration(Declaration.EmptyDeclaration ast) {
        addNode("EmptyDeclaration", null);
    }

    @Override
    public void visitSimpleVname(Vname.SimpleVname ast) {
        addNode("SimpleVname", () -> {
            if (ast.I != null) ast.I.visit(this);
        });
    }

    @Override
    public void visitSimpleTypeDenoter(TypeDenoter.SimpleTypeDenoter ast) {
        addNode("SimpleTypeDenoter", () -> {
            if (ast.I != null) ast.I.visit(this);
        });
    }

    @Override
    public void visitIdentifier(Terminal.Identifier ast) {
        addNode("Identifier(" + ast.spelling + ")", null);
    }

    @Override
    public void visitIntegerLiteral(Terminal.IntegerLiteral ast) {
        addNode("IntegerLiteral(" + ast.spelling + ")", null);
    }

    @Override
    public void visitBooleanLiteral(Terminal.BooleanLiteral ast) {
        addNode("BooleanLiteral(" + ast.spelling + ")", null);
    }

    @Override
    public void visitOperator(Terminal.Operator ast) {
        addNode("Operator(" + ast.spelling + ")", null);
    }

    @Override
    public void visitFloatLiteralExpression(Expression.FloatLiteralExpression ast) {
        addNode("FloatLiteralExpression", () -> {
            if (ast.FL != null) ast.FL.visit(this);
        });
    }

    @Override
    public void visitFloatLiteral(Terminal.FloatLiteral ast) {
        addNode("FloatLiteral(" + ast.spelling + ")", null);
    }
}