/* Generated By:JJTree: Do not edit this line. ASTVARIABLE.java Version 6.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTVARIABLE extends SimpleNode {
  protected boolean classScope = false;

  public ASTVARIABLE(int id) {
    super(id);
  }

  public ASTVARIABLE(Parser p, int id) {
    super(p, id);
  }

  @Override
  public void eval(SemanticErrors errors){
    SimpleNode firstChild = (SimpleNode) this.jjtGetChild(0);
    ASTIDENT secondChild = (ASTIDENT) this.jjtGetChild(1);

    TypeEnum type;

    if(firstChild.id == ParserTreeConstants.JJTIDENT){
      String name = ((ASTIDENT) firstChild).name;

      Symbol symbol = this.symbolTable.getSymbol(name);

      if(symbol == null){
        errors.addError(this.getCoords(), "Unrecognized type " + name + ".");
        return;
      }

      type = symbol.getType();
    } else {
      firstChild.addSymbolTable(this.symbolTable);
      firstChild.eval(errors);

      type = ((ASTTYPE) firstChild).typeID;
    }

    String key = (classScope ? "this." : "") + secondChild.name;

    if(this.symbolTable.existsSymbol(key)){
      errors.addError(this.getCoords(), "Variable " + key + " already declared.");
      return;
    }

    if(type == TypeEnum.ARRAY)
      this.symbolTable.addSymbol(key, new ArraySymbol(TypeEnum.INT));
    else
      this.symbolTable.addSymbol(key, new Symbol(type));
  }
}
/* JavaCC - OriginalChecksum=f4f029f02b50c27d11f0009ca087d42c (do not edit this line) */
