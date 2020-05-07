import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class CodeGenerator {

    private static String generatedCode;

    protected ASTCLASS classNode;

    //private static List<String> locals;
    protected static List<String> classVars;

    protected void writeToFile() {
        File file = new File("test/fixtures/libs/compiled/" + classNode.className + ".j");

        try{
            if (!file.exists())
            file.createNewFile();

            PrintWriter writer = new PrintWriter(file);

            writer.println(generatedCode);

            writer.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    protected static String space() {
        return " ";
    }

    protected static String nl() {
        return "\n";
    }

    protected static String tab() {
        return "\t";
    }

    protected static String standardInitializer(String extendedClass) {
        String code = nl() + ".method <init>()V" + nl();
        code += tab() + "aload_0" + nl();
        code += tab() + "invokenonvirtual " + extendedClass + "/<init>()V" + nl();
        code += tab() + "return" + nl();
        code += ".end method" + nl();

        return code;
    }

    protected static String mainHeader(){
        return nl() + ".method public static main([Ljava/lang/String;)V";
    }

    protected static String getSimpleJasminType(TypeEnum type){
        switch (type) {
            case INT:
                return "I";
            case STRING:
                return "Ljava/lang/String;";
            case BOOL:
                return "Z";
            case ARRAY:
                return "[I";
            default:
                return "V";
        }
    }

    protected static String getJasminType(String key, SimpleNode node){
        String jasminType = "";

        if(!node.symbolTable.existsSymbol(key))
            return jasminType;

        Symbol symbol = node.symbolTable.getSymbol(key);

        switch (symbol.getType()) {
            case OBJECT:
                jasminType = symbol.getClassType();
                break;
            case METHOD:
                break;
            case ARRAY:
                jasminType = "[";

                if(((ArraySymbol) symbol).getReturnType() == TypeEnum.INT)
                    jasminType += "I";
                else
                    jasminType += "Ljava/lang/String;";

                break;
            default:
                jasminType = getSimpleJasminType(symbol.getType());
        }
        return jasminType;
    }

    public void generate(SimpleNode root){
        this.generatedCode = "";

        classVars = new ArrayList<>(classNode.classVars);

        int numChildren = root.jjtGetNumChildren();
        int childIndex = 0;

        SimpleNode child;

        while(childIndex < numChildren){
            child = (SimpleNode) root.jjtGetChild(childIndex);

            if(child.equalsNodeType(ParserTreeConstants.JJTCLASS)){
                this.classNode = (ASTCLASS) child;

                generatedCode += convertClass();
            }

            childIndex++;
        }

        writeToFile();
    }

    public String convertClass(){
        String classCode = ".class public " + this.classNode.className + nl();

        String extendedClass = ((ClassSymbol) this.classNode.symbolTable.getSymbol(this.classNode.className)).getExtendedClass();

        boolean extendsClass = extendedClass == null;

        classCode += ".super " + (extendsClass ? "java/lang/Object" : extendedClass) + nl();

        classCode += standardInitializer(extendsClass ? "java/lang/Object" : extendedClass);

        classCode += convertClassBody(extendsClass);

        return classCode;
    }

    public String convertClassBody(boolean extendsClass){
        String classBodyCode = "";

        int numChildren = this.classNode.jjtGetNumChildren();
        int childIndex = (extendsClass ? 1 : 0);

        SimpleNode child;

        while(childIndex < numChildren){
            child = (SimpleNode) this.classNode.jjtGetChild(childIndex);

            if(child.equalsNodeType(ParserTreeConstants.JJTVARIABLE))
                classBodyCode += convertVarDeclaration((ASTVARIABLE) child);
            else if(child.equalsNodeType(ParserTreeConstants.JJTMETHOD))
                classBodyCode += convertMethodDeclaration((ASTMETHOD) child);
            else if(child.equalsNodeType(ParserTreeConstants.JJTMAINMETHOD))
                classBodyCode += convertMainMethodDeclaration((ASTMAINMETHOD) child);

            childIndex++;
        }

        return classBodyCode;
    }

    public String convertVarDeclaration(ASTVARIABLE varNode){
        String varCode = "";

        classVars.add(varNode.name);

        if(varNode.classScope){
            varCode += nl() + ".field public " + varNode.name + space() + getJasminType(varNode.name, varNode);
        }

        return varCode;
    }

    public String convertMethodDeclaration(ASTMETHOD methodNode){
        MethodGenerator methodGenerator = new MethodGenerator(methodNode);

        return methodGenerator.generateMethodCode();
    }

    public String convertMainMethodDeclaration(ASTMAINMETHOD methodNode){
        String methodCode = "";

        return methodCode;
    }
}