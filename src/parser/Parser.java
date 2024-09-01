package parser;

import utils.TokenType;
import java.util.ArrayList;
import lexical.Token;

public class Parser {
    ArrayList<Token> buffer;
    private int pos;
    private Token currentToken;
    private String tktxt;   // para debug
    private String tktp;    // para debug
    public Parser() {
        this.buffer = null;
        this.pos = 0;
        this.currentToken = null;
        this.tktxt = null;
        this.tktp = null;
    }

    public void setBuffer(ArrayList<Token> buffer) {
        this.buffer = buffer;
    }

    public void parse() {
        programa();
    }

    private void goNext() {
        if (pos < buffer.size()) {
            currentToken = buffer.get(pos++);
            tktxt = currentToken.getText();
            tktp = currentToken.getType().name();
        }
    }
    
    private boolean isCurrentTokenType(TokenType t) {
        return currentToken.getType() == t;
    }

    private boolean isCurrentTokenText(String s) {
        return currentToken.getText().equals(s);
    }

    private boolean error() {
        throw new RuntimeException("Erro Sintatico " + currentToken.getText());
    }

    private void programa() {
        goNext();
        if (isCurrentTokenText("program")) {
            goNext();
            if (isCurrentTokenType(TokenType.IDENTIFIER)) {
                goNext();
                if (isCurrentTokenText(";")) {
                    declaracoes_variaveis();
                    declaracoes_de_subprogramas();
                    comando_composto();
                    goNext();
                    if (!isCurrentTokenText(".")) {
                        error();
                    }
                } else {
                    error();
                }
            } else {
                error();
            }
        } else {
            error();
        }
    }

    private void declaracoes_variaveis() {
        goNext();
        if (isCurrentTokenText("var")) {
            lista_declaracoes_variaveis();
        }
    }

    private void lista_declaracoes_variaveis() {
        lista_de_identificadores();
        if (isCurrentTokenText(":")) {
            tipo();
            goNext();
            if (isCurrentTokenText(";")) {
                lista_declaracoes_variaveis2();
            } else {
                error();
            }
        } else {
            error();
        }
    }

    private void lista_declaracoes_variaveis2() {
        goNext();
        if (isCurrentTokenText(",")) {
            goNext();
            if (isCurrentTokenType(TokenType.IDENTIFIER)) {
                lista_declaracoes_variaveis2();
            } else {
                error();
            }
        }
    }

    private void tipo() {
        goNext();
        if (!isCurrentTokenText("integer") && 
            !isCurrentTokenText("real") && 
            !isCurrentTokenText("boolean")) {
            error();
        }
    }

    private void lista_de_identificadores() {
        goNext();
        if (isCurrentTokenType(TokenType.IDENTIFIER)) {
            lista_de_identificadores2();
        }
    }

    private void lista_de_identificadores2() {
        goNext();
        if (isCurrentTokenText(",")) {
            goNext();
            if (isCurrentTokenType(TokenType.IDENTIFIER)) {
                lista_de_identificadores2();
            } else {
                error();
            }
        }
    }

    private void declaracoes_de_subprogramas() {
        if (isCurrentTokenText("procedure")) {
            declaracoes_de_subprogramas2();
        }
    }

    private void declaracoes_de_subprogramas2() {
        declaracao_de_subprograma();
        goNext();
        if (isCurrentTokenText(";")) {
            declaracoes_de_subprogramas2();
        }
    }

    private void declaracao_de_subprograma() {
        if (isCurrentTokenText("procedure")) {
            goNext();
            if (isCurrentTokenType(TokenType.IDENTIFIER)) {
                argumentos();
                goNext();
                if (isCurrentTokenText(";")) {
                    declaracoes_variaveis();
                    declaracoes_de_subprogramas();
                    comando_composto();
                } else {
                    error();
                }
            } else {
                error();
            }
        } else {
            error();
        }
    }

    private void argumentos() {
        goNext();
        if (isCurrentTokenText("(")) {
            lista_de_parametros();
            if (!isCurrentTokenText(")")) {
                error();
            }
        }
    }

    private void lista_de_parametros() {
        lista_de_identificadores();
        if (isCurrentTokenText(":")) {
            tipo();
            lista_de_parametros2();
        } else {
            error();
        }
    }

