# Documentação Técnica: Etapa 4 - Análise de Contexto, Tipos Booleanos e Floats

## 1. Introdução
Esta etapa do compilador foca na implementação da **Análise de Contexto (Semântica)** e na expansão da linguagem para suportar nativamente os tipos **Boolean** e **Float**. O objetivo principal é garantir que um programa sintaticamente correto também faça sentido semanticamente, verificando a declaração de variáveis e a compatibilidade de tipos nas operações e atribuições.

## 2. Suporte a Tipos Booleanos e Floats

Para que o compilador pudesse reconhecer e trabalhar com literais booleanos (`true` e `false`) e de ponto flutuante, foram necessárias alterações em diversas camadas:

### 2.1. Análise Léxica (Scanner e Enums)
- **`Kind.java` e `Spelling.java`**: Adicionados os mapeamentos para os tokens `BOOLLITERAL` e `FLOATLITERAL`, além de garantir a representação literal das palavras reservadas e estruturas.
- **`Scanner.java`**: Modificado o método de reconhecimento de tokens (`scanToken`). Identificação correta de ponto flutuante considerando os dígitos decimais.

### 2.2. Árvore Sintática Abstrata (AST)
- **`Terminal.java`**: Criadas as classes `BooleanLiteral` e `FloatLiteral` para representar os nós folha que contêm o valor puro.
- **`Expression.java`**: Adicionadas as classes `BoolLiteralExpression` e `FloatLiteralExpression`.

### 2.3. Análise Sintática (Parser)
- **`Parser.java`**: Atualizado o método de análise de expressões primárias (`parsePrimaryExpression`). Ao encontrar o token `BOOLLITERAL`, o Parser agora instancia corretamente os terminais e as expressões booleanas, populando a AST sem erros sintáticos.

## 3. Análise de Contexto (TypeChecker)

A análise de contexto foi implementada através da classe **`TypeChecker`**, que estende a interface `Visitor`. Esta classe realiza a travessia na AST para validar regras semânticas, mantendo um ambiente de tipos (Tabela de Símbolos).

### 3.1. Gerenciamento de Escopo e Tipos
- **`IdentificationTable`**: Utilizada para armazenar as variáveis declaradas e seus respectivos tipos. O escopo é gerenciado dinamicamente: comandos como `let` criam sub-escopos locais (encadeados ao escopo pai), que são destruídos ao final do bloco.
- **Resolução de Tipos Nativos**: A análise baseia-se em tipos mapeados internamente, com ênfase inicial nos tipos base, como `Integer`, `Boolean` e `Float`.

### 3.2. Regras de Validação Semântica
Durante a visita aos nós, o `TypeChecker` aplica validações rigorosas:
- **Atribuições (`AssignCommand`)**: Verifica se o tipo da variável de destino corresponde ao tipo da expressão atribuída. Tentativas de atribuir um booleano a um inteiro (ou vice-versa) disparam erros semânticos.
- **Condicionais e Laços (`IfCommand`, `WhileCommand`)**: Garante que a expressão de teste/condição seja obrigatoriamente do tipo `Boolean`.
- **Expressões Binárias (`BinaryExpression`)**:
  - Operadores aritméticos (`+`, `-`, `*`, `/`) exigem operandos estritamente do tipo `Integer` e resultam em um `Integer`.
  - Operadores relacionais (`>`, `<`, `==`, `!=`) exigem que ambos os operandos sejam do mesmo tipo e resultam obrigatoriamente em um `Boolean`.

### 3.3. Adaptação dos Visitors Auxiliares
As mudanças na AST exigiram que a interface `Visitor` e suas implementações auxiliares (`PrintVisitor`, `GUIVisitor`) fossem atualizadas para reconhecer e processar corretamente os novos nós `BoolLiteralExpression`, `FloatLiteralExpression`, e terminais nativos.

## 4. Conclusão
Com a integração da análise semântica e a incorporação plena de tipos booleanos, o compilador adquire a capacidade de detectar códigos inconsistentes ou com tipagem conflitante antes da geração de código. Esse alicerce aumenta a robustez da ferramenta e aproxima o projeto das capacidades de linguagens fortemente tipadas comerciais.