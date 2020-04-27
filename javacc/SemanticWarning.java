public class SemanticWarning {
    public static final String RESET = "\033[0m";  // Text Reset
    public static final String YELLOW_BRIGHT = "\033[0;93m"; // YELLOW
    public static final String WHITE_UNDERLINED = "\033[4;37m";  // WHITE

    String exception;
    SourceCoords sourceCoords;

    SemanticWarning(String exception, SourceCoords sourceCoords){
        this.exception = exception;
        this.sourceCoords = sourceCoords;
    }

    public int getLineNum(){
        return sourceCoords.getTokenLine();
    }

    public int getColumnNum(){
        return sourceCoords.getTokenColumn();
    }

    public void printWarning(){
        System.out.println(YELLOW_BRIGHT + " ⚠ " + RESET + "Warning on " + "line " + WHITE_UNDERLINED + this.sourceCoords.getTokenLine() + RESET + " column " + WHITE_UNDERLINED + this.sourceCoords.getTokenColumn() + RESET + ":");
        System.out.println("   · \"" + exception + "\"\n");
    }
}

