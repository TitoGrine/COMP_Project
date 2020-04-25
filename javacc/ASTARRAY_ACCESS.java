/* Generated By:JJTree: Do not edit this line. ASTARRAY_ACCESS.java Version 6.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTARRAY_ACCESS extends Operator {
  protected String object;

  public ASTARRAY_ACCESS(int id) {
    super(id);
  }

  public ASTARRAY_ACCESS(Parser p, int id) {
    super(p, id);
  }

  @Override
  public void eval(SemanticErrors errors){
    SimpleNode firstChild = (SimpleNode) this.jjtGetChild(0);
    SimpleNode secondChild = (SimpleNode) this.jjtGetChild(1);

    if(firstChild.id == ParserTreeConstants.JJTIDENT){
      this.object = ((ASTIDENT) firstChild).name;
    } else if(firstChild.id == ParserTreeConstants.JJTFUNC_METHOD) {
      firstChild.addSymbolTable(this.symbolTable);
      firstChild.eval(errors);

      this.object = ((ASTFUNC_METHOD) firstChild).call;
    } else
      errors.addError(this.getCoords(), "Attempted to access a non array object like an array.");

    if(!this.symbolTable.existsSymbol(this.object)){

      if(!this.symbolTable.existsSymbol("this." + this.object)){
        errors.addError(this.getCoords(), "Trying to assign variable " + this.object + " that wasn't previously declared.");
      }

      this.object = "this." + this.object;
    }

    if(!this.validType(firstChild, TypeEnum.ARRAY, errors))
      errors.addError(this.getCoords(), "Variable " + this.object + " isn't an array but it's being accessed as one.");

    this.initializedUse(firstChild, errors);

    secondChild.addSymbolTable(this.symbolTable);
    secondChild.eval(errors);

    if(!this.validType(secondChild, TypeEnum.INT, errors)){
      System.out.println("Second child: " + secondChild);
      errors.addError(this.getCoords(), "Access to array " + this.object + " with invalid index.");
    }

    this.initializedUse(secondChild, errors);
  }
}
/* JavaCC - OriginalChecksum=22f8f358ed3b439b354e0322ba03ff68 (do not edit this line) */
