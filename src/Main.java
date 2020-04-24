// import sun.java2d.pipe.SpanShapeRenderer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.SimpleTimeZone;

public class Main {
    
    static String tmp = "";

    public static void main(String[] args) throws ParseException {


        java.io.FileInputStream file = null;
        try {
            file = new java.io.FileInputStream(new java.io.File(args[0]));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Parser parser = new Parser(file);

        try {
            SimpleNode root = parser.Program(); // returns reference to root node

            try{
                root.eval();
                System.out.println("\n"+root.getId()+ "\n");
                root.dump(""); // prints the tree on the screen
                printJasmin(root);
            } catch (Exception e){
                e.printStackTrace();
            }

            //root.dump(""); // prints the tree on the screen

            ////

            //root.printJasmin();

            ///

        } catch (ParseException e) {
            e.printStackTrace();
            throw new ParseException("Parser error");
        }
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

    static void printJasmin(SimpleNode root) throws IOException {

        //PrintWriter file = fileToWrite();


        //(verificar se o root é classe, ver os children e por aí fora)
        Node[] children =  root.jjtGetChildren();

        for(int i = 0; i < children.length; i++) {
            if (children[i].getId() == 6) {
                Node classChilds[] = displayClass(children[i]);
                displayVariables(classChilds);
                displayMainMethod(classChilds);
            }
        }

        System.out.println(tmp);
    }

    static Node[] displayClass(Node node) {
        SimpleNode classSimpleNode = (SimpleNode) node;
        Node[] classChilds = classSimpleNode.jjtGetChildren();

        tmp += ".public class " + ((ASTIDENT) classChilds[0]).name + '\n';
        tmp += ".super java/lang/Object\n";

        return classChilds;
    }

    static void displayVariables(Node node[]) {
        for (Node n : node) {
            SimpleNode simpleN = (SimpleNode) n;
            if (simpleN.toString().equals("VARIABLE")) {

                tmp += ".field " + ((ASTIDENT) simpleN.jjtGetChildren()[1]).name + " ";

                ASTTYPE typeN = (ASTTYPE)simpleN.jjtGetChildren()[0];
                switch (typeN.typeID) {
                    case STRING:
                        tmp += "V\n";
                        break;
                    case INT:
                        tmp += "I\n";
                        break;
                    case BOOL:
                        tmp += "Z\n";
                        break;
                }
            }
        }
    }

    static void displayMainMethod(Node node[]) {
        for (Node n : node){
            SimpleNode simpleN = (SimpleNode) n;
            if (simpleN.toString().equals("MAINMETHOD")) {

            }
        }
    }

}

