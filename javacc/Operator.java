public class Operator extends SimpleNode {

    public Operator(int i) {
        super(i);
    }

    public Operator(Parser p, int id) {
        super(p, id);
    }

    public TypeEnum getType(SimpleNode node) throws Exception {
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
                return ((MethodSymbol) this.symbolTable.getSymbol(((ASTFUNC_METHOD) node).call)).getReturnType();
            case ParserTreeConstants.JJTARRAY_ACCESS:
                String object = ((ASTARRAY_ACCESS) node).object;

                ArraySymbol arraySymbol = (ArraySymbol) this.symbolTable.getSymbol(object);

                if(arraySymbol == null)
                    arraySymbol = (ArraySymbol) this.symbolTable.getSymbol("this." + object);

                if(arraySymbol == null)
                    throw new Exception("Array " + object + "[] used, but isn't previously declared.");

                return arraySymbol.getReturnType();
            case ParserTreeConstants.JJTIDENT:
                String name = ((ASTIDENT) node).name;

                Symbol symbol = this.symbolTable.getSymbol(name);

                if(symbol == null)
                    symbol = this.symbolTable.getSymbol("this." + name);

                if(symbol == null)
                    throw new Exception("Variable " + name + " used, but isn't previously declared.");

                return symbol.getType();
            case ParserTreeConstants.JJTNEW:
                return this.symbolTable.getSymbol(((ASTNEW) node).object).getType();
            default:
                break;
        }

        return null;
    }

    public void initializedUse(SimpleNode node) throws Exception {
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
                    throw new Exception("Method " + ((ASTFUNC_METHOD) node).call + "() called but isn't initialized.");
                break;
            case ParserTreeConstants.JJTARRAY_ACCESS:
                String object = ((ASTARRAY_ACCESS) node).object;

                ArraySymbol arraySymbol = (ArraySymbol) this.symbolTable.getSymbol(object);

                if(arraySymbol == null)
                    arraySymbol = (ArraySymbol) this.symbolTable.getSymbol("this." + object);

                if(!arraySymbol.isInitialized())
                    throw new Exception("Array " + object + "[] used, but isn't previously initialized.");

                break;
            case ParserTreeConstants.JJTIDENT:
                String name = ((ASTIDENT) node).name;

                Symbol symbol = this.symbolTable.getSymbol(name);

                if(symbol == null)
                    symbol = this.symbolTable.getSymbol("this." + name);

                if(!symbol.isInitialized())
                    throw new Exception("Variable " + name + " used, but isn't previously initialized.");

                break;
            default:
                throw new Exception("Unrecognized node.");
        }
    }

    public boolean validType(SimpleNode node, TypeEnum type) throws Exception {

        TypeEnum expType = this.getType(node);

        return expType != null && expType == type;
    }
}
