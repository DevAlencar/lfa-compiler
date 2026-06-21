# Projeto de Compiladores - LFA Compiler

**Disciplina**: Linguagens Formais e Autômatos / Compiladores
**Professor**: Marcus Ramos

---

## 1. Linguagem-Fonte

### a. Descrição completa da linguagem-fonte

**i. Relação e estrutura de separadores**
Os separadores têm a função de delimitar os tokens. Na nossa linguagem, são considerados separadores:
- **Espaços em branco, tabulações e quebras de linha**: Ignorados pelo analisador léxico.
- **Comentários**: Iniciados pelo caractere `!` e se estendem até o final da linha. O conteúdo após o `!` é ignorado pelo Scanner.

**ii. Sintaxe livre de contexto**
A sintaxe livre de contexto está definida na gramática que foi adaptada para LL(1) no documento `gramatica-ll1.md`. Ela descreve a estrutura de blocos do programa (`program ID ; <corpo> .`), declarações (`var ID : <tipo>`), comandos de atribuição, iterativos (`while`) e condicionais (`if`).

**iii. Sintaxe dependente de contexto**
Refere-se às regras semânticas da linguagem:
- Uma variável não pode ser utilizada sem ter sido previamente declarada.
- Não é permitida a re-declaração de uma variável no mesmo escopo local, exigindo amarração consistente de contexto.
- A tipagem é estrita. Operadores matemáticos exigem operandos numéricos, laços e condicionais exigem operandos booleanos, e o tipo atribuído a uma variável deve corresponder ao seu tipo declarado.

**iv. Semântica**
A linguagem é imperativa e procedural, com execução sequencial. Apresenta suporte a blocos de código com possibilidade de escopos locais de declarações, e possui suporte básico aos tipos lógicos e primitivos nativos (`Integer`, `Boolean`, `Float`).

---

## 2. Descrição geral da arquitetura do compilador

O compilador segue uma arquitetura moderna orientada pela Árvore de Sintaxe Abstrata (AST) em passagens sucessivas:
1. **Análise Léxica (Scanner)**: Lê a stream de caracteres, separa e os classifica em Tokens lógicos.
2. **Análise Sintática (Parser)**: Através do método Top-Down, valida a sequência de tokens contra a gramática LL(1) e, concomitantemente, instancia a AST em memória.
3. **AST e Visitor**: A árvore abstrata construída suporta implementações do padrão de projeto *Visitor* (Double Dispatch) para as travessias subsequentes, separando a estrutura de dados da lógica de processamento.
4. **Análise de Contexto (TypeChecker)**: Visitor que percorre a AST identificando variáveis, gerenciando a tabela de símbolos e checando restrições de tipos.
5. **Geração de Código (Encoder)**: Visitor terminal que percorre a árvore validada emitindo instruções orientadas para a TAM (Triangle Abstract Machine).

---

## 3. Fundamentação teórica e técnicas empregadas na análise sintática

Foi empregada a técnica de **Análise Descendente Preditiva Recursiva**. Esta técnica requer a implementação de um método para cada não-terminal existente na gramática. As escolhas de regra são determinísticas, utilizando um *lookahead* de 1 token (símbolo de avanço).
Para viabilizar este modelo determinístico, a gramática sofreu adaptações técnicas para atender às rigorosas condições **LL(1)**: remoção completa de recursões à esquerda e emprego de fatoração à esquerda em produções que compartilhavam prefixos comuns (como nas expressões primárias).

---

## 4. Análise Léxica

**a. Relação de tokens**
- Identificadores (Iniciados por letras, com letras e números).
- Literais (`INT_LIT`, `FLOAT_LIT`, `BOOL_LIT` com valores `true`/`false`).
- Operadores (`+`, `-`, `*`, `/`, `<`, `>`, `=`, `and`, `or`).
- Símbolos Especiais e Palavras-Chave (`;`, `:`, `:=`, `.`, `(`, `)`, `program`, `var`, `integer`, `boolean`, `begin`, `end`, `if`, `then`, `else`, `while`, `do`).

**b. Gramática léxica**
Isolada a partir do parser:
- `ID → [a-z][a-z0-9]_`
- `INT_LIT → [0-9]+`
- `FLOAT_LIT → [0-9]+\.[0-9]* | \.[0-9]+`

**c. Manipulações na gramática original**
A extração da formação de caracteres em regras léxicas aliviou o Parser da responsabilidade léxica de formar palavras, impedindo recursões ou ambiguidades no âmbito dos caracteres isolados.

**d. Verificação da condição LL(1)**
Após a manipulação e o encapsulamento léxico, os terminais resultantes (Tokens) são plenamente LL(1).

**e. Técnicas utilizadas e funcionamento**
Uso de varredura sequencial com consumo guiado de caracteres via `takeIt()`. As quebras e ignorâncias de formatação foram abstraídas em `scanSeparator()`. A classificação baseia-se num dicionário e num enum de `Kind` e `Spelling`.

