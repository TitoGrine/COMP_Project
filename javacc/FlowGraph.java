import java.util.*;

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

    private void cleanGraph(){
        if(headNode == tailNode)
            return;

        HashSet visitedNodes = new HashSet();
        Queue<FlowNode> nodeQueue = new ArrayDeque<>();

        while(headNode.isEmpty()){
            ArrayList<FlowNode> successors = headNode.getSuccessors();

            if(successors.size() == 1){
                headNode = successors.get(0);
                headNode.deletePredecessors();
            } else if (successors.size() == 0){
                return;
            } else {
                nodeQueue.addAll(successors);
            }

//            visitedNodes.add(headNode);
        }

        nodeQueue.add(headNode);

        while(!nodeQueue.isEmpty()){
            FlowNode node = nodeQueue.poll();

            if(visitedNodes.contains(node))
                continue;

            visitedNodes.add(node);

            for(FlowNode successor : node.getSuccessors()){
                if(!visitedNodes.contains(successor))
                    nodeQueue.add(successor);
            }

            if(node.isEmpty()){
                if(node == tailNode){
                    ArrayList<FlowNode> predecessors = node.getPredecessors();

                    if(predecessors.size() == 1){
                        tailNode = predecessors.get(0);
                        node.link();
                    }
                } else {
                    node.link();
                }
            }
        }
    }

    public FlowGraph(SimpleNode node){

        if(node.equalsNodeType(ParserTreeConstants.JJTMETHOD))
            methodGraph(node);
        else if (node.equalsNodeType(ParserTreeConstants.JJTMAINMETHOD))
            mainMethodGraph(node);
        else
            scopeGraph(node);

        cleanGraph();

        if(ControlVars.PRINT_FLOW_GRAPH)
            print();
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

            System.out.println("Computing: " + child.id);

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

                     break;
                case ParserTreeConstants.JJTSCOPE:
                    result = scopeGraph(child);

                    if(headNode == null){
                        headNode = result.getHeadNode();
                        prevNode = result.getTailNode();
                    } else {
                        auxNode = result.getHeadNode();
                        prevNode.addSuccessor(auxNode);
                        prevNode = result.getTailNode();
                    }

                    break;
                default:

                    if(headNode == null){
                        prevNode = simpleFlowNode(child);
                        headNode = prevNode;
                    } else {
                        auxNode = simpleFlowNode(child);
                        prevNode.addSuccessor(auxNode);
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

        ifFlowGraph.getTailNode().addSuccessor(tailNode);
        elseFlowGraph.getTailNode().addSuccessor(tailNode);
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
            if(((ASTASSIGN) node).definition != null)
                flowNode.addDefinition(((ASTASSIGN) node).definition);
        }

        flowNode.addUses(node.getUses());

        return flowNode;
    }

    public HashMap<String, ArrayList<FlowNode>> analyseLiveness(){
        HashMap<FlowNode, ArrayList<String>> in = new HashMap<>();
        HashMap<FlowNode, ArrayList<String>> out = new HashMap<>();
        Queue<FlowNode> nodeQueue = new ArrayDeque<>();

        nodeQueue.add(tailNode);

        while(!nodeQueue.isEmpty()){
            FlowNode node = nodeQueue.poll();

            if(in.containsKey(node))
                continue;

            in.put(node, new ArrayList<>());
            out.put(node, new ArrayList<>());

            for(FlowNode predecessor : node.getPredecessors()){
                if(!in.containsKey(predecessor))
                    nodeQueue.add(predecessor);
            }
        }

        boolean finished = false;


        while(!finished){
            HashSet visitedNodes = new HashSet();
            finished = true;
            nodeQueue.add(tailNode);

            while(!nodeQueue.isEmpty()){
                FlowNode node = nodeQueue.poll();

                if(visitedNodes.contains(node))
                    continue;

                visitedNodes.add(node);

                for(FlowNode predecessor : node.getPredecessors()){
                    if(!visitedNodes.contains(predecessor))
                        nodeQueue.add(predecessor);
                }

                Set<String> set = new HashSet<>();

                for (FlowNode successor : node.getSuccessors()) {
                    set.addAll(in.get(successor));
                }

                ArrayList<String> nodeOut = new ArrayList<>(set);

                if(!out.get(node).equals(nodeOut))
                    finished = false;

                out.put(node, nodeOut);

                ArrayList<String> nodeIn = node.in(out.get(node));

                if(!in.get(node).equals(nodeIn))
                    finished = false;

                in.put(node, nodeIn);
            }
        }

        HashMap<String, ArrayList<FlowNode>> liveness = new HashMap<>();


        for(FlowNode node : in.keySet()){
            if(ControlVars.PRINT_NODE_TABLE){
                System.out.println(node);
                System.out.println("            - " + ControlVars.PURPLE + "IN " + ControlVars.RESET + " = " + in.get(node));
                System.out.println("            - " + ControlVars.PURPLE + "OUT" + ControlVars.RESET + " = " + out.get(node));
            }

            for(String var : in.get(node)){
                if(!liveness.containsKey(var))
                    liveness.put(var, new ArrayList<>());

                if(!liveness.get(var).contains(node))
                    liveness.get(var).add(node);
            }

            for(String var : out.get(node)){
                if(!liveness.containsKey(var))
                    liveness.put(var, new ArrayList<>());

                if(!liveness.get(var).contains(node))
                    liveness.get(var).add(node);
            }
        }

        if(ControlVars.PRINT_LIVENESS){
            for(String var : liveness.keySet()){
                System.out.println(" Var " + var + " is alive in:");
                for(FlowNode node : liveness.get(var))
                    System.out.println("    " + node);
            }
        }

        return liveness;
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

            if(node.getSuccessors().size() > 0)
                System.out.println("   " + ControlVars.CYAN + "Successors" + ControlVars.RESET  + ": ");

            for(FlowNode successor : node.getSuccessors()){
                System.out.println("            " + successor);

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
