import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class CodeGenerator {

    private PrintWriter writer;
    private static String generatedCode;
    protected ASTCLASS classNode;
    protected int labelCounter = 0;
    protected int conditionalCounter = 0;
    protected int loopCounter = 0;
    protected List<String> classVars;

    private void createFile(){
        if(!ControlVars.SAVE_JASMIN_CODE)
            return;

        try {
            File file = new File("test/fixtures/libs/compiled/jasminCode/" + classNode.className + ".j");

            if (!file.exists())
                file.createNewFile();

            writer = new PrintWriter(file);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void writeToFile(String code) {
        try{
            if(ControlVars.SAVE_JASMIN_CODE)
                writer.print(code);
            else
                System.out.println(code);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void closeFile(){
        if(ControlVars.SAVE_JASMIN_CODE)
            writer.close();
    }

    protected static String space() {
        return " ";
    }

    protected static String entry() {
        return ":";
    }

    protected static String us() { return "_"; }

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

    protected static String cleanseVar(String varName){
        switch (varName){
            case "field":
                return "field_";
            default:
                return varName;
        }
    }

    protected static String standardInitializer(String extendedClass) {
        String code = nl() + ".method public <init>()V" + nl();
        code += tab() + "aload_0" + nl();
        code += tab() + "invokenonvirtual " + extendedClass + "/<init>()V" + nl();
        code += tab() + "return" + nl();
        code += ".end method";

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

    protected int convertClassHeader(){
        String extendedClass = ((ClassSymbol) this.classNode.symbolTable.getSymbol(this.classNode.className)).getExtendedClass();
        boolean extendsClass = extendedClass == null;

        String classHeaderCode = "";

        classHeaderCode += ".class public " + this.classNode.className + nl();
        classHeaderCode += ".super " + (extendsClass ? "java/lang/Object" : extendedClass) + nl();

        int numChildren = this.classNode.jjtGetNumChildren();
        int childIndex = (extendsClass ? 1 : 0);

        SimpleNode child;

        while(childIndex < numChildren) {
            child = (SimpleNode) this.classNode.jjtGetChild(childIndex);

            if(child.equalsNodeType(ParserTreeConstants.JJTVARIABLE))
                classHeaderCode += nl() + convertVarDeclaration((ASTVARIABLE) child);
            else{
                classHeaderCode += nl();
                break;
            }

            childIndex++;
        }

        classHeaderCode += standardInitializer(extendsClass ? "java/lang/Object" : extendedClass);;

        writeToFile(classHeaderCode);

        return childIndex;
    }

    protected void convertClass(){
        int numChildren = this.classNode.jjtGetNumChildren();
        int childIndex = convertClassHeader();

        String classBodyCode = "";
        SimpleNode child;

        while(childIndex < numChildren){
            child = (SimpleNode) this.classNode.jjtGetChild(childIndex);

            classBodyCode = "";

            if(child.equalsNodeType(ParserTreeConstants.JJTMETHOD))
                classBodyCode += nl() + convertMethodDeclaration((ASTMETHOD) child);
            else if(child.equalsNodeType(ParserTreeConstants.JJTMAINMETHOD))
                classBodyCode += nl() + convertMainMethodDeclaration((ASTMAINMETHOD) child);

            writeToFile(classBodyCode);

            childIndex++;
        }
    }

    protected String convertVarDeclaration(ASTVARIABLE varNode){
        String varCode = "";

        classVars.add(varNode.varName);

        if(varNode.classScope){
            varCode += ".field private " + cleanseVar(varNode.varName) + space() + getJasminType(varNode.varName, varNode);
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
        generatedCode = "";

        int numChildren = root.jjtGetNumChildren();
        int childIndex = 0;

        SimpleNode child;

        while(childIndex < numChildren){
            child = (SimpleNode) root.jjtGetChild(childIndex);

            if(child.equalsNodeType(ParserTreeConstants.JJTCLASS)){
                this.classNode = (ASTCLASS) child;

                classVars = new ArrayList<>(classNode.classVars);

                createFile();

                convertClass();

                closeFile();
            }

            childIndex++;
        }
    }
}
