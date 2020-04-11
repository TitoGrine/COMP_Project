/* Generated By:JJTree: Do not edit this line. ASTPARAMETERS.java Version 6.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTPARAMETERS extends SimpleNode {
  public ASTPARAMETERS(int id) {
    super(id);
  }

  public ASTPARAMETERS(Parser p, int id) {
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
    int childIndex = 0;

    while(childIndex < numChildren){
      SimpleNode childNode = (SimpleNode) this.jjtGetChild(childIndex);

      if(childNode.id != ParserTreeConstants.JJTTYPE)
        throw new Exception("PARAMETERS has children of type different then Type.");

      childNode.addSymbolTable(this.symbolTable);
      childNode.eval();

      childIndex++;
    }
  }
}
/* JavaCC - OriginalChecksum=786a5aaeeb56b7ccb7dd033bb25a5a3a (do not edit this line) */
