/* Generated By:JJTree: Do not edit this line. ASTRETURN.java Version 6.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTRETURN extends SimpleNode {
  TypeEnum type;

  public ASTRETURN(int id) {
    super(id);
  }

  public ASTRETURN(Parser p, int id) {
    super(p, id);
  }

  @Override
  public void eval(SemanticAnalysis analysis){
    ASTTYPE childNode = (ASTTYPE) this.jjtGetChild(0);

    childNode.addSymbolTable(this.symbolTable);
    childNode.eval(analysis);

    this.type = childNode.typeID;
  }
}
/* JavaCC - OriginalChecksum=cbb0390d4299ac5f8d468f97868a53a4 (do not edit this line) */
