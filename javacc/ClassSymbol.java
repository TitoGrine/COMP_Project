import java.util.ArrayList;
import java.util.List;

public class ClassSymbol extends Symbol{
    List<TypeEnum> parameters = new ArrayList<>();
    String extendedClass = null;

    public ClassSymbol(){
        super(TypeEnum.OBJECT);
    }

    public ClassSymbol(ArrayList<TypeEnum> parameters) {
        super(TypeEnum.OBJECT);
        this.parameters = parameters;
    }

    public void setExtendedClass(String extendedClass){
        this.extendedClass = extendedClass;
    }

    public String getExtendedClass(){
        return extendedClass;
    }

    @Override
    public String toString() {
        return ControlVars.WHITE_BOLD + (this.type == null ? "" : "       " + "Type: " + ControlVars.RESET + this.type.toString() + '\n') + (extendedClass == null ? "" : ("       " + ControlVars.WHITE_BOLD + "Extends: " + ControlVars.RESET + this.extendedClass + '\n'));
    }
}
