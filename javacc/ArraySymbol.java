import java.util.ArrayList;
import java.util.List;

public class ArraySymbol extends Symbol{
    TypeEnum returnType;

    public ArraySymbol(TypeEnum returnType) {
        super(TypeEnum.ARRAY);
        this.returnType = returnType;
    }

    public TypeEnum getReturnType() {
        return returnType;
    }
}
