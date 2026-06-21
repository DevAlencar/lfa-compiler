# Etapa 1: Análise Léxica

## Objetivo
A primeira etapa do compilador `lfa-compiler` consiste na implementação do Analisador Léxico (Scanner). Seu papel fundamental é ler a sequência de caracteres do código-fonte e agrupá-los em unidades lógicas denominadas **Tokens**.

## Arquivos Envolvidos
- `Scanner.java`: Implementação central do analisador léxico.
- `Token.java`: Classe que representa um token, armazenando seu tipo (`kind`), valor literal (`spelling`), linha e coluna.
- `enums/Kind.java`: Enumeração com todos os tipos de tokens aceitos pela linguagem.
- `enums/Spelling.java`: Enumeração com as palavras reservadas e símbolos para mapeamento rápido de lexemas.

## Implementação

### 1. Gramática de Tokens
O Scanner foi implementado para reconhecer os seguintes padrões:
- **Identificadores**: Iniciam com uma letra, seguidos por letras ou dígitos.
- **Literais Inteiros**: Sequências de um ou mais dígitos.
- **Literais de Ponto Flutuante (Float)**: Sequências de dígitos separadas por ponto (ex: `3.14`, `.5`).
- **Literais Booleanos**: `true` ou `false`.
- **Operadores**: `+`, `-`, `*`, `/`, `<`, `>`, `=`, `and`, `or`.
- **Símbolos e Palavras-Chave**: `;`, `:`, `:=`, `.`, `(`, `)`, além de palavras reservadas como `program`, `integer`, `boolean`, `let`, `in`, `var`, `const`, `begin`, `end`, `if`, `then`, `else`, `while`, `do`.

### 2. Estratégia de Leitura
A leitura é feita de forma sequencial utilizando um `BufferedReader`. O método principal `scan()` orquestra o processo:
1. Ignora separadores (comentários, espaços e quebras de linha).
2. Identifica o início de um novo token.
3. Consome os caracteres pertencentes ao token através do método `takeIt()`.
4. Retorna um objeto `Token` devidamente classificado.

### 3. Separadores e Comentários
Os separadores são tratados pelo método `scanSeparator()`:
- **Espaços, Tabulações e Newlines**: Espaços em branco (` `), tabs (`\t`), retorno de carro (`\r`) e quebras de linha (`\n`) são consumidos e ignorados, servindo apenas para delimitar tokens.
- **Comentários**: Iniciados pelo caractere `!`, estendem-se até o final da linha. O Scanner ignora todo o conteúdo gráfico após o `!` até encontrar uma quebra de linha.

### 4. Tratamento de Erros
Caso o Scanner encontre um caractere que não corresponda a nenhum padrão da linguagem, uma `RuntimeException` é lançada indicando a linha e a coluna do erro, interrompendo o processo de compilação com uma mensagem clara sobre o caractere inesperado.

---
*Baseado nos slides de Análise Léxica e implementado conforme as diretrizes da disciplina.*