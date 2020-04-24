import sun.java2d.pipe.SpanShapeRenderer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.SimpleTimeZone;

public class Main {
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
        String tmp = "";

        //(verificar se o root é classe, ver os children e por aí fora)
        Node[] children =  root.jjtGetChildren();

        //check for classses
        for(int i = 0; i < children.length; i++) {
            if (children[i].getId() == 6) {

                SimpleNode classNode = (SimpleNode) children[i];
                Node[] grandChildren = classNode.jjtGetChildren();

                //get class identifier
                tmp += ".public class " + ((ASTIDENT) grandChildren[0]).name + '\n';
                tmp += ".super java/lang/Object\n";

                //declared variables
                for (Node n : grandChildren) {
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
                //get main method
                for (Node n : grandChildren){
                    SimpleNode simpleN = (SimpleNode) n;
                    if (simpleN.toString().equals("MAINMETHOD")) {


                    }
                }



            }

        }
        System.out.println(tmp);



    }

}
