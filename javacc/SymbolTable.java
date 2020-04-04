import java.util.Hashtable;

public class SymbolTable {

    SymbolTable parent = null;
    Hashtable<String, Symbol> table = new Hashtable<>();

    SymbolTable(SymbolTable parent){
        this.parent = parent;
    }


}
