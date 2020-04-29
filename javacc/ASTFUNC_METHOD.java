import java.util.ArrayList;

/* Generated By:JJTree: Do not edit this line. ASTFUNCMETHOD.java Version 6.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTFUNC_METHOD extends TypeSensitive {
  protected String call;
  protected ArrayList<TypeEnum> arguments = new ArrayList<>();

  public ASTFUNC_METHOD(int id) {
    super(id);
  }

  public ASTFUNC_METHOD(Parser p, int id) {
    super(p, id);
  }

  @Override
  public void eval(SemanticAnalysis analysis){
    SimpleNode firstChild = (SimpleNode) this.jjtGetChild(0);
    ASTCALL secondChild = (ASTCALL) this.jjtGetChild(1);

    String object;
    String extendedClass = null;

    firstChild.addSymbolTable(this.symbolTable);

    if(compareNode(firstChild, ParserTreeConstants.JJTTHIS)){
      firstChild.eval(analysis);

      ClassSymbol classSymbol = (ClassSymbol) this.symbolTable.getSymbol(((ASTTHIS) firstChild).className);

      object = ((ASTTHIS) firstChild).className;

      extendedClass = classSymbol.getExtendedClass();
    } else if(compareNode(firstChild, ParserTreeConstants.JJTIDENT)){
      object = ((ASTIDENT) firstChild).name;

      if(this.symbolTable.existsSymbol(object)){
        Symbol symbol = this.symbolTable.getSymbol(object);

        if(symbol.getType() == TypeEnum.OBJECT)
          object = this.symbolTable.getClassType(object);

        if(this.symbolTable.existsClassSymbol(object))
          extendedClass = ((ClassSymbol) this.symbolTable.getSymbol(object)).getExtendedClass();
      }
    } else if(compareNode(firstChild, ParserTreeConstants.JJTNEW)){
      firstChild.eval(analysis);
      object = ((ASTNEW) firstChild).object;
    } else if(compareNode(firstChild, ParserTreeConstants.JJTFUNC_METHOD)){
      firstChild.eval(analysis);
      object = ((ASTFUNC_METHOD) firstChild).call;
    } else{
      analysis.addError(this.getCoords(), "Method call to an invalid object.");
      return;
    }

    object += '.';

    secondChild.addSymbolTable(this.symbolTable);
    secondChild.eval(analysis);

    String method = secondChild.method;

    String message = "";

    boolean checkExtended = false;
    boolean invalidParams = false;

    if(!this.symbolTable.existsMethodSymbol(object + method)){
      checkExtended = true;
      message = "Method " + method + " doesn't exist for object " + (object.isEmpty() ? ((ASTTHIS) firstChild).className : object);
    } else {
      MethodSymbol methodSymbol = (MethodSymbol) this.symbolTable.getSymbol(object + method);

      if(!methodSymbol.acceptedParameters(secondChild.arguments)){
        checkExtended = true;
        invalidParams = true;
        message = "No method " + method + " accepts the given arguments.";
      }
    }

    if(checkExtended){
      if(extendedClass == null){
        analysis.addError(this.getCoords(), message);
        return;
      } else if(!this.symbolTable.existsMethodSymbol(extendedClass + '.' + method)){
        if(invalidParams)
          analysis.addError(this.getCoords(), message);
        else
          analysis.addError(this.getCoords(), "Method " + method + " doesn't exist for object " + (object.isEmpty() ? ((ASTTHIS) firstChild).className : object) + " nor the extended class " + extendedClass);
        return;
      }

      object = extendedClass + '.';

      MethodSymbol methodSymbol = (MethodSymbol) this.symbolTable.getSymbol(object + method);

      if(!methodSymbol.acceptedParameters(secondChild.arguments))
        analysis.addError(this.getCoords(), "No method " + method + " accepts the given arguments.");

    }

    this.arguments = secondChild.arguments;
    this.call = object + method;
  }
}
/* JavaCC - OriginalChecksum=54a9f6d33a5022ecd7338879131cb817 (do not edit this line) */
