public class Operator extends SimpleNode {

    public Operator(Parser p, int id) {
        super(p, id);
    }

    private TypeEnum getType(SimpleNode node){
        switch(node.id){
            case ParserTreeConstants.JJTADD:
            case ParserTreeConstants.JJTSUB:
            case ParserTreeConstants.JJTMUL:
            case ParserTreeConstants.JJTDIV:
            case ParserTreeConstants.JJTNUM:
                return TypeEnum.INT;
            case ParserTreeConstants.JJTAND:
            case ParserTreeConstants.JJTLESSTHAN:
            case ParserTreeConstants.JJTNEGATION:
            case ParserTreeConstants.JJTBOOL:
                return TypeEnum.BOOL;
            case ParserTreeConstants.JJTFUNC_METHOD:
                return ((ASTFUNC_METHOD) node).type;
            case ParserTreeConstants.JJTARRAY_ACCESS:
                break;
            case ParserTreeConstants.JJTIDENT:
                Symbol symbol = this.symbolTable.getSymbol(((ASTIDENT) node).name);
                return symbol.getType();
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
