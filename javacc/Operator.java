public class Operator extends SimpleNode {

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
                return TypeEnum.INT;
            case ParserTreeConstants.JJTAND:
            case ParserTreeConstants.JJTLESSTHAN:
            case ParserTreeConstants.JJTNEGATION:
            case ParserTreeConstants.JJTBOOL:
                return TypeEnum.BOOL;
            case ParserTreeConstants.JJTFUNC_METHOD:
                return null;
            case ParserTreeConstants.JJTARRAY_ACCESS:
                return null;
            case ParserTreeConstants.JJTIDENT:
                Symbol symbol = this.symbolTable.getSymbol(((ASTIDENT) node).name);
                return symbol.getType();
            case ParserTreeConstants.JJTNEW:
                return null;
            default:
                break;
        }

        return null;
    }

    public Operator(int i) {
        super(i);
    }

    public boolean validType(SimpleNode node, TypeEnum type){

        return this.getType(node) == type;
    }
}
