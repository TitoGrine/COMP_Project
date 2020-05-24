import java.util.ArrayList;
import java.util.Collections;

public class MethodSymbol extends Symbol{
    ArrayList<String> returnType = new ArrayList<>();
    ArrayList<ArrayList<String>> parametersOverload = new ArrayList<>();
    ArrayList<Boolean> staticValues = new ArrayList<>();

    private void addParameter(String returnType, boolean staticValue){
        ArrayList<String> parameters = new ArrayList<>();
        parameters.add(ControlVars.VOID);

        addParameter(parameters, returnType, staticValue);
    }

    private void addParameter(ArrayList<String> parameter, String returnType, boolean staticValue){
        if(!this.acceptedParameters(parameter)){
            this.returnType.add(returnType);
            this.parametersOverload.add(parameter);
            this.staticValues.add(staticValue);
        }
    }

    private boolean existsParameters(ArrayList<String> parameters){
        for(ArrayList<String> acceptedParameters : parametersOverload){
            if(acceptedParameters.size() == parameters.size()){
                boolean match = true;

                for(int i = 0; i < acceptedParameters.size(); i++){
                    if(!acceptedParameters.get(i).equals(parameters.get(i))){
                        match = false;
                        break;
                    }
                }

                if(match)
                    return true;
            }
        }

        return false;
    }

    public MethodSymbol(String returnType, ArrayList<String> parameters, boolean staticValue) {
        super(ControlVars.METHOD);

        if(parameters.isEmpty()){
            parameters = new ArrayList<>();
            parameters.add(ControlVars.VOID);
        }

        this.addParameter(parameters, returnType, staticValue);
    }

    public MethodSymbol(String returnType, ArrayList<String> parameters) {
        this(returnType, parameters, false);
    }

    public String getReturnType(ArrayList<String> parameters) {
        if(parameters.isEmpty()){
            parameters = new ArrayList<>();
            parameters.add(ControlVars.VOID);
        }

        int index = this.parametersOverload.indexOf(parameters);

        if(index == -1)
            return null;

        return returnType.get(index);
    }

    public boolean isStatic(ArrayList<String> parameters) {
        if(parameters.isEmpty()){
            parameters = new ArrayList<>();
            parameters.add(ControlVars.VOID);
        }

        int index = this.parametersOverload.indexOf(parameters);

        if(index == -1)
            return false;

        return staticValues.get(index);
    }


    public void addParameters(ArrayList<String> parameters, String returnType){
        this.addParameters(parameters, returnType, false);
    }

    public void addParameters(ArrayList<String> parameters, String returnType, boolean staticValue){
        if(parameters.isEmpty())
            this.addParameter(returnType, staticValue);
        else
            this.addParameter(parameters, returnType, staticValue);
    }

    public boolean repeatedMethod(String returnType, ArrayList<String> arguments){
        return acceptedParameters(arguments);
    }

    public boolean acceptedParameters(ArrayList<String> arguments){
        if(!arguments.isEmpty())
            return this.existsParameters(arguments);

        return this.existsParameters(arguments) || this.existsParameters(new ArrayList<>(Collections.singleton(ControlVars.VOID)));
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
