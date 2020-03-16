public class Main {
    public static void main(String[] args) {

        Parser parser = new Parser(System.in);

        try {
            SimpleNode root = parser.Program(); // returns reference to root node

            root.dump(""); // prints the tree on the screen
        }catch(Exception e) {
        }
    }
}