**f. Exemplos**
- *Bem Formada*: `var nome : integer ;`  -> `VAR`, `ID("nome")`, `COLON`, `INTEGER`, `SEMICOLON`
- *Mal Formada*: `var 1nome : integer ;` -> Gera erro léxico acusando dígito num prefixo não esperado.

---

## 5. Análise Sintática

**a. Gramática sintática**
Disponível em detalhes no arquivo auxiliar `docs/gramatica-ll1.md`.

**b. Manipulações e justificativas**
- Foram introduzidas sub-regras (como `<resto-expressão>`) para eliminar a **recursão à esquerda** das expressões aritméticas, sem perder o controle de precedência.
- O *left factoring* resolveu conflitos nas regras que derivavam expressões simples.

**c. Condição LL(1)**
O Parser é unicamente guiado pelo token atual para tomar as decisões das regras LL(1). A exceção e ajuste mais complexo é a resolução do *Dangling Else* onde, propositalmente, o Parser tem um comportamento *Shift* forçado para atrelar a cláusula `else` ao comando `if` mais profundo.

**d. Técnicas de funcionamento**
Implementação de funções `parseNonTerminal()`. Para checagem terminal, utiliza-se chamadas a `accept(Kind esperado)`, que avança se o token coincidir, ou reporta erro.

**e. Exemplos**
- *Cadeia aceita*: `begin x := 10 ; end`
- *Cadeia rejeitada*: `begin x := ; end` (Erro acusando ausência da expressão esperada após a atribuição).

---

## 6. Estruturas de dados principais

1. **Objetos AST**: Entidades imutáveis em termos de hierarquia (filhos são fixos via construtor). Subclasses agrupadas dentro de superclasses abstratas (`Command`, `Expression`, etc).
2. **IdentificationTable (Tabela de Símbolos)**: Implementada para rastrear variáveis no escopo durante a checagem semântica, gerenciando o aninhamento dos nós sem causar choques e controlando a vida útil de declarações locais.
3. **AddressTable**: Controla as resoluções finais de memória e offsets a nível de baixo nível para TAM.

---

## 7. Montagem e impressão da AST

**a. Estruturas de dados e algoritmos**
O preenchimento da AST é de baixo para cima nas chamadas, pois a raiz de uma produção solicita a avaliação dos sub-nós e os integra na inicialização da sua classe. A impressão foi isolada via **Visitor**, usando métodos de travessia do nó que chamam de volta as implementações da classe visitante (o *Double Dispatch*).
Há implementações como `PrintVisitor` (indentação de terminal) e `GUIVisitor` (uso do componente `JTree` nativo Java para visualização interfaceada).

**b. Exemplos**
Fonte: `while true do x := 1`
Árvore:
```
WhileCommand
  BoolLiteralExpression (true)
  AssignCommand
    Vname (x)
    IntLiteralExpression (1)
```

---

## 8. Análise de Contexto

**a. Fase de Identificação**
Implementado pelo `TypeChecker`. Quando a travessia atinge uma Declaração, insere o nome na `IdentificationTable`. Quando atinge um Vname (uso), consulta iterativamente até as bases do escopo atual; caso não encontre, reporta identificador não definido.

**b. Fase de Verificação de Tipos**
Nós de expressões avaliam e retornam seus tipos (por exemplo, os novos implementados `Boolean` e `Float`). Os `BinaryExpression` verificam a tipologia entre esquerda e direita. `AssignCommand` obriga compatibilidade estrita do operador de destino com o termo resolvido na fonte.

**c. Exemplos**
- *Aceita*: `var controle: boolean; controle := true;`
- *Rejeitada*: `var cont: integer; cont := true;` (Dispara rejeição de incompatibilidade).

---

## 9. Ambiente de Execução

**a. Tipo de máquina**
Máquina Abstrata baseada em Pilha (Triangle Abstract Machine - TAM).

**b. Avaliação de expressões**
O compilador transforma notação in-fix em notação post-fix na emissão TAM, utilizando os próprios encadeamentos para computar os dados da pilha em cascatas lógicas.

**c. Representação de dados**
Uso de registradores dedicados e armazenamento literal ou por deslocamentos.

**d. Alocação de memória**
Relacionada ao base frame (Registrador `LB` - Local Base) suportando offsets relativos em pilhas de dados gerenciados pela estrutura da linguagem imperativa.

**e. Parâmetros e valor de função**
Prepara espaços prévios (com `PUSH`), aciona as transferências e, após a computação pela rotina alvo, o montante da execução é fechado, mantendo apenas o offset final devolvido que substitui o escopo na limpeza dos dados não mais necessários (`POP (n) m`).

---

## 10. Linguagem-Objeto

**a. Relação de instruções**
Abrange: Cargas e Salvamentos (`LOAD`, `LOADL`, `STORE`), Reservas (`PUSH`, `POP`), Operações Aritméticas e Lógicas (Via rotinas `CALL`), e Controles de Fluxo (`JUMP`, `JUMPIF`). Terminal em `HALT`.

