import java.util.ArrayList;
import java.util.List;

public class ClassSymbol extends Symbol{
    List<TypeEnum> parameters = new ArrayList<>();
    String extendedClass = null;

    public ClassSymbol(ArrayList<TypeEnum> parameters) {
        super(TypeEnum.CLASS);
        this.parameters = parameters;
    }

    public void setExtendedClass(String extendedClass){
        this.extendedClass = extendedClass;
    }

    public String getExtendedClass(){
        return extendedClass;
    }
}
