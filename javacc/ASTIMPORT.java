/* Generated By:JJTree: Do not edit this line. ASTIMPORT.java Version 6.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTIMPORT extends SimpleNode {
  public String name;

  public ASTIMPORT(int id) {
    super(id);
  }

  public ASTIMPORT(Parser p, int id) {
    super(p, id);
  }

  public String toString() {
    return "IMPORT[" + name + "]";
  }
}
/* JavaCC - OriginalChecksum=88acf2e261b5ed8d1318a9e9015ad173 (do not edit this line) */
