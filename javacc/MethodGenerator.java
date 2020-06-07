import java.util.*;

public class MethodGenerator extends CodeGenerator{

    ASTMETHOD methodNode;
    private HashMap<String, Integer> locals;
    private ArrayDeque<HashMap<String, Integer>> constants;
    private int localsSize = 0;
    private int maxStack = 0;
    private int stack = 0;
    private boolean r_optimized = false;
    private boolean o_optimized = false;
    private boolean inDynamic = false;

    public MethodGenerator(ASTMETHOD methodNode, ASTCLASS classNode, List<String> classVars, Counter counter, boolean o_optimized){
        super.classNode = classNode;
        super.classVars = classVars;
        super.counter = counter;
        this.methodNode = methodNode;
        locals = new HashMap<>();
        constants = new ArrayDeque<>();
        constants.add(new HashMap<>());
        localsSize = methodNode.localSize;
        this.o_optimized = o_optimized;
    }

    public MethodGenerator(ASTMETHOD methodNode, ASTCLASS classNode, List<String> classVars, Counter counter, HashMap<String, Integer> locals, boolean o_optimized){
        super.classNode = classNode;
        super.classVars = classVars;
        super.counter = counter;
        this.methodNode = methodNode;
        this.locals = locals;
        constants = new ArrayDeque<>();
        constants.add(new HashMap<>());

        for(String key : locals.keySet())
            localsSize = Math.max(localsSize, locals.get(key) + 1);

        r_optimized = true;
        this.o_optimized = o_optimized;
    }

    private void resetConstant(String var){
        for(HashMap<String, Integer> table : constants){
            table.remove(var);
        }
    }

    private int getRegister(String key){
        if(locals.containsKey(key))
            return locals.get(key);

        return -1;
    }

    private void pushStack(int n){
        stack += n;
    }

    private void popStack(int n){
        maxStack = Math.max(stack, maxStack);
        stack -= n;
    }

    private void addArguments(ASTARGUMENTS argumentsNode){
        for(int i = 0; i < argumentsNode.arguments.size(); i++){
            SimpleNode child = (SimpleNode) argumentsNode.jjtGetChild(i);

            if(child.equalsNodeType(ParserTreeConstants.JJTARGUMENT)){
                if(!locals.containsKey(((ASTARGUMENT) child).argName) && !r_optimized)
                    locals.put(((ASTARGUMENT) child).argName, locals.size());
            }
        }
    }

    private void addVariable(ASTVARIABLE varNode){
        if(!locals.containsKey(varNode.varName) && !r_optimized)
            locals.put(varNode.varName, locals.size());
    }

    private boolean isStatic(ASTFUNC_METHOD funcNode){
        if(funcNode.symbolTable.existsMethodSymbol(funcNode.call)){
            MethodSymbol methodSymbol = (MethodSymbol) funcNode.symbolTable.getSymbol(funcNode.call);
            return methodSymbol.isStatic(funcNode.arguments);
        }

        return false;
    }

    private String checkSpecialCompare(SimpleNode lhside, SimpleNode rhside){
        if (rhside.equalsNodeType(ParserTreeConstants.JJTNUM)) {
            int num = ((ASTNUM) rhside).value;

            if (num == 0){
                popStack(1);
                return "ifge";
            }
        }

        return null;
    }

    private String checkSpecialAddition(SimpleNode leftNode, SimpleNode rightNode){
        String varName;

        if(leftNode.equalsNodeType(ParserTreeConstants.JJTIDENT)) {
            varName = ((ASTIDENT) leftNode).name;

            if (rightNode.equalsNodeType(ParserTreeConstants.JJTADD) || rightNode.equalsNodeType(ParserTreeConstants.JJTSUB)) {
                SimpleNode lhside = (SimpleNode) rightNode.jjtGetChild(0);
                SimpleNode rhside = (SimpleNode) rightNode.jjtGetChild(1);

                if (lhside.equalsNodeType(ParserTreeConstants.JJTIDENT) && rhside.equalsNodeType(ParserTreeConstants.JJTNUM)) {
                    int num = ((ASTNUM) rhside).value;

                    boolean addition = rightNode.equalsNodeType(ParserTreeConstants.JJTADD);

                    if (((ASTIDENT) lhside).name.equals(varName) && num < 127 && num > -128){
                        int index = getRegister(varName);

                        if(index != -1)
                            return "iinc " + getRegister(varName) + space() + (addition ? "" : "-") + num;
                    }
                }
            }
        }

        return null;
    }

