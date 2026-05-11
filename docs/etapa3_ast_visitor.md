# Documentação Técnica: Etapa 3 - Montagem da AST e Padrão Visitor

## 1. Introdução
Esta etapa do compilador Java foca na transformação do fluxo de tokens, validado pelo Analisador Sintático, em uma representação estruturada em memória: a **Árvore Sintática Abstrata (AST)**. Para manipular essa estrutura de forma limpa e extensível, foi implementado o padrão de projeto **Visitor**, que desvincula a estrutura de dados (os nós da árvore) das operações que podem ser realizadas sobre ela (impressão, análise de contexto, etc.).

## 2. Árvore Sintática Abstrata (AST)

### 2.1. Arquitetura e Organização
Seguindo as diretrizes de projeto, a AST foi implementada utilizando uma hierarquia de classes abstratas e concretas. Para manter o projeto organizado e evitar uma proliferação excessiva de arquivos `.java`, utilizamos o padrão de **Agrupamento por Arquivo Base**:

- **`AST.java`**: Classe base de todos os nós, definindo o contrato obrigatório `public abstract void visit(Visitor v)`.
- **`Program.java`**: Representa o nó raiz do programa.
- **`Command.java`**: Classe base para todos os comandos. Agrupa as subclasses estáticas: `AssignCommand`, `CallCommand`, `SequentialCommand`, `IfCommand`, `WhileCommand`, `LetCommand` e `EmptyCommand`.
- **`Expression.java`**: Classe base para expressões. Agrupa: `VnameExpression`, `IntLiteralExpression`, `UnaryExpression`, `BinaryExpression` e `EmptyExpression`.
- **`Declaration.java`**: Classe base para declarações. Agrupa: `SequentialDeclaration`, `ConstDeclaration`, `VarDeclaration` e `EmptyDeclaration`.
- **`Terminal.java`**: Representa as folhas da árvore (tokens literais), como `Identifier`, `IntegerLiteral` e `Operator`.
- **`Vname.java` e `TypeDenoter.java`**: Representam nomes de variáveis e especificadores de tipos.

### 2.2. Construção da Árvore (Parser)
O `Parser.java` foi adaptado para realizar a montagem da árvore em tempo de execução (análise recursiva Top-Down). As principais mudanças foram:
- **Tipos de Retorno**: Métodos que antes retornavam `void` agora retornam o tipo abstrato correspondente (ex: `private Command parseCommand()`).
- **Instanciação Recursiva**: Ao reconhecer uma regra gramatical, o Parser instancia o nó concreto passando os sub-nós (filhos) capturados pelas chamadas recursivas para o construtor do nó pai.
- **Captura de Terminais**: O Parser agora extrai o `spelling` (texto original) dos tokens no momento do `accept`, garantindo que nomes de variáveis e valores constantes sejam armazenados nos nós `Terminal`.

## 3. Padrão de Projeto Visitor

### 3.1. A Mecânica do Visitor
Para cumprir os requisitos rigorosos do padrão Visitor descritos nos slides:
1. **Interface `Visitor`**: Define métodos de visitação específicos para cada classe concreta da árvore (ex: `visitIfCommand(IfCommand ast)`).
2. **Double Dispatch**: Cada nó concreto implementa `visit(Visitor v)` executando `v.visitNomeDoNo(this)`. O uso do `this` garante que o Visitor saiba exatamente qual tipo de nó está processando sem necessidade de `instanceof` ou *casts*.

### 3.2. Implementações de Operações
Foram desenvolvidos dois tipos de operação utilizando este padrão:
- **`PrintVisitor`**: Realiza a travessia da árvore e imprime a estrutura hierárquica no console. Utiliza um controle de indentação para representar visualmente a profundidade dos nós (Pai -> Filho).
- **`GUIVisitor`**: Uma implementação avançada que converte a AST em um modelo de dados compatível com o componente `JTree` do Java Swing, plotando a árvore em uma janela gráfica expansível.

## 4. Conclusão
Com a implementação da AST e do Visitor, o compilador agora possui uma base sólida para as próximas etapas (Análise de Contexto e Geração de Código). A estrutura permite adicionar novas funcionalidades ao compilador apenas criando novos Visitors, sem nunca precisar alterar as classes da AST que já foram validadas.
