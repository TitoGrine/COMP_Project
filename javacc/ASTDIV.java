/* Generated By:JJTree: Do not edit this line. ASTDIV.java Version 6.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTDIV extends Operator {

  public ASTDIV(int id) {
    super(id);
  }

  public ASTDIV(Parser p, int id) {
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
      throw new Exception("DIV must have left hand side expression returning an integer.");

    this.initializedUse(firstChild);

    if(!this.validType(secondChild, TypeEnum.INT))
      throw new Exception("DIV must have right hand side expression returning an integer.");

    this.initializedUse(secondChild);
  }
}
/* JavaCC - OriginalChecksum=7c66fef492265519258f8aeb8df13d5f (do not edit this line) */
