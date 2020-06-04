import java.util.ArrayList;

public class LivenessAnalysis {
    ArrayList<FlowGraph> flowGraphs = new ArrayList<>();

    public void generateFlowGraphs(SimpleNode root){
        SimpleNode classNode = null;
        int numChildren = root.jjtGetNumChildren();
        int index = 0;

        while(classNode == null){
            SimpleNode child = (SimpleNode) root.jjtGetChild(index);

            if(child.equalsNodeType(ParserTreeConstants.JJTCLASS))
                classNode = child;

            index++;
        }

        numChildren = classNode.jjtGetNumChildren();
        index = 0;

        while(index < numChildren){
            SimpleNode child = (SimpleNode) classNode.jjtGetChild(index);

            if(child.equalsNodeType(ParserTreeConstants.JJTMETHOD))
                flowGraphs.add(new FlowGraph(child));

            index++;
        }
    }
}
