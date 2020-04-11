/* Generated By:JJTree: Do not edit this line. ASTSCOPE.java Version 6.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTSCOPE extends SimpleNode {
  public ASTSCOPE(int id) {
    super(id);
  }

  public ASTSCOPE(Parser p, int id) {
    super(p, id);
  }

  @Override
  public void addSymbolTable(SymbolTable symbolTable){
    this.symbolTable = new SymbolTable(symbolTable);
  }

  @Override
  public void eval() throws Exception {
    // TODO: Add symbol

    int numChildren = this.jjtGetNumChildren();
    int childIndex = 0;
    SimpleNode childNode;

    while(childIndex < numChildren){
      childNode = (SimpleNode) this.jjtGetChild(childIndex);

      childNode.addSymbolTable(this.symbolTable);
      childNode.eval();

      childIndex++;
    }
  }
}
/* JavaCC - OriginalChecksum=3a5f0f41a53986f6a6fb7874b1e64952 (do not edit this line) */
