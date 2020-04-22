import java.util.ArrayList;
import java.util.List;

/* Generated By:JJTree: Do not edit this line. ASTCLASS.java Version 6.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTCLASS extends SimpleNode {
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
    int numChildren = this.jjtGetNumChildren();
    boolean mainDeclared = false;
    int childIndex = 1;

    ASTIDENT firstChild = (ASTIDENT) this.jjtGetChild(0);

    ClassSymbol classSymbol = new ClassSymbol(new ArrayList<>());

    SimpleNode childNode = (SimpleNode) this.jjtGetChild(childIndex);

    List<ASTMETHOD> methods = new ArrayList<>();
    ASTMAINMETHOD mainMethod = null;

    if(childNode.id == ParserTreeConstants.JJTEXTENDS){
      childNode.addSymbolTable(this.symbolTable);
      childNode.eval();

      classSymbol.setExtendedClass(((ASTEXTENDS) childNode).extendedClass);

      childIndex++;
    }

    this.symbolTable.addSymbol(firstChild.name, classSymbol);

    while(childIndex < numChildren){
      childNode = (SimpleNode) this.jjtGetChild(childIndex);

      if (childNode.id == ParserTreeConstants.JJTVARIABLE) {
        childNode.addSymbolTable(this.symbolTable);
        childNode.eval();
      } else if (childNode.id == ParserTreeConstants.JJTMETHOD) {
        ASTMETHOD method = (ASTMETHOD) childNode;

        method.addSymbolTable(this.symbolTable);
        method.preProcessMethod();

        methods.add(method);

        this.symbolTable.addSymbol(method.methodName, method.methodSymbol);
      } else if (childNode.id == ParserTreeConstants.JJTMAINMETHOD){
        if(mainDeclared)
          throw new Exception("CLASS " + firstChild.name + " has more than one main method.");

        mainDeclared = true;
        mainMethod = (ASTMAINMETHOD) childNode;
      }
      childIndex++;
    }

    for(ASTMETHOD method : methods){
      method.eval();
    }

    if(mainMethod != null)
      mainMethod.eval();
  }
}
/* JavaCC - OriginalChecksum=4f01d04b73ea000fe01d2c7f87d2a603 (do not edit this line) */
