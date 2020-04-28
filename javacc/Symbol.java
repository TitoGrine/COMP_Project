public class Symbol {
    TypeEnum type;
    String classType;
    int initialized = 0;
    boolean volatileVar = false;

    public Symbol(TypeEnum type){
        this.type = type;
    }

    public Symbol(TypeEnum type, String classType){
        this.type = type;
        this.classType = classType;
    }

    public TypeEnum getType() {
        return type;
    }

    public String getClassType() {
        return classType;
    }

    public void setType(TypeEnum type) {
        this.type = type;
    }

    public void setAsVolatile(boolean volatileVar) {
        this.volatileVar = volatileVar;
    }

    public void incInitialized() {
        this.initialized++;
    }

    public void decInitialized() {
        this.initialized--;
    }

    public boolean isInitialized() {
        return initialized > 0;
    }

    public boolean isVolatile() {
        return volatileVar;
    }

    @Override
    public String toString() {
        return ControlVars.WHITE_BOLD + (this.type == null ? "" : "       " + "Type: " + ControlVars.RESET + this.type.toString() + '\n') + ControlVars.WHITE_BOLD + "       Initialized: " + (this.isInitialized() ? ControlVars.GREEN_BRIGHT + " ✓ " : ControlVars.RED_BRIGHT + " ❌ ") + ControlVars.RESET + (classType == null ? "" : ("\n       " + ControlVars.WHITE_BOLD + "Class Type: " + ControlVars.RESET + this.classType)) + "\n";
    }
}
