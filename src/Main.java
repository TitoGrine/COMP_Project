// import sun.java2d.pipe.SpanShapeRenderer;

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
        boolean r_optimization = false;
        boolean o_optimization = false;
        int k = 0;

        if(args.length > 1){
            int index = 1;

            while(index < args.length){
                String[] arg = args[index].split("=");

                if(arg[0].equals("-r")){
                    if(Integer.parseInt(arg[1]) < 1){

                        System.out.println("Flag -r must receive a positive integer, received " + Integer.parseInt(arg[1]) + ". Ignoring -r flag...");
                        index++;
                        continue;
                    }

                    r_optimization = true;
                    k = Integer.parseInt(arg[1]);
                } else if (arg[0].equals("-o")){
                    o_optimization = true;
                } else {
                    System.out.println("Unable to recognize flag " + args[index] + ". Ignoring...");
                }

                index++;
            }
        }

        try {
            SimpleNode root = parser.Program(); // returns reference to root node

            SemanticAnalysis analysis = new SemanticAnalysis();

            root.eval(analysis);

            analysis.throwErrors();

            if(ControlVars.PRINT_AST){
                System.out.println(ControlVars.CYAN + "\n +++++++++++ Abstract Syntax Tree +++++++++++\n" + ControlVars.RESET);
                root.dump(""); // prints the tree on the screen
            }

            if(ControlVars.GENERATE_JASMIN_CODE){
                new CodeGenerator(r_optimization, k, o_optimization).generate(root, args[0]);
            }

            analysis.showWarnings(ControlVars.THROW_WARNING_EXCEPTION);

        } catch (Exception e) {
            throw e;
        }

        //root.dump(""); // prints the tree on the screen
    }
}
