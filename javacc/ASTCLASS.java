import java.util.ArrayList;
import java.util.List;

/* Generated By:JJTree: Do not edit this line. ASTCLASS.java Version 6.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTCLASS extends SimpleNode {
  protected String className;
  protected int classVars = 0;

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
  public void eval(SemanticAnalysis analysis){
    int numChildren = this.jjtGetNumChildren();
    boolean mainDeclared = false;
    int childIndex = 1;

    ASTIDENT firstChild = (ASTIDENT) this.jjtGetChild(0);
    SimpleNode childNode;

    firstChild.addSymbolTable(this.symbolTable);
    this.className = firstChild.name;

    ClassSymbol classSymbol = new ClassSymbol();
    List<ASTMETHOD> methods = new ArrayList<>();
    ASTMAINMETHOD mainMethod = null;

    if(numChildren > childIndex){
      childNode = (SimpleNode) this.jjtGetChild(childIndex);

      if(childNode.id == ParserTreeConstants.JJTEXTENDS){
        childNode.addSymbolTable(this.symbolTable);
        childNode.eval(analysis);

        classSymbol.setExtendedClass(((ASTEXTENDS) childNode).extendedClass);

        childIndex++;
      }
    }

    this.symbolTable.addSymbol(this.className, classSymbol);

    String key;

    while(childIndex < numChildren){
      childNode = (SimpleNode) this.jjtGetChild(childIndex);

      childNode.addSymbolTable(this.symbolTable);

      if (compareNode(childNode, ParserTreeConstants.JJTVARIABLE)) {
        ((ASTVARIABLE) childNode).classScope = true;
        classVars++;

        childNode.eval(analysis);

      } else if (compareNode(childNode, ParserTreeConstants.JJTMETHOD)) {
        ASTMETHOD method = (ASTMETHOD) childNode;

        method.preProcessMethod(analysis);
        methods.add(method);

        key = className + '.' + method.methodName;

        this.symbolTable.addMethodSymbol(key, method.parameters, method.returnType);
      } else if (compareNode(childNode, ParserTreeConstants.JJTMAINMETHOD)){
        if(mainDeclared)
          analysis.addError(this.getCoords(), "CLASS " + firstChild.name + " has more than one main method.");

        ((ASTMAINMETHOD) childNode).preProcessMethod(analysis);

        mainMethod = (ASTMAINMETHOD) childNode;

        key = className + '.' + mainMethod.methodName;

        this.symbolTable.addStaticMethodSymbol(key, mainMethod.parameters, mainMethod.returnType);

        mainDeclared = true;
      }
      childIndex++;
    }

    if(ControlVars.PRINT_SYMBOLTABLE)
      this.symbolTable.print(this.className);

    for(ASTMETHOD method : methods){
      method.eval(analysis);
      // this.symbolTable.clearInitialized(); // TODO: Check for initialized class variables?
    }

    if(mainMethod != null)
      mainMethod.eval(analysis);
  }
}
/* JavaCC - OriginalChecksum=4f01d04b73ea000fe01d2c7f87d2a603 (do not edit this line) */
