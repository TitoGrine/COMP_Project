import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/* Generated By:JJTree: Do not edit this line. ASTSCOPE.java Version 6.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTSCOPE extends SimpleNode {
  protected ArrayList<String> scopeStack = null;

  public ASTSCOPE(int id) {
    super(id);
  }

  public ASTSCOPE(Parser p, int id) {
    super(p, id);
  }

  @Override
  public void eval(SemanticAnalysis analysis){
    int numChildren = this.jjtGetNumChildren();
    int childIndex = 0;
    SimpleNode childNode;

    ArrayList<String> stack;

    while(childIndex < numChildren){
      childNode = (SimpleNode) this.jjtGetChild(childIndex);

      if(scopeStack != null && compareNode(childNode, ParserTreeConstants.JJTASSIGN))
        ((ASTASSIGN) childNode).scopeStack = this.scopeStack;
      else if (scopeStack != null && compareNode(childNode, ParserTreeConstants.JJTSCOPE))
        ((ASTSCOPE) childNode).scopeStack = this.scopeStack;

      childNode.addSymbolTable(this.symbolTable);
      childNode.eval(analysis);

      if (compareNode(childNode, ParserTreeConstants.JJTIF_ELSE)){
        stack = ((ASTIF_ELSE) childNode).localStack;
        this.scopeStack.addAll(stack);
      }

      childIndex++;
    }

    Set<String> auxSet = new HashSet<>(this.scopeStack);
    scopeStack.clear();
    scopeStack.addAll(auxSet);
  }
}
/* JavaCC - OriginalChecksum=3a5f0f41a53986f6a6fb7874b1e64952 (do not edit this line) */
