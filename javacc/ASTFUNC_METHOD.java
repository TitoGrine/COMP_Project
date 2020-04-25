/* Generated By:JJTree: Do not edit this line. ASTFUNCMETHOD.java Version 6.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTFUNC_METHOD extends Operator {
  protected String call;

  public ASTFUNC_METHOD(int id) {
    super(id);
  }

  public ASTFUNC_METHOD(Parser p, int id) {
    super(p, id);
  }

  @Override
  public void eval(SemanticErrors errors){
    SimpleNode firstChild = (SimpleNode) this.jjtGetChild(0);
    ASTCALL secondChild = (ASTCALL) this.jjtGetChild(1);

    String object = "";
    String extendedClass = null;

    firstChild.addSymbolTable(this.symbolTable);

    if(firstChild.id == ParserTreeConstants.JJTTHIS){
      firstChild.eval(errors);

      ClassSymbol classSymbol = (ClassSymbol) this.symbolTable.getSymbol(((ASTTHIS) firstChild).className);

      object = ((ASTTHIS) firstChild).className;

      extendedClass = classSymbol.getExtendedClass();
    } else if(firstChild.id == ParserTreeConstants.JJTIDENT){
      object = ((ASTIDENT) firstChild).name;

      if(this.symbolTable.existsSymbol(object)){
        Symbol symbol = this.symbolTable.getSymbol(object);

        if(symbol.getType() == TypeEnum.OBJECT)
          object = symbol.getClassType();

      }
    } else if(firstChild.id == ParserTreeConstants.JJTNEW){
      firstChild.eval(errors);
      object = ((ASTNEW) firstChild).object;
    } else if(firstChild.id == ParserTreeConstants.JJTFUNC_METHOD){
      firstChild.eval(errors);
      object = ((ASTFUNC_METHOD) firstChild).call;
    } else{
      errors.addError(this.getCoords(), "Method call to an invalid object.");
      return;
    }

    object += '.';

    secondChild.addSymbolTable(this.symbolTable);
    secondChild.eval(errors);

    String method = secondChild.method;

    if(!this.symbolTable.existsMethodSymbol(object + method)){
      if(extendedClass == null){
        errors.addError(this.getCoords(), "Method " + method + " doesn't exist for object " + (object.isEmpty() ? ((ASTTHIS) firstChild).className : object) + ".");
        return;
      }else if(!this.symbolTable.existsMethodSymbol(extendedClass + method)){
        errors.addError(this.getCoords(), "Method " + method + " doesn't exist for object " + (object.isEmpty() ? ((ASTTHIS) firstChild).className : object) + " nor the extended class " + extendedClass + ".");
        return;
      }
      object = extendedClass + '.';
    }

    MethodSymbol methodSymbol = (MethodSymbol) this.symbolTable.getSymbol(object + method);

    if(!methodSymbol.acceptedParameters(secondChild.arguments))
      errors.addError(this.getCoords(), "Method " + method + " doesn't accept the given arguments.");

    this.call = object + method;
  }
}
/* JavaCC - OriginalChecksum=54a9f6d33a5022ecd7338879131cb817 (do not edit this line) */
