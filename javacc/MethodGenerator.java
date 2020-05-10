import java.util.ArrayList;
import java.util.List;

public class MethodGenerator extends CodeGenerator{

    ASTMETHOD methodNode;
    private static List<String> locals;

    MethodGenerator(ASTMETHOD methodNode, ASTCLASS classNode, List<String> classVars, int labelCounter){
        super.classNode = classNode;
        super.classVars = classVars;
        super.labelCounter = labelCounter;

        this.methodNode = methodNode;
        locals = new ArrayList<>(methodNode.localSize);
        locals.add(0, "this"); //TODO: methodName or 'this'?
    }

    private void addArguments(ASTARGUMENTS argumentsNode){
        for(int i = 0; i < argumentsNode.arguments.size(); i++){
            SimpleNode child = (SimpleNode) argumentsNode.jjtGetChild(i);

            if(child.equalsNodeType(ParserTreeConstants.JJTARGUMENT)){
                locals.add(((ASTARGUMENT) child).argName);
            }
        }
    }

    private void addVariable(ASTVARIABLE varNode){
        locals.add(varNode.varName);
    }

    private boolean isStatic(ASTFUNC_METHOD funcNode){
        if(funcNode.symbolTable.existsMethodSymbol(funcNode.call)){
            MethodSymbol methodSymbol = (MethodSymbol) funcNode.symbolTable.getSymbol(funcNode.call);
            return methodSymbol.isStatic(funcNode.arguments);
        }

        return false;
    }

    private String addStoreVar(SimpleNode node, int identation){
        String varName;
        String storeCode = tab(identation);
        boolean arrayAccess = false;

        if(node.equalsNodeType(ParserTreeConstants.JJTARRAY_ACCESS)){
            varName = ((ASTARRAY_ACCESS) node).object;
            arrayAccess = true;
        }
        else
            varName = ((ASTIDENT) node).name;

        int index = locals.indexOf(varName);

        if(index != -1){
            if(arrayAccess){
                storeCode += "aload_" + index + nl();
                storeCode += generateTypeSensitiveCode((SimpleNode) ((ASTARRAY_ACCESS) node).jjtGetChild(1), identation);

            } else{
                TypeEnum type = node.symbolTable.getSymbol(varName).getType();

                if(type == TypeEnum.ARRAY || type == TypeEnum.OBJECT)
                    storeCode += "astore_"; //TODO: Confirm its always the case
                else
                    storeCode += "istore_";

                storeCode += index + nl();
            }
        } else {
            index = classVars.indexOf(varName); //TODO: Necessary?
            storeCode += "aload_0" + nl(); //TODO: Check if it's in the correct place (first line of assign?)

            if(arrayAccess){
                storeCode += tab(identation) + "getfield " + classNode.className + "/" + varName + space() + getJasminType(varName, node) + nl();
                storeCode += generateTypeSensitiveCode((SimpleNode) ((ASTARRAY_ACCESS) node).jjtGetChild(1), identation);
            } else{
                storeCode += tab(identation) + "putfield " + classNode.className + "/" + varName + space() + getJasminType(varName, node) + nl();
            }
        }

        return storeCode;
    }

    private String parametersCode(){
        String code = "";
        List<TypeEnum> parameters = methodNode.parameters;

        for (TypeEnum parameterType : parameters) {
            code += getSimpleJasminType(parameterType);
        }

        return code;
    }

    private String argumentsCode(ASTFUNC_METHOD funcNode){
        String code = "";
        List<TypeEnum> arguments = funcNode.arguments;

        for (TypeEnum argumentType : arguments) {
            code += getSimpleJasminType(argumentType);
        }

        return code;
    }

    private String methodReturnCode(ASTFUNC_METHOD funcNode){
        TypeEnum returnType = TypeEnum.VOID;

        if(funcNode.symbolTable.existsMethodSymbol(funcNode.call)){
            MethodSymbol methodSymbol = (MethodSymbol) funcNode.symbolTable.getSymbol(funcNode.call);
            returnType = methodSymbol.getReturnType(funcNode.arguments);
        }

        return getSimpleJasminType(returnType);
    }

    private String generateStackCode(){
        String stackCode = "";

        stackCode += tab() + ".limit stack " + 99 + nl(); //TODO: Provisório!!
        stackCode += tab() + ".limit locals " + methodNode.localSize + nl();

        return stackCode;
    }

    private String generateMethodHeader(){
        String headerCode = "";

        headerCode += ".method public " + methodNode.methodName + "(" + parametersCode() + ")" + getSimpleJasminType(methodNode.returnType) + nl();
        headerCode += generateStackCode() + nl();

        return headerCode;
    }

