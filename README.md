# Compiler

## Table of Content

1. [Lexical Analyzer](#1-lexical-analyzer)
   1. [Automatons](#11-automatons)
   2. `lexical.Token`
   3. `lexical.Scanner`
   4. `utils.TokenType`
2. Syntatic Analizer
3. Semantic Analyzer

## 1. Lexical Analyzer

### 1.1. Automatons

#### 1.1.1. Identifier Token Automata

![Identifier Token Automata](/static/identifier_tokens_automata.png)

#### 1.1.2. Math Operator Automata

![Math Operator Automata](/static/math_operator_automata.png)

### 1.1.3. Assignment Operator Automata

![Assignment Operator Automata](/static/assignment_automata.png)

### 1.1.4. Relational Operator Automata

![Relational Operator Automata](/static/relational_automata.png)

#### 1.1.6. Integer and Floating Numbers Automata

![Integer and Floating Numbers Automata](/static/integer_and_floating_numbers_automata.png)

#### 1.1.8. Single Line Comment Automata

![Single Line Comment Automata](/static/single_line_comment_automata.png)
