package parser;
import utils.TokenType;
import utils.ScopeStack;
import static utils.TokenType.*;
import java.util.ArrayList;
import lexical.Token;

public class Parser {
    private ArrayList<Token> buffer;
    private int pos;
    private Token currentToken;
    private ScopeStack scopeStack;
    private ArrayList<Token> PcT;
    private String text; // para debug
    private String type; // para debug 
    private String sIDs; // para debug
    public Parser() {
        this.pos = 0;
        this.buffer = null;
        this.currentToken = null;
        this.text = null;
        this.type = null;
        this.sIDs = null;
        this.scopeStack = new ScopeStack();
        this.PcT = new ArrayList<Token>();
    }

    // inicia a analize a partir de um buffer de tokens
    public void parse(ArrayList<Token> buffer) {
        this.buffer = buffer;
        if (this.buffer == null) {
            throw new RuntimeException("Syntatic Error: buffer of tokens is null");
        }
        advance(); // Avançar para o primeiro token
        programa();
    }

    // token atual = próximo token
    private void advance() {
        if (pos < buffer.size()) {
            currentToken = buffer.get(pos++);
            text = currentToken.getText();          // debug
            type = currentToken.getType().name();   // debug
        }// else {
        //     currentToken = null; // final de input
        // }
    }

    // auxiliar pra verificar o tipo do token
    private boolean isCurrentTokenType(TokenType t) {
        return currentToken != null && currentToken.getType() == t;
    }

    // auxiliar para verificar a string do token
    private boolean isCurrentTokenText(String s) {
        return currentToken != null && currentToken.getText().equals(s);
    }

    // auxiliar para correspondência de string do token
    // esse método verifica a string e avança ou alerta erro
    private void match(String expectedText) {
        if (isCurrentTokenText(expectedText)) {
            advance();
        } else {
            errorExpected(expectedText);
        }
    }

    // auxiliar para correspondência do tipo do token
    // esse método verifica o tipo e avança ou alerta erro
    private void match(TokenType expectedType) {
        if (isCurrentTokenType(expectedType)) {
            advance();
        } else {
            error();
        }
    }

    // erro - token inesperado
    private void error() {
        throw new RuntimeException(
            "Syntatic Error: unexpected token '" 
            + currentToken.getText()  
            + "' at line " 
            + currentToken.getRow() 
            + ", column " 
            + currentToken.getCol());
    }

    // erro - faltando algo
    private void errorExpected(String expectedText){
        throw new RuntimeException(
            "Syntatic Error: invalid token '" + currentToken.getText()
            + "' at line " + currentToken.getRow()
            + ", column " + currentToken.getCol()
            + ". Expected '"  + expectedText + "' instead.");
    }

    // erro - expressão mal formatada
    private void errorMalformedExpression() {
        throw new RuntimeException(
            "Syntatic Error: malformed expression at line "
            + currentToken.getRow() + ", column "
            + currentToken.getCol() + ". Unexpected token: '" 
            + currentToken.getText() + "'.");
    }

    private void errorUndeclared() {
        throw new RuntimeException(
            "Semantic Error: undeclared var/procedure '"
            + currentToken.getText() + "' at line "
            + currentToken.getRow() + ", column "
            + currentToken.getCol()
        );
    }

    private void errorAlreadyDeclared() {
        throw new RuntimeException(
            "Semantic Error: at line "
            + currentToken.getRow() + ", column " 
            + currentToken.getCol()  + ". '"
            + currentToken.getText() + "' is already declared");
    }

    private void errorType () {
        Token tkErr = PcT.get(PcT.size()-1);
        throw new RuntimeException (
            "Semantic Error: '"
            + tkErr.getText() +
            "' has a invalid type of "
            + tkErr.getType()
            + " at line " + tkErr.getRow()
            + ", col " + tkErr.getCol()
        );
    }