    private String constantFolding(SimpleNode operationNode, int indentation){
        SimpleNode leftSide = (SimpleNode) operationNode.jjtGetChild(0);
        SimpleNode rightSide = (SimpleNode) operationNode.jjtGetChild(1);

        if(leftSide.equalsNodeType(ParserTreeConstants.JJTNUM) && rightSide.equalsNodeType(ParserTreeConstants.JJTNUM)){
            int val1 = ((ASTNUM) leftSide).value;
            int val2 = ((ASTNUM) rightSide).value;

            int result;

            switch(operationNode.id){
                case ParserTreeConstants.JJTADD:
                    result = val1 + val2;
                    break;
                case ParserTreeConstants.JJTSUB:
                    result = val1 - val2;
                    break;
                case ParserTreeConstants.JJTMUL:
                    result = val1 * val2;
                    break;
                case ParserTreeConstants.JJTDIV:
                    result = val1 / val2;
                    break;
                case ParserTreeConstants.JJTLESSTHAN:
                    result = val1 < val2 ? 1 : 0;
                    break;
                default:
                    return "";
            }

            pushStack(1);
            return tab(indentation) + constantInstruction(result);
        } else if (leftSide.equalsNodeType(ParserTreeConstants.JJTBOOL) && rightSide.equalsNodeType(ParserTreeConstants.JJTBOOL)){
            boolean val1 = ((ASTBOOL) leftSide).truth_value;
            boolean val2 = ((ASTBOOL) rightSide).truth_value;

            int result = val1 && val2 ? 1 : 0;

            pushStack(1);
            return tab(indentation) + constantInstruction(result);
        }

        return "";
    }

    private boolean uselessAssign(SimpleNode varNode){
        String varName;

        if(varNode.equalsNodeType(ParserTreeConstants.JJTARRAY_ACCESS))
            varName = ((ASTARRAY_ACCESS) varNode).object;
        else
            varName = ((ASTIDENT) varNode).name;

        return !varName.contains("this.") && !locals.containsKey(varName) && ControlVars.IGNORE_USELESS_ASSIGNS;
    }

    private String addStoreVar(SimpleNode varNode, SimpleNode expNode, int indentation){
        String varName;
        String storeCode = "";
        boolean arrayAccess = false;

        if(varNode.equalsNodeType(ParserTreeConstants.JJTARRAY_ACCESS)){
            varName = ((ASTARRAY_ACCESS) varNode).object;
            arrayAccess = true;
        }
        else
            varName = ((ASTIDENT) varNode).name;

        int index = getRegister(varName);

        if(index != -1){

            if(arrayAccess){
                pushStack(1);
                storeCode += tab(indentation);
                storeCode += "aload" + (index < 4 ? us() : space()) + index + nl();
                storeCode += generateTypeSensitiveCode((SimpleNode) varNode.jjtGetChild(1), indentation, true);
                storeCode += generateTypeSensitiveCode(expNode, indentation, true);
            } else{
                String type = varNode.symbolTable.getSymbol(varName).getType();
                storeCode += generateTypeSensitiveCode(expNode, indentation, true);
                storeCode += tab(indentation);

                popStack(1);
                switch(type){
                    case ControlVars.BOOL:
                    case ControlVars.INT:
                    case ControlVars.VOID:
                        storeCode += "istore";
                        break;
                    default:
                        storeCode += "astore";
                        break;
                }

                storeCode += (index < 4 ? us() : space()) + index + nl();
            }
        } else {
            index = classVars.indexOf(varName); //TODO: Necessary?
            pushStack(1);
            storeCode += tab(indentation) + "aload_0" + nl();

            if(arrayAccess){
                storeCode += tab(indentation) + "getfield " + classNode.className + "/" + cleanseVar(varName) + space() + getJasminType(varName, varNode) + nl();
                storeCode += generateTypeSensitiveCode((SimpleNode) ((ASTARRAY_ACCESS) varNode).jjtGetChild(1), indentation, true);
                storeCode += generateTypeSensitiveCode(expNode, indentation, true);
            } else{
                storeCode += generateTypeSensitiveCode(expNode, indentation, true);
                popStack(2);
                storeCode += tab(indentation) + "putfield " + classNode.className + "/" + cleanseVar(varName) + space() + getJasminType(varName, varNode) + nl();
            }
        }

        return storeCode;
    }

