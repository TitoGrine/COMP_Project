import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

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
                root.dump(""); // prints the tree on the screen
            } catch (Exception e){
                e.printStackTrace();
            }

            root.dump(""); // prints the tree on the screen

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

    void printJasmin() {

        PrintWriter file = fileToWrite();


    }

}
