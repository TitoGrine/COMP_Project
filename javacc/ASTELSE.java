/* Generated By:JJTree: Do not edit this line. ASTELSE.java Version 6.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTELSE extends SimpleNode {
  public ASTELSE(int id) {
    super(id);
  }

  public ASTELSE(Parser p, int id) {
    super(p, id);
  }

  @Override
  public void eval(SemanticAnalysis analysis){
    SimpleNode firstChild = (SimpleNode) this.jjtGetChild(0);

    if(firstChild.id == ParserTreeConstants.JJTASSIGN){
      ((ASTASSIGN) firstChild).unstableVar = true;
      ((ASTASSIGN) firstChild).elseScope = true;
    }
    else if (firstChild.id == ParserTreeConstants.JJTSCOPE){
      ((ASTSCOPE) firstChild).unstableScope = true;
      ((ASTSCOPE) firstChild).elseScope = true;

    }

    firstChild.addSymbolTable(this.symbolTable);
    firstChild.eval(analysis);
  }
}
/* JavaCC - OriginalChecksum=ee889ff077a4e76bc82114aaed4651e5 (do not edit this line) */
