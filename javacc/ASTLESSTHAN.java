import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/* Generated By:JJTree: Do not edit this line. ASTLESSTHAN.java Version 6.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTLESSTHAN extends TypeSensitive {

  public ASTLESSTHAN(int id) {
    super(id);
  }

  public ASTLESSTHAN(Parser p, int id) {
    super(p, id);
  }

  @Override
  public ArrayList<String> getUses() {
    SimpleNode firstChild = (SimpleNode) this.jjtGetChild(0);
    SimpleNode secondChild = (SimpleNode) this.jjtGetChild(1);

    Set<String> uses = new HashSet<>(firstChild.getUses());
    uses.addAll(secondChild.getUses());

    return new ArrayList<>(uses);
  }

  @Override
  public void addSymbolTable(SymbolTable symbolTable){
    this.symbolTable = symbolTable;
  }

  @Override
  public void eval(SemanticAnalysis analysis){
    SimpleNode firstChild = (SimpleNode) this.jjtGetChild(0);
    SimpleNode secondChild = (SimpleNode) this.jjtGetChild(1);

    firstChild.addSymbolTable(this.symbolTable);
    secondChild.addSymbolTable(this.symbolTable);

    firstChild.eval(analysis);
    secondChild.eval(analysis);

    if(!this.validType(firstChild, ControlVars.INT, analysis))
      analysis.addError(this.getCoords(), "LESSTHAN must have left hand side expression returning an integer.");

    this.initializedUse(firstChild, analysis);

    if(!this.validType(secondChild, ControlVars.INT, analysis))
      analysis.addError(this.getCoords(), "LESSTHAN must have right hand side expression returning an integer.");

    this.initializedUse(secondChild, analysis);
  }
}
/* JavaCC - OriginalChecksum=e2b1718ad281406bb412afd2b9ecc955 (do not edit this line) */
