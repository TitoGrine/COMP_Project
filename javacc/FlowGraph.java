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
        switch (node.id){
            case ParserTreeConstants.JJTMETHOD:
                methodGraph(node);
                break;
            case ParserTreeConstants.JJTSCOPE:
                scopeGraph(node);
                break;
            default:
                break;
        }
    }

    public void methodGraph(SimpleNode node){
        SimpleNode methodBody = null;
        int numChildren;
        int childIndex = 2;

        while(methodBody == null){
            SimpleNode child = (SimpleNode) node.jjtGetChild(childIndex);

            if(child.equalsNodeType(ParserTreeConstants.JJTMETHOD_BODY))
                methodBody = child;

            childIndex++;
        }

        numChildren = methodBody.jjtGetNumChildren();
        childIndex = 0;
        FlowNode prevNode = null;

        // Find first node with uses or a definition.
        while(childIndex < numChildren){
            SimpleNode child = (SimpleNode) methodBody.jjtGetChild(childIndex);

            FlowGraph possibleBranch = complexNode(node, null);

            childIndex++;

            if(possibleBranch != null){
                this.headNode = possibleBranch.getHeadNode();
                prevNode = possibleBranch.getTailNode();
            }
        }

        if(prevNode == null)
            return;

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
                    if(headNode == null){
                        prevNode = simpleFlowNode(node);
                        headNode = prevNode;
                    } else {
                        auxNode = simpleFlowNode(node);
                        auxNode.addPredecessor(prevNode);
                        prevNode.addSuccessor(auxNode);
                        prevNode = auxNode;
                    }

                    break;
            }
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
}
