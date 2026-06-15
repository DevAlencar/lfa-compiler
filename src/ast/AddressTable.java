package ast;

import java.util.HashMap;
import java.util.Map;

public class AddressTable {
    private final Map<String, Integer> table = new HashMap<>();
    private final AddressTable parent;
    private int localOffset = 0; // Controla o deslocamento neste escopo

    public AddressTable(AddressTable parent) {
        this.parent = parent;
    }

    public int enter(String name) {
        if (table.containsKey(name)) return table.get(name);
        int addr = localOffset;
        localOffset += 1; // Cada tipo nativo da sua gramática ocupa 1 palavra de memória na TAM
        table.put(name, addr);
        return addr;
    }

    public int retrieve(String name) {
        if (table.containsKey(name)) return table.get(name);
        if (parent != null) return parent.retrieve(name);
        throw new RuntimeException("Erro de Geração de Código: Variável não mapeada: " + name);
    }

    public int getLocalOffset() {
        return this.localOffset;
    }

    public AddressTable getParent() {
        return this.parent;
    }
}