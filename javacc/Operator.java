public class Operator extends SimpleNode {

    public Operator(int i) {
        super(i);
    }

    public Operator(Parser p, int id) {
        super(p, id);
    }

    public TypeEnum getType(SimpleNode node){
        switch(node.id){
            case ParserTreeConstants.JJTADD:
            case ParserTreeConstants.JJTSUB:
            case ParserTreeConstants.JJTMUL:
            case ParserTreeConstants.JJTDIV:
            case ParserTreeConstants.JJTNEW_ARRAY:
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
            case ParserTreeConstants.JJTFUNC_METHOD:
                return ((MethodSymbol) this.symbolTable.getSymbol(((ASTFUNC_METHOD) node).call)).getReturnType();
            case ParserTreeConstants.JJTARRAY_ACCESS:
                ArraySymbol arraySymbol = (ArraySymbol) this.symbolTable.getSymbol(((ASTARRAY_ACCESS) node).object);

                if(arraySymbol == null)
                    arraySymbol = (ArraySymbol) this.symbolTable.getSymbol("this." + ((ASTARRAY_ACCESS) node).object);

                return arraySymbol.getReturnType();
            case ParserTreeConstants.JJTIDENT:
                Symbol symbol = this.symbolTable.getSymbol(((ASTIDENT) node).name);

                if(symbol == null)
                    symbol = this.symbolTable.getSymbol("this." + ((ASTIDENT) node).name);

                return symbol.getType();
            case ParserTreeConstants.JJTNEW:
                return this.symbolTable.getSymbol(((ASTNEW) node).object).getType();
            default:
                break;
        }

        return null;
    }

    public boolean initializedUse(SimpleNode node){
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
                return true;
            case ParserTreeConstants.JJTFUNC_METHOD:
                return this.symbolTable.getSymbol(((ASTFUNC_METHOD) node).call).isInitialized();
            case ParserTreeConstants.JJTARRAY_ACCESS:
                ArraySymbol arraySymbol = (ArraySymbol) this.symbolTable.getSymbol(((ASTARRAY_ACCESS) node).object);

                if(arraySymbol == null)
                    arraySymbol = (ArraySymbol) this.symbolTable.getSymbol("this." + ((ASTARRAY_ACCESS) node).object);

                return arraySymbol.isInitialized();
            case ParserTreeConstants.JJTIDENT:
                Symbol symbol = this.symbolTable.getSymbol(((ASTIDENT) node).name);

                if(symbol == null)
                    symbol = this.symbolTable.getSymbol("this." + ((ASTIDENT) node).name);

                return symbol.isInitialized();
            default:
                break;
        }

        return false;
    }

    public boolean validType(SimpleNode node, TypeEnum type){

        TypeEnum expType = this.getType(node);

        return expType != null && expType == type;
    }
}