    private void tryToPush(String id, TokenType tt) {
        if (scopeStack.scopeContains(id)) {
            errorAlreadyDeclared();
        } else {
            Token tk = new Token(tt, id, currentToken.getRow(), currentToken.getCol());
            scopeStack.push(tk);
            sIDs = scopeStack.toString(); // debug
        }
    }

    private void cleanScope() {
        scopeStack.cleanScope();
        sIDs = scopeStack.toString(); // debug
    }

    private void assignTypeWhereIsNull(TokenType tt) {
        scopeStack.assignTypeWhereIsNull(tt);
        sIDs = scopeStack.toString();
    }

    private Token findTokenOnScopeStack(String id) {
        Token tk;
        for (int i = 1; i != scopeStack.size(); i++) {
            tk = scopeStack.getFromTop(i); 
            if (tk.getText().equals(id)) {
                return tk;
            }
        }
        return null;
    }

    private void programa() {
        if (isCurrentTokenText("program")) {
            tryToPush("$", MARK);   // NOVO ESCOPO PILHA SEMÂNTICA
            advance();
            tryToPush(currentToken.getText(), PROGRAM);
            match(TokenType.IDENTIFIER);
            match(";");
            declaracoes_variaveis();
            declaracoes_de_subprogramas();
            comando_composto();
            match(".");
            cleanScope();       // LIMPAR ESCOPO PILHA SEMÂNTICA
        } else {
            error();
        }
    }

    private void declaracoes_variaveis() {
        if (isCurrentTokenText("var")) {
            advance();
            lista_declaracoes_variaveis();
        }
        // epsilon - não fazer nada
    }

    private void lista_declaracoes_variaveis() {
        lista_de_identificadores();
        match(":");
        tipo();
        match(";");
        lista_declaracoes_variaveis2();
    }

    private void lista_declaracoes_variaveis2() {
        if (isCurrentTokenType(IDENTIFIER)) {
            lista_de_identificadores();
            match(":");
            tipo();
            match(";");
            lista_declaracoes_variaveis2();
        }
        // epsilon - não fazer nada
    }

    private void lista_de_identificadores() {
        if (isCurrentTokenType(IDENTIFIER)) {
            tryToPush(currentToken.getText(), null);      // EMPILHAR NO ESCOPO PILHA SEMÂNTICA COM TIPO NULL (MARKADO)
            match(IDENTIFIER);
            lista_de_identificadores2();
        } else {
            errorExpected("identifier");
        }
    }

    private void lista_de_identificadores2() {
        if (isCurrentTokenText(",")) {
            advance();
            if (isCurrentTokenType(IDENTIFIER)) {
                tryToPush(currentToken.getText(), null);   // EMPILHAR NO ESCOPO PILHA SEMÂNTICA COM TIPO NULL (MARCADO)
                match(IDENTIFIER);
                lista_de_identificadores2();
            } else {
                errorExpected("identifier");
            }
        }
        // epsilon
    }

    private void tipo() {
        if (isCurrentTokenText("integer")) {
            assignTypeWhereIsNull(INT_NUMBER);
            advance();
        } else if (isCurrentTokenText("real")) {
            assignTypeWhereIsNull(REAL_NUMBER);
            advance();
        } else if (isCurrentTokenText("boolean")) {
            assignTypeWhereIsNull(BOOLEAN);
            advance();
        } else {
            error();
        }
    }

    private void declaracoes_de_subprogramas() {
        declaracoes_de_subprogramas2();
    }

    private void declaracoes_de_subprogramas2() {
        if (isCurrentTokenText("procedure")) {
            declaracao_de_subprograma();
            match(";");
            declaracoes_de_subprogramas2();
        }
        // epsilon
    }

    private void declaracao_de_subprograma() {
        match("procedure");
        if (isCurrentTokenType(IDENTIFIER)) {
            tryToPush(currentToken.getText(), PROCEDURE);
            match(IDENTIFIER);
            tryToPush("$", MARK);
            argumentos();
            match(";");
            declaracoes_variaveis();
            declaracoes_de_subprogramas();
            comando_composto();
            cleanScope();
        }
    }

