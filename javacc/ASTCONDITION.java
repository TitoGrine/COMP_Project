/* Generated By:JJTree: Do not edit this line. ASTCONDITION.java Version 6.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTCONDITION extends Operator {
  public ASTCONDITION(int id) {
    super(id);
  }

  public ASTCONDITION(Parser p, int id) {
    super(p, id);
  }

  @Override
  public void eval(SemanticErrors errors){
    SimpleNode childNode = (SimpleNode) this.jjtGetChild(0);

    childNode.addSymbolTable(this.symbolTable);
    childNode.eval(errors);

    if(!this.validType(childNode, TypeEnum.BOOL, errors))
      errors.addError(this.getCoords(), "Expression in condition doesn't return a boolean value.");
  }
}
/* JavaCC - OriginalChecksum=58fa4c516c1f33d07663e796cee56418 (do not edit this line) */
