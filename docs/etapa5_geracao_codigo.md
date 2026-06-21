# Etapa 5: Geração de Código

## Objetivo
A quinta etapa do compilador é responsável por traduzir a Árvore de Sintaxe Abstrata (AST) decorada em código de máquina de baixo nível. O alvo da geração é a **TAM (Triangle Abstract Machine)**, uma máquina baseada em pilha.

## Arquivos Envolvidos
- `ast/Encoder.java`: Implementação do Visitor responsável pela geração de código.
- `ast/AddressTable.java`: Tabela de símbolos especializada para gerenciar endereços e deslocamentos na pilha.

## Implementação

### 1. Padrão Visitor
A geração de código é implementada através do padrão de projeto **Visitor**. A classe `Encoder` percorre a AST e, para cada tipo de nó, emite as instruções correspondentes. Isso mantém a lógica de geração de código separada da definição da estrutura de dados da árvore.

### 2. Gerenciamento da Pilha e Endereçamento
Como a TAM é uma máquina de pilha, a gestão de memória foca em deslocamentos relativos ao **Frame Base (LB)** local:
- **Variáveis Locais e Globais**: Cada variável declarada no programa principal ou nos blocos locais recebe um endereço (offset) único dentro de seu escopo.
- **AddressTable**: Mantém o rastreamento de qual identificador corresponde a qual posição na pilha.

### 3. Instruções Geradas
As principais instruções da TAM utilizadas são:
- `LOAD (n) offset[LB]`: Carrega `n` palavras da memória para o topo da pilha.
- `LOADL valor`: Carrega um valor literal para o topo da pilha.
- `STORE (n) offset[LB]`: Armazena `n` palavras do topo da pilha no endereço especificado.
- `PUSH n`: Reserva espaço para `n` palavras na pilha.
- `POP (n) m`: Remove `m` elementos abaixo dos `n` elementos do topo.
- `CALL primitiva`: Chama operações aritméticas ou lógicas (ex: `Add`, `Sub`, `Mult`, `Lt`, `Eq`).
- `JUMP label` e `JUMPIF (n) label`: Controlam o fluxo de execução para condicionais e laços.

### 4. Estruturas de Controle
- **Condicionais (If)**: Utiliza `JUMPIF` para desviar para o bloco `else` caso a condição seja falsa (0), e `JUMP` para pular o bloco `else` após executar o `then`.
- **Laços (While)**: Utiliza um rótulo no início para reavaliação da condição e um `JUMPIF` para sair do laço, retornando ao início com um `JUMP` ao final do corpo.
- **Escopos Globais e Locais**: Ao processar a raiz do nó `Program`, ou entrar em um comando `let`, o `AddressTable` gerencia um novo nível de variáveis. Ao finalizar, o `Encoder` emite uma instrução `POP` para limpar essas variáveis da pilha, garantindo a integridade da memória antes do encerramento (no caso do programa) ou da volta ao escopo anterior.

### 5. Finalização
Todo programa gerado termina com a instrução `HALT`, indicando o fim da execução para o interpretador da TAM.

---
*Implementado como a etapa final do ciclo de compilação do lfa-compiler.*