    private String parametersCode(){
        String code = "";
        List<String> parameters = methodNode.parameters;

        for (String parameterType : parameters) {
            code += getSimpleJasminType(parameterType);
        }

        return code;
    }

    private String argumentsCode(ASTFUNC_METHOD funcNode){
        String code = "";
        List<String> arguments = funcNode.arguments;

        for (String argumentType : arguments) {
            code += getSimpleJasminType(argumentType);
        }

        return code;
    }

    private String methodReturnCode(ASTFUNC_METHOD funcNode){
        String returnType = ControlVars.VOID;

        if(funcNode.symbolTable.existsMethodSymbol(funcNode.call)){
            MethodSymbol methodSymbol = (MethodSymbol) funcNode.symbolTable.getSymbol(funcNode.call);
            returnType = methodSymbol.getReturnType(funcNode.arguments);
        }

        return getSimpleJasminType(returnType);
    }

    private String generateStackCode(){
        String stackCode = "";

        stackCode += tab() + ".limit stack " + maxStack + nl();
        stackCode += tab() + ".limit locals " + localsSize + nl();

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

    private String generateNumCode(ASTNUM numNode, int indentation){
        pushStack(1);
        return tab(indentation) + constantInstruction(numNode.value);
    }

    private String generateBoolCode(ASTBOOL boolNode, int indentation){
        pushStack(1);
        return tab(indentation) + (boolNode.truth_value ? "iconst_1" : "iconst_0");
    }

    private String generateOperationCode(SimpleNode operationNode, int indentation){
        String operationCode = "";
        String leftSide, rightSide = "";
        boolean binaryOperation = operationNode.jjtGetNumChildren() > 1;

        if(o_optimized && binaryOperation){
            String constantFolding = constantFolding(operationNode, indentation);

            if(!constantFolding.equals(""))
                return constantFolding;
        }

        SimpleNode firstchild = (SimpleNode) operationNode.jjtGetChild(0);
        SimpleNode secondChild = null;

        leftSide = generateTypeSensitiveCode(firstchild, indentation, true);

        if(binaryOperation){
            secondChild = (SimpleNode) operationNode.jjtGetChild(1);

            rightSide = generateTypeSensitiveCode(secondChild, indentation + (operationNode.equalsNodeType(ParserTreeConstants.JJTAND) ? 1 : 0), true);
        }

        switch(operationNode.id){
            case ParserTreeConstants.JJTADD:
                operationCode += leftSide;
                operationCode += rightSide;
                popStack(1);
                operationCode += tab(indentation) + "iadd";
                break;
            case ParserTreeConstants.JJTSUB:
                operationCode += leftSide;
                operationCode += rightSide;
                popStack(1);
                operationCode += tab(indentation) + "isub";
                break;
            case ParserTreeConstants.JJTMUL:
                operationCode += leftSide;
                operationCode += rightSide;
                popStack(1);
                operationCode += tab(indentation) + "imul";
                break;
            case ParserTreeConstants.JJTDIV:
                operationCode += leftSide;
                operationCode += rightSide;
                popStack(1);
                operationCode += tab(indentation) + "idiv";
                break;
            case ParserTreeConstants.JJTAND:
                operationCode += leftSide;
                popStack(1);
                operationCode += tab(indentation) + "ifeq and_" + counter.andCounter + nl();
                operationCode += rightSide;
                popStack(1);
                operationCode += tab(indentation + 1) + "ifeq and_" + counter.andCounter + nl();
                operationCode += tab(indentation + 2) + "iconst_1" + nl();
                operationCode += tab(indentation + 2) + "goto end_and_" + counter.andCounter + nl();
                operationCode += tab(indentation) + "and_" + counter.andCounter + entry() + nl();
                operationCode += tab(indentation + 1) + "iconst_0" + nl();
                operationCode += tab(indentation) + "end_and_" + counter.andCounter + entry() + nl();
                pushStack(1);
                counter.andCounter++;
                break;
            case ParserTreeConstants.JJTNEGATION:
                operationCode += leftSide;
                popStack(1);
                operationCode += tab(indentation) + "ifeq neg_" + counter.negCounter + nl();
                operationCode += tab(indentation + 1) + "iconst_0" + nl();
                operationCode += tab(indentation + 1) + "goto end_neg_" + counter.negCounter + nl();
                operationCode += tab(indentation) + "neg_" + counter.negCounter + entry() + nl();
                operationCode += tab(indentation + 1) + "iconst_1" + nl();
                operationCode += tab(indentation) + "end_neg_" + counter.negCounter + entry();
                pushStack(1);
                counter.negCounter++;

                break;
            case ParserTreeConstants.JJTLESSTHAN:
                operationCode += leftSide;
                operationCode += rightSide;
                popStack(2);
                operationCode += tab(indentation) + "if_icmplt less_" + counter.lessCounter + nl();
                operationCode += tab(indentation + 1) + "iconst_0" + nl();
                operationCode += tab(indentation + 1) + "goto end_less_" + counter.lessCounter + nl();
                operationCode += tab(indentation) + "less_" + counter.lessCounter + entry() + nl();
                operationCode += tab(indentation + 1) + "iconst_1" + nl();
                operationCode += tab(indentation) + "end_less_" + counter.lessCounter + entry();
                pushStack(1);
                counter.lessCounter++;
                break;
            default:
                break;
        }

        return operationCode;
    }

    private String generateArrayAccessCode(ASTARRAY_ACCESS accessNode, int indentation){
        SimpleNode firstChild = (SimpleNode) accessNode.jjtGetChild(0);
        SimpleNode secondChild = (SimpleNode) accessNode.jjtGetChild(1);

        String accessCode = "";
        accessCode += generateTypeSensitiveCode(firstChild, indentation, true);
        accessCode += generateTypeSensitiveCode(secondChild, indentation, true);
        popStack(1);
        accessCode += tab(indentation) + "iaload";

        return accessCode;
    }

    private String generateNewArrayCode(ASTNEW_ARRAY arrayNode, int indentation){
        SimpleNode child = (SimpleNode) arrayNode.jjtGetChild(0);

        return generateTypeSensitiveCode(child, indentation, true) + tab(indentation) + "newarray int";
    }

    private String generateLengthCode(ASTLENGTH lengthNode, int indentation){
        SimpleNode child = (SimpleNode) lengthNode.jjtGetChild(0);
        String lengthCode = generateTypeSensitiveCode(child, indentation, true);
        lengthCode += tab(indentation) + "arraylength";

        return lengthCode;
    }

    private String generateVarCode(ASTIDENT identNode, int indentation){
        String varCode = "";
        String varName = identNode.name;
        int index = getRegister(varName);

        if(index != -1){
            String varType = identNode.symbolTable.getSymbol(varName).getType();

            varCode += tab(indentation);

            pushStack(1);

            if(o_optimized && constants.peek().containsKey(varName)) {
                int value = constants.peek().get(varName);
                varCode += constantInstruction(value);
            } else {
                switch(varType){
                    case ControlVars.BOOL:
                    case ControlVars.INT:
                    case ControlVars.VOID:
                        varCode += "iload" + (index < 4 ? us() : space()) + index;
                        break;
                    default:
                        varCode += "aload" + (index < 4 ? us() : space()) + index;
                        break;
                }
            }

        } else {
            pushStack(1);
            varCode += tab(indentation) + "aload_0" + nl();
            varCode += tab(indentation) + "getfield " + classNode.className + "/" + cleanseVar(varName) + space() + getJasminType(varName, identNode);
        }

        return varCode;
    }

    private String generateTypeSensitiveCode(SimpleNode child, int indentation, boolean used){
        String code = "";

        switch(child.id){
            case ParserTreeConstants.JJTNUM:
                code += generateNumCode((ASTNUM) child, indentation) + nl();
                break;
            case ParserTreeConstants.JJTBOOL:
                code += generateBoolCode((ASTBOOL) child, indentation) + nl();
                break;
            case ParserTreeConstants.JJTADD:
            case ParserTreeConstants.JJTSUB:
            case ParserTreeConstants.JJTMUL:
            case ParserTreeConstants.JJTDIV:
            case ParserTreeConstants.JJTAND:
            case ParserTreeConstants.JJTLESSTHAN:
            case ParserTreeConstants.JJTNEGATION:
                code += generateOperationCode(child, indentation) + nl();
                break;
            case ParserTreeConstants.JJTNEW:
                code += generateNewCode((ASTNEW) child, indentation) + nl();
                break;
            case ParserTreeConstants.JJTNEW_ARRAY:
                code += generateNewArrayCode((ASTNEW_ARRAY) child, indentation) + nl();
                break;
            case ParserTreeConstants.JJTARRAY_ACCESS:
                code += generateArrayAccessCode((ASTARRAY_ACCESS) child, indentation) + nl();
                break;
            case ParserTreeConstants.JJTFUNC_METHOD:
                code += generateCallCode((ASTFUNC_METHOD) child, indentation, used) + nl();;
                break;
            case ParserTreeConstants.JJTLENGTH:
                code += generateLengthCode((ASTLENGTH) child, indentation) + nl();
                break;
            case ParserTreeConstants.JJTIDENT:
                code += generateVarCode((ASTIDENT) child, indentation) + nl();
                break;
            default:
                break;
        }

        return code;
    }

    private String generateReturnCode(ASTRETURN_EXP returnNode){
        SimpleNode child = (SimpleNode) returnNode.jjtGetChild(0);
        String returnCode = "";

        returnCode += generateTypeSensitiveCode(child, 1, true);
        returnCode += tab();

        popStack(stack);
        switch(returnNode.expType){
            case ControlVars.BOOL:
            case ControlVars.INT:
                returnCode += "ireturn";
                break;
            case ControlVars.VOID:
                returnCode += nl() + "return";
            default:
                returnCode += "areturn";
        }

        returnCode += nl();

        return returnCode;
    }

    private String generateSimpleScopeCode(SimpleNode node, int indentation){
        String simpleCode = "";

        switch(node.id){
            case ParserTreeConstants.JJTVARIABLE:
                addVariable((ASTVARIABLE) node);
                break;
            case ParserTreeConstants.JJTASSIGN:
                simpleCode += generateAssignCode((ASTASSIGN) node, indentation);
                break;
            case ParserTreeConstants.JJTSCOPE:
                simpleCode += generateScopeCode(node, indentation);
                break;
            case ParserTreeConstants.JJTIF_ELSE:
                simpleCode += generateConditionalCode((ASTIF_ELSE) node, indentation);
                break;
            case ParserTreeConstants.JJTWHILE:
                simpleCode += generateLoopCode((ASTWHILE) node, indentation);
                break;
            default:
                simpleCode += generateTypeSensitiveCode(node, indentation, false) + nl();
                break;
        }

        return simpleCode;
    }

    private String generateScopeCode(SimpleNode scopeNode, int indentation){
        String scopeCode = "";

        if (scopeNode.equalsNodeType(ParserTreeConstants.JJTSCOPE) || scopeNode.equalsNodeType(ParserTreeConstants.JJTMETHOD_BODY)) {
            int numChildren = scopeNode.jjtGetNumChildren();
            int childIndex = 0;
            SimpleNode child;

            while(childIndex < numChildren){
                child = (SimpleNode) scopeNode.jjtGetChild(childIndex);

                scopeCode += generateSimpleScopeCode(child, indentation);

                childIndex++;
            }
        } else
            scopeCode += generateSimpleScopeCode(scopeNode, indentation);


        return scopeCode;
    }

    private String generateAssignCode(ASTASSIGN assignNode, int indentation){
        String assignCode = "";
        SimpleNode firstChild = (SimpleNode) assignNode.jjtGetChild(0);
        SimpleNode secondChild = (SimpleNode) assignNode.jjtGetChild(1);
        
        if(uselessAssign(firstChild))
            return assignCode;

        if(o_optimized && firstChild.equalsNodeType(ParserTreeConstants.JJTIDENT)) {
            String varName = ((ASTIDENT)firstChild).name;

            resetConstant(varName);

            if (secondChild.equalsNodeType(ParserTreeConstants.JJTNUM)) {
                constants.peek().put(varName, ((ASTNUM) secondChild).value);
            }
        }
        

        String quickAssign = checkSpecialAddition(firstChild, secondChild);

        if(quickAssign != null)
            return tab(indentation) + quickAssign + nl();

        assignCode += addStoreVar(firstChild, secondChild, indentation);

        if(firstChild.equalsNodeType(ParserTreeConstants.JJTARRAY_ACCESS)){
            popStack(3);
            assignCode += tab(indentation) + "iastore" + nl();
        }

        assignCode += nl();

        return assignCode;
    }

    private String generateCallCode(ASTFUNC_METHOD funcNode, int indentation, boolean used){
        String callCode = "";
        SimpleNode objectNode = (SimpleNode) funcNode.jjtGetChild(0);
        ASTCALL callNode = (ASTCALL) funcNode.jjtGetChild(1);
        int numArgs = funcNode.arguments.size();

        if(!isStatic(funcNode)){
            if(objectNode.equalsNodeType(ParserTreeConstants.JJTTHIS)){
                pushStack(1);
                callCode += tab(indentation) + "aload_0";
            }
            else if(objectNode.equalsNodeType(ParserTreeConstants.JJTIDENT))
                callCode += generateVarCode((ASTIDENT) objectNode, indentation); //TODO: Check this is correct
            else if(objectNode.equalsNodeType(ParserTreeConstants.JJTFUNC_METHOD))
                callCode += tab(indentation) + generateCallCode((ASTFUNC_METHOD) objectNode, indentation, true);
            else if(objectNode.equalsNodeType(ParserTreeConstants.JJTNEW))
                callCode += generateNewCode((ASTNEW) objectNode, indentation);

            callCode += nl();
        }

        if(numArgs > 0){
            SimpleNode child;
            int childIndex = 0;
            ASTARGUMENTS argumentsNode = (ASTARGUMENTS) callNode.jjtGetChild(1);

            while(childIndex < numArgs){
                child = (SimpleNode) argumentsNode.jjtGetChild(childIndex);

                callCode += generateTypeSensitiveCode(child, indentation, true);

                childIndex++;
            }
        }

        callCode += tab(indentation);

        if(isStatic(funcNode)){
            popStack(numArgs - 1);
            callCode += "invokestatic";
        } else {
            popStack(numArgs);
            callCode += "invokevirtual";
        }

        String returnCode = methodReturnCode(funcNode);

        callCode += space() + funcNode.call.replace('.', '/') + "(" + argumentsCode(funcNode) + ")" + returnCode;

        if(!used && !returnCode.equals("V")){
            popStack(1);
            callCode += nl() + tab(indentation) + "pop" + nl();
        }

        return callCode;
    }

    private String generateNewCode(ASTNEW newNode, int indentation){
        String newCode = "";

        pushStack(1);
        newCode += tab(indentation) + "new " + newNode.object + nl();
        pushStack(1);
        newCode += tab(indentation) + "dup" + nl();
        newCode += tab(indentation) + "invokespecial " + newNode.object + "/<init>()V";

        return newCode;
    }

    private String generateConditionCode(ASTCONDITION conditionNode, int indentation){
        String conditionCode = "";
        SimpleNode child = (SimpleNode) conditionNode.jjtGetChild(0);

        switch(child.id){
            case ParserTreeConstants.JJTLESSTHAN:
                SimpleNode leftSideNode = (SimpleNode) ((ASTLESSTHAN) child).jjtGetChild(0);
                SimpleNode rightSideNode = (SimpleNode) ((ASTLESSTHAN) child).jjtGetChild(1);

                conditionCode += generateTypeSensitiveCode(leftSideNode, indentation, true);

                String aux = checkSpecialCompare(leftSideNode, rightSideNode);

                if(aux != null){
                    conditionCode += tab(indentation) + aux;

                    return conditionCode;
                }

                conditionCode += generateTypeSensitiveCode(rightSideNode, indentation, true);
                popStack(2);
                conditionCode += tab(indentation) + "if_icmpge";

                break;
            default:
                conditionCode += generateTypeSensitiveCode(child, indentation, true);
                popStack(1);
                conditionCode += tab(indentation) + "ifeq";
                break;
        }

        return conditionCode;
    }

    private String generateConditionalCode(ASTIF_ELSE conditionalNode, int indentation){
        ASTIF ifNode = (ASTIF) conditionalNode.jjtGetChild(0);
        ASTELSE elseNode = (ASTELSE) conditionalNode.jjtGetChild(1);

        ASTCONDITION conditionNode = (ASTCONDITION) ifNode.jjtGetChild(0);
        SimpleNode ifScopeNode = (SimpleNode) ifNode.jjtGetChild(1);
        SimpleNode elseScopeNode = (SimpleNode) elseNode.jjtGetChild(0);

        String conditionalCode = "";
        int index = counter.conditionalCounter;
        counter.conditionalCounter++;

        HashMap<String, Integer> ifConstants = (HashMap<String, Integer>) constants.peek().clone();
        HashMap<String, Integer> elseConstants = (HashMap<String, Integer>) constants.peek().clone();

        conditionalCode += generateConditionCode(conditionNode, indentation) + space() + "else_" + index + nl();
        constants.push(ifConstants);
        conditionalCode += generateScopeCode(ifScopeNode, indentation + 1);
        constants.pop();
        conditionalCode += tab(indentation + 1) + "goto endif_" + index + nl();
        conditionalCode += tab(indentation) + "else_" + index + entry() + nl();
        constants.push(elseConstants);
        conditionalCode += generateScopeCode(elseScopeNode, indentation + 1);
        constants.pop();
        conditionalCode += tab(indentation) + "endif_" + index + entry() + nl(2);

        return conditionalCode;
    }

    private String generateLoopCode(ASTWHILE loopNode, int indentation){
        ASTCONDITION conditionNode = (ASTCONDITION) loopNode.jjtGetChild(0);
        SimpleNode loopScopeNode = (SimpleNode) loopNode.jjtGetChild(1);

        String loopCode = "";
        int index = counter.loopCounter;
        counter.loopCounter++;

        loopCode += generateConditionCode(conditionNode, indentation) + space() + "endloop_" + index + nl(2);
        constants.push(new HashMap<>());
        loopCode += tab(indentation) + "loop_" + index + entry() + nl();
        loopCode += generateScopeCode(loopScopeNode, indentation + 1);
        loopCode += generateConditionCode(conditionNode, indentation) + space() + "endloop_" + index + nl(2);
        loopCode += tab(indentation + 1) + "goto loop_" + index + nl();
        loopCode += tab(indentation) + "endloop_" + index + entry() + nl(2);
        constants.pop();

        return loopCode;
    }

    public String generateMethodCode(){
        String methodCode = "";
        String auxCode = "";
        int numChildren = methodNode.jjtGetNumChildren();
        int childIndex = 0;
        SimpleNode child;

        if(!locals.containsKey("this") && !r_optimized)
            locals.put("this", locals.size());

        while(childIndex < numChildren){
            child = (SimpleNode) methodNode.jjtGetChild(childIndex);

            if(child.equalsNodeType(ParserTreeConstants.JJTARGUMENTS))
                addArguments((ASTARGUMENTS) child);
            else if(child.equalsNodeType(ParserTreeConstants.JJTMETHOD_BODY))
                auxCode += generateScopeCode(child, 1);
            else if(child.equalsNodeType(ParserTreeConstants.JJTRETURN_EXP))
                auxCode += generateReturnCode((ASTRETURN_EXP) child);

            childIndex++;
        }

        methodCode += nl() + generateMethodHeader();
        methodCode += auxCode;
        methodCode += ".end method";

        return methodCode;
    }

    public String generateMainMethodCode(){
        String methodCode = "";
        String auxCode = "";
        ASTIDENT firstChild = (ASTIDENT) methodNode.jjtGetChild(0);
        SimpleNode secondChild = (SimpleNode) methodNode.jjtGetChild(1);

        if(!locals.containsKey(firstChild.name) && !r_optimized)
            locals.put(firstChild.name, locals.size());

        auxCode += generateScopeCode(secondChild, 1);

        methodCode += nl() + generateMainMethodHeader();
        methodCode += auxCode;
        methodCode += tab() + "return" + nl();
        methodCode += ".end method";

        return methodCode;
    }
}
