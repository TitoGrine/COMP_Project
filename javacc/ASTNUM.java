/* Generated By:JJTree: Do not edit this line. ASTNUM.java Version 6.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTNUM extends SimpleNode {
  public int value;

  public ASTNUM(int id) {
    super(id);
  }

  public ASTNUM(Parser p, int id) {
    super(p, id);
  }

  @Override
  public void eval() {

  }

  public String toString() {
    return "NUM[" + value + "]";
  }
}
/* JavaCC - OriginalChecksum=25da7da480b9539054009c129a22d81e (do not edit this line) */
