import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class CodeGenerator {

    private PrintWriter writer;
    private static String generatedCode;
    protected ASTCLASS classNode;
    protected Counter counter = new Counter();
    protected List<String> classVars;

    /**
     * Creates an appropriately named file to store the generated Jasmin Code.
     */
    private void createFile(){
        if(!ControlVars.SAVE_JASMIN_CODE){
            System.out.println(ControlVars.GREEN + "\n +++++++++++ Generated Jasmin Code +++++++++++\n" + ControlVars.RESET);
            return;
        }

        try {
             File file = new File("test/fixtures/libs/compiled/jasminCode/" + classNode.className + ".j");
            //File file = new File("test/fixtures/libs/compiled/" + classNode.className + ".j");

            if (!file.exists())
                file.createNewFile();

            writer = new PrintWriter(file);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Writes the given code either in a file or in the terminal depending on the control variable.
     *
     * @param code      Code to write.
     */
    private void writeToFile(String code) {

        if(code.length() == 0)
            return;

        try{
            if(ControlVars.SAVE_JASMIN_CODE)
                writer.print(code);
            else{
                System.out.println(code);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * If the generated code was written to a file, it closes said file.
     */
    private void closeFile(){
        if(ControlVars.SAVE_JASMIN_CODE)
            writer.close();
    }

    /**
     * Shortcut for writing a space character.
     *
     * @return      A space character.
     */
    protected static String space() {
        return " ";
    }

    /**
     * Shortcut for writing an entry.
     *
     * @return      A colon character.
     */
    protected static String entry() {
        return ":";
    }

    /**
     * Shortcut for writing an underscore character.
     *
     * @return      An underscore character.
     */
    protected static String us() { return "_"; }

    /**
     * Short cut for writing num new lines.
     *
     * @param num   Number of newlines to add.
     * @return      Return num newline characters.
     */
    protected static String nl(int num) {
        return "\n".repeat(num);
    }

    /**
     * Short cut for writing a new line.
     *
     * @return      A newline character.
     */
    protected static String nl() {
        return "\n";
    }

    /**
     * Short cut for writing num tab characters.
     *
     * @param indentation   Number of tabs to add.
     * @return              Return num tab characters.
     */
    protected static String tab(int indentation){
        return "\t".repeat(indentation);
    }

    /**
     * Short cut for writing a tab.
     *
     * @return      A tab character.
     */
    protected static String tab() {
        return "\t";
    }

    /**
     * Prepends an underscore to the end of the given variable name in order to
     * prevent name conflicts with Jasmin keywords.
     *
     * @param varName       Variable name to prepend.
     * @return              Secure variable name to be used in Jasmin code.
     */
    protected static String cleanseVar(String varName){
        varName = varName.replace("this.", "");

        return "$_" + varName;
    }

    /**
     * Forms the standard code for class declaration.
     *
     * @param extendedClass     Name of the class the main class extends (default is Object).
     * @return                  A string with the standard code.
     */
    protected static String standardInitializer(String extendedClass) {
        String code = nl() + ".method public <init>()V" + nl();
        code += tab() + "aload_0" + nl();
        code += tab() + "invokenonvirtual " + extendedClass + "/<init>()V" + nl();
        code += tab() + "return" + nl();
        code += ".end method";

        return code;
    }

    /**
     * Converts a given simple variable type into its appropriate Jasmin encoding.
     *
     * @param type      A given type.
     * @return          Jasmin encoding for the given type.
     */
    protected static String getSimpleJasminType(String type){
        switch(type){
            case ControlVars.BOOL:
                return "Z";
            case ControlVars.INT:
                return "I";
            case ControlVars.ARRAY:
                return "[I";
            case ControlVars.STRING:
                return "Ljava/lang/String;";
            case ControlVars.VOID:
                return "V";
            case ControlVars.METHOD:
                return "";
            default:
                return "L" + type + ";";
        }
    }

    /**
     * Finds the correct Jasmin encoding of the given variable type.
     *
     * @param key       Key in the symbol table of the variable.
     * @param node      SimpleNode where key appeared, used to access the symbol table.
     * @return          Jasmin encoding for the type of the given key.
     */
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
            case ControlVars.METHOD:
                break;
            case ControlVars.ARRAY:
                jasminType = "[";

                if(((ArraySymbol) symbol).getReturnType().equals(ControlVars.INT))
                    jasminType += "I";
                else
                    jasminType += "Ljava/lang/String;";

                break;
            default:
                jasminType = getSimpleJasminType(symbol.getType());
        }

        return jasminType;
    }

    /**
     * Forms the Jasmin header code for the file's class.
     *
     * @return      Jasmin code representing  the header.
     */
    protected int convertClassHeader(){
        String extendedClass = ((ClassSymbol) this.classNode.symbolTable.getSymbol(this.classNode.className)).getExtendedClass();
        boolean extendsClass = extendedClass == null;

        String classHeaderCode = "";

        classHeaderCode += ".source " + this.classNode.className + ".j" + nl();
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

    /**
     * Iterates through all class methods and writes their generated code in the appropriate output.
     */
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

    /**
     * Generates the Jasmin code for the class variable declaration.
     *
     * @param varNode       Node of the variable declaration,
     * @return              Jasmin code representing the variable declaration.
     */
    protected String convertVarDeclaration(ASTVARIABLE varNode){
        String varCode = "";

        classVars.add(varNode.varName);

        if(varNode.classScope){
            varCode += ".field private " + cleanseVar(varNode.varName) + space() + getJasminType(varNode.varName, varNode);
        }

        return varCode;
    }

    /**
     * Generates the Jasmin code representing the given method declaration.
     *
     * @param methodNode    Node of the method declaration.
     * @return              Jasmin code representing the method declaration and body.
     */
    protected String convertMethodDeclaration(ASTMETHOD methodNode){
        MethodGenerator methodGenerator = new MethodGenerator(methodNode, classNode, classVars, counter);

        return methodGenerator.generateMethodCode();
    }

    /**
     * Generates the Jasmin code representing the main method declaration.
     *
     * @param methodNode    Node of the main method declaration.
     * @return              Jasmin code representing the main method declaration and body.
     */
    protected String convertMainMethodDeclaration(ASTMAINMETHOD methodNode){
        MethodGenerator methodGenerator = new MethodGenerator(methodNode, classNode, classVars, counter);

        return methodGenerator.generateMainMethodCode();
    }

    /**
     * Generates the necessary Jasmin code of the code represented in the given AST.
     *
     * @param root      Root node of the AST.
     */
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
