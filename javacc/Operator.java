public class Operator extends SimpleNode {

    public Operator(int i) {
        super(i);
    }

    public Operator(Parser p, int id) {
        super(p, id);
    }

    public TypeEnum getType(SimpleNode node, SemanticErrors errors){
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

                System.out.println(call);

                MethodSymbol methodSymbol = (MethodSymbol) this.symbolTable.getSymbol(call);

                if(methodSymbol == null) // TODO: Check if the identifier is from the class.
                    return null;

                return methodSymbol.getReturnType();
            case ParserTreeConstants.JJTARRAY_ACCESS:
                String object = ((ASTARRAY_ACCESS) node).object;

                ArraySymbol arraySymbol = (ArraySymbol) this.symbolTable.getSymbol(object);

                if(arraySymbol == null)
                    arraySymbol = (ArraySymbol) this.symbolTable.getSymbol("this." + object);

                if(arraySymbol == null){
                    errors.addError(this.getCoords(), "Array " + object + "[] used, but isn't previously declared.");
                    return null;
                }

                return arraySymbol.getReturnType();
            case ParserTreeConstants.JJTIDENT:
                String name = ((ASTIDENT) node).name;

                Symbol symbol = this.symbolTable.getSymbol(name);

                if(symbol == null)
                    symbol = this.symbolTable.getSymbol("this." + name);

                if(symbol == null){
                    errors.addError(this.getCoords(), "Variable " + name + " used, but isn't previously declared.");
                    return null;
                }

                return symbol.getType();
            case ParserTreeConstants.JJTNEW:
                return this.symbolTable.getSymbol(((ASTNEW) node).object).getType();
            default:
                break;
        }

        return null;
    }

    public void initializedUse(SimpleNode node, SemanticErrors errors){
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
                break;
            case ParserTreeConstants.JJTFUNC_METHOD:
                if(!this.symbolTable.getSymbol(((ASTFUNC_METHOD) node).call).isInitialized())
                    errors.addError(this.getCoords(), "Method " + ((ASTFUNC_METHOD) node).call + "() called but isn't initialized.");
                break;
            case ParserTreeConstants.JJTARRAY_ACCESS:
                String object = ((ASTARRAY_ACCESS) node).object;

                ArraySymbol arraySymbol = (ArraySymbol) this.symbolTable.getSymbol(object);

                if(arraySymbol == null)
                    arraySymbol = (ArraySymbol) this.symbolTable.getSymbol("this." + object);

                if(arraySymbol == null)
                    return;

                if(!arraySymbol.isInitialized()){
                    errors.addError(this.getCoords(), "Array " + object + "[] used, but isn't previously initialized.");
                    return;
                }

                break;
            case ParserTreeConstants.JJTIDENT:
                String name = ((ASTIDENT) node).name;

                Symbol symbol = this.symbolTable.getSymbol(name);

                if(symbol == null)
                    symbol = this.symbolTable.getSymbol("this." + name);

                if(symbol == null)
                    return;

                if(!symbol.isInitialized()){
                    errors.addError(this.getCoords(), "Variable " + name + " used, but isn't previously initialized.");
                    return;
                }

                break;
            default:
                errors.addError(this.getCoords(), "Unrecognized node.");
        }
    }

    public boolean validType(SimpleNode node, TypeEnum type, SemanticErrors errors){

        TypeEnum expType = this.getType(node, errors);

        return expType != null && expType == type;
    }
}
