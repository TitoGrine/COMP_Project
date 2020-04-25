import java.util.ArrayList;
import java.util.Collections;

public class MethodSymbol extends Symbol{
    TypeEnum returnType;
    ArrayList<ArrayList<TypeEnum>> parametersOverload = new ArrayList<>();

    private void addParameter(){
        ArrayList<TypeEnum> parameters = new ArrayList<>(Collections.singleton(TypeEnum.VOID));

        addParameter(parameters);
    }

    private void addParameter(ArrayList<TypeEnum> parameter){
        if(!this.acceptedParameters(parameter))
            this.parametersOverload.add(parameter);
    }

    public MethodSymbol(TypeEnum returnType) {
        super(TypeEnum.METHOD);
        this.returnType = returnType;
        this.initialized = true;
        this.addParameter();
    }

    public MethodSymbol(TypeEnum returnType, ArrayList<TypeEnum> parameters) {
        super(TypeEnum.METHOD);
        this.returnType = returnType;
        this.initialized = true;
        this.addParameter(parameters);
    }

    public TypeEnum getReturnType() {
        return returnType;
    }

    public void addParameters(ArrayList<ArrayList<TypeEnum>> parametersOverload){
        for(ArrayList<TypeEnum> parameters : parametersOverload){
            if (parameters.isEmpty()) {
                this.addParameter();
            } else {
                this.addParameter(parameters);
            }
        }
    }

    public boolean acceptedParameters(ArrayList<TypeEnum> arguments){
        if(!arguments.isEmpty())
            return parametersOverload.contains(arguments);

        return parametersOverload.contains(arguments) || parametersOverload.contains(new ArrayList<>(Collections.singleton(TypeEnum.VOID)));
    }

    @Override
    public String toString() {
        return super.toString() + "       \033[1;37mReturn Type\033[0m: " + this.returnType + '\n';
    }
}
