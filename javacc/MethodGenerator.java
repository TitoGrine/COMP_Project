import java.util.ArrayList;
import java.util.List;

public class MethodGenerator extends CodeGenerator{

    ASTMETHOD methodNode;
    private static List<String> locals;
    private int localsCounter = 1;

    MethodGenerator(ASTMETHOD methodNode){
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

    private String generateMethodHeader(){
        String headerCode = "";

        headerCode += ".method public " + methodNode.methodName + "(" + parametersCode() + ")" + getSimpleJasminType(methodNode.returnType) + nl();
        headerCode += tab() + ".limit stack " + 99 + nl(); //TODO: Provisório!!
        headerCode += tab() + ".limit locals " + methodNode.localSize + nl();

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
        locals.add(varNode.name);
        this.localsCounter++;
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

            childIndex++;
        }

        return methodCode;
    }

    public String generateBodyCode(ASTMETHOD_BODY bodyNode){
        String bodyCode = "";
        int numChildren = methodNode.jjtGetNumChildren();
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
                case ParserTreeConstants.JJTFUNC_METHOD:
                    bodyCode += generateCallCode((ASTFUNC_METHOD) child);
                    break;
                case ParserTreeConstants.JJTNEW:
                    bodyCode += generateNewCode((ASTNEW) child);
                    break;
                default:
                    break;
            }

            childIndex++;
        }


        return bodyCode;
    }

    public String generateAssignCode(ASTASSIGN assignNode){
        String assignCode = nl();

        return assignCode;
    }

    public String generateCallCode(ASTFUNC_METHOD funcNode){
        String callCode = nl();
        SimpleNode objectNode = (SimpleNode) funcNode.jjtGetChild(0);
        ASTCLASS callNode = (SimpleNode) funcNode.jjtGetChild(1);
        int numArgs = funcNode.arguments.size();

        String object = funcNode.call.split("\\.")[0];

        if(objectNode.equalsNodeType(ParserTreeConstants.JJTTHIS))
            callCode += "aload_0";
        else if(objectNode.equalsNodeType(ParserTreeConstants.JJTIDENT))
            callCode += "aload_" + locals.indexOf(((ASTIDENT) objectNode).name);
        else if(objectNode.equalsNodeType(ParserTreeConstants.JJTFUNC_METHOD))
            callCode += generateCallCode((ASTFUNC_METHOD) objectNode); //TODO: Probs wrong
        else if(objectNode.equalsNodeType(ParserTreeConstants.JJTNEW))
            callCode += generateNewCode((ASTNEW) objectNode); //TODO: Probs wrong



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
