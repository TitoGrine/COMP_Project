/* Generated By:JJTree: Do not edit this line. ASTIDENT.java Version 6.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTIDENT extends SimpleNode {
  public String name;

  public ASTIDENT(int id) {
    super(id);
  }

  public ASTIDENT(Parser p, int id) {
    super(p, id);
  }

  @Override
  public void eval() {
  }

  public String toString() {
    return "IDENT[" + (name == null ? "" : name) + "]";
  }
}
/* JavaCC - OriginalChecksum=51e214b95606552ad63a32c1fc88d236 (do not edit this line) */
