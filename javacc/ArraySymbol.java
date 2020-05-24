import java.util.ArrayList;
import java.util.List;

public class ArraySymbol extends Symbol{
    String returnType;

    public ArraySymbol(String returnType) {
        super(ControlVars.ARRAY);
        this.returnType = returnType;
    }

    public String getReturnType() {
        return returnType;
    }
}