    private void argumentos() {
        if (isCurrentTokenText("(")) {
            advance();
            lista_de_parametros();
            match(")");
        }
        // epsilon
    }

    private void lista_de_parametros() {
        lista_de_identificadores();
        match(":");
        tipo();
        lista_de_parametros2();
    }

    private void lista_de_parametros2() {
        if (isCurrentTokenText(";")) {
            advance();
            lista_de_identificadores();
            match(":");
            tipo();
            lista_de_parametros2();
        }
        // epsilon
    }

    // Método para a produção COMANDO_COMPOSTO
    /*private void comando_composto() {
        match("begin");
        comandos_opcionais();
        match("end");
    }*/
    private void comando_composto() {
        if(isCurrentTokenText("begin")){
            tryToPush("$", MARK);   // NOVO ESCOPO PILHA SEMÂNTICA
            match("begin");
            comandos_opcionais();

            if(isCurrentTokenText("end")){
                cleanScope();       // LIMPAR ESCOPO PILHA SEMÂNTICA
                match("end");
            }else{
                errorExpected("end");
            }
        } else{
            errorExpected("begin");
        }
    }   

    private void comandos_opcionais() {
        if (isCurrentTokenType(IDENTIFIER) || 
            isCurrentTokenText("begin") || 
            isCurrentTokenText("if") || 
            isCurrentTokenText("while")) {
                lista_de_comandos();
        }
        // epsilon
    }

    private void lista_de_comandos() {
        comando();
        lista_de_comandos2();
    }

    private void lista_de_comandos2() {
        if (isCurrentTokenText(";")) {
            advance();
            // permite que ";" seja opicional ao final 
            // do último comando  do escopo
            if (!isCurrentTokenText("end")) {
                comando();
                lista_de_comandos2();
            }
        }
        // epsilon
    }

    // Método para a produção COMANDO
    // ESTÁ FUNCIONANDO NÃO MEXA!!!!!!!!!
    
    /*private void comando() {
        if (isCurrentTokenType(IDENTIFIER)) {
            match(IDENTIFIER);
            comando_opt();
        } else if (isCurrentTokenText("begin")) {
            comando_composto();
        } else if (isCurrentTokenText("if")) {
            advance();
            expressao();
            match("then");
            comando();
            parte_else();
        } else if (isCurrentTokenText("while")) {
            advance();
            expressao();
            match("do");
            comando();
        } else {
            error();
        }
    }*/


    // MEXI PERDÃO, MAS O TEU TÁ COMENTADO EM CIMA!!
    private void comando() {
        if (isCurrentTokenType(IDENTIFIER)) {
            // Variável e Ativacao_de_procedimento
            if (scopeStack.contains(currentToken.getText())) {
                // adiciona uma cópia de uma variavel ja declarada na pilha TcP
                // com a linha e a coluna onde ela esta sendo usada
                Token tkAux = findTokenOnScopeStack(currentToken.getText());
                PcT.add(new Token(
                    tkAux.getType(),
                    tkAux.getText(),
                    currentToken.getRow(),
                    currentToken.getCol()
                ));
                match(IDENTIFIER);
                comando_opt();
            } else {
                errorUndeclared();
            }
        } else if (isCurrentTokenText("begin")) {
            comando_composto();
        } else if (isCurrentTokenText("if")) {
            advance();
            expressao();
            if (isCurrentTokenText("then")) {
                match("then");
                comando();
                parte_else();
            } else {
                errorExpected("then");  // Se "then" estiver ausente após "if"
            }
        } else if (isCurrentTokenText("while")) {
            advance();
            expressao();
            if (isCurrentTokenText("do")) {
                match("do");
                comando();
            } else {
                errorExpected("do");  // se "do" estiver ausente após "while"
            }
        } else {
            error();  // caso o token atual não corresponda a nenhum caso esperado
        }
    }   

