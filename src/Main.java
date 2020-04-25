import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws Exception {

        java.io.FileInputStream file = null;
        try {
            file = new java.io.FileInputStream(new java.io.File(args[0]));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Parser parser = new Parser(file);

        try {
            SimpleNode root = parser.Program(); // returns reference to root node

            SemanticErrors errors = new SemanticErrors();

            try{
                root.eval(errors);

                errors.throwErrors();

                root.dump(""); // prints the tree on the screen
            } catch (Exception e){
                e.printStackTrace();
                //throw e;
            }

        } catch (ParseException e) {
            e.printStackTrace();
            throw new ParseException("Parser error");
        }
    }
}
