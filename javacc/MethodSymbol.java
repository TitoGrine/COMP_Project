import java.util.ArrayList;
import java.util.Collections;

public class MethodSymbol extends Symbol{
    ArrayList<TypeEnum> returnType = new ArrayList<>();
    ArrayList<ArrayList<TypeEnum>> parametersOverload = new ArrayList<>();

    private void addParameter(TypeEnum returnType){
        ArrayList<TypeEnum> parameters = new ArrayList<>(Collections.singleton(TypeEnum.VOID));

        addParameter(parameters, returnType);
    }

    private void addParameter(ArrayList<TypeEnum> parameter, TypeEnum returnType){
        if(!this.acceptedParameters(parameter)){
            this.returnType.add(returnType);
            this.parametersOverload.add(parameter);
        }
    }

    public MethodSymbol(TypeEnum returnType) {
        super(TypeEnum.METHOD);
        this.addParameter(returnType);
    }

    public MethodSymbol(TypeEnum returnType, ArrayList<TypeEnum> parameters) {
        super(TypeEnum.METHOD);
        this.addParameter(parameters, returnType);
    }

    public TypeEnum getReturnType(ArrayList<TypeEnum> parameters) {
        int index = this.parametersOverload.indexOf(parameters);

        if(index == -1)
            return null;

        return returnType.get(index);
    }

    public void addParameters(ArrayList<TypeEnum> parameters, TypeEnum returnType){
        if(parameters.isEmpty())
            this.addParameter(returnType);
        else
            this.addParameter(parameters, returnType);
    }

    public boolean repeatedMethod(TypeEnum returnType, ArrayList<TypeEnum> arguments){
        return acceptedParameters(arguments) && returnType != this.getReturnType(arguments);
    }

    public boolean acceptedParameters(ArrayList<TypeEnum> arguments){
        if(!arguments.isEmpty())
            return parametersOverload.contains(arguments);

        return parametersOverload.contains(arguments) || parametersOverload.contains(new ArrayList<>(Collections.singleton(TypeEnum.VOID)));
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
        return ControlVars.WHITE_BOLD + (this.type == null ? "" : "       " + "Type: " + ControlVars.RESET + this.type.toString() + '\n') + "       " + ControlVars.WHITE_BOLD + "Return Type:" + ControlVars.RESET + this.returnType + '\n';
    }
}
