import java.util.ArrayList;
import java.util.List;

public class ArraySymbol extends Symbol{
    List<String> array = new ArrayList<>();
    TypeEnum returnType;

    public ArraySymbol(TypeEnum returnType) {
        super(TypeEnum.ARRAY);
        this.returnType = returnType;
    }

    public ArraySymbol(TypeEnum returnType, ArrayList<String> array) {
        super(TypeEnum.ARRAY);
        this.returnType = returnType;
        this.array = array;
    }

    @Override
    public TypeEnum getType() {
        return super.getType();
    }

    public List<String> getArray() {
        return array;
    }
}
