public class Symbol {
    TypeEnum type;
    String classType;
    boolean initialized = false;
    int volatileVar = 0;

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

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public void incVolatily(){
        this.volatileVar++;
    }

    public void decVolatily(){
        this.volatileVar--;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public boolean isVolatile() {
        return false; // TODO: change when fixed
    }

    @Override
    public String toString() {
        return ControlVars.WHITE_BOLD + (this.type == null ? "" : "       " + "Type: " + ControlVars.RESET + this.type.toString() + '\n') + ControlVars.WHITE_BOLD + "       Initialized: " + (this.isInitialized() ? ControlVars.GREEN_BRIGHT + " ✓ " : ControlVars.RED_BRIGHT + " ❌ ") + ControlVars.RESET + (classType == null ? "" : ("\n       " + ControlVars.WHITE_BOLD + "Class Type: " + ControlVars.RESET + this.classType)) + "\n";
    }
}
