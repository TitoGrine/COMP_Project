import java.util.ArrayList;
import java.util.List;

public class MethodSymbol extends Symbol{
    List<TypeEnum> parameters = new ArrayList<>();
    TypeEnum returnType;

    public MethodSymbol(TypeEnum returnType, ArrayList<TypeEnum> parameters) {
        super(TypeEnum.METHOD);
        this.returnType = returnType;
        this.parameters = parameters;
    }

    @Override
    public TypeEnum getType() {
        return super.getType();
    }

    public List<TypeEnum> getParameters() {
        return parameters;
    }
}
