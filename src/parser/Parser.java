package parser;
import utils.TokenType;
import static utils.TokenType.*;
import java.util.ArrayList;
import lexical.Token;

public class Parser {
    ArrayList<Token> buffer;
    private int pos;
    private Token currentToken;
    private String text; // debug
    private String type; // debug

    public Parser() {
        this.pos = 0;
        this.buffer = null;
        this.currentToken = null;
        this.text = null;
        this.type = null;
    }

    public void parse(ArrayList<Token> buffer) {
        this.buffer = buffer;
        if (this.buffer == null) {
            throw new RuntimeException("Syntatic Error: null token buffer");
        }
        advance(); // Avançar para o primeiro token
        programa();
        System.out.println("Syntatic compilation successfully");
    }

    private void advance() {
        if (pos < buffer.size()) {
            currentToken = buffer.get(pos++);
            text = currentToken.getText();
            type = currentToken.getType().name();
        } else {
            currentToken = null; // final de input
        }
    }

    private boolean isCurrentTokenType(TokenType t) {
        return currentToken != null && currentToken.getType() == t;
    }

    private boolean isCurrentTokenText(String s) {
        return currentToken != null && currentToken.getText().equals(s);
    }

    // Método para correspondência de texto
    private void match(String expectedText) {
        if (isCurrentTokenText(expectedText)) {
            advance();
        } else {
            error();
        }
    }

    // Método para correspondência de tipo de token
    private void match(TokenType expectedType) {
        if (isCurrentTokenType(expectedType)) {
            advance();
        } else {
            error();
        }
    }

    private void error() {
        throw new RuntimeException(
            "Syntatic Error: unexpected token '" 
            + currentToken.getText()  
            + "' at line " 
            + currentToken.getRow() 
            + ", column " 
            + currentToken.getCol());
    }

    // Método para a produção PROGRAMA
    private void programa() {
        if (isCurrentTokenText("program")) {
            advance();
            match(TokenType.IDENTIFIER);
            match(";");
            declaracoes_variaveis();
            declaracoes_de_subprogramas();
            comando_composto();
            match(".");
        } else {
            error();
        }
    }

    // Método para a produção DECLARACOES_VARIAVEIS
    private void declaracoes_variaveis() {
        if (isCurrentTokenText("var")) {
            advance();
            lista_declaracoes_variaveis();
        }
        // epsilon - não fazer nada
    }

    // Método para a produção LISTA_DECLARACOES_VARIAVEIS
    private void lista_declaracoes_variaveis() {
        lista_de_identificadores();
        match(":");
        tipo();
        match(";");
        lista_declaracoes_variaveis2();
    }

    // Método para a produção LISTA_DECLARACOES_VARIAVEIS2
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

    // Método para a produção LISTA_DE_IDENTIFICADORES
    private void lista_de_identificadores() {
        match(IDENTIFIER);
        lista_de_identificadores2();
    }

    // Método para a produção LISTA_DE_IDENTIFICADORES2
    private void lista_de_identificadores2() {
        if (isCurrentTokenText(",")) {
            advance();
            match(IDENTIFIER);
            lista_de_identificadores2();
        }
        // epsilon - não fazer nada
    }

    // Método para a produção TIPO
    private void tipo() {
        if (isCurrentTokenText("integer")
            || isCurrentTokenText("real")
            || isCurrentTokenText("boolean")) {
            advance();
        } else {
            error();
        }
    }

    // Método para a produção DECLARACOES_DE_SUBPROGRAMAS
    private void declaracoes_de_subprogramas() {
        declaracoes_de_subprogramas2();
    }

    // Método para a produção DECLARACOES_DE_SUBPROGRAMAS2
    private void declaracoes_de_subprogramas2() {
        if (isCurrentTokenText("procedure")) {
            declaracao_de_subprograma();
            match(";");
            declaracoes_de_subprogramas2();
        }
        // epsilon - não fazer nada
    }

    // Método para a produção DECLARACAO_DE_SUBPROGRAMA
    private void declaracao_de_subprograma() {
        match("procedure");
        match(IDENTIFIER);
        argumentos();
        match(";");
        declaracoes_variaveis();
        declaracoes_de_subprogramas();
        comando_composto();
    }

