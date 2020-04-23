public class Symbol {
    TypeEnum type;
    boolean initialized = false;

    public Symbol(TypeEnum type){
        this.type = type;
    }

    public TypeEnum getType() {
        return type;
    }

    public void setType(TypeEnum type) {
        this.type = type;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public String toString() {
        return this.type.toString();
    }
}
