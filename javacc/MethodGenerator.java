import java.util.ArrayList;
import java.util.List;

public class MethodGenerator extends CodeGenerator{

    ASTMETHOD methodNode;
    private static List<String> locals;
    private int localsCounter = 1;

    MethodGenerator(ASTMETHOD methodNode, ASTCLASS classNode, List<String> classVars, int labelCounter){
        super.classNode = classNode;
        super.classVars = classVars;
        super.labelCounter = labelCounter;

        this.methodNode = methodNode;
        locals = new ArrayList<>(methodNode.localSize);
        locals.add(methodNode.methodName); //TODO: methodName or 'this'?
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

    private void addArguments(ASTARGUMENTS argumentsNode){
        for(int i = 0; i < argumentsNode.arguments.size(); i++){
            SimpleNode child = (SimpleNode) argumentsNode.jjtGetChild(i);

            if(child.equalsNodeType(ParserTreeConstants.JJTARGUMENT)){
                locals.add(((ASTARGUMENT) child).argName);
                this.localsCounter++;
            }
        }
    }

    private void addVariable(ASTVARIABLE varNode){
        locals.add(varNode.varName);
        this.localsCounter++;
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
                storeCode += "astore " + index;
            } else{
                TypeEnum type = node.symbolTable.getSymbol(varName).getType();

                if(type == TypeEnum.ARRAY)
                    storeCode += "iastore ";
                else if (type == TypeEnum.OBJECT)
                    storeCode += "astore "; //TODO: Confirm its always the case
                else
                    storeCode += "istore ";

                storeCode += index;
            }
        } else {
            index = classVars.indexOf(varName); //TODO: Necessary?

            storeCode += "putfield " + classNode.className + "/" + varName + space() + getJasminType(varName, node);
        }

        storeCode += nl();