    // Método para a produção ARGUMENTOS
    private void argumentos() {
        if (isCurrentTokenText("(")) {
            advance();
            lista_de_parametros();
            match(")");
        }
        // epsilon - não fazer nada
    }

    // Método para a produção LISTA_DE_PARAMETROS
    private void lista_de_parametros() {
        lista_de_identificadores();
        match(":");
        tipo();
        lista_de_parametros2();
    }

    // Método para a produção LISTA_DE_PARAMETROS2
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
    private void comando_composto() {
        match("begin");
        comandos_opcionais();
        match("end");
    }

    // Método para a produção COMANDOS_OPCIONAIS
    private void comandos_opcionais() {
        if (isCurrentTokenType(IDENTIFIER) || isCurrentTokenText("begin") || isCurrentTokenText("if") || isCurrentTokenText("while")) {
            lista_de_comandos();
        }
        // epsilon - não fazer nada
    }

    // Método para a produção LISTA_DE_COMANDOS
    private void lista_de_comandos() {
        comando();
        lista_de_comandos2();
    }

    // Método para a produção LISTA_DE_COMANDOS2
    private void lista_de_comandos2() {
        if (isCurrentTokenText(";")) {
            advance();
            comando();
            lista_de_comandos2();
        }
        // epsilon - não fazer nada
    }

    // Método para a produção COMANDO
    private void comando() {
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
    }

    // Método para a produção COMANDO_OPT
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

    // Método para a produção PARTE_ELSE
    private void parte_else() {
        if (isCurrentTokenText("else")) {
            advance();
            comando();
        }
        // epsilon - não fazer nada
    }

    // Método para a produção LISTA_DE_EXPRESSOES
    private void lista_de_expressoes() {
        expressao();
        lista_de_expressoes2();
    }

    // Método para a produção LISTA_DE_EXPRESSOES2
    private void lista_de_expressoes2() {
        if (isCurrentTokenText(",")) {
            advance();
            expressao();
            lista_de_expressoes2();
        }
        // epsilon - não fazer nada
    }

    // Método para a produção EXPRESSAO
    private void expressao() {
        expressao_simples();
        expressao_opt();
    }

    // Método para a produção EXPRESSAO_OPT
    private void expressao_opt() {
        if (isCurrentTokenType(REL_OPERATOR)) {
            advance();
            expressao_simples();
        }
        // epsilon - não fazer nada
    }

    // Método para a produção EXPRESSAO_SIMPLES
    private void expressao_simples() {
        if (isCurrentTokenText("+") || isCurrentTokenText("-")) {
            sinal();
        }
        termo();
        expressao_simples2();
    }

    // Método para a produção EXPRESSAO_SIMPLES2
    private void expressao_simples2() {
        if (isCurrentTokenType(ADD_OPERATOR) || isCurrentTokenText("or")) {
            advance();
            termo();
            expressao_simples2();
        }
        // epsilon - não fazer nada
    }

    // Método para a produção TERMO
    private void termo() {
        fator();
        termo2();
    }

    // Método para a produção TERMO2
    private void termo2() {
        if (isCurrentTokenType(MULT_OPERATOR) || isCurrentTokenText("and")) {
            advance();
            fator();
            termo2();
        }
        // epsilon - não fazer nada
    }

    // Método para a produção FATOR
    private void fator() {
        if (isCurrentTokenType(IDENTIFIER)) {
            match(IDENTIFIER);
            fator_opt();
        } else if (isCurrentTokenType(INT_NUMBER) || isCurrentTokenType(REAL_NUMBER) || isCurrentTokenText("true")) {
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

    // Método para a produção FATOR_OPT
    private void fator_opt() {
        if (isCurrentTokenText("(")) {
            advance();
            lista_de_expressoes();
            match(")");
        }
        // epsilon - não fazer nada
    }

    // Método para a produção SINAL
    private void sinal() {
        if (isCurrentTokenText("+") || isCurrentTokenText("-")) {
            advance();
        } else {
            error();
        }
    }
}