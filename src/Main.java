// import sun.java2d.pipe.SpanShapeRenderer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.SimpleTimeZone;

public class Main {
    static CodeGenerator codeGenerator;

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
                codeGenerator = new CodeGenerator(root);
                codeGenerator.generate();
            } catch (Exception e){
                e.printStackTrace();
                //throw e;
            }

            //root.dump(""); // prints the tree on the screen

        } catch (ParseException e) {
            e.printStackTrace();
            throw new ParseException("Parser error");
        }
    }

}

