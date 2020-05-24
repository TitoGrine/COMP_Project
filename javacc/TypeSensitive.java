public class TypeSensitive extends SimpleNode {

    public TypeSensitive(int i) {
        super(i);
    }

    public TypeSensitive(Parser p, int id) {
        super(p, id);
    }

    public String getType(SimpleNode node, SemanticAnalysis analysis){
        switch(node.id){
            case ParserTreeConstants.JJTADD:
            case ParserTreeConstants.JJTSUB:
            case ParserTreeConstants.JJTMUL:
            case ParserTreeConstants.JJTDIV:
            case ParserTreeConstants.JJTNUM:
            case ParserTreeConstants.JJTLENGTH:
                return ControlVars.INT;
            case ParserTreeConstants.JJTAND:
            case ParserTreeConstants.JJTLESSTHAN:
            case ParserTreeConstants.JJTNEGATION:
            case ParserTreeConstants.JJTBOOL:
                return ControlVars.BOOL;
            case ParserTreeConstants.JJTVOID:
                return ControlVars.VOID;
            case ParserTreeConstants.JJTNEW_ARRAY:
                return ControlVars.ARRAY;
            case ParserTreeConstants.JJTFUNC_METHOD:
                ASTFUNC_METHOD funcMethod = ((ASTFUNC_METHOD) node);
                String call = funcMethod.call;

                if(!this.symbolTable.existsMethodSymbol(call)){
                    return null;
                }

                MethodSymbol methodSymbol = (MethodSymbol) this.symbolTable.getSymbol(call);

                if(methodSymbol == null) // TODO: Check if the identifier is from the class.
                    return null;

                return methodSymbol.getReturnType(funcMethod.arguments);
            case ParserTreeConstants.JJTARRAY_ACCESS:
                String object = ((ASTARRAY_ACCESS) node).object;

                if(object != null && object.equals("[new int array]"))
                    return ControlVars.INT;

                if(!this.symbolTable.existsArraySymbol(object) && !this.symbolTable.existsArraySymbol("this." + object)){
                    if(object != null && !this.symbolTable.existsSymbol(object) && !this.symbolTable.existsSymbol("this." + object))
                        analysis.addError(this.getCoords(), "Variable " + object + " used, but isn't previously declared.");

                    if(object != null && this.symbolTable.existsMethodSymbol(object)){
                        return ControlVars.INT;
                    }

                    return null;
                }

                ArraySymbol arraySymbol = (ArraySymbol) this.symbolTable.getSymbol(object);

                if(arraySymbol == null){
                    arraySymbol = (ArraySymbol) this.symbolTable.getSymbol("this." + object);
                }

                if(arraySymbol == null)
                    return null;

                return arraySymbol.getReturnType();
            case ParserTreeConstants.JJTIDENT:
                String name = ((ASTIDENT) node).name;

                if(!this.symbolTable.existsSymbol(name) && !this.symbolTable.existsSymbol("this." + name)){
                    if(name != null)
                        analysis.addError(this.getCoords(), "Variable " + name + " used, but isn't previously declared.");

                    return null;
                }

                Symbol symbol = this.symbolTable.getSymbol(name);

                if(symbol == null){
                    symbol = this.symbolTable.getSymbol("this." + name);
                }

                if(symbol == null)
                    return null;

                return symbol.getType();
            case ParserTreeConstants.JJTNEW:

                String newObject = ((ASTNEW) node).object;

                if(!this.symbolTable.existsSymbol(newObject))
                    return null;

                return this.symbolTable.getSymbol(newObject).getType();
            default:
                break;
        }

        return null;
    }

    public void initializedUse(SimpleNode node, SemanticAnalysis analysis){

        switch(node.id){
            case ParserTreeConstants.JJTADD:
            case ParserTreeConstants.JJTSUB:
            case ParserTreeConstants.JJTMUL:
            case ParserTreeConstants.JJTDIV:
            case ParserTreeConstants.JJTNEW_ARRAY:
            case ParserTreeConstants.JJTNUM:
            case ParserTreeConstants.JJTLENGTH:
            case ParserTreeConstants.JJTAND:
            case ParserTreeConstants.JJTLESSTHAN:
            case ParserTreeConstants.JJTNEGATION:
            case ParserTreeConstants.JJTBOOL:
            case ParserTreeConstants.JJTNEW:
            case ParserTreeConstants.JJTVOID:
            case ParserTreeConstants.JJTFUNC_METHOD:
                break;
            case ParserTreeConstants.JJTARRAY_ACCESS:
                String object = ((ASTARRAY_ACCESS) node).object;

                if(object == null || (!this.symbolTable.existsArraySymbol(object) && !this.symbolTable.existsArraySymbol("this." + object)))
                    return;

                ArraySymbol arraySymbol = (ArraySymbol) this.symbolTable.getSymbol(object);

                if(arraySymbol == null){
                    arraySymbol = (ArraySymbol) this.symbolTable.getSymbol("this." + object);
                }

                if(arraySymbol == null)
                    return;

                if(!arraySymbol.isInitialized()){
                    if(ControlVars.THROW_VAR_INIT_ERROR)
                        if(arraySymbol.isVolatile())
                            analysis.addError(this.getCoords(), "Array " + object + "[] used, but but might not be initialized.");
                        else
                            analysis.addError(this.getCoords(), "Array " + object + "[] used, but isn't previously initialized.");
                    else
                        if(arraySymbol.isVolatile())
                            analysis.addWarning(this.getCoords(), "Array " + object + "[] used, but but might not be initialized.");
                        else
                            analysis.addWarning(this.getCoords(), "Array " + object + "[] used, but isn't previously initialized.");
                    return;
                }

                break;
            case ParserTreeConstants.JJTIDENT:
                String name = ((ASTIDENT) node).name;

                if(name == null || (!this.symbolTable.existsSymbol(name) && !this.symbolTable.existsSymbol("this." + name)))
                    return;

                Symbol symbol = this.symbolTable.getSymbol(name);

                if(symbol == null){
                    symbol = this.symbolTable.getSymbol("this." + name);
                }

                if(symbol == null)
                    return;

                if(!symbol.isInitialized()){
                    if(ControlVars.THROW_VAR_INIT_ERROR){
                        if(symbol.isVolatile())
                            analysis.addError(this.getCoords(), "Variable " + name + " used, but might not be initialized.");
                        else
                            analysis.addError(this.getCoords(), "Variable " + name + " used, but isn't previously initialized.");
                    }
                    else{
                        if(symbol.isVolatile())
                            analysis.addWarning(this.getCoords(), "Variable " + name + " used, but might not be initialized.");
                        else
                            analysis.addWarning(this.getCoords(), "Variable " + name + " used, but isn't previously initialized.");
                    }

                    return;
                }

                break;
            default:
                analysis.addError(this.getCoords(), "Unrecognized node.");
        }
    }

    public boolean validType(SimpleNode node, String type, SemanticAnalysis analysis){

        String expType = this.getType(node, analysis);

        return expType != null && expType.equals(type);
    }
}
