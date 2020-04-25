
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.SimpleTimeZone;

public class CodeGenerator {
    static String generated;
    private SimpleNode root;
    

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
                addMainMethod(classChilds);
                addStandardInitializer();
                addMainHeader();
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
                //Do stuff
            }
        }
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