    private void comando_opt() {
        if (isCurrentTokenText(":=")) {
            advance();
            expressao();

            // VERIFICA SE OS VALORES À ESQUERDA DE UMA ATRIBUIÇÃO
            // POSSUEM TIPOS VÁLIDOS
            //
            // TABELA DA ESPECIFICAÇÃO 2.C
            //
            if (checkTypes(INT_NUMBER, INT_NUMBER)) {
                atualizePcT(INT_NUMBER);
            } else if (checkTypes(REAL_NUMBER, REAL_NUMBER)) {
                atualizePcT(REAL_NUMBER);
            } else if (checkTypes(INT_NUMBER, REAL_NUMBER)) {
                atualizePcT(REAL_NUMBER);
            } else if (checkTypes(BOOLEAN, BOOLEAN)){
                atualizePcT(BOOLEAN);
            } else {
                errorType();
            }
        } else if (isCurrentTokenText("(")) {
            advance();
            lista_de_expressoes();
            match(")");
        }
        // epsilon  
    }

    private void parte_else() {
        if (isCurrentTokenText("else")) {
            advance();
            comando();
        }
        // epsilon
    }

    private void lista_de_expressoes() {
        expressao();
        lista_de_expressoes2();
    }

    private void lista_de_expressoes2() {
        if (isCurrentTokenText(",")) {
            advance();
            expressao();
            lista_de_expressoes2();
        }
        // epsilon
    }

    private void expressao() {
        expressao_simples();
        expressao_opt();
    }

    private void expressao_opt() {
        if (isCurrentTokenType(REL_OPERATOR)) {
            advance();
            expressao_simples();
            if (checkTypes(BOOLEAN, BOOLEAN)
            ||checkTypes(REAL_NUMBER, REAL_NUMBER)
            ||checkTypes(INT_NUMBER, INT_NUMBER)
            ||checkTypes(REAL_NUMBER, INT_NUMBER)
            ||checkTypes(INT_NUMBER, REAL_NUMBER)) {
                atualizePcT(BOOLEAN);
            } else {
                errorType();
            }
        }
        // epsilon - não fazer nada
    }

    /*private void expressao_simples() {
        if (isCurrentTokenText("+") || isCurrentTokenText("-")) {
            sinal();
        }
        termo();
        expressao_simples2();
    }*/

    private void expressao_simples(){
        if(isCurrentTokenText("+") || isCurrentTokenText("-")) {
            sinal();
        } 

        if(!isCurrentTokenType(IDENTIFIER) && 
        !isCurrentTokenType(INT_NUMBER) && 
        !isCurrentTokenType(REAL_NUMBER) && 
        !isCurrentTokenText("(") && 
        !isCurrentTokenText("true") && 
        !isCurrentTokenText("not")) {
            errorMalformedExpression();
        }

        termo();

        if (isCurrentTokenType(IDENTIFIER)) {
            // adiciona uma cópia de uma variavel ja declarada na pilha TcP
            // com a linha e a coluna onde ela esta sendo usada
            Token tkAux = findTokenOnScopeStack(currentToken.getText());
            PcT.add(new Token(
                tkAux.getType(),
                tkAux.getText(),
                currentToken.getRow(),
                currentToken.getCol()
            ));
        } else if (isCurrentTokenType(INT_NUMBER)
               || isCurrentTokenType(REAL_NUMBER)
               || isCurrentTokenType(BOOLEAN)) {
            // empilha uma cópia do token em PcT 
            PcT.add(new Token(
                currentToken.getType(),
                currentToken.getText(),
                currentToken.getRow(),
                currentToken.getCol()
            ));
        }

        expressao_simples2();
        
    }

    private void atualizePcT(TokenType tipoResultante) {
        PcT.remove(PcT.size()-1); // pop
        Token subtopo = PcT.remove(PcT.size()-1); // pop

        // push tipo resultante
        PcT.add(new Token(
            tipoResultante, 
            subtopo.getText(), 
            subtopo.getRow(), 
            subtopo.getCol()));
    }

