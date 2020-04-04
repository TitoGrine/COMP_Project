import java.io.FileNotFoundException;

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
                //root.eval();
            } catch (Exception e){
                e.printStackTrace();
            }

            root.dump(""); // prints the tree on the screen
        } catch (ParseException e) {
            e.printStackTrace();
            throw new ParseException("Parser error");
        }
    }
}
