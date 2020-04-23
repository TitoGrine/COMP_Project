import java.util.Hashtable;

public class SymbolTable {

    SymbolTable parent = null;
    Hashtable<String, Symbol> table = new Hashtable<>();

    SymbolTable(SymbolTable parent){
        this.parent = parent;
    }

    public void addSymbol(String key, MethodSymbol symbol){
        if(table.containsKey(key))
            ((MethodSymbol) table.get(key)).addParameters(symbol.parametersOverload);
    }

    public void addSymbol(String key, Symbol symbol){
        table.put(key, symbol);
    }

    public Symbol getSymbol(String key){
        Symbol symbol = table.get(key);

        if(symbol != null)
            return symbol;

        if(parent != null)
            return parent.getSymbol(key);

        return null;
    }

    public boolean existsSymbol(String key){
        return this.table.containsKey(key);
    }

    public boolean existsMethodSymbol(String key){
        Symbol symbol = this.getSymbol(key);

        return symbol != null && symbol.type == TypeEnum.METHOD;
    }

    public boolean existsClassSymbol(String key){
        Symbol symbol = this.getSymbol(key);

        return symbol != null && symbol.type == TypeEnum.OBJECT;
    }

    public void setInitialized(String key){
        Symbol symbol = this.getSymbol(key);

        if(symbol != null)
            symbol.setInitialized(true);
    }
}