    // Auxiliar pra verificar os tipos do topo e subtopo da pilha PcT
    private boolean checkTypes(TokenType t1, TokenType t2) {
        if (!PcT.isEmpty() && PcT.size() > 1) {
            Token top = PcT.get(PcT.size()-1);
            Token subtop = PcT.get(PcT.size()-2);
            return (top.getType() == t1 && subtop.getType() == t2);
        }
        return false;
    }

    // Método para a produção EXPRESSAO_SIMPLES2
    private void expressao_simples2() {
        if (isCurrentTokenType(ADD_OPERATOR) ||isCurrentTokenText("or")) {
            advance();
            termo();
            
            if (checkTypes(INT_NUMBER, INT_NUMBER)) {
                atualizePcT(INT_NUMBER);
            } else if (checkTypes(REAL_NUMBER, REAL_NUMBER)) {
                atualizePcT(REAL_NUMBER);
            } else if (checkTypes(INT_NUMBER, REAL_NUMBER)) {
                atualizePcT(REAL_NUMBER);
            } else if (checkTypes(REAL_NUMBER, INT_NUMBER)) {
                atualizePcT(REAL_NUMBER);
            } else if (checkTypes(BOOLEAN, BOOLEAN)){
                atualizePcT(BOOLEAN);
            } else {
                errorType();
            }
            
            expressao_simples2();
        }
        // epsilon - não fazer nada
    }

    private void termo() {
        fator();
        termo2();
    }

    // Método para a produção TERMO2
    private void termo2() {
        if (isCurrentTokenType(MULT_OPERATOR) || isCurrentTokenText("and")) {
            advance();
            fator();

            if (checkTypes(INT_NUMBER, INT_NUMBER)) {
                atualizePcT(INT_NUMBER);
            } else if (checkTypes(REAL_NUMBER, REAL_NUMBER)) {
                atualizePcT(REAL_NUMBER);
            } else if (checkTypes(INT_NUMBER, REAL_NUMBER)) {
                atualizePcT(REAL_NUMBER);
            } else if (checkTypes(REAL_NUMBER, INT_NUMBER)) {
                atualizePcT(REAL_NUMBER);
            }  else if (checkTypes(BOOLEAN, BOOLEAN)){
                atualizePcT(BOOLEAN);
            } else {
                errorType();
            }

            termo2();
        }
        // epsilon
    }

    private void fator(){
        if(isCurrentTokenType(IDENTIFIER)){
            // adiciona uma cópia de uma variavel ja declarada na pilha TcP
            // com a linha e a coluna onde ela esta sendo usada

            Token tkAux = findTokenOnScopeStack(currentToken.getText());
            PcT.add(new Token(
                tkAux.getType(), 
                tkAux.getText(),
                currentToken.getRow(),
                currentToken.getCol()
                ));

            match(IDENTIFIER);
            fator_opt();
        }else if(isCurrentTokenType(INT_NUMBER) ||
                 isCurrentTokenType(REAL_NUMBER) ||
                 isCurrentTokenText("true")){
                    // empilha a cópia do token atual no PcT
                    PcT.add(
                        new Token(currentToken.getType(), 
                        currentToken.getText(), 
                        currentToken.getRow(), 
                        currentToken.getCol()));
                    advance();
        } else if(isCurrentTokenText("(")){
            advance();
            expressao();
            //match(")");
            if(!isCurrentTokenText(")")){
                errorExpected(")");
            }else{
                advance();
            }
        }else if(isCurrentTokenText("not")){
            advance();
            fator();
        }else {
            errorMalformedExpression(); 
        } 
        
    }

    private void fator_opt() {
        if (isCurrentTokenText("(")) {
            advance();
            lista_de_expressoes();
            match(")");
        }
        // epsilon - não fazer nada
    }

    private void sinal() {
        if (isCurrentTokenText("+") || isCurrentTokenText("-")) {
            advance();
        } else {
            //error();
            errorExpected("sign ('+' or '-')");
        }
    }
}