    private String generateMainMethodHeader(){
        String headerCode = "";

        headerCode += ".method public static main([Ljava/lang/String;)V" + nl();
        headerCode += generateStackCode() + nl();

        return headerCode;
    }

    private String generateNumCode(ASTNUM numNode, int identation){
        return tab(identation) + (numNode.value > -2 && numNode.value < 6 ? "iconst_" :"bipush ") + numNode.value;
    }

    private String generateBoolCode(ASTBOOL boolNode, int identation){
        return tab(identation) + (boolNode.truth_value ? "iconst_1" : "iconst_0");
    }

    private String generateOperationCode(SimpleNode operationNode, int identation){
        String operationCode = "";
        boolean binaryOperation = operationNode.jjtGetNumChildren() > 1;

        SimpleNode firstchild = (SimpleNode) operationNode.jjtGetChild(0);
        operationCode += generateTypeSensitiveCode(firstchild, identation);

        if(binaryOperation){
            SimpleNode secondChild = (SimpleNode) operationNode.jjtGetChild(1);
            operationCode += generateTypeSensitiveCode(secondChild, identation);
        }

        operationCode += tab(identation);

        switch(operationNode.id){
            case ParserTreeConstants.JJTADD:
                operationCode += "iadd";
                break;
            case ParserTreeConstants.JJTSUB:
                operationCode += "isub";
                break;
            case ParserTreeConstants.JJTMUL:
                operationCode += "imul";
                break;
            case ParserTreeConstants.JJTDIV:
                operationCode += "idiv";
                break;
            case ParserTreeConstants.JJTAND:
                operationCode += "iand";
                break;
            case ParserTreeConstants.JJTNEGATION:
                operationCode += "ineg";
                break;
            case ParserTreeConstants.JJTLESSTHAN:
                operationCode += "if_cmple label_" + labelCounter + nl();
                operationCode += tab(identation + 1) + "iconst_0" + nl();
                operationCode += tab(identation + 1) + "goto label_" + (labelCounter + 1) + nl();
                operationCode += tab(identation) + "label_" + labelCounter + ":" + nl();
                operationCode += tab(identation + 1) + "iconst_1" + nl();
                operationCode += tab(identation) + "label_" + (labelCounter + 1) + ":";

                labelCounter += 2;
                break;
            default:
                break;
        }

        return operationCode;
    }

    private String generateArrayAccessCode(ASTARRAY_ACCESS accessNode, int identation){
        SimpleNode firstChild = (SimpleNode) accessNode.jjtGetChild(0);
        SimpleNode secondChild = (SimpleNode) accessNode.jjtGetChild(1);

        String accessCode = "";
        accessCode += generateTypeSensitiveCode(firstChild, identation);
        accessCode += generateTypeSensitiveCode(secondChild, identation);
        accessCode += tab(identation) + "iaload";

        return accessCode;
    }

    private String generateNewArrayCode(ASTNEW_ARRAY arrayNode, int identation){
        SimpleNode child = (SimpleNode) arrayNode.jjtGetChild(0);

        return generateTypeSensitiveCode(child, identation) + tab(identation) + "newarray int";
    }

    private String generateLengthCode(ASTLENGTH lengthNode, int identation){
        SimpleNode child = (SimpleNode) lengthNode.jjtGetChild(0);

        return generateTypeSensitiveCode(child, identation) + tab(identation) + "arraylength"; //TODO: Only for arrays?
    }

    private String generateVarCode(ASTIDENT identNode, int identation){
        String varCode = "";
        String varName = identNode.name;
        int index = locals.indexOf(varName);

        if(index != -1){
            TypeEnum varType = ((Symbol) identNode.symbolTable.getSymbol(varName)).getType();

            varCode += tab(identation);

            if(varType == TypeEnum.ARRAY)
                varCode += "aload_" + index;
            else
                varCode += "iload_" + index;
        } else {
            index = classVars.indexOf(varName); //TODO: necessary?

            varCode += tab(identation) + "aload_0" + nl();
            varCode += tab(identation) + "getfield " + classNode.className + "/" + varName + space() + getJasminType(varName, identNode);
        }

        return varCode;
    }

    private String generateTypeSensitiveCode(SimpleNode child, int identation){
        return generateTypeSensitiveCode(child, identation, false);
    }

