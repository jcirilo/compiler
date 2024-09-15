package parser;
import utils.TokenType;
import utils.ScopeIDStack;
import static utils.TokenType.*;
import java.util.ArrayList;
import lexical.Token;

public class Parser {
    private ArrayList<Token> buffer;
    private int pos;
    private Token currentToken;
    private ScopeIDStack scopeIDs;
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
        this.scopeIDs = new ScopeIDStack();
    }

    // inicia a analize a partir de um buffer de tokens
    public void parse(ArrayList<Token> buffer) {
        this.buffer = buffer;
        if (this.buffer == null) {
            throw new RuntimeException("Syntatic Error: buffer of tokens is null");
        }
        advance(); // Avançar para o primeiro token
        programa();
        System.out.println("Syntatic compilation successfully");
        System.out.println(scopeIDs.toString());
    }

    // token atual = próximo token
    private void advance() {
        if (pos < buffer.size()) {
            currentToken = buffer.get(pos++);
            text = currentToken.getText();          // debug
            type = currentToken.getType().name();   // debug
        } else {
            currentToken = null; // final de input
        }
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
            "Syntatic Error: missing '" + expectedText + "' at line "
            + currentToken.getRow() + ", column "
            + currentToken.getCol() + ". Found '" + currentToken.getText() + "' instead.");
    }

    // erro - expressão mal formatada
    private void errorMalformedExpression() {
        throw new RuntimeException(
            "Syntatic Error: malformed expression at line "
            + currentToken.getRow() + ", column "
            + currentToken.getCol() + ". Unexpected token: '" 
            + currentToken.getText() + "'.");
    }


    private void tryToPush(String id) {
        if (scopeIDs.contains(id)) {
            throw new RuntimeException(
                "Semantic Error: at line "
                + currentToken.getRow() + ", column " 
                + currentToken.getCol()  + ". '"
                + id + "' is already declared");
        } else {
            scopeIDs.push(id);
            sIDs = scopeIDs.toString();
        }
    }

    private void cleanScope() {
        scopeIDs.cleanScope();
        sIDs = scopeIDs.toString();
    }

    private void programa() {
        if (isCurrentTokenText("program")) {
            // para o semântico ficar mais visível e quebrar os
            // padroões do código
            tryToPush("$");                     // NOVO ESCOPO PILHA SEMÂNTICA
            advance();
            match(TokenType.IDENTIFIER);
            match(";");
            declaracoes_variaveis();
            declaracoes_de_subprogramas();
            comando_composto();
            match(".");
            cleanScope();                       // LIMPAR ESCOPO PILHA SEMÂNTICA
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
            tryToPush(currentToken.getText());      // EMPILHAR NO ESCOPO PILHA SEMÂNTICA
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
                tryToPush(currentToken.getText());   // EMPILHAR NO ESCOPO PILHA SEMÂNTICA
                match(IDENTIFIER);
                lista_de_identificadores2();
            } else {
                errorExpected("identifier");
            }
        }
        // epsilon - não fazer nada
    }

    private void tipo() {
        if (isCurrentTokenText("integer")
            || isCurrentTokenText("real")
            || isCurrentTokenText("boolean")) {
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
        // epsilon - não fazer nada
    }

    private void declaracao_de_subprograma() {
        match("procedure");
        match(IDENTIFIER);
        tryToPush("$");
        argumentos();
        match(";");
        declaracoes_variaveis();
        declaracoes_de_subprogramas();
        comando_composto();
        cleanScope();
    }

    private void argumentos() {
        if (isCurrentTokenText("(")) {
            advance();
            lista_de_parametros();
            match(")");
        }
        // epsilon - não fazer nada
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
        // epsilon - não fazer nada
    }

    // Método para a produção COMANDO_COMPOSTO
    /*private void comando_composto() {
        match("begin");
        comandos_opcionais();
        match("end");
    }*/
    private void comando_composto() {
        if(isCurrentTokenText("begin")){
            match("begin");
            tryToPush("$");                         // NOVO ESCOPO PILHA SEMÂNTICA
            comandos_opcionais();

            if(isCurrentTokenText("end")){
                match("end");
                cleanScope();                       // LIMPAR ESCOPO PILHA SEMÂNTICA
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
        // epsilon - não fazer nada
    }

    private void lista_de_comandos() {
        comando();
        lista_de_comandos2();
    }

    private void lista_de_comandos2() {
        if (isCurrentTokenText(";")) {
            advance();
            // permite que ";" seja opicional ao final 
            // do ultimo comando  do escopo
            if (!isCurrentTokenText("end")) {
                comando();
                lista_de_comandos2();
            }
        }
        // epsilon - não fazer nada
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
            match(IDENTIFIER);
            comando_opt();
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
        } else if (isCurrentTokenText("(")) {
            advance();
            lista_de_expressoes();
            match(")");
        }
        // epsilon - não fazer nada  
    }

    private void parte_else() {
        if (isCurrentTokenText("else")) {
            advance();
            comando();
        }
        // epsilon - não fazer nada
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
        // epsilon - não fazer nada
    }

    private void expressao() {
        expressao_simples();
        expressao_opt();
    }

    private void expressao_opt() {
        if (isCurrentTokenType(REL_OPERATOR)) {
            advance();
            expressao_simples();
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
        expressao_simples2();
        
    }
  
    // Método para a produção EXPRESSAO_SIMPLES2
    /*private void expressao_simples2() {
        if (isCurrentTokenType(ADD_OPERATOR) || isCurrentTokenText("or")) {
            advance();
            termo();
            expressao_simples2();
        }
        // epsilon - não fazer nada
    }*/

    private void expressao_simples2(){
        while(isCurrentTokenType(ADD_OPERATOR) || isCurrentTokenText("or")){
            advance();
            termo();
        }
    }


        private void termo() {
        fator();
        termo2();
    }
    

    // Método para a produção TERMO2
    /*private void termo2() {
        if (isCurrentTokenType(MULT_OPERATOR) || isCurrentTokenText("and")) {
            advance();
            fator();
            termo2();
        }
        // epsilon - não fazer nada
    }*/

    private void termo2(){
        while(isCurrentTokenType(MULT_OPERATOR)|| isCurrentTokenText("and")){
            advance();
            fator();
        }
    }

    // Método para a produção FATOR
/*    private void fator() {
        if (isCurrentTokenType(IDENTIFIER)) {
            match(IDENTIFIER);
            fator_opt();
        } else if ( isCurrentTokenType(INT_NUMBER) || 
                    isCurrentTokenType(REAL_NUMBER) || 
                    isCurrentTokenText("true")) {
            advance();
        } else if (isCurrentTokenText("(")) {
            advance();
            expressao();
            match(")");
        } else if (isCurrentTokenText("not")) {
            advance();
            fator();
        } else {
            error();
        }
    }
*/

    private void fator(){
        if(isCurrentTokenType(IDENTIFIER)){
            match(IDENTIFIER);
            fator_opt();
        }else if(isCurrentTokenType(INT_NUMBER) ||
                 isCurrentTokenType(REAL_NUMBER) ||
                 isCurrentTokenText("true")){
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