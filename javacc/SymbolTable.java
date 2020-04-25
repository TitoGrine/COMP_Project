import java.util.Hashtable;

public class SymbolTable {

    SymbolTable parent = null;
    Hashtable<String, Symbol> table = new Hashtable<>();

    SymbolTable(){
    }

    SymbolTable(SymbolTable parent){
        this.parent = parent;
    }

    public void addSymbol(String key, MethodSymbol symbol){
        if(table.containsKey(key))
            ((MethodSymbol) table.get(key)).addParameters(symbol.parametersOverload);
        else
            table.put(key, symbol);
    }

    public void addSymbol(String key, Symbol symbol){
        table.put(key, symbol);
    }

    public Symbol getSymbol(String key){
        if(key == null)
            return null;

        Symbol symbol = table.get(key);

        if(symbol != null)
            return symbol;

        if(parent != null)
            return parent.getSymbol(key);

        return null;
    }

    public boolean existsSymbol(String key){
        return (key != null && this.getSymbol(key) != null);
    }

    public boolean existsMethodSymbol(String key){
        if(key == null)
            return false;

        Symbol symbol = this.getSymbol(key);

        return symbol != null && symbol.type == TypeEnum.METHOD;
    }

    public boolean existsClassSymbol(String key){
        if(key == null)
            return false;

        Symbol symbol = this.getSymbol(key);

        return symbol != null && symbol.type == TypeEnum.OBJECT;
    }

    public void setInitialized(String key){
        if(key == null)
            return;

        Symbol symbol = this.getSymbol(key);

        if(symbol != null)
            symbol.setInitialized(true);
    }

    @Override
    public String toString() {
        String convert = "\n\033[1;35m ========== SYMBOL TABLE ==========\033[0m\n\n";

        if(parent != null)
            convert += parent.toStringParent();

        convert += "\033[0;34m  --------- Scope Table ---------\033[0m\n\n";

        for(String key : this.table.keySet())
            convert += "    \uD83D\uDDDD \033[1;37mKey\033[0m: " + key + "\n       \033[1;37mSymbol\033[0m: " + this.table.get(key).toString() + "\n\n";

        return convert;
    }

    public String toStringParent() {
        String convert = "";

        if(parent != null)
            convert += parent.toStringParent();

        convert += "\033[0;34m  --------- Parent Table ---------\033[0m\n\n";

        for(String key : this.table.keySet())
            convert += "    \uD83D\uDDDD \033[1;37mKey\033[0m: " + key + "\n       \033[1;37mSymbol\033[0m: " + this.table.get(key).toString() + "\n\n";

        return convert;
    }
}
