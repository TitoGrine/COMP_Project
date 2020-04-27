
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.SimpleTimeZone;
import java.util.ArrayList;


public class CodeGenerator {
    static String generated;
    private static SimpleNode root;
    static int localIndex = 1;
    private static String[] locals = new String[99];
    private static String classIdent="";
    private static Node classNode;
    

    public CodeGenerator(SimpleNode root) {
        this.root = root;
        generated = "";
    }


    public void generate() {
        //(verificar se o root é classe, ver os children e por aí fora)
        Node[] children =  root.jjtGetChildren();

        for(Node child: children) {
            if (child.getId() == 6) {
                this.classNode = child;
                Node classChilds[] = addClass(child);
                addVariables(classChilds);
                addStandardInitializer();
                addMainHeader();
                addMainMethod(classChilds);
            }
        }
        print();        
    }

    static Node[] addClass(Node node) {
        SimpleNode classSimpleNode = (SimpleNode) node;
        Node[] classChilds = classSimpleNode.jjtGetChildren();

        classIdent = ((ASTIDENT) classChilds[0]).name;
        generated += ".public class " + classIdent;
        nl();
        generated += ".super java/lang/Object";
        nl();

        return classChilds;
    }


    static void addVariables(Node node[]) {
        for (Node n : node) {
            SimpleNode simpleN = (SimpleNode) n;

            if (simpleN.toString().equals("VARIABLE")) {
                generated += ".field " + ((ASTIDENT) simpleN.jjtGetChildren()[1]).name;
                space();

                ASTTYPE typeN = (ASTTYPE)simpleN.jjtGetChildren()[0];
                switch (typeN.typeID) {
                    case STRING:
                        generated += "V";
                        nl();
                        break;
                    case INT:
                        generated += "I";
                        nl();
                        break;
                    case BOOL:
                        generated += "Z";
                        nl();
                        break;
                }
            }
        }
    }


    static void addMainMethod(Node node[]) {
        for (Node n : node){
            SimpleNode simpleN = (SimpleNode) n;
            if (simpleN.toString().equals("MAINMETHOD")) {
                Node mainMethodChilds[] = simpleN.jjtGetChildren();
                for (Node mainBodyCandidate: mainMethodChilds) {
                    SimpleNode mainSimpleNode = (SimpleNode) mainBodyCandidate;
                    if (mainSimpleNode.toString().equals("METHOD_BODY"))
                        // Node mainNode = (Node) mainSimpleNode;
                        // Node methodBodyChilds = mainNode.get
                        addAssigns(mainSimpleNode.jjtGetChildren());
                }
            }
        }
    }

    static void addAssigns(Node methodBodyChilds[]) {
        for (Node candidate : methodBodyChilds)
            if (candidate.toString().equals("ASSIGN")) {
                switch(candidate.jjtGetChild(1).toString()) {
                    case "NEW":
                        addNew(candidate);
                        break;
                    case "SUB":
                        addOperation(candidate);
                        break;
                    case "FUNC_METHOD":
                        addMethodCall(candidate);
                        break;
                    default:
                        addVariableAllocation(candidate);
                        break;
                }
            }
    }

    
    static void addNew(Node candidate) {
        String varIdent = ((ASTIDENT)((SimpleNode)candidate).jjtGetChild(0)).name;
        String classIdent = ((ASTIDENT)((SimpleNode)((SimpleNode)candidate).jjtGetChild(1)).jjtGetChild(0)).name;

        nl();
        tab();
        generated += "new";
        space();
        generated += classIdent;
        nl();
        tab();
        generated += "dup";
        nl();
        tab();
        generated += "invokespecial";
        space();
        generated += classIdent;
        generated += "/<init>()V";
        nl();        
    }


    static void addMethodCall(Node candidate) {
        String varIdent = (((ASTIDENT)candidate.jjtGetChild(0)).name);
        // System.out.println(varIdent);

        Node callNode = candidate.jjtGetChild(1).jjtGetChild(1);
        String funcName = ((ASTIDENT)callNode.jjtGetChild(0)).name;

        Node[] args = ((SimpleNode)callNode.jjtGetChild(1)).jjtGetChildren();
        ArrayList<String> localVars = getFunctionLocals(args);

        nl();
        for(String s: localVars) {
            tab();
            generated += "iload ";
            generated += s;
            nl();
        }

        tab();
        generated += "invokenonvirtual";
        space();
        generated += classIdent;
        generated += "/";
        generated += funcName;
        generated += "(";
        for (int i=0; i < args.length; i++)
            generated += "I"; //hard coded for now
        generated += ")";

        TypeEnum returnType = getMethodReturnType(funcName);        
        generated += parseType(returnType);
    }

