import java.util.ArrayList;
import java.util.HashMap;

public class LivenessAnalysis {
    private static FlowGraph flowGraph;

    public static HashMap<String, Integer> getRegisterAllocation(SimpleNode methodNode, int k) throws Exception {
        if(!methodNode.equalsNodeType(ParserTreeConstants.JJTMETHOD) && !methodNode.equalsNodeType(ParserTreeConstants.JJTMAINMETHOD))
            return null;

        flowGraph = new FlowGraph(methodNode);
        RIGraph rigraph = new RIGraph(flowGraph.analyseLiveness());

        return rigraph.colorGraph(k);
    }
}
