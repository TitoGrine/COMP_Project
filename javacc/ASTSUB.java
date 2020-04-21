/* Generated By:JJTree: Do not edit this line. ASTSUB.java Version 6.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTSUB extends Operator {

  public ASTSUB(int id) {
    super(id);
  }

  public ASTSUB(Parser p, int id) {
    super(p, id);
  }

  @Override
  public void addSymbolTable(SymbolTable symbolTable){
    this.symbolTable = symbolTable;
  }

  @Override
  public void eval() throws Exception {
    int numChildren = this.jjtGetNumChildren();

    if(numChildren != 2)
      throw new Exception("SUB must have two children.");

    SimpleNode firstChild = (SimpleNode) this.jjtGetChild(0);
    SimpleNode secondChild = (SimpleNode) this.jjtGetChild(1);

    firstChild.addSymbolTable(this.symbolTable);
    secondChild.addSymbolTable(this.symbolTable);

    firstChild.eval();
    secondChild.eval();

    if(!this.validType(firstChild, TypeEnum.INT))
      throw new Exception("SUB must have left hand side expression of type INT.");
    if(!this.validType(secondChild, TypeEnum.INT))
      throw new Exception("SUB must have right hand side expression of type INT.");
  }
}
/* JavaCC - OriginalChecksum=98d1976b6139d7188e8c28dcbea08ceb (do not edit this line) */
