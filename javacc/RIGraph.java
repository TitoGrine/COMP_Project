import java.util.ArrayList;
import java.util.HashMap;

public class RIGraph {
    HashMap<String, ArrayList<FlowNode>> liveness;
    HashMap<String, RINode> nodes = new HashMap();

    private boolean interferes(ArrayList<FlowNode> i1, ArrayList<FlowNode> i2){
        for(FlowNode node : i1){
            if(i2.contains(node))
                return true;
        }

        return false;
    }

    private void buildRIGraph(){
        for(String var : liveness.keySet())
            nodes.put(var, new RINode(var));

        HashMap<String, ArrayList<FlowNode>> aux = (HashMap<String, ArrayList<FlowNode>>) liveness.clone();

        for(String key : nodes.keySet()){
            ArrayList<FlowNode> interferences = liveness.get(key);

            for(String var : aux.keySet()){
                if(var.equals(key))
                    continue;

                ArrayList<FlowNode> auxInterferences = liveness.get(var);

                if(interferes(interferences, auxInterferences)){
                    nodes.get(key).addConnection(nodes.get(var));
                }
            }

            aux.remove(key);
        }
    }

    public RIGraph(HashMap<String, ArrayList<FlowNode>> liveness){
        this.liveness = liveness;

        this.buildRIGraph();
    }

    public HashMap<String, Integer> colorGraph(int k) throws Exception {
        ArrayList<RINode> stack = new ArrayList<>();

        int minimum = 0;
        int prevSize = 0;

        while(true){
            while(true){
                for(String key : nodes.keySet()){
                    if(!stack.contains(nodes.get(key)) && nodes.get(key).degree(stack) < k)
                        stack.add(0, nodes.get(key));
                }

                if(prevSize == stack.size())
                    break;

                prevSize = stack.size();
            }

            if(stack.size() == nodes.size())
                break;

            // Select the better candidate for spilling
            RINode candidate = null;

            for(String key : nodes.keySet()){
                if(!stack.contains(nodes.get(key))){
                    if(candidate == null || candidate.degree(stack) < nodes.get(key).degree(stack))
                        candidate = nodes.get(key);
                }
            }

            stack.add(0, candidate);
        }

        HashMap<String, Integer> registers = new HashMap<>();

        while(!stack.isEmpty()){
            RINode node = stack.remove(0);
            int color = node.color(stack);

            registers.put(node.getId(), color - 1);

            if(color > minimum)
                minimum = color;
        }

        if(ControlVars.PRINT_RI_GRAPH){
            print();
            System.out.println("Minimum colors needed: " + minimum + "\n");
        }

        if(k < minimum){
            throw new Exception(String.valueOf(minimum));
        }

        return registers;
    }

    public void print(){
        for(String key : nodes.keySet())
            System.out.println(nodes.get(key) + "\n");
    }
}