        return storeCode;
    }

    private boolean isStatic(ASTFUNC_METHOD funcNode){
        if(funcNode.symbolTable.existsMethodSymbol(funcNode.call)){
            MethodSymbol methodSymbol = (MethodSymbol) funcNode.symbolTable.getSymbol(funcNode.call);
            return methodSymbol.isStatic(funcNode.arguments);
        }

        return false;
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

        operationCode += nl() + tab(identation);

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
                operationCode += tab(identation) + "iconst_0" + nl();
                operationCode += tab(identation) + "goto label_" + (labelCounter + 1) + nl();
                operationCode += tab(identation) + "label_" + labelCounter + ":";
                operationCode += tab(identation) + "iconst_1" + nl();
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
                code += generateNumCode((ASTNUM) child, identation);
                break;
            case ParserTreeConstants.JJTBOOL:
                code += generateBoolCode((ASTBOOL) child, identation);
                break;
            case ParserTreeConstants.JJTADD:
            case ParserTreeConstants.JJTSUB:
            case ParserTreeConstants.JJTMUL:
            case ParserTreeConstants.JJTDIV:
            case ParserTreeConstants.JJTAND:
            case ParserTreeConstants.JJTLESSTHAN:
            case ParserTreeConstants.JJTNEGATION:
                code += generateOperationCode(child, identation);
                break;
            case ParserTreeConstants.JJTNEW:
                code += generateNewCode((ASTNEW) child);
                break;
            case ParserTreeConstants.JJTNEW_ARRAY:
                code += generateNewArrayCode((ASTNEW_ARRAY) child, identation);
                break;
            case ParserTreeConstants.JJTARRAY_ACCESS:
                code += generateArrayAccessCode((ASTARRAY_ACCESS) child, identation);
                break;
            case ParserTreeConstants.JJTFUNC_METHOD:
                code += generateCallCode((ASTFUNC_METHOD) child);

                /*if(!assigned)
                    code += nl() + tab(identation) + "pop";*/ //TODO: Check if this is needed

                break;
            case ParserTreeConstants.JJTLENGTH:
                code += ""; //TODO: generateLengthCode((ASTLENGTH) child);
                break;
            case ParserTreeConstants.JJTIDENT:
                code += generateVarCode((ASTIDENT) child, identation);
                break;
            default:
                break;
        }

        code += nl();

        return code;
    }

    public String generateReturnCode(ASTRETURN_EXP returnNode){
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
                methodCode += generateBodyCode((ASTMETHOD_BODY) child);
            else if(child.equalsNodeType(ParserTreeConstants.JJTRETURN_EXP))
                methodCode += generateReturnCode((ASTRETURN_EXP) child);

            childIndex++;
        }

        methodCode += ".end method" + nl();

        return methodCode;
    }

    public String generateMainMethodCode(){
        String methodCode = nl() + generateMainMethodHeader();
        ASTIDENT firstChild = (ASTIDENT) methodNode.jjtGetChild(0);
        ASTMETHOD_BODY secondChild = (ASTMETHOD_BODY) methodNode.jjtGetChild(1);

        locals.add(firstChild.name);
        localsCounter++;

        methodCode += generateBodyCode(secondChild);
        methodCode += tab() + "return" + nl();
        methodCode += ".end method" + nl();

        return methodCode;
    }

    public String generateBodyCode(ASTMETHOD_BODY bodyNode){
        String bodyCode = "";
        int numChildren = bodyNode.jjtGetNumChildren();
        int childIndex = 0;
        SimpleNode child;

        while(childIndex < numChildren){
            child = (SimpleNode) bodyNode.jjtGetChild(childIndex);

            switch(child.id){
                case ParserTreeConstants.JJTVARIABLE:
                    addVariable((ASTVARIABLE) child);
                    break;
                case ParserTreeConstants.JJTASSIGN:
                    bodyCode += generateAssignCode((ASTASSIGN) child);
                    break;
                default:
                    bodyCode += generateTypeSensitiveCode(child, 1);
                    break;
            }

            childIndex++;
        }

        return bodyCode;
    }

    public String generateAssignCode(ASTASSIGN assignNode){
        String assignCode = "";
        SimpleNode firstChild = (SimpleNode) assignNode.jjtGetChild(0);
        SimpleNode secondChild = (SimpleNode) assignNode.jjtGetChild(1);

        assignCode += generateTypeSensitiveCode(secondChild, 1, true);
        assignCode += addStoreVar(firstChild, 1);

        return assignCode;
    }

    public String generateCallCode(ASTFUNC_METHOD funcNode){
        String callCode = nl();
        SimpleNode objectNode = (SimpleNode) funcNode.jjtGetChild(0);
        ASTCALL callNode = (ASTCALL) funcNode.jjtGetChild(1);
        ASTARGUMENTS argumentsNode = (ASTARGUMENTS) callNode.jjtGetChild(1);
        int numArgs = funcNode.arguments.size();

        if(isStatic(funcNode)){
            if(objectNode.equalsNodeType(ParserTreeConstants.JJTTHIS))
                callCode += "aload_0";
            else if(objectNode.equalsNodeType(ParserTreeConstants.JJTIDENT))
                callCode += "aload " + locals.indexOf(((ASTIDENT) objectNode).name);
            else if(objectNode.equalsNodeType(ParserTreeConstants.JJTFUNC_METHOD))
                callCode += generateCallCode((ASTFUNC_METHOD) objectNode); //TODO: Probs wrong
            else if(objectNode.equalsNodeType(ParserTreeConstants.JJTNEW))
                callCode += generateNewCode((ASTNEW) objectNode); //TODO: Probs wrong

            callCode += nl();
        }

        SimpleNode child;
        int childIndex = 0;

        while(childIndex < numArgs){
            child = (SimpleNode) argumentsNode.jjtGetChild(childIndex);

            callCode += generateTypeSensitiveCode(child, 1);

            childIndex++;
        }

        callCode += tab() + (isStatic(funcNode) ? "invokestatic " : "invokevirtual ") + funcNode.call.replace('.', '/');
        callCode += "(" + argumentsCode(funcNode) + ")" + methodReturnCode(funcNode);

        return callCode;
    }

    public String generateNewCode(ASTNEW newNode){
        String newCode = nl();

        newCode += tab() + "new " + newNode.object + nl();
        newCode += tab() + "dup" + nl();
        newCode += tab() + "invokespecial " + newNode.object + "/<init>()V" + nl();

        return newCode;
    }
}