**b/c. Sintaxe e Semântica TAM**
A instrução TAM consiste em um Operador (o código mnemônico da operação), o tamanho da variação a ser considerada, e um endereço/literal a ser operado relativo ou estático.

---

## 11. Geração de Código

**a. Funções de código**
A classe `Encoder` traduz árvores completas validamente conectadas à abstração em strings de instrução de pilhas da linguagem-objeto.

**b. Descrição estrutural**
Para cada nó de comando na travessia (por exemplo, `IfCommand`), as rotinas `Encoder.visitIfCommand` registram *Labels* temporárias, avaliam a expressão condicional e emitem desvios `JUMPIF` e `JUMP` contornando as seções lógicas geradas, garantindo o paralelismo condicional de execuções.

**c. Exemplo de geração**
```pascal
if (x > 0) then x := 1
```
Tradução simplificada equivalente:
```
LOAD (1) x[LB]
LOADL 0
CALL Gt
JUMPIF (0) L_ELSE
LOADL 1
STORE (1) x[LB]
JUMP L_END
... L_ELSE
... L_END
```

---

## 12. Manual de Compilação

Para compilar o código fonte deste compilador Java:
1. Requisito de versão: **Java JDK (11 ou superior)**.
2. Através da linha de comando ou script automatizado no diretório raiz, compilar utilizando `javac`:
   ```bash
   javac -d bin src/**/*.java
   ```
   *(Caso seja um projeto estruturado com Maven ou Gradle, rode `mvn clean install` ou `./gradlew build` no raiz).*

---

## 13. Manual de Instalação

As dependências são mínimas uma vez que o executável roda dentro da JVM padrão. 
Não existe script de instalação; a cópia da pasta binária para o sistema destino e a adição de um Wrapper em Script (`.sh` ou `.bat`) com as variáveis `$JAVA_HOME` apontando para sua instalação JVM são o suficiente para tornar o uso transparente.

---

## 14. Manual de Utilização

**a. Requisitos Sistêmicos**
Plataforma com suporte a Java JRE. 

**b. Execução passo a passo**
Invocação via Terminal do Ponto de Entrada da Aplicação, com as devidas flags acionando interrupções no ciclo do compilador de acordo com a meta da análise.

Forma genérica de invocação (pode variar de acordo com CLI implementado):
```bash
# Executa apenas Análise Léxica
java -cp bin LFACompiler --lex meu_programa.lfa

# Executa até Análise Sintática
java -cp bin LFACompiler --syn meu_programa.lfa

# Imprime a Árvore (Visual/Texto) e finaliza
java -cp bin LFACompiler --ast meu_programa.lfa

# Verificações de Contextos e Tipos Finalizadas
java -cp bin LFACompiler --sem meu_programa.lfa

# Fluxo Completo (Salva arquivo código objeto TAM)
java -cp bin LFACompiler --gen meu_programa.lfa
```

**c. Localizações de arquivos-fonte**
Por padrão, arquivos devem possuir uma localização válida e absoluta. Extensões podem ser exigidas pela interface final do usuário (`.lfa`, `.txt`).

**d. Apresentação das Saídas**
Relatórios de logs e erros na tela (Terminal), além de visualizações em novas janelas quando requisitados via Swing GUI. O código final em modo `--gen` é gerado como um arquivo na mesma raiz.

**e. Mensagens de Erro**
São acompanhadas do número da linha e coluna de identificação para depuração simples pelo programador.
- **Erros Léxicos**: Caracteres desconhecidos geram abortamento da fila do Scanner.
- **Erros Sintáticos**: Quebras no LL(1) acusam exatamente a categoria `Kind` gramatical que falhou e a que o sistema esperava receber.
- **Erros Semânticos**: Mensagens diretas de Violação de Tipagem Incompatível ou de Variáveis não Encontradas em Escopo.

---

## 15. Conclusões Finais

**a. Avaliação do projeto**
O presente projeto representou um grande laboratório de desenvolvimento conceitual prático de Software de Base. A integração progressiva (desde um analisador puramente lógico textual, até uma abstração visual Visitor geradora de código-objeto) demonstrou os sólidos fundamentos da literatura técnica na disciplina de compiladores. 

**b. Dificuldades**
A adequação da gramática LL(1) inicial mostrou-se não ser apenas uma transcrição literal, mas sim um rigor matemático fundamental a ser exercitado, em especial na resolução de recursões. Outra grande complexidade encontrada ocorreu na expansão com a manipulação da Tabela de Símbolos, na garantia da coerência semântica e na implementação das hierarquias Visitor e conversões para os tipos em cascata na Árvore AST. 

**c. Críticas e sugestões**
Como sugestão, para um ambiente mais escalável, seria importante integrar mecanismos de automação de testes com JUnit nas diversas camadas separadamente. Isso tornaria os reajustes de AST e expansões sintáticas e semânticas das linguagens futuras muito mais confortáveis para o refactoring sem medo.