    private static String parseType(TypeEnum returnType) {
        switch(returnType) {
            case INT:
                return "I";
            //TODO
            default:
                return "";
        }

    }

    static TypeEnum getMethodReturnType(String methodIdent) {
        Node[] classChilds = ((SimpleNode)classNode).jjtGetChildren();

        for (Node classChild: classChilds) {
            if (classChild.toString().equals("METHOD")) {
                if (((ASTIDENT)((SimpleNode)classChild).jjtGetChild(1)).name.equals(methodIdent)) {
                    return (((ASTTYPE)((SimpleNode)(((SimpleNode) classChild).jjtGetChild(0))).jjtGetChild(0)).typeID);
                }
            }
        }

        return null;
    }


    static ArrayList<String> getFunctionLocals(Node args[]) {
        ArrayList<String> localVars = new ArrayList<String>();
        int counter = 0;
        for (Node n: args) {
            for (int i= 1; i < 99; i++) {
                if (locals[i] != null)
                    if (((ASTIDENT)n).name.equals(locals[i])) {
                        // System.out.println(i);
                        // System.out.println(((ASTIDENT)n).name);
                        localVars.add(Integer.toString(i));
                        counter++;
                    }
            }

        }

        return localVars;
    }


    private static void addOperation(Node candidate) {
        SimpleNode operation = (SimpleNode)candidate.jjtGetChild(1);
        tab();
        generated += "ldc " + ((ASTNUM) operation.jjtGetChild(0)).value;
        nl();
        tab();
        generated += "ldc " + ((ASTNUM) operation.jjtGetChild(1)).value;
        nl();
        tab();
        generated += "isub"; //TODO verificar o tipo de variavel
        nl();
        tab();
        generated += "istore " + localIndex;
        locals[localIndex] = ((ASTIDENT)candidate.jjtGetChild(0)).name;
        localIndex++;
        nl();


    }


    public static void addVariableAllocation(Node assign) {
        Node value = assign.jjtGetChild(1);
        int valueString = ((ASTNUM) value).value;

        nl();   
        tab();
        generated += "bipush";
        space();
        generated += valueString;

        Node identificationNode = assign.jjtGetChild(0);
        String identification = ((ASTIDENT)identificationNode).name;

        TypeEnum type = getVariableType(identification);

        nl();
        tab();
        generated += "istore";
        space();
        generated += localIndex;
        nl();

        locals[localIndex] = identification;
        localIndex++;
    }


    static TypeEnum getVariableType(String identification) {
        Node methodBodyNode = root.jjtGetChild(1).jjtGetChild(1).jjtGetChild(1);
        Node methodBodyChilds[] = ((SimpleNode)methodBodyNode).jjtGetChildren();

        for (Node n: methodBodyChilds) {
            if (n.toString().equals("VARIABLE")) {
                Node identNode = n.jjtGetChild(1);
                String nodeID =((ASTIDENT)identNode).name;

                if (identification.equals(nodeID)) {
                    Node typeNode = n.jjtGetChild(0);
                    return ((ASTTYPE)typeNode).typeID;
                }
            }
        }
        return null;
    }


    public static void addStandardInitializer() {
        nl();
        generated += ".method public<init>()V";
        nl();
        tab();
        generated += "aload_0";
        nl();
        tab();
        generated += "invokenonvirtual java/lang/Object/<init>()V";
        nl();
        tab();
        generated += "return";
        nl();
        generated += ".end method";
        nl();
    }


    public static void addMainHeader() {
        nl();
        generated += ".method public static main([Ljava/lang/String;)V"; 
        nl();
        tab();
        generated += ".limit stack 99"; 
        nl();
        tab();
        generated += ".limit locals 99"; 
        nl();
    }


    public void print() {
        System.out.println("\n\n" + this.generated);
    }


    public static void space() {
        generated += " ";
    }


    public static void nl() {
        generated += "\n";
    }


    public static void tab() {
        generated += "\t";
    }


    public PrintWriter fileToWrite() throws IOException {

        File dir = new File("jvm");
        if (!dir.exists()) dir.mkdirs();

        File file = new File("jvm/jasmin.j");

        if(!file.exists())
            file.createNewFile();

        PrintWriter writer = new PrintWriter(file);
        return writer;
    }
}