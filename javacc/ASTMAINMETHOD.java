/* Generated By:JJTree: Do not edit this line. ASTMAINMETHOD.java Version 6.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTMAINMETHOD extends SimpleNode {
  public ASTMAINMETHOD(int id) {
    super(id);
  }

  public ASTMAINMETHOD(Parser p, int id) {
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

    if(numChildren != 4)
      throw new Exception("METHOD has an invalid number of children");

    SimpleNode childNode1 = (SimpleNode) this.jjtGetChild(0);
    SimpleNode childNode2 = (SimpleNode) this.jjtGetChild(1);

    if(childNode1.id == ParserTreeConstants.JJTIDENT){
      childNode1.addSymbolTable(this.symbolTable);
      childNode1.eval();
    } else {
      throw new Exception("MAIN_METHOD doesn't have the array IDENT node in the correct place or at all.");
    }

    if(childNode2.id == ParserTreeConstants.JJTMETHOD_BODY){
      childNode2.addSymbolTable(this.symbolTable);
      childNode2.eval();
    } else {
      throw new Exception("MAIN_METHOD doesn't have the METHOD_BODY node in the correct place or at all.");
    }
  }
}
/* JavaCC - OriginalChecksum=929a511142c3eb4332a56f5c02fad9ec (do not edit this line) */
