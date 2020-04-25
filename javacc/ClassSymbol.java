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
        return super.toString() + (extendedClass == null ? "" : ("       \033[1;37mExtends\033[0m: " + this.extendedClass + '\n'));
    }
}
