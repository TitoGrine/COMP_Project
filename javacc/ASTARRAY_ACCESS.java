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
  public void eval(SemanticAnalysis analysis){
    SimpleNode firstChild = (SimpleNode) this.jjtGetChild(0);
    SimpleNode secondChild = (SimpleNode) this.jjtGetChild(1);

    if(compareNode(firstChild, ParserTreeConstants.JJTIDENT)){
      this.object = ((ASTIDENT) firstChild).name;
    } else if(compareNode(firstChild, ParserTreeConstants.JJTFUNC_METHOD)) {
      firstChild.addSymbolTable(this.symbolTable);
      firstChild.eval(analysis);

      this.object = ((ASTFUNC_METHOD) firstChild).call;
    } else
      analysis.addError(this.getCoords(), "Attempted to access a non array object like an array.");

    if(!this.symbolTable.existsSymbol(this.object)){

      if(!this.symbolTable.existsSymbol("this." + this.object)){
        if(this.object != null)
          analysis.addError(this.getCoords(), "Trying to assign variable " + this.object + " that wasn't previously declared.");
      }
      if(this.object != null)
        this.object = "this." + this.object;
    }

    if(!this.validType(firstChild, TypeEnum.ARRAY, analysis)){
      if(this.object != null)
        analysis.addError(this.getCoords(), "Variable " + this.object + " isn't an array but it's being accessed as one.");
    }

    this.initializedUse(firstChild, analysis);

    secondChild.addSymbolTable(this.symbolTable);
    secondChild.eval(analysis);

    if(!this.validType(secondChild, TypeEnum.INT, analysis)){
      System.out.println("Second child: " + secondChild);
      analysis.addError(this.getCoords(), "Access to array " + this.object + " with invalid index.");
    }

    this.initializedUse(secondChild, analysis);
  }
}
/* JavaCC - OriginalChecksum=22f8f358ed3b439b354e0322ba03ff68 (do not edit this line) */
