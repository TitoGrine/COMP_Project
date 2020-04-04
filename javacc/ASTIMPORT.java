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

  @Override
  public void addSymbolTable(SymbolTable symbolTable){
    this.symbolTable = symbolTable;
  }

  @Override
  public void eval() throws Exception {
    // TODO: Add symbol

    int numChildren = this.jjtGetNumChildren();

    if(numChildren != 2)
      throw new Exception("Import has an irregular amount number of children nodes.");

    SimpleNode firstChild = (SimpleNode) this.jjtGetChild(0);
    SimpleNode secondChild = (SimpleNode) this.jjtGetChild(1);

    if(firstChild.id == ParserTreeConstants.JJTPARAMETERS){
      firstChild.addSymbolTable(this.symbolTable);
      firstChild.eval();
    } else {
      throw new Exception("Import doesn't have the parameters node in the correct place or at all.");
    }

    if(secondChild.id == ParserTreeConstants.JJTRETURN){
      secondChild.addSymbolTable(this.symbolTable);
      secondChild.eval();
    } else {
      throw new Exception("Import doesn't have the return node in the correct place or at all.");
    }
  }

  public String toString() {
    return "IMPORT[" + name + "]";
  }
}
/* JavaCC - OriginalChecksum=88acf2e261b5ed8d1318a9e9015ad173 (do not edit this line) */
