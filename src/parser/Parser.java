package parser;

import utils.TokenType;
import java.util.ArrayList;
import lexical.Token;

public class Parser {
    ArrayList<Token> buffer;
    private int i;

    public Parser() {
        this.buffer = null;
        this.i = 0;
    }

    public void setBuffer(ArrayList<Token> buffer) {
        this.buffer = buffer;
    }

    public void start() {
        programa();
    }

    public Token nextToken() {
        return buffer.get(this.i++);
    }

    public void back() {
        this.i--;
    }

    private void programa () {
        Token tk = nextToken();
        if (tk.getText().equals("program")) {
            tk = nextToken();
            if (tk.getType() == TokenType.IDENTIFIER) {
                tk = nextToken();
                if (tk.getText().equals(";")) {
                    declaracao_variaveis();
                    declaracao_subprogramas();
                    comando_composto();
                    tk = nextToken();
                    if (!tk.getText().equals(".")) {
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

    private void declaracao_variaveis() {
        Token tk = nextToken();
        if (tk.getText().equals("var")) {
            lista_declaracoes_variaveis();
        } else {
            back();
        }
    }

    private void declaracao_subprogramas() {
        Token tk = nextToken();
    }

    private void comando_composto() {
        Token tk = nextToken();
    }

    public void lista_declaracoes_variaveis() {
        // TODO
    }

}
