/* Generated By:JJTree: Do not edit this line. ASTARGUMENTS.java Version 6.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTARGUMENTS extends Operator {
  protected TypeEnum[] arguments;

  public ASTARGUMENTS(int id) {
    super(id);
  }

  public ASTARGUMENTS(Parser p, int id) {
    super(p, id);
  }

  @Override
  public void addSymbolTable(SymbolTable symbolTable){
    this.symbolTable = new SymbolTable(symbolTable);
  }

  @Override
  public void eval() throws Exception {
    int numChildren = this.jjtGetNumChildren();
    int childIndex = 0;
    SimpleNode childNode;

    this.arguments = new TypeEnum[numChildren];

    if(!(numChildren > 0))
      throw new Exception("ARGUMENTS must have at least one child.");

    while(childIndex < numChildren){
      childNode = (SimpleNode) this.jjtGetChild(childIndex);

      childNode.addSymbolTable(this.symbolTable);
      childNode.eval();

      if(childNode.id != ParserTreeConstants.JJTARGUMENT)
        this.arguments[childIndex] = this.getType(childNode);

      childIndex++;
    }
  }
}
/* JavaCC - OriginalChecksum=4206cf5195297e62078a7d896983dc2e (do not edit this line) */
