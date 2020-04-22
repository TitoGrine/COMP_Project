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
            case ParserTreeConstants.JJTLENGTH:
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
                return this.symbolTable.getSymbol(((ASTIDENT) node).name).getType();
            case ParserTreeConstants.JJTNEW:
                return this.symbolTable.getSymbol(((ASTNEW) node).object).getType();
            case ParserTreeConstants.JJTVOID:
                return TypeEnum.VOID;
            default:
                break;
        }

        return null;
    }

    public Operator(int i) {
        super(i);
    }

    public boolean validType(SimpleNode node, TypeEnum type){

        TypeEnum expType = this.getType(node);

        return expType != null && expType == type;
    }
}
