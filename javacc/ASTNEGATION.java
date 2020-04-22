/* Generated By:JJTree: Do not edit this line. ASTNEGATION.java Version 6.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTNEGATION extends Operator {

  public ASTNEGATION(int id) {
    super(id);
  }

  public ASTNEGATION(Parser p, int id) {
    super(p, id);
  }

  @Override
  public void addSymbolTable(SymbolTable symbolTable){
    this.symbolTable = symbolTable;
  }

  @Override
  public void eval() throws Exception {
    SimpleNode childNode = (SimpleNode) this.jjtGetChild(0);

    childNode.addSymbolTable(this.symbolTable);
    childNode.eval();

    if(!this.validType(childNode, TypeEnum.BOOL))
      throw new Exception("NEGATION of a non boolean type.");
  }
}
/* JavaCC - OriginalChecksum=4ffa54cb323c32b97a8d1dfeae6338b8 (do not edit this line) */
