/* Generated By:JJTree: Do not edit this line. ASTMETHOD_BODY.java Version 6.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTMETHOD_BODY extends SimpleNode {
  public ASTMETHOD_BODY(int id) {
    super(id);
  }

  public ASTMETHOD_BODY(Parser p, int id) {
    super(p, id);
  }

  @Override
  public void eval() throws Exception {
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
/* JavaCC - OriginalChecksum=2c8fe84559602b622525e8e9ee1fb873 (do not edit this line) */
