import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class FlowNode {
    private ArrayList<FlowNode> successors = new ArrayList<>();
    private ArrayList<FlowNode> predecessors = new ArrayList<>();
    private ArrayList<String> definitions = new ArrayList<>();
    private ArrayList<String> uses = new ArrayList<>();

    public boolean isEmpty(){
        return definitions.isEmpty() && uses.isEmpty();
    }

    public void addSuccessor(FlowNode successor){
        if(!successors.contains(successor)){
            successor.addPredecessor(this);
            successors.add(successor);
        }
    }

    public void addPredecessor(FlowNode predecessor){
        if(!predecessors.contains(predecessor))
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

    public void addUses(ArrayList<String> identifiers){
        Set<String> set = new HashSet<>(this.uses);
        set.addAll(identifiers);
        this.uses.clear();
        this.uses = new ArrayList<>(set);
    }

    public ArrayList<FlowNode> getSuccessors() {
        return successors;
    }

    public ArrayList<FlowNode> getPredecessors() {
        return predecessors;
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
        return " · FlowNode - { def: " + definitions + " | use: " + uses + '}';
    }
}
