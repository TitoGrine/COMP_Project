/* Generated By:JJTree: Do not edit this line. ASTTHIS.java Version 6.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTTHIS extends SimpleNode {
  protected String className;

  public ASTTHIS(int id) {
    super(id);
  }

  public ASTTHIS(Parser p, int id) {
    super(p, id);
  }

  @Override
  public void eval(SemanticErrors errors){
    SimpleNode parentNode = (SimpleNode) this.jjtGetParent();
    boolean foundClass = false;

    while(!foundClass){
      if(parentNode.id == ParserTreeConstants.JJTCLASS){
        className = ((ASTCLASS) parentNode).className;
        foundClass = true;
      } else {
        parentNode = (SimpleNode) parentNode.jjtGetParent();
      }
    }
  }
}
/* JavaCC - OriginalChecksum=cc9ff3a6217769746e01f0b278090457 (do not edit this line) */
