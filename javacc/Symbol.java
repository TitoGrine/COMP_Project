import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Symbol {
    TypeEnum type;
    String classType;
    boolean initialized = false;

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

    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public String toString() {
        return "       \033[1;37mType\033[0m: " + this.type.toString() + (classType == null ? "" : ("\n       \033[1;37mClass Type\033[0m: " + this.classType)) + "\n";
    }


}
