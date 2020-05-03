/* Generated By:JJTree: Do not edit this line. ASTTYPE.java Version 6.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTTYPE extends SimpleNode {
  public TypeEnum typeID = null;
  public String varName;

  public ASTTYPE(int id) {
    super(id);
  }

  public ASTTYPE(Parser p, int id) {
    super(p, id);
  }

  @Override
  public void eval(SemanticAnalysis analysis){
    if(typeID != null)
      return;

    SimpleNode child = (SimpleNode) this.jjtGetChild(0);

    varName = ((ASTIDENT) child).name;

    Symbol symbol = this.symbolTable.getSymbol(varName);

    if(symbol == null){
      analysis.addError(this.getCoords(), "Unrecognized type " + varName);
      return;
    }

    this.typeID = symbol.getType();
  }

  public String toString() {
    return "TYPE[" + (typeID == null ? "IDENT" : typeID.toString()) + "]";
  }
}
/* JavaCC - OriginalChecksum=29a55f5e39a6079796b397360dc9d240 (do not edit this line) */
