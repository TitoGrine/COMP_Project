import java.util.ArrayList;
import java.util.List;

public class MethodSymbol extends Symbol{
    TypeEnum returnType;
    ArrayList<ArrayList<TypeEnum>> parametersOverload = new ArrayList<>();

    public MethodSymbol(TypeEnum returnType) {
        super(TypeEnum.METHOD);
        this.returnType = returnType;

        this.parametersOverload.add(new ArrayList<>());
    }

    public MethodSymbol(TypeEnum returnType, ArrayList<TypeEnum> parameters) {
        super(TypeEnum.METHOD);
        this.returnType = returnType;

        this.parametersOverload.add(parameters);
    }

    public TypeEnum getReturnType() {
        return returnType;
    }

    public void addParameters(){
        ArrayList<TypeEnum> parameters = new ArrayList<>();

        if(!this.acceptedParameters(parameters))
            this.parametersOverload.add(parameters);
    }

    public void addParameters(ArrayList<ArrayList<TypeEnum>> parametersOverload){
        for(ArrayList<TypeEnum> parameters : parametersOverload)
            if(!this.acceptedParameters(parameters))
                this.parametersOverload.add(parameters);
    }

    public boolean acceptedParameters(ArrayList<TypeEnum> arguments){
        return parametersOverload.contains(arguments);
    }
}
