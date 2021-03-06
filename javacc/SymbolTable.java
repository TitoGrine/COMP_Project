import java.util.ArrayList;
import java.util.Hashtable;

public class SymbolTable {
    SymbolTable parent = null;
    Hashtable<String, Symbol> table = new Hashtable<>();

    SymbolTable(){
    }

    SymbolTable(SymbolTable parent){
        this.parent = parent;
    }

    public void addStaticMethodSymbol(String key, ArrayList<String> parameters, String returnType){
        if(table.containsKey(key))
            ((MethodSymbol) table.get(key)).addParameters(parameters, returnType, true);
        else
            table.put(key, new MethodSymbol(returnType, parameters, true));
    }

    public void addMethodSymbol(String key, ArrayList<String> parameters, String returnType){
        if(table.containsKey(key))
            ((MethodSymbol) table.get(key)).addParameters(parameters, returnType);
        else
            table.put(key, new MethodSymbol(returnType, parameters));
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

    public boolean existsArraySymbol(String key){
        if(key == null)
            return false;

        Symbol symbol = this.getSymbol(key);

        return symbol != null && symbol.type.equals(ControlVars.ARRAY);
    }

    public boolean existsMethodSymbol(String key){
        if(key == null)
            return false;

        Symbol symbol = this.getSymbol(key);

        return symbol != null && symbol.type.equals(ControlVars.METHOD);
    }

    public boolean existsClassSymbol(String key){
        if(key == null)
            return false;

        Symbol symbol = this.getSymbol(key);

        return symbol != null && symbol.type.equals(key);
    }

    public void setInitialized(String key){
        if(key == null)
            return;

        Symbol symbol = this.getSymbol(key);

        if(symbol != null)
            symbol.incInitialized();
    }

    public boolean repeatedMethod(String key, String returnType, ArrayList<String> arguments){
        if(existsMethodSymbol(key))
            return ((MethodSymbol) this.getSymbol(key)).repeatedMethod(returnType, arguments);

        return false;
    }

    public String getClassType(String key){
        Symbol symbol = this.getSymbol(key);

        if(symbol instanceof ClassSymbol)
            return key;
        else
            return symbol.getClassType();
    }

    public void clearInitialized(ArrayList<String> unstableVars){
        for(String key : unstableVars){
            if(key != null){
                Symbol symbol = this.getSymbol(key);
                symbol.decInitialized();

                if(!symbol.isInitialized())
                    symbol.setAsVolatile(true);
            }
        }
    }

    public void print(String scopeName) {
        String convert = ControlVars.PURPLE + " ========== "+ (scopeName == null ? "" : scopeName + "\'s ") + "SYMBOL TABLE ==========\n\n" + ControlVars.RESET;
        Symbol symbol;

        if(parent != null)
            convert += parent.toStringParent();

        convert += ControlVars.BLUE + "  --------- Scope Table ---------\n\n" + ControlVars.RESET;

        for(String key : this.table.keySet()){
            symbol = this.table.get(key);

            convert += symbol == null ? "" : "    \uD83D\uDDDD " + ControlVars.WHITE_BOLD + "Key: " + ControlVars.RESET + key + "\n" + symbol.toString() + "\n";
        }

        System.out.println(convert);
    }

    public String toStringParent() {
        String convert = "";
        Symbol symbol;

        if(parent != null)
            convert += parent.toStringParent();

        convert += ControlVars.BLUE + "  --------- Parent Table ---------\n\n" + ControlVars.RESET;

        for(String key : this.table.keySet()){
            symbol = this.table.get(key);

            if(symbol != null)
                convert += "    \uD83D\uDDDD " + ControlVars.WHITE_BOLD + "Key: " + ControlVars.RESET + key + "\n" + symbol.toString() + "\n";
        }

        return convert;
    }
}
