import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class CodeGenerator {

    protected ASTCLASS classNode;
    private static String generatedCode;
    protected int labelCounter = 0;
    protected int conditionalCounter = 0;
    protected List<String> classVars;

    //private static List<String> locals;

    protected void writeToFile() {
        File file = new File("test/fixtures/libs/compiled/jasminCode/" + classNode.className + ".j");

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

    protected static String entry() {
        return ":";
    }

    protected static String rnl() { return "\r\b\r"; }

    protected static String nl(int num) {
        return "\n".repeat(num);
    }

    protected static String nl() {
        return "\n";
    }

    protected static String tab(int indentation){
        return "\t".repeat(indentation);
    }

    protected static String tab() {
        return "\t";
    }

    protected static String standardInitializer(String extendedClass) {
        String code = nl() + ".method public <init>()V" + nl();
        code += tab() + "aload_0" + nl();
        code += tab() + "invokenonvirtual " + extendedClass + "/<init>()V" + nl();
        code += tab() + "return" + nl();
        code += ".end method" + nl();

        return code;
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
        Symbol symbol;

        if(!node.symbolTable.existsSymbol(key)){

            if(!node.symbolTable.existsSymbol("this." + key))
                return jasminType;

            symbol = node.symbolTable.getSymbol("this." + key);
        } else {
            symbol = node.symbolTable.getSymbol(key);
        }

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

    protected String convertClass(){
        String classCode = ".class public " + this.classNode.className + nl();

        String extendedClass = ((ClassSymbol) this.classNode.symbolTable.getSymbol(this.classNode.className)).getExtendedClass();

        boolean extendsClass = extendedClass == null;

        classCode += ".super " + (extendsClass ? "java/lang/Object" : extendedClass) + nl();

        classCode += standardInitializer(extendsClass ? "java/lang/Object" : extendedClass);

        classCode += convertClassBody(extendsClass);

        return classCode;
    }

    protected String convertClassBody(boolean extendsClass){
        String classBodyCode = "";

        int numChildren = this.classNode.jjtGetNumChildren();
        int childIndex = (extendsClass ? 1 : 0);

        SimpleNode child;

        while(childIndex < numChildren){
            child = (SimpleNode) this.classNode.jjtGetChild(childIndex);

            if(child.equalsNodeType(ParserTreeConstants.JJTVARIABLE))
                classBodyCode += convertVarDeclaration((ASTVARIABLE) child);
            else if(child.equalsNodeType(ParserTreeConstants.JJTMETHOD))
                classBodyCode += nl() + convertMethodDeclaration((ASTMETHOD) child);
            else if(child.equalsNodeType(ParserTreeConstants.JJTMAINMETHOD))
                classBodyCode += nl() + convertMainMethodDeclaration((ASTMAINMETHOD) child);

            childIndex++;
        }

        return classBodyCode;
    }

    protected String convertVarDeclaration(ASTVARIABLE varNode){
        String varCode = "";

        classVars.add(varNode.varName);

        if(varNode.classScope){
            varCode += nl() + ".field public " + varNode.varName + space() + getJasminType(varNode.varName, varNode);
        }

        return varCode;
    }

    protected String convertMethodDeclaration(ASTMETHOD methodNode){
        MethodGenerator methodGenerator = new MethodGenerator(methodNode, classNode, classVars, labelCounter);

        return methodGenerator.generateMethodCode();
    }

    protected String convertMainMethodDeclaration(ASTMAINMETHOD methodNode){
        MethodGenerator methodGenerator = new MethodGenerator(methodNode, classNode, classVars, labelCounter);

        return methodGenerator.generateMainMethodCode();
    }

    public void generate(SimpleNode root){
        this.generatedCode = "";

        int numChildren = root.jjtGetNumChildren();
        int childIndex = 0;

        SimpleNode child;

        while(childIndex < numChildren){
            child = (SimpleNode) root.jjtGetChild(childIndex);

            if(child.equalsNodeType(ParserTreeConstants.JJTCLASS)){
                this.classNode = (ASTCLASS) child;

                classVars = new ArrayList<>(classNode.classVars);

                generatedCode += convertClass();
            }

            childIndex++;
        }

        //writeToFile();
        System.out.println(generatedCode);
    }
}
