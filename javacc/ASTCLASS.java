/* Generated By:JJTree: Do not edit this line. ASTCLASS.java Version 6.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTCLASS extends SimpleNode {
  public String name;

  public ASTCLASS(int id) {
    super(id);
  }

  public ASTCLASS(Parser p, int id) {
    super(p, id);
  }

  @Override
  public void addSymbolTable(SymbolTable symbolTable){
    this.symbolTable = new SymbolTable(symbolTable);
  }

  @Override
  public void eval() throws Exception {
    // TODO: Add symbol

    int numChildren = this.jjtGetNumChildren();
    boolean varsDeclared = false;
    boolean mainDeclared = false;
    int childIndex = 1;

    SimpleNode childNode = (SimpleNode) this.jjtGetChild(0);

    if (childNode.id == ParserTreeConstants.JJTEXTENDS){
      // TODO: Get value?
    } else if (childNode.id == ParserTreeConstants.JJTVARIABLE) {

    } else if (childNode.id == ParserTreeConstants.JJTMETHOD) {
      varsDeclared = true;
    } else if (childNode.id == ParserTreeConstants.JJTMAINMETHOD){
      varsDeclared = true;
      mainDeclared = true;
    } else
      throw new Exception("CLASS has children of type not allowed.");

    childNode.addSymbolTable(this.symbolTable);
    childNode.eval();

    while(childIndex < numChildren){
      childNode = (SimpleNode) this.jjtGetChild(childIndex);

      if (childNode.id == ParserTreeConstants.JJTEXTENDS){
        throw new Exception("CLASS has EXTENDS in the wrong place.");
      } else if (childNode.id == ParserTreeConstants.JJTVARIABLE) {
        if(varsDeclared)
          throw new Exception("CLASS has variable declaration after methods have been declared.");
      } else if (childNode.id == ParserTreeConstants.JJTMETHOD) {
        varsDeclared = true;
      } else if (childNode.id == ParserTreeConstants.JJTMAINMETHOD){
        if(mainDeclared)
          throw new Exception("CLASS has more than one main method.");

        varsDeclared = true;
        mainDeclared = true;
      } else
        throw new Exception("CLASS has children of type not allowed.");

      childNode.addSymbolTable(this.symbolTable);
      childNode.eval();

      childIndex++;
    }
  }

  public String toString() {
    return "CLASS[" + name + "]";
  }
}
/* JavaCC - OriginalChecksum=4f01d04b73ea000fe01d2c7f87d2a603 (do not edit this line) */
