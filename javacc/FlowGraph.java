import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class FlowGraph {
    private FlowNode headNode = null;
    private FlowNode tailNode = null;

    public FlowNode getHeadNode() {
        return headNode;
    }

    public FlowNode getTailNode() {
        return tailNode;
    }

    private FlowGraph(FlowNode head, FlowNode tail){
        headNode = head;
        tailNode = tail;
    }

    public FlowGraph(SimpleNode node){

        if(node.equalsNodeType(ParserTreeConstants.JJTMETHOD))
            methodGraph(node);
        else if (node.equalsNodeType(ParserTreeConstants.JJTMAINMETHOD))
            mainMethodGraph(node);
        else
            scopeGraph(node);
    }

    public void mainMethodGraph(SimpleNode node){
        SimpleNode methodBody = (SimpleNode) node.jjtGetChild(1);

        FlowGraph methodGraph = scopeGraph(methodBody);
        headNode = methodGraph.getHeadNode();
        tailNode = methodGraph.getTailNode();
    }

    public void methodGraph(SimpleNode node){
        SimpleNode methodBody = null;
        SimpleNode returnNode = node;
        int numChildren = node.jjtGetNumChildren();
        int childIndex = 2;

        while(methodBody == null && childIndex < numChildren){
            SimpleNode child = (SimpleNode) node.jjtGetChild(childIndex);

            if(child.equalsNodeType(ParserTreeConstants.JJTMETHOD_BODY)){
                methodBody = child;
                returnNode = (SimpleNode) node.jjtGetChild(childIndex + 1);
            }

            childIndex++;
        }

        FlowGraph methodGraph = scopeGraph(methodBody);
        tailNode = simpleFlowNode(returnNode);
        headNode = methodGraph.getHeadNode();
        FlowNode auxNode = methodGraph.getTailNode();
        auxNode.addSuccessor(tailNode);
    }

    public FlowGraph scopeGraph(SimpleNode node){
        int numChildren = node.jjtGetNumChildren();
        int childIndex = 0;
        FlowNode headNode = null;
        FlowNode prevNode = null;
        FlowNode auxNode;
        FlowGraph result;

        while(childIndex < numChildren) {
            SimpleNode child = (SimpleNode) node.jjtGetChild(childIndex);

            switch(child.id){
                case ParserTreeConstants.JJTIF_ELSE:

                    if(prevNode == null)
                        prevNode = new FlowNode();

                    auxNode = new FlowNode();

                    ifElseGraph((ASTIF_ELSE) child, prevNode, auxNode);

                    if(headNode == null)
                        headNode = prevNode;

                    prevNode = auxNode;

                    break;
                case ParserTreeConstants.JJTWHILE:
                     result = whileGraph((ASTWHILE) child);

                    if(headNode == null){
                        prevNode = new FlowNode();
                        headNode = result.getHeadNode();
                        headNode.addSuccessor(prevNode);
                        auxNode = result.getTailNode();
                        auxNode.addSuccessor(prevNode);
                     } else {
                         auxNode = result.getHeadNode();
                         prevNode.addSuccessor(auxNode);
                         prevNode = new FlowNode();
                         auxNode.addSuccessor(prevNode);
                         auxNode = result.getTailNode();
                         auxNode.addSuccessor(prevNode);
                     }

//                    System.out.println("HEAD: " + headNode);
//                    System.out.println("TAIL: " + prevNode);

                     break;
                case ParserTreeConstants.JJTSCOPE:
                    result = scopeGraph(child);

                    if(headNode == null){
                        headNode = result.getHeadNode();
                        prevNode = result.getTailNode();
                    } else {
                        auxNode = result.getHeadNode();
                        auxNode.addPredecessor(prevNode);
                        prevNode = result.getTailNode();
                    }

                    break;
                default:
                    System.out.println("Well " + child.id);

                    if(headNode == null){
                        prevNode = simpleFlowNode(child);
                        headNode = prevNode;
                    } else {
                        auxNode = simpleFlowNode(child);
//                        System.out.println("\n" + auxNode);
                        prevNode.addSuccessor(auxNode);
                        System.out.println("Successors:");
                        for(FlowNode successor : prevNode.getSuccessors()){
                            System.out.println(successor);
                        }
                        prevNode = auxNode;
                    }

                    break;
            }

            childIndex++;
        }

        // Case where the scope contains nothing
        if(headNode == null){
            headNode = new FlowNode();
            prevNode = headNode;
        }

        return new FlowGraph(headNode, prevNode);
    }

    public FlowGraph whileGraph(ASTWHILE node){
        FlowNode headNode = new FlowNode();
        FlowNode prevNode;

        SimpleNode firstChild = (SimpleNode) node.jjtGetChild(0);
        SimpleNode secondChild = (SimpleNode) node.jjtGetChild(1);

        headNode.addUses(firstChild.getUses());

        if(secondChild.equalsNodeType(ParserTreeConstants.JJTIF_ELSE)){
            prevNode = new FlowNode();

            ifElseGraph((ASTIF_ELSE) secondChild, headNode, prevNode);
        } else {
            FlowGraph result = scopeGraph(secondChild);

            headNode.addSuccessor(result.getHeadNode());
            prevNode = result.getTailNode();
        }

        prevNode.addSuccessor(headNode);

        return new FlowGraph(headNode, prevNode);
    }

    public void ifElseGraph(ASTIF_ELSE node, FlowNode headNode, FlowNode tailNode){
        ASTIF firstChild = (ASTIF) node.jjtGetChild(0);
        ASTELSE secondChild = (ASTELSE) node.jjtGetChild(1);

        FlowGraph ifFlowGraph = ifGraph(firstChild);
        FlowGraph elseFlowGraph = elseGraph(secondChild);

        headNode.addSuccessor(ifFlowGraph.getHeadNode());
        headNode.addSuccessor(elseFlowGraph.getHeadNode());

        tailNode.addPredecessor(ifFlowGraph.getTailNode());
        tailNode.addPredecessor(elseFlowGraph.getTailNode());
    }

    public FlowGraph ifGraph(ASTIF ifNode){
        FlowNode headNode = new FlowNode();
        FlowNode prevNode;

        SimpleNode firstChild = (SimpleNode) ifNode.jjtGetChild(0);
        SimpleNode secondChild = (SimpleNode) ifNode.jjtGetChild(1);

        headNode.addUses(firstChild.getUses());

        if(secondChild.equalsNodeType(ParserTreeConstants.JJTIF_ELSE)){
            prevNode = new FlowNode();

            ifElseGraph((ASTIF_ELSE) secondChild, headNode, prevNode);
        } else {
            FlowGraph result = scopeGraph(secondChild);

            headNode.addSuccessor(result.getHeadNode());
            prevNode = result.getTailNode();
        }

        return new FlowGraph(headNode, prevNode);
    }

    public FlowGraph elseGraph(ASTELSE elseNode){
        SimpleNode firstChild = (SimpleNode) elseNode.jjtGetChild(0);

        return scopeGraph(firstChild);
    }

    public FlowNode simpleFlowNode(SimpleNode node){
        FlowNode flowNode = new FlowNode();

        if(node.equalsNodeType(ParserTreeConstants.JJTASSIGN)){
            flowNode.addDefinition(((ASTASSIGN) node).definition);
        }

        flowNode.addUses(node.getUses());

        return flowNode;
    }

    public void print(){
        HashSet visitedNodes = new HashSet();
        Queue<FlowNode> nodeQueue = new ArrayDeque<>();

        nodeQueue.add(headNode);

        System.out.println(" --------- GRAPH NODES --------- ");

        while(!nodeQueue.isEmpty()){
            FlowNode node = nodeQueue.poll();

            if(visitedNodes.contains(node))
                continue;

            visitedNodes.add(node);

            System.out.println(node);

            for(FlowNode successor : node.getSuccessors()){
                if(!visitedNodes.contains(successor))
                    nodeQueue.add(successor);
            }
        }

        System.out.println("");

        if(!visitedNodes.contains(tailNode)){
            System.out.println("Something isn't right. Tail node is detached from graph.");
        }
    }
}
