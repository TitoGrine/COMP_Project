/* Generated By:JJTree: Do not edit this line. ASTRETURN_EXP.java Version 6.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTRETURN_EXP extends TypeSensitive {
  String expType = null;

  public ASTRETURN_EXP(int id) {
    super(id);
  }

  public ASTRETURN_EXP(Parser p, int id) {
    super(p, id);
  }

  @Override
  public void eval(SemanticAnalysis analysis){
    SimpleNode childNode = (SimpleNode) this.jjtGetChild(0);

    childNode.addSymbolTable(this.symbolTable);
    childNode.eval(analysis);

    this.initializedUse(childNode, analysis);

    this.expType = this.getType(childNode, analysis);
  }
}
/* JavaCC - OriginalChecksum=8faf53b79b26fa1318455054445c390c (do not edit this line) */
