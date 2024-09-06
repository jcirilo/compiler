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
            throw new RuntimeException("Syntatic Error: buffer is empty!");
        }
        programa();
        System.out.println("Syntatic compilation sucessfully");
    }

    private void goNext() {
        if (pos < buffer.size()) {
            currentToken = buffer.get(pos++);
            text = currentToken.getText();
            type = currentToken.getType().name();
        }
    }

    private boolean isCurrentTokenType(TokenType t) {
        return currentToken.getType() == t;
    }

    private boolean isCurrentTokenText(String s) {
        return currentToken.getText().equals(s);
    }

    private void error() {
        throw new RuntimeException(
            "Syntatic Error: unespected token '" 
            + currentToken.getText()  
            + "' at line " 
            + currentToken.getRow() 
            + ", column " 
            + currentToken.getCol());
    }

    private void programa() {
        goNext();
        if (isCurrentTokenText("program")) {
            goNext();
            if (isCurrentTokenType(IDENTIFIER)) {
                goNext();
                if (isCurrentTokenText(";")) {
                    goNext();
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

    private void declaracoes_variaveis() {
        if (isCurrentTokenText("var")) {
            goNext();
            lista_declaracoes_variaveis();
        }
    }

    private void lista_declaracoes_variaveis() {
        if (isCurrentTokenType(IDENTIFIER)) {
            lista_de_identificadores();
            //goNext();
            if (isCurrentTokenText(":")) {
                goNext();
                tipo();
                goNext();
                if (isCurrentTokenText(";")) {
                    goNext();
                    lista_declaracoes_variaveis2();
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

    private void lista_declaracoes_variaveis2() {
        if (isCurrentTokenType(IDENTIFIER)) {
            lista_de_identificadores();
            //goNext();
            if (isCurrentTokenText(":")) {
                goNext();
                tipo();
                goNext();
                if (isCurrentTokenText(";")) {
                    lista_declaracoes_variaveis2();
                } else {
                    error();
                }
            }
        }
    }

    private void lista_de_identificadores() {
        //goNext();
        if (isCurrentTokenType(IDENTIFIER)) {
            goNext();
            lista_de_identificadores2();
        } else {
            error();
        }
    }

    private void lista_de_identificadores2() {
        if (isCurrentTokenText(",")) {
            goNext();
            if (isCurrentTokenType(IDENTIFIER)) {
                goNext();
                lista_de_identificadores2();
            } else {
                error();
            }
        }
    }

    private void tipo() {
        if (isCurrentTokenText("integer")) {
        } else if (isCurrentTokenText("real")) {
        } else if (isCurrentTokenText("boolean")) {
        } else {
            error();
        }
    }

    private void declaracoes_de_subprogramas() {
        //goNext();
        if (isCurrentTokenText("procedure")) {
            declaracoes_de_subprogramas2();
        }
    }

    private void declaracoes_de_subprogramas2() {
        goNext();
        if (isCurrentTokenText("procedure")) {
            declaracao_de_subprograma();
            goNext();
            if (isCurrentTokenText(";")) {
                declaracoes_de_subprogramas2();
            } else {
                error();
            }
        }
    }

    private void declaracao_de_subprograma() {
        goNext();
        if (isCurrentTokenText("procedure")) {
            goNext();
            if (isCurrentTokenType(IDENTIFIER)) {
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
        }
    }

    private void lista_de_parametros() {
        goNext();
        if (isCurrentTokenType(IDENTIFIER)) {
            lista_de_identificadores();
            goNext();
            if (isCurrentTokenText(":")) {
                tipo();
                lista_de_parametros2();
            }
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
        //goNext();
        if (isCurrentTokenText("begin")) {
            comandos_opcionais();
            goNext();
            if (isCurrentTokenText("end")) {
                // error se nao terminar com end
            } else {
                error();
            }
        } else {
            error();
        }
    }

    private void comandos_opcionais() {
        goNext();
        if (isCurrentTokenType(IDENTIFIER)
            || isCurrentTokenText("begin")
            || isCurrentTokenText("if")
            || isCurrentTokenText("while")
        ) {
            lista_de_comandos();
        }
    }

    private void lista_de_comandos() {
        //goNext();
        if (isCurrentTokenType(IDENTIFIER)
            || isCurrentTokenText("begin")
            || isCurrentTokenText("if")
            || isCurrentTokenText("while")
        ) {
            comando();
            lista_de_comandos2();
        } else {
            error();
        }
    }

    private void lista_de_comandos2() {
        //goNext();
        if (isCurrentTokenText(";")) {
            comando();
            lista_de_comandos2();
        }
    }

    private void comando() {
        //goNext();
        if (isCurrentTokenType(IDENTIFIER)) {
            comando_opt();
        } else if (isCurrentTokenText("begin")) {
            comando_composto();
        } else if (isCurrentTokenText("if")) {
            expressao();
            goNext();
            if(isCurrentTokenText("then")) {
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
        } else {
            error();
        }
    }

    private void comando_opt() {
        goNext();
        if (isCurrentTokenText(":=")) {
            expressao();
        } else if (isCurrentTokenText("(")) {
            lista_de_expressoes();
            goNext();
            if (isCurrentTokenText(")")) {
                // fim lista expressoes
            } else {
                error();
            }
        }
    }

    private void parte_else() {
        goNext();
        if (isCurrentTokenText("else")) {
            comando();
        }
    }

    private void lista_de_expressoes() {
        if (isCurrentTokenType(IDENTIFIER)
            || isCurrentTokenType(INT_NUMBER)
            || isCurrentTokenType(INT_NUMBER)
            || isCurrentTokenType(REAL_NUMBER)
            || isCurrentTokenText("true")
            || isCurrentTokenText("(")
            || isCurrentTokenText("not")
            || isCurrentTokenText("+")
            || isCurrentTokenText("-")
        ) {
            expressao();
            lista_de_expressoes2();
        } else {
            error();
        }
    }

    private void lista_de_expressoes2() {
        goNext();
        if (isCurrentTokenText(",")) {
            expressao();
            lista_de_expressoes2();
        }
    }

    private void expressao() {
        goNext();
        if (isCurrentTokenType(IDENTIFIER) 
            || isCurrentTokenType(INT_NUMBER) 
            || isCurrentTokenType(INT_NUMBER) 
            || isCurrentTokenType(REAL_NUMBER) 
            || isCurrentTokenText("true") 
            || isCurrentTokenText("(") 
            || isCurrentTokenText("not") 
            || isCurrentTokenText("+") 
            || isCurrentTokenText("-")
        ) {
            expressao_simples();
            expressao_opt();
        } else {
            error();
        }
    }

    private void expressao_opt() {
        //goNext();
        if (isCurrentTokenText("=") 
            || isCurrentTokenText("<")
            || isCurrentTokenText(">")
            || isCurrentTokenText("<=")
            || isCurrentTokenText(">=")
            || isCurrentTokenText("<>")
        ) {
            op_relacional();
            expressao_simples();
        }
    }

    private void expressao_simples() {
        //goNext();
        if (isCurrentTokenType(IDENTIFIER)
            || isCurrentTokenType(INT_NUMBER)
            || isCurrentTokenType(REAL_NUMBER)
            || isCurrentTokenText("true")
            || isCurrentTokenText("(")
            || isCurrentTokenText("not")
        ) {
            termo();
            expressao_simples2();
        } else if (isCurrentTokenText("+") || isCurrentTokenText("-") ) {
            sinal();
            termo();
            expressao_simples2();
        } else {
            error();
        }   
    }

    private void expressao_simples2() {
        //goNext();
        if (isCurrentTokenText("+")
            || isCurrentTokenText("-")
            || isCurrentTokenText("or")
        ) {
            op_aditivo();
            goNext();
            termo();
            expressao_simples2();
        }
    }

    private void termo() {
        //goNext();
        if (isCurrentTokenType(IDENTIFIER)
            || isCurrentTokenType(INT_NUMBER)
            || isCurrentTokenType(REAL_NUMBER)
            || isCurrentTokenText("true")
            || isCurrentTokenText("(")
            || isCurrentTokenText("not")
        ) {
            fator();
            termo2();
        } else {
            error();
        }
    }

    private void termo2() {
        //goNext();
        if (isCurrentTokenText("*")
            || isCurrentTokenText("/")
            || isCurrentTokenText("and")
        ) {
            op_multiplicativo();
            fator();
            termo2();
        }
    }

    private void fator() {
        //goNext();
        if (isCurrentTokenType(IDENTIFIER)) {
            fator_opt();
        } else if (isCurrentTokenType(INT_NUMBER)
                || isCurrentTokenType(REAL_NUMBER)
                || isCurrentTokenText("true")
        ) {
            // passa
        } else if (isCurrentTokenText("(")) {
            expressao();
            goNext();
            if (isCurrentTokenText(")")) {
                // fim fator expressao
            } else {
                error();
            }
        } else if (isCurrentTokenText("not")) {
            fator();
        } else {
            error();
        }
    }

    private void fator_opt() {
        //goNext();
        if (isCurrentTokenText("(")) {
            lista_de_expressoes();
            goNext();
            if (isCurrentTokenText(")")) {
                // fim fator opt lista de expressoes
            } else {
                error();
            }
        }
    }

    private void sinal() {
        //goNext();
        if (isCurrentTokenText("+")
            || isCurrentTokenText("-")
        ) {
            // ok
        } else {
            error();
        }
    }

    private void op_relacional() {
        //goNext();
        if (isCurrentTokenText("=") 
            || isCurrentTokenText("<") 
            || isCurrentTokenText(">")
            || isCurrentTokenText("<=")
            || isCurrentTokenText(">=")
            || isCurrentTokenText("<>")
        ){
            // ok
        } else {
            error();
        }
    }

    private void op_aditivo() {
        //goNext();
        if (isCurrentTokenText("+") 
            || isCurrentTokenText("-") 
            || isCurrentTokenText("or")
        ) {
                // ok
        } else {
            error();
        }
    }

    private void op_multiplicativo() {
        //goNext();
        if (isCurrentTokenText("*") 
            || isCurrentTokenText("/")
            || isCurrentTokenText("and")
        ) {
            // ok
        } else {
            error();
        }
    }
}