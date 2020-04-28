import java.util.ArrayList;

/* Generated By:JJTree: Do not edit this line. ASTIF.java Version 6.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTIF extends SimpleNode {
  protected ArrayList<String> localStack = new ArrayList<>();

  public ASTIF(int id) {
    super(id);
  }

  public ASTIF(Parser p, int id) {
    super(p, id);
  }

  @Override
  public void eval(SemanticAnalysis analysis){
    ASTCONDITION firstChild = (ASTCONDITION) this.jjtGetChild(0);
    SimpleNode secondChild = (SimpleNode) this.jjtGetChild(1);

    firstChild.addSymbolTable(this.symbolTable);
    firstChild.eval(analysis);

    if(secondChild.id == ParserTreeConstants.JJTASSIGN)
      ((ASTASSIGN) secondChild).scopeStack = localStack;
    else if (secondChild.id == ParserTreeConstants.JJTSCOPE)
      ((ASTSCOPE) secondChild).scopeStack = localStack;

    secondChild.addSymbolTable(this.symbolTable);
    secondChild.eval(analysis);
  }
}
/* JavaCC - OriginalChecksum=21bbd7f7d1c38606ab1a5d16f7da009d (do not edit this line) */
