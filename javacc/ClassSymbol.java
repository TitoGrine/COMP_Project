import java.util.ArrayList;
import java.util.List;

public class ClassSymbol extends Symbol{
    List<String> parameters = new ArrayList<>();
    String extendedClass = null;

    public ClassSymbol(String className){
        super(className);
        this.classType = className;
    }

    public ClassSymbol(String className, ArrayList<String> parameters) {
        super(className);
        this.parameters = parameters;
        this.classType = className;
    }

    public void setExtendedClass(String extendedClass){
        this.extendedClass = extendedClass;
    }

    public String getExtendedClass(){
        return extendedClass;
    }

    @Override
    public boolean isInitialized() {
        return true;
    }

    @Override
    public boolean isVolatile() {
        return false;
    }

    @Override
    public String toString() {
        return ControlVars.WHITE_BOLD + (this.type == null ? "" : "       " + "Type: " + ControlVars.RESET + this.type.toString() + '\n') + (extendedClass == null ? "" : ("       " + ControlVars.WHITE_BOLD + "Extends: " + ControlVars.RESET + this.extendedClass + '\n'));
    }
}
