public class Operator extends SimpleNode {

    public Operator(Parser p, int id) {
        super(p, id);
    }

    private boolean validateNumeric(SimpleNode node) throws Exception {
        if(node.id == ParserTreeConstants.JJTNUM)
            return true;

        if(node.id == ParserTreeConstants.JJTIDENT){
            String var_name = ((ASTIDENT) node).name;

            Symbol symbol = this.symbolTable.getSymbol(var_name);

            if(symbol.getType() == TypeEnum.INT)
                return true;
        } else if (node.id == ParserTreeConstants.JJTTYPE){
            node.eval();

            if(((ASTTYPE) node).typeID == TypeEnum.INT)
                return true;
        }

        return false;
    }

    private boolean validateLogic(SimpleNode node){
        if(node.id == ParserTreeConstants.JJTBOOL)
            return true;

        if(node.id == ParserTreeConstants.JJTIDENT){
            String var_name = ((ASTIDENT) node).name;

            Symbol symbol = this.symbolTable.getSymbol(var_name);

            if(symbol.getType() == TypeEnum.BOOL)
                return true;
        }

        return false;
    }

    public Operator(int i) {
        super(i);
    }

    public boolean validType(SimpleNode node, int type){

        if(type == ParserTreeConstants.JJTNUM)
            return this.validateNumeric(node);

        if(type == ParserTreeConstants.JJTBOOL)
            return this.validateLogic(node);

        return true;
    }
}
