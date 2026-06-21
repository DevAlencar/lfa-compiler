package ast;

public class TypeChecker implements Visitor {

    private IdentificationTable currentEnv = new IdentificationTable(null);

    // Atributos auxiliares para transportar metadados durante a varredura
    private String currentType = "";
    private String currentStringValue = "";

    // Tipos nativos da linguagem mapeados como strings
    private static final String INT_TYPE = "Integer";
    private static final String BOOL_TYPE = "Boolean";
    private static final String FLOAT_TYPE = "Float";
    private static final String ERROR_TYPE = "Unknown";

    public void check(Program p) {
        System.out.println("---> Iniciando análise semântica...");
        p.visit(this);
        System.out.println("---> Análise semântica concluída sem erros!");
    }

    @Override
    public void visitProgram(Program ast) {
        if (ast.name != null) {
            ast.name.visit(this);
        }
        if (ast.D != null) {
            ast.D.visit(this);
        }
        if (ast.C != null) {
            ast.C.visit(this);
        }
    }

    // --- Gerenciamento de Comandos ---

    @Override
    public void visitAssignCommand(Command.AssignCommand ast) {
        // Descobre o tipo da variável alvo
        ast.V.visit(this);
        String targetType = currentType;

        // Descobre o tipo da expressão sendo atribuída
        ast.E.visit(this);
        String exprType = currentType;

        if (!targetType.equals(exprType)) {
            throw new RuntimeException("Erro Semântico: Tipos incompatíveis na atribuição. Não é possível atribuir "
                    + exprType + " à variável do tipo " + targetType);
        }
    }

    @Override
    public void visitCallCommand(Command.CallCommand ast) {
        // Para uma chamada de função/procedimento genérica (ex: putint(x))
        ast.I.visit(this);
        ast.E.visit(this);
    }

    @Override
    public void visitSequentialCommand(Command.SequentialCommand ast) {
        if (ast.C1 != null) ast.C1.visit(this);
        if (ast.C2 != null) ast.C2.visit(this);
    }

    @Override
    public void visitIfCommand(Command.IfCommand ast) {
        ast.E.visit(this);
        if (!currentType.equals(BOOL_TYPE)) {
            throw new RuntimeException("Erro Semântico: A condição do 'if' deve resultar em um tipo Boolean, encontrado: " + currentType);
        }
        if (ast.C1 != null) ast.C1.visit(this);
        if (ast.C2 != null) ast.C2.visit(this);
    }

    @Override
    public void visitWhileCommand(Command.WhileCommand ast) {
        ast.E.visit(this);
        if (!currentType.equals(BOOL_TYPE)) {
            throw new RuntimeException("Erro Semântico: A condição do 'while' deve resultar em um tipo Boolean, encontrado: " + currentType);
        }
        if (ast.C != null) ast.C.visit(this);
    }

    @Override
    public void visitLetCommand(Command.LetCommand ast) {
        // Cria um novo escopo local encadeado ao atual
        IdentificationTable localEnv = new IdentificationTable(currentEnv);
        currentEnv = localEnv;

        if (ast.D != null) ast.D.visit(this);
        if (ast.C != null) ast.C.visit(this);

        // Ao sair do bloco let, destrói o escopo local restaurando o escopo pai
        currentEnv = currentEnv.retrieveParentEnvironment();
    }

    // Método auxiliar adicionado para contornar o encapsulamento se necessário,
    // ou você pode simplesmente fazer: currentEnv = currentEnv.parent (se colocar o atributo parent como público ou criar o getter na IdentificationTable)

    @Override
    public void visitEmptyCommand(Command.EmptyCommand ast) {
        // Nada a validar
    }

    // --- Gerenciamento de Expressões ---

    @Override
    public void visitVnameExpression(Expression.VnameExpression ast) {
        ast.V.visit(this); // Preenche currentType com o tipo da variável
    }

    @Override
    public void visitIntLiteralExpression(Expression.IntLiteralExpression ast) {
        ast.IL.visit(this);
        currentType = INT_TYPE; // Qualquer número literal é implicitamente um Integer
    }

    @Override
    public void visitBoolLiteralExpression(Expression.BoolLiteralExpression ast) {
        ast.BL.visit(this);
        currentType = BOOL_TYPE; // true ou false são implicitamente Boolean
    }

