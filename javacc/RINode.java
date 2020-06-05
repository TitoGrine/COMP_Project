import java.util.ArrayList;

public class RINode {
    private String id;
    private int color;
    private ArrayList<RINode> connections = new ArrayList<>();

    public RINode(String id){
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public int getColor() {
        return color;
    }


}
