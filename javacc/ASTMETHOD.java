import java.util.ArrayList;

/* Generated By:JJTree: Do not edit this line. ASTMETHOD.java Version 6.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTMETHOD extends SimpleNode {

  protected String methodName;
  protected TypeEnum returnType;
  protected ArrayList<TypeEnum> parameters = new ArrayList<>();
  protected int localSize = 0;

  public ASTMETHOD(int id) {
    super(id);
  }

  public ASTMETHOD(Parser p, int id) {
    super(p, id);
  }

  public void preProcessMethod(SemanticAnalysis errors){
    ASTRETURN firstChild = (ASTRETURN) this.jjtGetChild(0);
    ASTIDENT secondChild = (ASTIDENT) this.jjtGetChild(1);

    firstChild.addSymbolTable(this.symbolTable);
    secondChild.addSymbolTable(this.symbolTable);
    firstChild.eval(errors);

    this.returnType = firstChild.type;
    this.methodName = secondChild.name;

    SimpleNode thirdChild = (SimpleNode) this.jjtGetChild(2);

    if(compareNode(thirdChild, ParserTreeConstants.JJTARGUMENTS)){
      ASTARGUMENTS arguments = (ASTARGUMENTS) thirdChild;

      arguments.addSymbolTable(this.symbolTable);
      arguments.eval(errors);

      this.parameters = arguments.arguments;
    }
  }

  @Override
  public void addSymbolTable(SymbolTable symbolTable){
    this.symbolTable = new SymbolTable(symbolTable);
  }

  @Override
  public void eval(SemanticAnalysis analysis){
    int childIndex = 2;

    SimpleNode childNode = (SimpleNode) this.jjtGetChild(childIndex);

    if(compareNode(childNode, ParserTreeConstants.JJTARGUMENTS)){
      childIndex++;
      childNode = (SimpleNode) this.jjtGetChild(childIndex);
    }

    ASTMETHOD_BODY methodBody = (ASTMETHOD_BODY) childNode;

    methodBody.addSymbolTable(symbolTable);
    methodBody.eval(analysis);

    this.localSize = 1 + this.parameters.size() + methodBody.numVars;

    childIndex++;
    ASTRETURN_EXP returnExp = (ASTRETURN_EXP) this.jjtGetChild(childIndex);

    returnExp.addSymbolTable(this.symbolTable);
    returnExp.eval(analysis);

    if(returnExp.expType != this.returnType)
      analysis.addError(this.getCoords(), "Method " + methodName + " returns type different from declaration.");

    if(ControlVars.PRINT_SYMBOLTABLE)
      this.symbolTable.print(this.methodName);
  }
}
/* JavaCC - OriginalChecksum=ea7e13413ab21b460f76667a3725c2ac (do not edit this line) */
