import java.util.ArrayList;
import java.util.HashMap;

public class RIGraph {
    HashMap<String, ArrayList<FlowNode>> liveness;
    ArrayList<RINode> nodes = new ArrayList<>();

    private void buildRIGraph(){
        for(String var : liveness.keySet())
            nodes.add(new RINode(var));


    }

    public RIGraph(HashMap<String, ArrayList<FlowNode>> liveness){
        this.liveness = liveness;
    }

}
