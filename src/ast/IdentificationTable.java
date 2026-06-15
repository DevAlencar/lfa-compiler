package ast;

import java.util.HashMap;
import java.util.Map;

public class IdentificationTable {
    private final Map<String, String> table = new HashMap<>();
    private final IdentificationTable parent;

    public IdentificationTable(IdentificationTable parent) {
        this.parent = parent;
    }

    public void enter(String name, String type) {
        if (table.containsKey(name)) {
            throw new RuntimeException("Erro Semântico: O identificador '" + name + "' já foi declarado neste escopo.");
        }
        table.put(name, type);
    }

    public String retrieve(String name) {
        if (table.containsKey(name)) {
            return table.get(name);
        }
        if (parent != null) {
            return parent.retrieve(name);
        }
        throw new RuntimeException("Erro Semântico: O identificador '" + name + "' não foi declarado.");
    }

    public IdentificationTable retrieveParentEnvironment() {
        return this.parent;
    }
}