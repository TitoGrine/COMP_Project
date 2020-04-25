import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SemanticErrors {
    public static final String RESET = "\033[0m";  // Text Reset
    public static final String RED_BOLD = "\033[1;31m";    // RED
    public static final String YELLOW_UNDERLINED = "\033[4;33m";  // WHITE
    List<SemanticError> errors = new ArrayList<>();

    public void addError(SourceCoords sourceCoords, String exception){
        this.errors.add(new SemanticError(exception, sourceCoords));
    }

    public void throwErrors() throws Exception{
        if(errors.isEmpty())
            return;

        System.out.println(RED_BOLD + "\n    --------- Semantic Errors ---------\n" + RESET);

        Collections.sort(errors, new Comparator<SemanticError>() {
            @Override
            public int compare(SemanticError error1, SemanticError error2) {
                return error1.getLineNum() - error2.getLineNum();
            }
        });

        for(SemanticError error : errors)
            error.printError();

        throw new Exception("Detected " + YELLOW_UNDERLINED + errors.size() + RESET + " semantic " + (errors.size() == 1 ? "error." : "errors."));
    }
}
