import java.util.ArrayList;
import java.util.Collections;

/* Generated By:JJTree: Do not edit this line. ASTMAINMETHOD.java Version 6.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTMAINMETHOD extends ASTMETHOD {

  public ASTMAINMETHOD(int id) {
    super(id);
  }

  public ASTMAINMETHOD(Parser p, int id) {
    super(p, id);
  }

  @Override
  public void preProcessMethod(SemanticAnalysis errors) {
    methodName = "main";
    returnType = ControlVars.VOID;
    parameters.add(ControlVars.ARRAY);
  }

  @Override
  public void addSymbolTable(SymbolTable symbolTable){
    this.symbolTable = new SymbolTable(symbolTable);
  }

  @Override
  public void eval(SemanticAnalysis analysis){

    ASTIDENT firstChild = (ASTIDENT) this.jjtGetChild(0);

    firstChild.addSymbolTable(this.symbolTable);

    ArraySymbol arraySymbol = new ArraySymbol(ControlVars.STRING);

    arraySymbol.incInitialized();

    this.symbolTable.addSymbol(firstChild.name, arraySymbol);

    SimpleNode childNode = (SimpleNode) this.jjtGetChild(1);

    ASTMETHOD_BODY methodBody = (ASTMETHOD_BODY) childNode;

    methodBody.addSymbolTable(symbolTable);
    methodBody.eval(analysis);

    this.localSize = 1 + methodBody.numVars;

    if(ControlVars.PRINT_SYMBOLTABLE)
      this.symbolTable.print(this.methodName);
  }
}
/* JavaCC - OriginalChecksum=929a511142c3eb4332a56f5c02fad9ec (do not edit this line) */
