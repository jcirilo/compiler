package utils;
import java.util.ArrayList;

// PILHA DE ESCOPO DE IDENTIFICADORES

public class ScopeIDStack {
    private ArrayList<String> ids;

    public ScopeIDStack() {
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
    public boolean contains(String id) {
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

    // Limpa todas variáveis declaradas dentro de um escopo
    public void cleanScope () {
        String popedId = pop(); 
        while (!popedId.equals("$")) {
            popedId = pop();
        }
    }

    // Para debug
    @Override
    public String toString() {
        String data = new String("[");
        for (String id : ids) {
            data += id + " ";
        }
        return (data + "]");
    } 
}