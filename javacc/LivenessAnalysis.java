import java.util.ArrayList;

public class LivenessAnalysis {
    private static ArrayList<FlowGraph> flowGraphs = new ArrayList<>();

    public static void generateFlowGraphs(SimpleNode root, int k){
        SimpleNode classNode = null;
        int numChildren;
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

            if(child.equalsNodeType(ParserTreeConstants.JJTMETHOD) || child.equalsNodeType(ParserTreeConstants.JJTMAINMETHOD))
                flowGraphs.add(new FlowGraph(child));

            index++;
        }

        for(FlowGraph graph : flowGraphs){

            RIGraph rigraph = new RIGraph(graph.analyseLiveness());
            rigraph.colorGraph(k);
        }
    }
}
