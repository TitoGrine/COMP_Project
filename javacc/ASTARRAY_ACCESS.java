import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/* Generated By:JJTree: Do not edit this line. ASTARRAY_ACCESS.java Version 6.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTARRAY_ACCESS extends TypeSensitive {
  protected String object;

  public ASTARRAY_ACCESS(int id) {
    super(id);
  }

  public ASTARRAY_ACCESS(Parser p, int id) {
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
  public void eval(SemanticAnalysis analysis){
    SimpleNode firstChild = (SimpleNode) this.jjtGetChild(0);
    SimpleNode secondChild = (SimpleNode) this.jjtGetChild(1);

    boolean new_array = compareNode(firstChild, ParserTreeConstants.JJTNEW_ARRAY);

    if(!new_array) {
      if (compareNode(firstChild, ParserTreeConstants.JJTIDENT)) {
        firstChild.addSymbolTable(this.symbolTable);
        this.object = ((ASTIDENT) firstChild).name;
      } else if (compareNode(firstChild, ParserTreeConstants.JJTFUNC_METHOD)) {
        firstChild.addSymbolTable(this.symbolTable);
        firstChild.eval(analysis);

        ASTFUNC_METHOD method = ((ASTFUNC_METHOD) firstChild);

        if(this.symbolTable.existsMethodSymbol(method.call)){
          MethodSymbol methodSymbol = (MethodSymbol) this.symbolTable.getSymbol(method.call);

          if(methodSymbol.getReturnType(method.arguments) != ControlVars.ARRAY)
            analysis.addError(this.getCoords(), "Return of function method isn't an array, but it's being accessed as one.");
          else
            this.object = method.call;
        }
      } else
        analysis.addError(this.getCoords(), "Attempted to access a non array object like an array.");

      if (!this.symbolTable.existsSymbol(this.object)) {

        if (!this.symbolTable.existsSymbol("this." + this.object)) {
          if (this.object != null)
            analysis.addError(this.getCoords(), "Trying to assign variable " + this.object + " that wasn't previously declared.");
        }

        if (this.object != null){
          this.object = "this." + this.object;
        }
      }

      if (!this.validType(firstChild, ControlVars.ARRAY, analysis)) {
        if (this.object != null)
          analysis.addError(this.getCoords(), "Variable " + this.object + " isn't an array but it's being accessed as one.");
      }
    } else
        this.object = "[new int array]";


    this.initializedUse(firstChild, analysis);

    secondChild.addSymbolTable(this.symbolTable);
    secondChild.eval(analysis);

    if(!this.validType(secondChild, ControlVars.INT, analysis)){
      System.out.println("Second child: " + secondChild);
      analysis.addError(this.getCoords(), "Access to array " + this.object + " with invalid index.");
    }

    this.initializedUse(secondChild, analysis);
  }
}
/* JavaCC - OriginalChecksum=22f8f358ed3b439b354e0322ba03ff68 (do not edit this line) */
