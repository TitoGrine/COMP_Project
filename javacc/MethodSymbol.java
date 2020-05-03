import java.util.ArrayList;
import java.util.Collections;

public class MethodSymbol extends Symbol{
    ArrayList<TypeEnum> returnType = new ArrayList<>();
    ArrayList<ArrayList<TypeEnum>> parametersOverload = new ArrayList<>();
    ArrayList<Boolean> staticValues = new ArrayList<>();

    private void addParameter(TypeEnum returnType, boolean staticValue){
        ArrayList<TypeEnum> parameters = new ArrayList<>(Collections.singleton(TypeEnum.VOID));

        addParameter(parameters, returnType, staticValue);
    }

    private void addParameter(ArrayList<TypeEnum> parameter, TypeEnum returnType, boolean staticValue){
        if(!this.acceptedParameters(parameter)){
            this.returnType.add(returnType);
            this.parametersOverload.add(parameter);
            this.staticValues.add(staticValue);
        }
    }

    public MethodSymbol(TypeEnum returnType, ArrayList<TypeEnum> parameters, boolean staticValue) {
        super(TypeEnum.METHOD);

        if(parameters.isEmpty())
            parameters = new ArrayList<>(Collections.singleton(TypeEnum.VOID));

        this.addParameter(parameters, returnType, staticValue);
    }

    public MethodSymbol(TypeEnum returnType, ArrayList<TypeEnum> parameters) {
        this(returnType, parameters, false);
    }

    public TypeEnum getReturnType(ArrayList<TypeEnum> parameters) {
        if(parameters.isEmpty())
            parameters = new ArrayList<>(Collections.singleton(TypeEnum.VOID));

        int index = this.parametersOverload.indexOf(parameters);

        if(index == -1)
            return null;

        return returnType.get(index);
    }

    public boolean isStatic(ArrayList<TypeEnum> parameters) {
        if(parameters.isEmpty())
            parameters = new ArrayList<>(Collections.singleton(TypeEnum.VOID));

        int index = this.parametersOverload.indexOf(parameters);

        if(index == -1)
            return false;

        return staticValues.get(index);
    }


    public void addParameters(ArrayList<TypeEnum> parameters, TypeEnum returnType){
        this.addParameters(parameters, returnType, false);
    }

    public void addParameters(ArrayList<TypeEnum> parameters, TypeEnum returnType, boolean staticValue){
        if(parameters.isEmpty())
            this.addParameter(returnType, staticValue);
        else
            this.addParameter(parameters, returnType, staticValue);
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