    private void lista_de_parametros2() {
        goNext();
        if (isCurrentTokenText(";")) {
            lista_de_identificadores();
            goNext();
            if (isCurrentTokenText(":")) {
                tipo();
                lista_de_parametros2();
            } else {
                error();
            }
        }
    }

    private void comando_composto() {
        if (isCurrentTokenText("begin")) {
            comandos_opcionais();
            goNext();
            if (!isCurrentTokenText("end")) {
                error();
            }
        }
    }

    private void comandos_opcionais() {
        lista_de_comandos();        
    }

    private void lista_de_comandos() {
        comando();
        lista_de_comandos2();
    }

    private void lista_de_comandos2() {
        goNext();
        if (isCurrentTokenText(";")) {
            comando();
            lista_de_comandos2();
        }
    }

    // TODO: FALTA TERMINAR DAQUI PARA BAIXO 
    private void comando() {

        if (isCurrentTokenType(TokenType.IDENTIFIER)) {
            goNext();
            // variavel
            if (isCurrentTokenText(":=")) {
                expressao();

            // ativacao_de_procedimento
            } else if (isCurrentTokenText("(")) {
                lista_de_expressoes();
            } else {
                error();
            }
        } else if (isCurrentTokenText("if")) {
            expressao();
            goNext();
            if (isCurrentTokenText("then")) {
                comando();
                parte_else();
            } else {
                error();
            }
        } else if (isCurrentTokenText("while")) {
            expressao();
            goNext();
            if (isCurrentTokenText("do")) {
                comando();
            } else {
                error();
            }
        } else if (isCurrentTokenText("begin")) {
            comando_composto();
        } else {
            error();
        }
    }
    private void parte_else() {
        goNext();
        if (isCurrentTokenText("else")) {
            comando();
        }
    }

    private void lista_de_expressoes() {
        expressao();
        lista_de_expressoes2();
    }

    private void lista_de_expressoes2() {
        goNext();
        if(isCurrentTokenText(",")) {
            expressao();
            lista_de_expressoes2();
        }
    }

    private void expressao() {
        expressao_simples();
        // ou
        lista_de_expressoes();
        goNext();
        if (isCurrentTokenText(",")) {
            expressao();
        } else {
            error();
        }
    }

    private void expressao_simples() {
        if (isCurrentTokenType(TokenType.IDENTIFIER)) {
            termo();
        } else if (isCurrentTokenText("-") || isCurrentTokenText("+")) {
            sinal();
            termo();
        } else {
            expressao_simples();
            op_aditivo();
            termo();
        }
    }

    private void termo() {
        fator();
        // ou
        termo();
        op_multiplicativo();
        fator();
    }

    private void fator() {
        goNext();
        if (isCurrentTokenType(TokenType.IDENTIFIER)) {
            goNext();
            if (isCurrentTokenText("(")) {
                lista_de_expressoes();
                goNext();
                if (!isCurrentTokenText(")")) {
                    error();
                }
            }
        } else if (isCurrentTokenType(TokenType.INT_NUMBER)) {

        } else if (isCurrentTokenType(TokenType.REAL_NUMBER)) {

        } else if (isCurrentTokenText("true")) {

        } else if (isCurrentTokenText("false")) {

        } else if (isCurrentTokenText("(")) {
            expressao();
            goNext();
            if (!isCurrentTokenText(")")) {
                error();
            }
        } else if (isCurrentTokenText("not")) {
            fator();
        } else {
            error();
        }
    }

    private void sinal() {
        goNext();
        if (!isCurrentTokenText("+") && !isCurrentTokenText("-")) {
            error();
        }
    }

    private void op_relacional() {
        if (!isCurrentTokenText("=") &&
            !isCurrentTokenText("<") &&
            !isCurrentTokenText(">") &&
            !isCurrentTokenText("<=") &&
            !isCurrentTokenText(">=") &&
            !isCurrentTokenText("<>")) 
        {
            error();
        }
    }

    private void op_aditivo () {
        goNext();
        if (!isCurrentTokenText("tktp") &&
            !isCurrentTokenText("-") &&
            !isCurrentTokenText("or")
        ) {
            error();
        }
    }

    private void op_multiplicativo() {
        goNext();
        if (!isCurrentTokenText("*") &&
            !isCurrentTokenText("/") &&
            !isCurrentTokenText("and")
        ) {
            error();
        }
    }
}