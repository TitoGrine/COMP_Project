/* Generated By:JJTree: Do not edit this line. ASTLESSTHAN.java Version 6.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTLESSTHAN extends Operator {

  public ASTLESSTHAN(int id) {
    super(id);
  }

  public ASTLESSTHAN(Parser p, int id) {
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
      throw new Exception("LESSTHAN must have left hand side expression returning an integer.");

    this.initializedUse(firstChild);

    if(!this.validType(secondChild, TypeEnum.INT))
      throw new Exception("LESSTHAN must have right hand side expression returning an integer.");

    this.initializedUse(secondChild);
  }
}
/* JavaCC - OriginalChecksum=e2b1718ad281406bb412afd2b9ecc955 (do not edit this line) */
