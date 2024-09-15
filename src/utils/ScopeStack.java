package utils;
import java.util.ArrayList;

// PILHA DE ESCOPO DE IDENTIFICADORES

public class ScopeStack {
    private ArrayList<String> ids;

    public ScopeStack() {
        ids = new ArrayList<String>();
    }

    public boolean push(String id) {
        return ids.add(id);
    }

    public String pop() {
        if (ids == null) {
            return null;
        }
        return ids.remove(ids.size()-1);
    }

    // Métodod para verificar se já ha o id no escopo
    // Procura o primeiro id antes e até o $
    // Ex.:
    //                      topo
    //                       v
    // pilha: [$ a b c $ x y z]
    //                 ^
    //            procura o id até esse simbolo
    public boolean scopeContains(String id) {
        if (id == "$") {
            return false;
        }

        for (int i = ids.size()-1; i > 0; i--) {
            if (ids.get(i).equals("$")) {
                break;
            }
            if (ids.get(i).equals(id)) {
                return true;
            }
        }

        return false;
    }

    // procura o identificador em toda a a pilha
    // serve para na parte de ativação de procedimentos
    public boolean contains(String id) {
        return ids.contains(id);
    }

    // Limpa todas variáveis declaradas dentro de um escopo
    // Ex.:
    // pilha:
    // [$ a b c $ x y z]
    //
    // pilha dps chamar de cleanScope() 1 vez:
    // [$ a b c]
    public void cleanScope () {
        String popedId = pop(); 
        while (!popedId.equals("$")) {
            popedId = pop();
        }
    }

    @Override
    public String toString() {
        String data = new String("[");
        for (String id : ids) {
            data += id + " ";
        }
        return (data + "]");
    } 
}