Documentação de Adequação da Gramática para LL(1)

Este documento descreve as não-conformidades encontradas na gramática original do compilador em relação aos requisitos de
uma gramática LL(1) e apresenta a gramática corrigida e reestruturada.  
 ──────

## 1. Não-Conformidades Identificadas na Gramática Original

Para que uma gramática seja LL(1), ela não pode conter recursão à esquerda (pois causa loops infinitos no parser  
 descendente) e não pode conter prefixos comuns nas produções de um mesmo não-terminal (o que gera ambiguidade de escolha
com lookahead de 1).

Abaixo estão os problemas críticos detectados na gramática original:

### A. Recursão à Esquerda Direta

Várias regras importantes da gramática original iniciam-se recursivamente com elas mesmas:

1.  <expressão-simples> :  
    <expressão-simples> ::= <expressão-simples> <op-ad> <termo> | <termo>
2.  <termo> :  
    <termo> ::= <termo> <op-mul> <fator> | <fator>
3.  <declarações> :  
    <declarações> ::= <declaração> ; | <declarações> <declaração> ; | <vazio>
4.  <lista-de-comandos> :  
    <lista-de-comandos> ::= <comando> ; | <lista-de-comandos> <comando> ; | <vazio>
5.  <lista-de-ids> :  
    <lista-de-ids> ::= <id> | <lista-de-ids> , <id>  


### B. Necessidade de Fatoração à Esquerda (Prefixos Comuns)

A regra de <expressão> possui um prefixo idêntico em ambas as suas alternativas:

• <expressão> ::= <expressão-simples> | <expressão-simples> <op-rel> <expressão-simples>  
 Problema: Ao tentar expandir <expressão> , o parser lê o primeiro token de <expressão-simples> e não sabe se deve  
 aplicar a primeira produção (expressão simples) ou a segunda (expressão com operador relacional).

### C. Acoplamento Léxico no Sintático

As regras <id> , <int-lit> , <float-lit> , <bool-lit> , <letra> e <digito> descrevem a formação de caracteres.  
 Definir a estrutura interna de identificadores e literais diretamente no parser gera recursão à esquerda e torna a  
 gramática sintática excessivamente complexa.  
 Solução: Devem ser tratadas como tokens terminais produzidos pelo Analisador Léxico (Lexer).

### D. Inconsistência na Regra <tipo>

• <tipo> ::= | <tipo-simples>  
 A barra vertical inicial indica uma alternativa vazia implícita. No entanto, em linguagens Fortemente Tipadas (como  
 Pascal/C), variáveis precisam ter tipos definidos. Assumiremos a obrigatoriedade do tipo para evitar declarações vazias  
 ambíguas.  
 ──────

## 2. Soluções e Modificações Aplicadas

### I. Eliminação de Recursão à Esquerda

Aplicamos a fórmula padrão para remover recursão à esquerda do tipo A → Aα|β:

    A → βA'

    A' → αA'|varepsilon


• Expressões Simples:  
 • <expressão-simples> ::= <termo> <resto-expr-simples>  
 • <resto-expr-simples> ::= <op-ad> <termo> <resto-expr-simples> | <vazio>  
 • Termos:  
 • <termo> ::= <fator> <resto-termo>  
 • <resto-termo> ::= <op-mul> <fator> <resto-termo> | <vazio>  
 • Declarações e Listas (reestruturadas para recursão à direita):  
 • <declarações> ::= <declaração> ; <declarações> | <vazio>  
 • <lista-de-comandos> ::= <comando> ; <lista-de-comandos> | <vazio>  
 • <lista-de-ids> ::= ID <resto-lista-de-ids>  
 • <resto-lista-de-ids> ::= , ID <resto-lista-de-ids> | <vazio>

### II. Fatoração à Esquerda (Left Factoring)

A regra de <expressão> foi fatorada isolando o prefixo comum <expressão-simples> :

• <expressão> ::= <expressão-simples> <resto-expressão>  
 • <resto-expressão> ::= <op-rel> <expressão-simples> | <vazio>

### III. Abstração de Terminais pelo Lexer

Os seguintes símbolos passam a ser tratados como tokens terminais:

• ID (substituindo <id> )  
 • INT_LIT (substituindo <int-lit> )  
 • FLOAT_LIT (substituindo <float-lit> )  
 • BOOL_LIT (substituindo <bool-lit> )

As regras léxicas correspondentes no Lexer devem usar expressões regulares equivalentes:

• ID → [a-z][a-z0-9]_  
 • INT_LIT → [0-9]+  
 • FLOAT_LIT → [0-9]+\.[0-9]_ | \.[0-9]+  
 • BOOL_LIT → true | false  
 ──────

## 3. Nova Gramática Proposta (LL(1) Compatível)

Abaixo está a especificação completa da gramática ajustada. O símbolo <vazio> representa a produção vazia (varepsilon).

    <programa> ::=
        program ID ; <corpo> .

    <corpo> ::=
        <declarações> <comando-composto>

    <declarações> ::=
        <declaração> ; <declarações>
        | <vazio>

    <declaração> ::=
        <declaração-de-variável>

    <declaração-de-variável> ::=
        var ID : <tipo>

    <tipo> ::=
        <tipo-simples>

    <tipo-simples> ::=
        integer
        | boolean

    <comando-composto> ::=
        begin <lista-de-comandos> end

    <lista-de-comandos> ::=
        <comando> ; <lista-de-comandos>
        | <vazio>

    <comando> ::=
        <atribuição>
        | <condicional>
        | <iterativo>
        | <comando-composto>

    <atribuição> ::=
        <variável> := <expressão>

    <variável> ::=
        ID

    <condicional> ::=
        if <expressão> then <comando> <resto-condicional>

    <resto-condicional> ::=
        else <comando>
        | <vazio>

    <iterativo> ::=
        while <expressão> do <comando>

    <expressão> ::=
        <expressão-simples> <resto-expressão>

    <resto-expressão> ::=
        <op-rel> <expressão-simples>
        | <vazio>

    <expressão-simples> ::=
        <termo> <resto-expr-simples>

    <resto-expr-simples> ::=
        <op-ad> <termo> <resto-expr-simples>
        | <vazio>

    <termo> ::=
        <fator> <resto-termo>

    <resto-termo> ::=
        <op-mul> <fator> <resto-termo>
        | <vazio>

    <fator> ::=
        ID
        | <literal>
        | "(" <expressão> ")"

    <literal> ::=
        BOOL_LIT
        | INT_LIT
        | FLOAT_LIT

    <op-ad> ::=
        +
        | -
        | or

    <op-mul> ::=
        *
        | /
        | and

    <op-rel> ::=
        <
        | >
        | =
    ──────

## 4. O Caso Especial do "Dangling Else"

A regra <resto-condicional> introduz um conflito clássico de ambiguidade LL(1) conhecido como Dangling Else (Senão  
 Pendente).

• O conjunto de escolha para a alternativa else <comando> contém o token { else } .
• O conjunto Follow de <resto-condicional> também pode conter o token { else } (em condicionais aninhados).

Resolução Recomendada:
Este conflito é nativo da estrutura sintática de linguagens Pascal-like. Para resolvê-lo sem alterar a usabilidade da  
 linguagem, o gerador de parser (ou implementação manual) deve ser configurado para dar preferência ao
empilhamento/associação direta (Shift) ao ler o token else , vinculando-o ao if mais interno e recente.
