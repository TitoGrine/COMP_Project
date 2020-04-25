public class SemanticError {
    public static final String RESET = "\033[0m";  // Text Reset
    public static final String RED_BOLD = "\033[1;31m";    // RED
    public static final String WHITE_UNDERLINED = "\033[4;37m";  // WHITE

    String exception;
    SourceCoords sourceCoords;

    SemanticError(String exception, SourceCoords sourceCoords){
        this.exception = exception;
        this.sourceCoords = sourceCoords;
    }

    public int getLineNum(){
        return sourceCoords.getTokenLine();
    }

    public void printError(){
        System.out.println(RED_BOLD + " ⚠ " + RESET + "Error on " + WHITE_UNDERLINED + "line " + this.sourceCoords.getTokenLine() + RESET + ":");
        System.out.println("   · \"" + exception + "\"\n");
    }
}