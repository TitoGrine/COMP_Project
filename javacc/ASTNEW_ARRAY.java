/* Generated By:JJTree: Do not edit this line. ASTNEW_ARRAY.java Version 6.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTNEW_ARRAY extends TypeSensitive {
  public ASTNEW_ARRAY(int id) {
    super(id);
  }

  public ASTNEW_ARRAY(Parser p, int id) {
    super(p, id);
  }

  @Override
  public void eval(SemanticAnalysis analysis){
    SimpleNode childNode = (SimpleNode) this.jjtGetChild(0);

    childNode.addSymbolTable(this.symbolTable);
    childNode.eval(analysis);

    if(!this.validType(childNode, ControlVars.INT, analysis))
      analysis.addError(this.getCoords(), "Integer array initialized with invalid size.");
  }
}
/* JavaCC - OriginalChecksum=5b8f02a40100dbc10f6cbbb183dccada (do not edit this line) */
