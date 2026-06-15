# Etapa 2: Análise Sintática

## Objetivo
A segunda etapa do compilador implementa o Analisador Sintático (Parser). O objetivo é verificar se a sequência de tokens gerada pelo Scanner respeita as regras gramaticais da linguagem e, simultaneamente, construir a **Árvore de Sintaxe Abstrata (AST)**.

## Arquivos Envolvidos
- `Parser.java`: Analisador sintático principal.
- `ast/*.java`: Classes que definem os nós da AST (Program, Command, Expression, Declaration, etc.).

## Implementação

### 1. Método Recursivo Descendente
A implementação utiliza a técnica de **Análise Recursiva Descendente Preditiva**. Para cada símbolo não-terminal da gramática, existe um método correspondente no `Parser.java` (ex: `parseCommand()`, `parseExpression()`).

A gramática foi adaptada para ser **LL(1)**, eliminando ambiguidades e recursões à esquerda, permitindo que o analisador decida qual caminho seguir observando apenas o token atual (`currentToken`).

### 2. Métodos de Suporte
- `accept(byte expectedKind)`: Valida se o token atual é do tipo esperado. Se for, avança para o próximo; caso contrário, lança um erro sintático.
- `acceptIt()`: Avança incondicionalmente para o próximo token fornecido pelo Scanner.

### 3. Estrutura Gramatical (EBNF)
O Parser segue as produções definidas na gramática, tais como:
- `Program ::= single-Command`
- `Command ::= single-Command ( ; single-Command )*`
- `Declaration ::= single-Declaration ( ; single-Declaration )*`
- `Expression ::= primary-Expression ( Operator primary-Expression )*`

### 4. Construção da AST
Diferente de um parser puramente reconhecedor, este Parser instancia objetos das classes do pacote `ast` à medida que as regras são validadas. Ao final do processamento do método `parseProgram()`, uma árvore completa representando a estrutura lógica do programa é retornada para as etapas subsequentes (Análise de Contexto e Geração de Código).

### 5. Tratamento de Erros Sintáticos
Quando o Parser encontra um token inesperado (falha no `accept()`), ele lança uma `RuntimeException` detalhando:
- O tipo de token esperado.
- O tipo de token efetivamente encontrado.
- A localização exata (linha e coluna) no código-fonte.

---
*Baseado no Capítulo 4 de "Programming Language Processors in Java" e implementado conforme os slides da disciplina.*