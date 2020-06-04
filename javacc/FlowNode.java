import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class FlowNode {
    ArrayList<FlowNode> successors = new ArrayList<>();
    ArrayList<FlowNode> predecessors = new ArrayList<>();
    ArrayList<String> definitions = new ArrayList<>();
    ArrayList<String> uses = new ArrayList<>();

    public boolean isEmpty(){
        return definitions.isEmpty() && uses.isEmpty();
    }

    public void addSuccessor(FlowNode successor){
        if(!successors.contains(successor) && !successor.isEmpty())
            successors.add(successor);
    }

    public void addPredecessor(FlowNode predecessor){
        if(!predecessors.contains(predecessor) && !predecessor.isEmpty())
            predecessors.add(predecessor);
    }

    public void addDefinition(String identifier){
        if(!definitions.contains(identifier))
            definitions.add(identifier);
    }

    public void addUse(String identifier){
        if(!uses.contains(identifier))
            uses.add(identifier);
    }

    public ArrayList<String> in(){
        Set<String> set = new HashSet<>(this.out());

        for(String definition : definitions){
            set.remove(definition);
        }

        set.addAll(uses);

        return new ArrayList<>(set);
    }

    public ArrayList<String> out(){
        Set<String> set = new HashSet<>();

        for (FlowNode successor : successors) {
            set.addAll(successor.in());
        }

        return new ArrayList<>(set);
    }

    @Override
    public String toString() {
        return "FlowNode{" +
                "successors=" + successors +
                ", predecessors=" + predecessors +
                ", definitions=" + definitions +
                ", uses=" + uses +
                '}';
    }
}
