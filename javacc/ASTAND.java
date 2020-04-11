/* Generated By:JJTree: Do not edit this line. ASTAND.java Version 6.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTAND extends SimpleNode {
  public ASTAND(int id) {
    super(id);
  }

  public ASTAND(Parser p, int id) {
    super(p, id);
  }

  @Override
  public void addSymbolTable(SymbolTable symbolTable){
    this.symbolTable = symbolTable;
  }

  @Override
  public void eval() throws Exception {
    // TODO: Add symbol

    int numChildren = this.jjtGetNumChildren();

    if(numChildren != 2)
      throw new Exception("AND must have two children.");

    SimpleNode firstChild = (SimpleNode) this.jjtGetChild(0);
    SimpleNode secondChild = (SimpleNode) this.jjtGetChild(1);

    firstChild.addSymbolTable(this.symbolTable);
    firstChild.eval();

    secondChild.addSymbolTable(this.symbolTable);
    secondChild.eval();
  }
}
/* JavaCC - OriginalChecksum=93bee1d279b1b3198c3ca0603cca2ed4 (do not edit this line) */
