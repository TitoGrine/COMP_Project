/* Generated By:JJTree: Do not edit this line. ASTADD.java Version 6.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTADD extends Operator {
  public ASTADD(int id) {
    super(id);
  }

  public ASTADD(Parser p, int id) {
    super(p, id);
  }

  @Override
  public void addSymbolTable(SymbolTable symbolTable){
    this.symbolTable = symbolTable;
  }

  @Override
  public void eval() throws Exception {
    SimpleNode firstChild = (SimpleNode) this.jjtGetChild(0);
    SimpleNode secondChild = (SimpleNode) this.jjtGetChild(1);

    firstChild.addSymbolTable(this.symbolTable);
    secondChild.addSymbolTable(this.symbolTable);

    firstChild.eval();
    secondChild.eval();

    if(!this.validType(firstChild, TypeEnum.INT))
      throw new Exception("ADD must have left hand side expression of type INT.");
    if(!this.validType(secondChild, TypeEnum.INT))
      throw new Exception("ADD must have right hand side expression of type INT.");
  }
}
/* JavaCC - OriginalChecksum=d4240bbc69b92e6c5c083af45168f306 (do not edit this line) */
