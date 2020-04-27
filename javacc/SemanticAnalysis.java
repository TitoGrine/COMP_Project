import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SemanticAnalysis {
    List<SemanticError> errors = new ArrayList<>();
    List<SemanticWarning> warnings = new ArrayList<>();

    public void addError(SourceCoords sourceCoords, String exception){
        this.errors.add(new SemanticError(exception, sourceCoords));
    }

    public void addWarning(SourceCoords sourceCoords, String exception){
        this.warnings.add(new SemanticWarning(exception, sourceCoords));
    }

    public void showWarnings(boolean throwException) throws Exception{
        if(warnings.isEmpty() || !ControlVars.ANALYSE_SEMANTICS)
            return;

        System.out.println(ControlVars.YELLOW_BOLD + "\n    -------- Semantic Warnings --------\n" + ControlVars.RESET);

        Collections.sort(warnings, (warning1, warning2) -> {
            if(warning1.getLineNum() == warning2.getLineNum())
                return warning1.getColumnNum() - warning2.getColumnNum();

            return warning1.getLineNum() - warning2.getLineNum();
        });

        for(SemanticWarning warning : warnings)
            warning.printWarning();

        if(throwException)
            throw new Exception("Detected " + ControlVars.YELLOW_UNDERLINED + warnings.size() + ControlVars.RESET + " semantic " + (warnings.size() == 1 ? "warning." : "warnings."));
    }

    public void throwErrors() throws Exception{
        if(errors.isEmpty()|| !ControlVars.ANALYSE_SEMANTICS)
            return;

        this.showWarnings(false);

        System.out.println(ControlVars.RED_BOLD + "\n    --------- Semantic Errors ---------\n" + ControlVars.RESET);

        Collections.sort(errors, (error1, error2) -> {
            if(error1.getLineNum() == error2.getLineNum())
                return error1.getColumnNum() - error2.getColumnNum();

            return error1.getLineNum() - error2.getLineNum();
        });

        for(SemanticError error : errors)
            error.printError();

        throw new Exception("Detected " + (warnings.size() > 0 ? (ControlVars.YELLOW_UNDERLINED + warnings.size() + ControlVars.RESET + " semantic " + (warnings.size() == 1 ? "warning and " : "warnings and ")) : "") + ControlVars.RED_UNDERLINED + errors.size() + ControlVars.RESET + " semantic " + (errors.size() == 1 ? "error." : "errors."));
    }
}
