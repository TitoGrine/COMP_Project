public class FlowGraph {
    FlowNode headNode = null;
    FlowNode tailNode = null;

    public FlowNode getHeadNode() {
        return headNode;
    }

    public FlowNode getTailNode() {
        return tailNode;
    }

    public FlowGraph(SimpleNode node){
        switch (node.id){
            case ParserTreeConstants.JJTMETHOD:
                methodGraph(node);
                break;
            case ParserTreeConstants.JJTSCOPE:
                scopeGraph(node);
                break;
            default:
                otherGraph(node);
                break;
        }
    }

    public void methodGraph(SimpleNode node){

    }

    public void scopeGraph(SimpleNode node){

    }

    public void otherGraph(SimpleNode node){

    }
}
