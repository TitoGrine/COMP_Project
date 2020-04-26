import java.util.ArrayList;

/* Generated By:JJTree: Do not edit this line. ASTIMPORT.java Version 6.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public
class ASTIMPORT extends SimpleNode {
  public ASTIMPORT(int id) {
    super(id);
  }

  public ASTIMPORT(Parser p, int id) {
    super(p, id);
  }

  @Override
  public void eval(SemanticErrors errors){
    int numChildren = this.jjtGetNumChildren();

    ASTIDENT firstChild = (ASTIDENT) this.jjtGetChild(0);

    String key = firstChild.name;
    TypeEnum returnType = TypeEnum.VOID;
    ArrayList<TypeEnum> parameters = new ArrayList<>();

    int childIndex = 1;
    boolean classImport = true;

    if (numChildren > childIndex){
      SimpleNode nextChild = (SimpleNode) this.jjtGetChild(childIndex);

      if(nextChild.id == ParserTreeConstants.JJTIDENT){
        if(!this.symbolTable.existsClassSymbol(key))
          errors.addError(this.getCoords(), "Non static method " + ((ASTIDENT) nextChild).name + " import declaration without the import for class " + firstChild.name);

        key += '.' + ((ASTIDENT) nextChild).name;

        classImport = false;

        childIndex++;
      }

      while(numChildren > childIndex){
        nextChild = (SimpleNode) this.jjtGetChild(childIndex);
        nextChild.addSymbolTable(this.symbolTable);
        nextChild.eval(errors);

        if (nextChild.id == ParserTreeConstants.JJTPARAMETERS)
          parameters = ((ASTPARAMETERS) nextChild).parameters;
        else if (!classImport)
          returnType = ((ASTRETURN) nextChild).type;
        else
          errors.addError(this.getCoords(), "Class import declaration can't have return type");

        childIndex++;
      }
    }

    if(classImport) {
      this.symbolTable.addSymbol(key, new ClassSymbol(parameters));
    }
    else{
      if(this.symbolTable.repeatedMethod(key, returnType, parameters)){
        errors.addError(this.getCoords(), "Method " + key + " was already imported.");
        return;
      }

      this.symbolTable.addSymbol(key, new MethodSymbol(returnType, parameters));}
  }
}
/* JavaCC - OriginalChecksum=88acf2e261b5ed8d1318a9e9015ad173 (do not edit this line) */
