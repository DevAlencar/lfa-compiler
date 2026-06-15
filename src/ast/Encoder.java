package ast;

public class Encoder implements Visitor {

    private final StringBuilder code = new StringBuilder();
    private AddressTable currentEnv = new AddressTable(null);

    private int labelCounter = 0;
    private String currentSpelling = "";

    private String newLabel() {
        return "L" + (labelCounter++);
    }

    public String getGeneratedCode() {
        return code.toString();
    }

    @Override
    public void visitProgram(Program ast) {
        if (ast.C != null) ast.C.visit(this);
        code.append("HALT\n");
    }

    @Override
    public void visitAssignCommand(Command.AssignCommand ast) {
        // 1. Calcula o valor da expressão no topo da pilha
        ast.E.visit(this);

        // 2. Busca o deslocamento da variável na pilha
        ast.V.visit(this);
        int offset = currentEnv.retrieve(currentSpelling);

        // 3. Desempilha salvando no endereço relativo ao Frame Base local [LB]
        // O tamanho padrão de tipos primitivos (Integer, Boolean) na TAM é 1 palavra.
        code.append("STORE (1) ").append(offset).append("[LB]\n");
    }

    @Override
    public void visitBinaryExpression(Expression.BinaryExpression ast) {
        ast.E1.visit(this);
        ast.E2.visit(this);

        ast.O.visit(this);
        String op = currentSpelling;

        // Mapeamento oficial de operadores para chamadas primitivas da TAM
        switch (op) {
            case "+":  code.append("CALL Add\n"); break;
            case "-":  code.append("CALL Sub\n"); break;
            case "*":  code.append("CALL Mult\n"); break;
            case "/":  code.append("CALL Div\n"); break;
            case "<":  code.append("CALL Lt\n"); break;
            case ">":  code.append("CALL Gt\n"); break;
            case "==": code.append("CALL Eq\n"); break;
            case "!=": code.append("CALL Ne\n"); break;
        }
    }

    @Override
    public void visitIntLiteralExpression(Expression.IntLiteralExpression ast) {
        ast.IL.visit(this);
        code.append("LOADL ").append(currentSpelling).append("\n");
    }

    @Override
    public void visitBoolLiteralExpression(Expression.BoolLiteralExpression ast) {
        ast.BL.visit(this);
        // Na TAM, true costuma ser representado por 1 e false por 0
        String val = currentSpelling.equals("true") ? "1" : "0";
        code.append("LOADL ").append(val).append("\n");
    }

    @Override
    public void visitVnameExpression(Expression.VnameExpression ast) {
        ast.V.visit(this);
        int offset = currentEnv.retrieve(currentSpelling);
        // Carrega 1 palavra localizada no deslocamento a partir de LB para o topo da pilha
        code.append("LOAD (1) ").append(offset).append("[LB]\n");
    }

    @Override
    public void visitIfCommand(Command.IfCommand ast) {
        String labelElse = newLabel();
        String labelEnd = newLabel();

        // Avalia a expressão condicional
        ast.E.visit(this);

        // JUMPIF(0) desvia se o valor no topo da pilha for 0 (false)
        code.append("JUMPIF (0) ").append(labelElse).append("\n");

        // Bloco Then
        if (ast.C1 != null) ast.C1.visit(this);
        code.append("JUMP ").append(labelEnd).append("\n");

        // Bloco Else
        code.append(labelElse).append(":\n");
        if (ast.C2 != null) ast.C2.visit(this);

        code.append(labelEnd).append(":\n");
    }

    @Override
    public void visitWhileCommand(Command.WhileCommand ast) {
        String labelStart = newLabel();
        String labelEnd = newLabel();

        code.append(labelStart).append(":\n");

        // Avalia condição
        ast.E.visit(this);

        // Se for falso (0), quebra o laço pulando pro final
        code.append("JUMPIF (0) ").append(labelEnd).append("\n");

        // Corpo do loop
        if (ast.C != null) ast.C.visit(this);

        // Retorna para reavaliar a condição
        code.append("JUMP ").append(labelStart).append("\n");

        code.append(labelEnd).append(":\n");
    }

    @Override
    public void visitLetCommand(Command.LetCommand ast) {
        // Guarda o ambiente atual e inicia um ambiente local
        AddressTable oldEnv = currentEnv;
        currentEnv = new AddressTable(oldEnv);

        // Visita as declarações para mapear os offsets das novas variáveis na pilha
        if (ast.D != null) ast.D.visit(this);

        // Visita o comando interno que usará esse escopo
        if (ast.C != null) ast.C.visit(this);

        // Importante na TAM: Limpeza da pilha após fechar o escopo local!
        // POP (n) (m) remove 'm' elementos abaixo dos 'n' elementos do topo.
        // Como comandos não deixam retorno no topo da pilha, n = 0.
        int allocatedVariables = currentEnv.getLocalOffset();
        if (allocatedVariables > 0) {
            code.append("POP (0) ").append(allocatedVariables).append("\n");
        }

        // Restaura o ambiente anterior
        currentEnv = oldEnv;
    }

    @Override
    public void visitVarDeclaration(Declaration.VarDeclaration ast) {
        ast.I.visit(this);
        currentEnv.enter(currentSpelling);
        // Reservamos espaço na pilha para a variável incrementando o ST implicitamente.
        // Na TAM real, muitas vezes emite-se uma instrução PUSH para reservar espaço,
        // mas o próprio empilhamento dinâmico subsequente ou o controle do compilador gerencia isso.
    }

    @Override
    public void visitConstDeclaration(Declaration.ConstDeclaration ast) {
        ast.I.visit(this);
        String constName = currentSpelling;
        int offset = currentEnv.enter(constName);

        // Avalia o valor da constante e armazena na pilha imediatamente
        ast.E.visit(this);
        code.append("STORE (1) ").append(offset).append("[LB]\n");
    }

    @Override
    public void visitSequentialCommand(Command.SequentialCommand ast) {
        if (ast.C1 != null) ast.C1.visit(this);
        if (ast.C2 != null) ast.C2.visit(this);
    }

    @Override
    public void visitSequentialDeclaration(Declaration.SequentialDeclaration ast) {
        if (ast.D1 != null) ast.D1.visit(this);
        if (ast.D2 != null) ast.D2.visit(this);
    }

    @Override
    public void visitSimpleVname(Vname.SimpleVname ast) {
        ast.I.visit(this);
    }

    @Override
    public void visitIdentifier(Terminal.Identifier ast) { currentSpelling = ast.spelling; }
    @Override
    public void visitIntegerLiteral(Terminal.IntegerLiteral ast) { currentSpelling = ast.spelling; }
    @Override
    public void visitBooleanLiteral(Terminal.BooleanLiteral ast) { currentSpelling = ast.spelling; }
    @Override
    public void visitOperator(Terminal.Operator ast) { currentSpelling = ast.spelling; }

    // Ignorados na geração direta de código executável
    @Override public void visitSimpleTypeDenoter(TypeDenoter.SimpleTypeDenoter ast) {}
    @Override public void visitCallCommand(Command.CallCommand ast) {}
    @Override public void visitUnaryExpression(Expression.UnaryExpression ast) {}
    @Override public void visitEmptyCommand(Command.EmptyCommand ast) {}
    @Override public void visitEmptyExpression(Expression.EmptyExpression ast) {}
    @Override public void visitEmptyDeclaration(Declaration.EmptyDeclaration ast) {}
}