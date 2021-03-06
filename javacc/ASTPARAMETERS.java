import java.util.ArrayList;

/* Generated By:JJTree: Do not edit this line. ASTPARAMETERS.java Version 6.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTPARAMETERS extends SimpleNode {
  ArrayList<String> parameters = new ArrayList<>();

  public ASTPARAMETERS(int id) {
    super(id);
  }

  public ASTPARAMETERS(Parser p, int id) {
    super(p, id);
  }

  @Override
  public void eval(SemanticAnalysis analysis){
    int numChildren = this.jjtGetNumChildren();
    int childIndex = 0;

    while(childIndex < numChildren){
      ASTTYPE childNode = (ASTTYPE) this.jjtGetChild(childIndex);

      childNode.addSymbolTable(this.symbolTable);
      childNode.eval(analysis);

      parameters.add(childNode.typeID);

      childIndex++;
    }
  }
}
/* JavaCC - OriginalChecksum=786a5aaeeb56b7ccb7dd033bb25a5a3a (do not edit this line) */
