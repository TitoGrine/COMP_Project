public class TypeSensitive extends SimpleNode {

    public TypeSensitive(int i) {
        super(i);
    }

    public TypeSensitive(Parser p, int id) {
        super(p, id);
    }

    public TypeEnum getType(SimpleNode node, SemanticAnalysis analysis){
        switch(node.id){
            case ParserTreeConstants.JJTADD:
            case ParserTreeConstants.JJTSUB:
            case ParserTreeConstants.JJTMUL:
            case ParserTreeConstants.JJTDIV:
            case ParserTreeConstants.JJTNUM:
            case ParserTreeConstants.JJTLENGTH:
                return TypeEnum.INT;
            case ParserTreeConstants.JJTAND:
            case ParserTreeConstants.JJTLESSTHAN:
            case ParserTreeConstants.JJTNEGATION:
            case ParserTreeConstants.JJTBOOL:
                return TypeEnum.BOOL;
            case ParserTreeConstants.JJTVOID:
                return TypeEnum.VOID;
            case ParserTreeConstants.JJTNEW_ARRAY:
                return TypeEnum.ARRAY;
            case ParserTreeConstants.JJTFUNC_METHOD:
                String call = ((ASTFUNC_METHOD) node).call;

                if(!this.symbolTable.existsMethodSymbol(call))
                    return null;

                MethodSymbol methodSymbol = (MethodSymbol) this.symbolTable.getSymbol(call);

                if(methodSymbol == null) // TODO: Check if the identifier is from the class.
                    return null;

                return methodSymbol.getReturnType();
            case ParserTreeConstants.JJTARRAY_ACCESS:
                String object = ((ASTARRAY_ACCESS) node).object;

                if(!this.symbolTable.existsArraySymbol(object) && !this.symbolTable.existsArraySymbol("this." + object)){
                    if(object != null && !this.symbolTable.existsSymbol(object) && !this.symbolTable.existsSymbol("this." + object))
                        analysis.addError(this.getCoords(), "Variable " + object + " used, but isn't previously declared.");

                    return null;
                }

                ArraySymbol arraySymbol = (ArraySymbol) this.symbolTable.getSymbol(object);

                if(arraySymbol == null)
                    arraySymbol = (ArraySymbol) this.symbolTable.getSymbol("this." + object);

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

                if(symbol == null)
                    symbol = this.symbolTable.getSymbol("this." + name);

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
        if(!ControlVars.ANALYSE_VAR_INIT)
            return;

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

                if(arraySymbol == null)
                    arraySymbol = (ArraySymbol) this.symbolTable.getSymbol("this." + object);

                if(arraySymbol == null)
                    return;

                if(!arraySymbol.isInitialized()){
                    analysis.addWarning(this.getCoords(), "Array " + object + "[] used, but isn't previously initialized.");
                    return;
                }

                break;
            case ParserTreeConstants.JJTIDENT:
                String name = ((ASTIDENT) node).name;

                if(name == null || (!this.symbolTable.existsSymbol(name) && !this.symbolTable.existsSymbol("this." + name)))
                    return;

                Symbol symbol = this.symbolTable.getSymbol(name);

                if(symbol == null)
                    symbol = this.symbolTable.getSymbol("this." + name);

                if(symbol == null)
                    return;

                if(!symbol.isInitialized()){
                    analysis.addWarning(this.getCoords(), "Variable " + name + " used, but isn't previously initialized.");
                    return;
                }

                break;
            default:
                analysis.addError(this.getCoords(), "Unrecognized node.");
        }
    }

    public boolean validType(SimpleNode node, TypeEnum type, SemanticAnalysis analysis){

        TypeEnum expType = this.getType(node, analysis);

        return expType != null && expType == type;
    }
}