    private String generateTypeSensitiveCode(SimpleNode child, int identation, boolean assigned){
        String code = "";

        switch(child.id){
            case ParserTreeConstants.JJTNUM:
                code += generateNumCode((ASTNUM) child, identation) + nl();
                break;
            case ParserTreeConstants.JJTBOOL:
                code += generateBoolCode((ASTBOOL) child, identation) + nl();
                break;
            case ParserTreeConstants.JJTADD:
            case ParserTreeConstants.JJTSUB:
            case ParserTreeConstants.JJTMUL:
            case ParserTreeConstants.JJTDIV:
            case ParserTreeConstants.JJTAND:
            case ParserTreeConstants.JJTLESSTHAN:
            case ParserTreeConstants.JJTNEGATION:
                code += generateOperationCode(child, identation) + nl();
                break;
            case ParserTreeConstants.JJTNEW:
                code += generateNewCode((ASTNEW) child) + nl();
                break;
            case ParserTreeConstants.JJTNEW_ARRAY:
                code += generateNewArrayCode((ASTNEW_ARRAY) child, identation) + nl();
                break;
            case ParserTreeConstants.JJTARRAY_ACCESS:
                code += generateArrayAccessCode((ASTARRAY_ACCESS) child, identation) + nl();
                break;
            case ParserTreeConstants.JJTFUNC_METHOD:
                code += generateCallCode((ASTFUNC_METHOD) child) + nl();;

                /*if(!assigned)
                    code += nl() + tab(identation) + "pop";*/ //TODO: Check if this is needed

                break;
            case ParserTreeConstants.JJTLENGTH:
                code += generateLengthCode((ASTLENGTH) child, identation) + nl(); //TODO: generateLengthCode((ASTLENGTH) child);
                break;
            case ParserTreeConstants.JJTIDENT:
                code += generateVarCode((ASTIDENT) child, identation) + nl();
                break;
            default:
                break;
        }

        return code;
    }

    private String generateReturnCode(ASTRETURN_EXP returnNode){
        SimpleNode child = (SimpleNode) returnNode.jjtGetChild(0);
        String returnCode = "";

        returnCode += generateTypeSensitiveCode(child, 1);
        returnCode += tab();

        if(returnNode.expType == TypeEnum.OBJECT || returnNode.expType == TypeEnum.ARRAY)
            returnCode += "areturn";
        else if(returnNode.expType == TypeEnum.VOID)
            returnCode += "return";
        else
            returnCode += "ireturn";

        returnCode += nl();

        return returnCode;
    }

    private String generateScopeCode(SimpleNode scopeNode, int identation){
        String scopeCode = "";
        int numChildren = scopeNode.jjtGetNumChildren();
        int childIndex = 0;
        SimpleNode child;

        while(childIndex < numChildren){
            child = (SimpleNode) scopeNode.jjtGetChild(childIndex);

            switch(child.id){
                case ParserTreeConstants.JJTVARIABLE:
                    addVariable((ASTVARIABLE) child);
                    break;
                case ParserTreeConstants.JJTASSIGN:
                    scopeCode += generateAssignCode((ASTASSIGN) child, identation);
                    break;
                case ParserTreeConstants.JJTSCOPE:
                    scopeCode += generateScopeCode(child, identation);
                    break;
                case ParserTreeConstants.JJTIF_ELSE:
                    scopeCode += generateConditionalCode((ASTIF_ELSE) child, identation);
                    break;
                default:
                    scopeCode += generateTypeSensitiveCode(child, identation);
                    break;
            }

            childIndex++;
        }

        return scopeCode;
    }

    private String generateAssignCode(ASTASSIGN assignNode, int identation){
        String assignCode = "";
        SimpleNode firstChild = (SimpleNode) assignNode.jjtGetChild(0);
        SimpleNode secondChild = (SimpleNode) assignNode.jjtGetChild(1);

        if(firstChild.equalsNodeType(ParserTreeConstants.JJTARRAY_ACCESS)){
            assignCode += addStoreVar(firstChild, identation);
            assignCode += generateTypeSensitiveCode(secondChild, identation, true);
            assignCode += tab(identation) + "iastore" + nl();
        } else {
            assignCode += generateTypeSensitiveCode(secondChild, identation, true);
            assignCode += addStoreVar(firstChild, identation);
        }

        assignCode += nl();

        return assignCode;
    }

