/* Generated By:JJTree: Do not edit this line. ASTAND.java Version 6.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTAND extends TypeSensitive {
  public ASTAND(int id) {
    super(id);
  }

  public ASTAND(Parser p, int id) {
    super(p, id);
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

    if(!this.validType(firstChild, TypeEnum.BOOL, analysis))
      analysis.addError(this.getCoords(), "AND must have left hand side expression returning a boolean.");

    this.initializedUse(firstChild, analysis);

    if(!this.validType(secondChild, TypeEnum.BOOL, analysis))
      analysis.addError(this.getCoords(), "AND must have right hand side expression returning a boolean.");

    this.initializedUse(secondChild, analysis);

  }
}
/* JavaCC - OriginalChecksum=93bee1d279b1b3198c3ca0603cca2ed4 (do not edit this line) */
