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

    public void addSuccessors(ArrayList<FlowNode> newSuccessors){
        for(FlowNode successor : newSuccessors)
            if(!successors.contains(successor)){
                successor.addPredecessor(this);
                successors.add(successor);
            }
    }

    public void addPredecessor(FlowNode predecessor){
        if(!predecessors.contains(predecessor))
            predecessors.add(predecessor);
    }

    public void removeSuccessor(FlowNode successor){
        successors.remove(successor);
    }

    public void removePredecessor(FlowNode predecessor){
        predecessors.remove(predecessor);
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

    public ArrayList<String> getDefinitions() {
        return definitions;
    }

    public ArrayList<String> getUses() {
        return uses;
    }

    public void deletePredecessors() {
        predecessors = new ArrayList<>();
    }

    public ArrayList<String> in(ArrayList<String> out){
        Set<String> set = new HashSet<>(out);

        for(String definition : definitions){
            set.remove(definition);
        }

        set.addAll(uses);

        return new ArrayList<>(set);
    }

    @Override
    public String toString() {
        return " · " + ControlVars.GREEN_BRIGHT + "FlowNode" + ControlVars.RESET + " - { def: " + definitions + " | use: " + uses + '}';
    }

    public void link() {
        for(FlowNode predecessor : predecessors){
            predecessor.addSuccessors(successors);
            predecessor.removeSuccessor(this);
        }

        for(FlowNode successor : successors)
            successor.removePredecessor(this);
    }
}
