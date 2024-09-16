package utils;
import java.util.ArrayList;

import lexical.Token;

// PILHA DE ESCOPO DE IDENTIFICADORES

public class ScopeStack {
    private ArrayList<Token> tokens;

    public ScopeStack() {
        tokens = new ArrayList<Token>();
    }

    public boolean push(Token tk) {
        return tokens.add(tk);
    }

    public Token pop() {
        if (tokens == null) {
            return null;
        }
        return tokens.remove(tokens.size()-1);
    }

    public Token getFromTop(int pos) {
        return tokens.get(tokens.size()-pos);
    }

    // SERVE PARA MARCAR O TIPO DOS IDENTIFICADORES APÓS
    // NA PRODUÇÃO TIPO
    public void assignTypeWhereIsNull (TokenType tt) {
        for (int i = tokens.size()-1; i > 0; i--) {
            if (tokens.get(i).getType() == null) {
                tokens.get(i).setType(tt);
            } else {
                break;
            }
        }
    }

    // Métodod para verificar se já ha o id no escopo
    // Procura o primeiro id antes e até o $
    // Ex.:
    //                      topo
    //                       v
    // pilha: [$ a b c $ x y z]
    //                 ^
    //            procura o id até esse simbolo
    public boolean scopeContains(String tk) {
        if (tk == "$") {
            return false;
        }

        for (int i = tokens.size()-1; i > 0; i--) {
            if (tokens.get(i).getText().equals("$")) {
                break;
            }
            if (tokens.get(i).getText().equals(tk)) {
                return true;
            }
        }

        return false;
    }

    // procura o identificador em toda a a pilha
    // do topo para a base e para ao encontrar
    // garantindo a prcedência de uso local -> global
    // serve para na parte de ativação de procedimentos
    public boolean contains(String id) {
        for (int i = tokens.size()-1; i > 0; i--) {
            if (tokens.get(i).getText().equals(id)) {
                return true;
            }
        }
        return false;
    }

    // Limpa todas variáveis declaradas dentro de um escopo
    // Ex.:
    // pilha:
    // [$ a b c $ x y z]
    //
    // pilha dps chamar de cleanScope() 1 vez:
    // [$ a b c]
    public void cleanScope () {
        Token popedId = pop(); 
        while (!popedId.getText().equals("$")) {
            popedId = pop();
        }
    }

    public int size() {
        return this.tokens.size();
    }
    
    @Override
    public String toString() {
        String data = new String("[");
        for (Token id : tokens) {
            data += " (" + id.getText() + ", " + id.getType() + ")";
        }
        return (data + " ]");
    } 
}