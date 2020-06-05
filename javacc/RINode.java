import java.util.ArrayList;

public class RINode {
    private String id;
    private int color = 0;
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

    public void addConnection(RINode node){
        if(!connections.contains(node)){
            connections.add(node);
            node.addConnection(this);
        }
    }

    public int degree(ArrayList<RINode> removed){
        int degree = 0;

        for(RINode node : connections){
            if(!removed.contains(node))
                degree++;
        }

        return degree;
    }

    public int color(ArrayList<RINode> removed){
        boolean coloured = false;

        while(!coloured){
            coloured = true;
            color++;

            for(RINode node : connections){
                if(!removed.contains(node) && color == node.getColor()){
                    coloured = false;
                    break;
                }
            }
        }

        return color;
    }

    @Override
    public String toString() {
        String output = " · RINode - { id = " + id + " ; color = " + color + "}\n";

        if(connections.size() == 0)
            return output;

        output += "   Connections: \n";
        for(RINode node : connections)
            output += "          - Node { id = " + node.getId() + " }\n";

        return output;
    }
}
