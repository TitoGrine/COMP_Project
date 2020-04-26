
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.SimpleTimeZone;
import java.util.ArrayList;


public class CodeGenerator {
    static String generated;
    private SimpleNode root;
    static int localIndex = 1;
    private String[] locals = new String[999];
    

    public CodeGenerator(SimpleNode root) {
        this.root = root;
        generated = "";
    }


    public void generate() {
        //(verificar se o root é classe, ver os children e por aí fora)
        Node[] children =  root.jjtGetChildren();

        for(Node child: children) {
            if (child.getId() == 6) {
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

        generated += ".public class " + ((ASTIDENT) classChilds[0]).name;
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
                        //TODO
                        break;
                    case "SUB":
                        addOperation(candidate);
                        break;
                    case "FUNC_METHOD":
                        //TODO
                        break;
                    default:
                        addVariableAllocation(candidate);
                        break;
                }
            }
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
        generated += "isub"; //TODO verificar o tipo de variavel e operação
        nl();
        tab();
        generated += "istore " + localIndex;
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

        // locals[localIndex] = 
        TypeEnum type = getVariableType(assign);

        nl();
        tab();
        generated += "istore";
        space();
        generated += localIndex;
        nl();

        localIndex++;
    }


    static TypeEnum getVariableType(Node assign) {
        Node identificationNode = assign.jjtGetChild(0);
        String identification = ((ASTIDENT)identificationNode).name;

        Node methodBodyNode = assign.jjtGetParent();
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