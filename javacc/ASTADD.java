import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/* Generated By:JJTree: Do not edit this line. ASTADD.java Version 6.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTADD extends TypeSensitive {
  public ASTADD(int id) {
    super(id);
  }

  public ASTADD(Parser p, int id) {
    super(p, id);
  }

  @Override
  public ArrayList<String> getUses() {
    SimpleNode firstChild = (SimpleNode) this.jjtGetChild(0);
    SimpleNode secondChild = (SimpleNode) this.jjtGetChild(1);

    Set<String> uses = new HashSet<>(firstChild.getUses());
    uses.addAll(secondChild.getUses());

    uses.removeIf(var -> !this.symbolTable.existsSymbol(var));

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
      analysis.addError(this.getCoords(), "ADD must have left hand side expression returning an integer.");

    this.initializedUse(firstChild, analysis);

    if(!this.validType(secondChild, ControlVars.INT, analysis))
      analysis.addError(this.getCoords(), "ADD must have right hand side expression returning an integer.");

    this.initializedUse(secondChild, analysis);
  }
}
/* JavaCC - OriginalChecksum=d4240bbc69b92e6c5c083af45168f306 (do not edit this line) */
