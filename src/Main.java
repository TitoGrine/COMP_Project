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

            SemanticAnalysis analysis = new SemanticAnalysis();

            root.eval(analysis);

            analysis.throwErrors();

            if(ControlVars.PRINT_AST){
                System.out.println(ControlVars.CYAN + "\n ++++++++++++++ AST ++++++++++++++\n" + ControlVars.RESET);
                root.dump(""); // prints the tree on the screen
            }

            analysis.showWarnings(true);

        } catch (Exception e) {
            throw e;
        }
    }
}
