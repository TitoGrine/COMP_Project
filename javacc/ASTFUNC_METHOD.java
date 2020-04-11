/* Generated By:JJTree: Do not edit this line. ASTFUNCMETHOD.java Version 6.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTFUNC_METHOD extends SimpleNode {

  public ASTFUNC_METHOD(int id) {
    super(id);
  }

  public ASTFUNC_METHOD(Parser p, int id) {
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
      throw new Exception("ARRAY_ACCESS has an invalid number of children.");

    SimpleNode firstChild = (SimpleNode) this.jjtGetChild(0);
    SimpleNode secondChild = (SimpleNode) this.jjtGetChild(1);

    firstChild.addSymbolTable(this.symbolTable);
    firstChild.eval();

    secondChild.addSymbolTable(this.symbolTable);
    secondChild.eval();
  }
}
/* JavaCC - OriginalChecksum=54a9f6d33a5022ecd7338879131cb817 (do not edit this line) */