    @Override
    public void visitUnaryExpression(Expression.UnaryExpression ast) {
        ast.O.visit(this);
        String op = currentStringValue;

        ast.E.visit(this);
        String exprType = currentType;

        // Se sua linguagem tiver operadores unários lógicos ou aritméticos (ex: -5 ou !flag)
        if (op.equals("-") && !exprType.equals(INT_TYPE)) {
            throw new RuntimeException("Erro Semântico: Operador unário '-' exige operando do tipo Integer.");
        }
        currentType = exprType;
    }

    @Override
    public void visitBinaryExpression(Expression.BinaryExpression ast) {
        ast.E1.visit(this);
        String typeLeft = currentType;

        ast.O.visit(this);
        String op = currentStringValue;

        ast.E2.visit(this);
        String typeRight = currentType;

        // Regra para operadores aritméticos discretos
        if (op.equals("+") || op.equals("-") || op.equals("*") || op.equals("/")) {
            if (typeLeft.equals(INT_TYPE) && typeRight.equals(INT_TYPE)) {
                currentType = INT_TYPE;
            } else {
                throw new RuntimeException("Erro Semântico: Operador '" + op + "' exige operandos numéricos (Integer). Encontrado: " + typeLeft + " e " + typeRight);
            }
        }
        // Regra para operadores de comparação relacionais
        else if (op.equals(">") || op.equals("<") || op.equals("==") || op.equals("!=")) {
            if (typeLeft.equals(typeRight)) {
                currentType = BOOL_TYPE; // O resultado de x > y passa a ser um Boolean
            } else {
                throw new RuntimeException("Erro Semântico: Não é possível comparar tipos diferentes: " + typeLeft + " com " + typeRight);
            }
        } else {
            currentType = ERROR_TYPE;
        }
    }

    @Override
    public void visitEmptyExpression(Expression.EmptyExpression ast) {
        currentType = ERROR_TYPE;
    }

    // --- Gerenciamento de Declarações ---

    @Override
    public void visitSequentialDeclaration(Declaration.SequentialDeclaration ast) {
        if (ast.D1 != null) ast.D1.visit(this);
        if (ast.D2 != null) ast.D2.visit(this);
    }

    @Override
    public void visitConstDeclaration(Declaration.ConstDeclaration ast) {
        ast.I.visit(this);
        String constName = currentStringValue;

        ast.E.visit(this);
        String constType = currentType;

        currentEnv.enter(constName, constType);
    }

    @Override
    public void visitVarDeclaration(Declaration.VarDeclaration ast) {
        ast.I.visit(this);
        String varName = currentStringValue;

        ast.T.visit(this);
        String varType = currentType;

        currentEnv.enter(varName, varType);
    }

    @Override
    public void visitEmptyDeclaration(Declaration.EmptyDeclaration ast) {
        // Nada a validar
    }

    // --- Terminais e Tipos Nativos ---

    @Override
    public void visitSimpleVname(Vname.SimpleVname ast) {
        ast.I.visit(this);
        String varName = currentStringValue;
        // Recupera o tipo correto associado à variável na tabela
        currentType = currentEnv.retrieve(varName);
    }

    @Override
    public void visitSimpleTypeDenoter(TypeDenoter.SimpleTypeDenoter ast) {
        ast.I.visit(this);
        if (currentStringValue.equals("integer")) {
             currentType = INT_TYPE;
        } else if (currentStringValue.equals("boolean")) {
             currentType = BOOL_TYPE;
        } else {
             currentType = currentStringValue;
        }
    }

    @Override
    public void visitIdentifier(Terminal.Identifier ast) {
        currentStringValue = ast.spelling;
    }

    @Override
    public void visitIntegerLiteral(Terminal.IntegerLiteral ast) {
        currentStringValue = ast.spelling;
    }

    @Override
    public void visitBooleanLiteral(Terminal.BooleanLiteral ast) {
        currentStringValue = ast.spelling;
    }

    @Override
    public void visitOperator(Terminal.Operator ast) {
        currentStringValue = ast.spelling;
    }

    @Override
    public void visitFloatLiteralExpression(Expression.FloatLiteralExpression ast) {
        ast.FL.visit(this);
        currentType = FLOAT_TYPE;
    }

    @Override
    public void visitFloatLiteral(Terminal.FloatLiteral ast) {
        currentStringValue = ast.spelling;
    }
}