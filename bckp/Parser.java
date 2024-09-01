package parser;

import utils.TokenType;
import java.util.ArrayList;
import lexical.Token;

public class Parser {
    ArrayList<Token> buffer;
    private int pos;
    private Token tk;

    public Parser() {
        this.buffer = null;
        this.pos = 0;
        this.tk = null;
    }

    public void setBuffer(ArrayList<Token> buffer) {
        this.buffer = buffer;
    }

    public void parse() {
        programa();
    }

    private Token nextToken() {
        return buffer.get(pos++);
    }

    private void next() {
        pos++;
        if (pos < buffer.size()) {
            tk = buffer.get(pos);
        }
    }
    
    private boolean matchType(TokenType t) {
        return tk.isType(t);
    }

    private boolean matchText(String s) {
        return tk.isText(s);
    }

    private void back() {
        pos--;
        tk = buffer.get(pos);
    }

    private void programa () {
        tk = nextToken();
        if (tk.isText("program")) {
            tk = nextToken();
            if (tk.isType(TokenType.IDENTIFIER)) {
                tk = nextToken();
                if (tk.isText(";")) {
                    declaracoes_variaveis();
                    declaracoes_de_subprogramas();
                    comando_composto();
                    tk = nextToken();
                    if (!tk.isText(".")) {
                        throw new RuntimeException("Error " + tk.toString());    
                    }
                } else {
                    throw new RuntimeException("Error " + tk.toString());    
                }
            } else {
                throw new RuntimeException("Error " + tk.toString());
            }
        } else {
            throw new RuntimeException("Error " + tk.toString());
        }
    }

    private void declaracoes_variaveis() {
        tk = nextToken();
        if (tk.isText("var")) {
            lista_declaracoes_variaveis();
        } else {
            // epslon
            back();
        }
    }

    private void lista_declaracoes_variaveis() {
        lista_de_identificadores();
        tk = nextToken();
        if (tk.isText(":")) {
            tipo();
            tk = nextToken();
            if (tk.isText(";")) {
                lista_declaracoes_variaveis2();
            } else {
                throw new RuntimeException("Error " + tk.toString());    
            }
        } else {
            throw new RuntimeException("Error " + tk.toString());
        }
    }

    // loop removido usando a regra
    private void lista_declaracoes_variaveis2() {
        tk = nextToken();
        if (tk.isType(TokenType.IDENTIFIER)) {
            lista_de_identificadores();
            tk = nextToken();
            if (tk.isText(":")) {
                tipo();
                tk = nextToken();
                if (tk.isText(";")) {
                    lista_declaracoes_variaveis2();
                } else {
                    throw new RuntimeException("Error " + tk.toString());
                }
            }
        } else {
            back();
        }
    }

    public void lista_de_identificadores() {
        tk = nextToken();
        if (tk.isType(TokenType.IDENTIFIER)) {
            lista_de_identificadores2();
        } else {
            throw new RuntimeException("Error " + tk.toString());
        }
    }

    // loop removido usando a regra
    public void lista_de_identificadores2() {
        tk = nextToken();
        if (tk.isText(",")) {
            tk = nextToken();
            if (tk.isType(TokenType.IDENTIFIER)) {
                lista_de_identificadores2();
            } else {
                throw new RuntimeException("Error " + tk.toString());
            }
        } else {
            //epslon
            back();
        }
    }

    public void tipo() {
        tk = nextToken();

        if (!tk.isText("integer") 
            && !tk.isText("real")
            && !tk.isText("boolean")) {
                throw new RuntimeException("Error " + tk.toString());
        }
    }

    private void declaracoes_de_subprogramas() {
        declaracoes_subprogramas2();
    }


    // loop removido usando a regra
    private void declaracoes_subprogramas2() {
        tk = nextToken();
        if (tk.isText("procedure")) {
            back();
            declaracao_subprograma();
            tk = nextToken();
            if (tk.isText(";")) {
                declaracoes_subprogramas2();
            }
        } else {
            // epslon
            back();
        }
    }

    private void declaracao_subprograma() {
        tk = nextToken();
        if (tk.isText("procedure")) {
            tk = nextToken();
            if (tk.isType(TokenType.IDENTIFIER)) {
                argumentos();
                tk = nextToken();
                if (tk.isText(";")) {
                    declaracoes_variaveis();
                    declaracoes_de_subprogramas();
                    comando_composto();
                } else {
                    throw new RuntimeException("Error " + tk.toString());
                }
            } else {
                throw new RuntimeException("Error " + tk.toString());
            }
        } else {
            throw new RuntimeException("Error " + tk.toString());
        }
    }

    private void argumentos() {
        tk = nextToken();
        if (tk.isText("(")) {
            lista_de_parametros();
            tk = nextToken();
            if (!tk.isText(")")) {
                throw new RuntimeException("Error " + tk.toString());
            }
        } else {
            // epslon
            back();
        }
    }

    private void lista_de_parametros() {
        lista_de_identificadores();
        tk = nextToken();
        if (tk.isText(":")) {
            tipo();
            lista_de_parametros2();
        } else {
            throw new RuntimeException("Error " + tk.toString());
        }
    }

    private void lista_de_parametros2() {
        tk = nextToken();
        if (tk.isText(";")) {
            lista_de_identificadores();
            tk = nextToken();
            if (tk.isText(":")) {
                tipo();
                lista_de_parametros2();
            } else {
                throw new RuntimeException("Error " + tk.toString());
            }
        } else {
            // epslon
            back();
        }
    }

    private void comando_composto() {
        tk = nextToken();
        if (tk.isText("begin")) {
            comandos_opcionais();
            tk = nextToken();
            if (!tk.isText("end")) {
                throw new RuntimeException("Error " + tk.toString());
            }
        } else {
            throw new RuntimeException("Error " + tk.toString());
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
        tk = nextToken();
        if (tk.isText(";")) {
            comando();
            lista_de_comandos2();
        } else {
            // epslon
            back();
        }
    }

    private void comando() {
    //comando ->
    //      variável := expressão
    //      | ativação_de_procedimento
    //      | comando_composto
    //      | if expressão then comando parte_else
    //      | while expressão do 
    
    }

    private void variavel() {

    }
}