    private String generateCallCode(ASTFUNC_METHOD funcNode){
        String callCode = "";
        SimpleNode objectNode = (SimpleNode) funcNode.jjtGetChild(0);
        ASTCALL callNode = (ASTCALL) funcNode.jjtGetChild(1);
        int numArgs = funcNode.arguments.size();

        if(!isStatic(funcNode)){
            if(objectNode.equalsNodeType(ParserTreeConstants.JJTTHIS))
                callCode += tab() + "aload_0";
            else if(objectNode.equalsNodeType(ParserTreeConstants.JJTIDENT))
                callCode += tab() + "aload_" + locals.indexOf(((ASTIDENT) objectNode).name);
            else if(objectNode.equalsNodeType(ParserTreeConstants.JJTFUNC_METHOD))
                callCode += tab() + generateCallCode((ASTFUNC_METHOD) objectNode); //TODO: Probs wrong
            else if(objectNode.equalsNodeType(ParserTreeConstants.JJTNEW))
                callCode += generateNewCode((ASTNEW) objectNode); //TODO: Probs wrong

            callCode += nl();
        }

        if(numArgs > 0){
            SimpleNode child;
            int childIndex = 0;
            ASTARGUMENTS argumentsNode = (ASTARGUMENTS) callNode.jjtGetChild(1);

            while(childIndex < numArgs){
                child = (SimpleNode) argumentsNode.jjtGetChild(childIndex);

                callCode += generateTypeSensitiveCode(child, 1);

                childIndex++;
            }
        }

        callCode += tab() + (isStatic(funcNode) ? "invokestatic " : "invokevirtual ") + funcNode.call.replace('.', '/');
        callCode += "(" + argumentsCode(funcNode) + ")" + methodReturnCode(funcNode);

        return callCode;
    }

    private String generateNewCode(ASTNEW newNode){
        String newCode = "";

        newCode += tab() + "new " + newNode.object + nl();
        newCode += tab() + "dup" + nl();
        newCode += tab() + "invokespecial " + newNode.object + "/<init>()V";

        return newCode;
    }

    private String generateConditionCode(ASTCONDITION conditionNode, int identation){
        String conditionCode = "";
        SimpleNode child = (SimpleNode) conditionNode.jjtGetChild(0);
        int conditionalIndex = conditionalCounter;

        switch(child.id){
            case ParserTreeConstants.JJTLESSTHAN:
                SimpleNode leftSideNode = (SimpleNode) ((ASTLESSTHAN) child).jjtGetChild(0);
                SimpleNode rightSideNode = (SimpleNode) ((ASTLESSTHAN) child).jjtGetChild(1);

                conditionCode += generateTypeSensitiveCode(leftSideNode, identation);
                conditionCode += generateTypeSensitiveCode(rightSideNode, identation);
                conditionCode += tab(identation) + "if_icmplt";

                break;
            default:
                conditionCode += generateTypeSensitiveCode(child, identation);
                conditionCode += tab(identation) + "ifeq";
                break;
        }

        conditionCode += space() + "else_" + conditionalIndex + nl();

        conditionalCounter++;

        return conditionCode;
    }

    private String generateConditionalCode(ASTIF_ELSE conditionalNode, int identation){
        ASTIF ifNode = (ASTIF) conditionalNode.jjtGetChild(0);
        ASTELSE elseNode = (ASTELSE) conditionalNode.jjtGetChild(1);

        ASTCONDITION conditionNode = (ASTCONDITION) ifNode.jjtGetChild(0);
        SimpleNode ifScopeNode = (SimpleNode) ifNode.jjtGetChild(1);

        String conditionalCode = "";
        int conditionalIndex = conditionalCounter;

        conditionalCode += generateConditionCode(conditionNode, identation);
        conditionalCode += generateScopeCode(ifScopeNode, identation + 1);
        conditionalCode += tab(identation + 1) + "goto endif_" + conditionalIndex + nl();
        conditionalCode += tab(identation) + "else_" + conditionalIndex + entry() + nl();
        conditionalCode += generateScopeCode(elseNode, identation + 1);
        conditionalCode += tab(identation) + "endif_" + conditionalIndex + entry() + nl(2);

        return conditionalCode;
    }

    public String generateMethodCode(){
        String methodCode = nl() + generateMethodHeader();
        int numChildren = methodNode.jjtGetNumChildren();
        int childIndex = 0;
        SimpleNode child;

        while(childIndex < numChildren){
            child = (SimpleNode) methodNode.jjtGetChild(childIndex);

            if(child.equalsNodeType(ParserTreeConstants.JJTARGUMENTS))
                addArguments((ASTARGUMENTS) child);
            else if(child.equalsNodeType(ParserTreeConstants.JJTMETHOD_BODY))
                methodCode += generateScopeCode(child, 1);
            else if(child.equalsNodeType(ParserTreeConstants.JJTRETURN_EXP))
                methodCode += generateReturnCode((ASTRETURN_EXP) child);

            childIndex++;
        }

        methodCode += ".end method";

        return methodCode;
    }

    public String generateMainMethodCode(){
        String methodCode = nl() + generateMainMethodHeader();
        ASTIDENT firstChild = (ASTIDENT) methodNode.jjtGetChild(0);
        SimpleNode secondChild = (SimpleNode) methodNode.jjtGetChild(1);

        locals.add(firstChild.name);

        methodCode += generateScopeCode(secondChild, 1);
        methodCode += tab() + "return" + nl();
        methodCode += ".end method";

        return methodCode;
    }
}
