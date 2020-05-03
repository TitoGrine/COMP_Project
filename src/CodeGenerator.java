import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;

public class CodeGenerator {
    static String generated;
    private static SimpleNode root;
    static int localIndex = 1;
    static int classIndex = 1;
    private static String[] locals = new String[99];
    private static String[] classVars = new String[99];
    private static String classIdent="";
    private static Node classNode;
    private PrintWriter out;
    private static String extendedClass = "";
    private static int localsCounter = 1;
    private static int labelCounter = 1;

    public CodeGenerator(SimpleNode root) {
        this.root = root;
        generated = "";
    }



    public void generate() {
        //(verificar se o root é classe, ver os children e por aí fora)
        Node[] children =  root.jjtGetChildren();

        for(Node child: children) {
            if (child.getId() == ParserTreeConstants.JJTCLASS) {
                this.classNode = child;
                Node classChilds[] = addClass(child);
                addVariables(classChilds);
                addStandardInitializer();
                addMainHeader();
                addMainMethod(classChilds);
                checkMethods(classChilds);
            }
        }

        try {
            PrintWriter out = fileToWrite();
            out.println(generated);
            print();
            out.close();
        }
        catch (IOException e) {
			e.printStackTrace();
        }
    }

    private void checkMethods(Node[] classChilds) {
        for (Node n : classChilds){
            SimpleNode simpleN = (SimpleNode) n;
            if (simpleN.toString().equals("METHOD")) {
                Node methodChilds[] = simpleN.jjtGetChildren();
                addMethod(simpleN);
            }
        }
    }

    private void addMethod(Node method) {

        Node[] methodChilds = ((SimpleNode)method).jjtGetChildren();

        ArrayList<TypeEnum> argsType = getArgsType(methodChilds[2]);
        nl();
        nl();
        generated += ".method public ";
        generated += ((ASTIDENT)methodChilds[1]).name;
        generated += "(";
        printTypes(argsType);
        TypeEnum returnType = ((ASTTYPE)((SimpleNode)methodChilds[0]).jjtGetChild(0)).typeID;
        getJType(returnType);
        nl();
        tab();
        generated += ".limit stack 99";
        nl();
        tab();
        generated += ".limit locals 99";
        nl();
        Node argumentsNode;

        String[] backup = this.locals;
        int backupCounter = this.localsCounter;
        this.localsCounter = 1;
        for(Node node : methodChilds){


          if (((SimpleNode)node).id == ParserTreeConstants.JJTARGUMENTS) {
            argumentsNode = node;
            addMethodArguments(((SimpleNode)argumentsNode).jjtGetChildren());
          }


          if ((((SimpleNode)node).id == ParserTreeConstants.JJTMETHOD_BODY) && ((SimpleNode)node).jjtGetNumChildren() != 0) {
              storeVariables(((SimpleNode)node).jjtGetChildren());
                
            methodBody(((SimpleNode)node).jjtGetChildren());
            }
        }
        // printLocals();
        methodReturn(methodChilds[methodChilds.length -1], returnType);
        this.locals = backup;
        this.localsCounter = backupCounter;
    }


    static void makeOperation(Node operationNode){
        
        SimpleNode operation = (SimpleNode) operationNode;
        SimpleNode oper1 = (SimpleNode) operation.jjtGetChild(0);
        SimpleNode oper2 = (SimpleNode) operation.jjtGetChild(1);
        if (oper1.id == ParserTreeConstants.JJTADD ||
            oper1.id == ParserTreeConstants.JJTMUL ||
            oper1.id == ParserTreeConstants.JJTSUB ||
            oper1.id == ParserTreeConstants.JJTDIV ||
            oper1.id == ParserTreeConstants.JJTAND ||
            oper1.id == ParserTreeConstants.JJTLESSTHAN) {
          makeOperation(operation.jjtGetChild(0));

        } else {
          if (oper1.id == ParserTreeConstants.JJTNUM) {
            generated += "\n\tbipush " + ((ASTNUM)oper1).value;
          } else if (oper1.id == ParserTreeConstants.JJTBOOL) {

            if (((ASTBOOL)oper1).truth_value)
              generated += "\n\ticonst_1";
            else
              generated += "\n\ticonst_0";

          } else if (oper1.id == ParserTreeConstants.JJTFUNC_METHOD) {
            addMethodCall(((SimpleNode)oper1));

        } else {

            int classVarI = checkIfClassVar(oper1);

            if(classVarI != -1){
              tab();
              generated += "getfield ";
              space();
              generated += getClassName() + "/";
              generated += classVars[classVarI];
              space();
              getJType(getClassVarType(classVars[classVarI]));
              nl();
            }else{

               Node[] tmp = {operation.jjtGetChild(0)};

                ArrayList<String> localVars = getFunctionLocals(tmp);

                nl();
                for (String s : localVars) {
                    tab();
                    generated += "iload ";
                    generated += s;
                    nl();
                }
            }
          }
        }

        if (oper2.id == ParserTreeConstants.JJTADD ||
            oper2.id == ParserTreeConstants.JJTMUL ||
            oper2.id == ParserTreeConstants.JJTSUB ||
            oper2.id == ParserTreeConstants.JJTDIV ||
            oper2.id == ParserTreeConstants.JJTAND ||
            oper2.id == ParserTreeConstants.JJTLESSTHAN) {
          makeOperation(operation.jjtGetChild(1));
        } else if (oper2.id == ParserTreeConstants.JJTNUM) {
          generated += "\n\tbipush " + ((ASTNUM) oper2).value;
        } else if (oper2.id == ParserTreeConstants.JJTBOOL) {

          if (((ASTBOOL)oper2).truth_value)
            generated += "\n\ticonst_1";
          else
            generated += "\n\ticonst_0";
        } else if (oper2.id == ParserTreeConstants.JJTFUNC_METHOD) {
            addMethodCall(((SimpleNode)oper2));

        } else {

          int classVarI2 = checkIfClassVar(oper2);

          if (classVarI2 != -1) {
            tab();
            generated += "getfield ";
            generated += getClassName() + "/" + classVars[classVarI2];
            space();
            getJType(getClassVarType(classVars[classVarI2]));
            nl();
          } else {
            Node[] tmp = {operation.jjtGetChild(1)};

            ArrayList<String> localVars = getFunctionLocals(tmp);


            nl();
            for (String s : localVars) {
              tab();
              generated += "iload ";
              generated += s;
            }
          }
        }

        switch(operation.id){
            case ParserTreeConstants.JJTADD: 
                generated += "\n\tiadd\n";
                break;
            case ParserTreeConstants.JJTSUB:
              generated += "\n\tisub";
              break;
            case ParserTreeConstants.JJTDIV:
              generated += "\n\tidiv";
              break;
            case ParserTreeConstants.JJTMUL:
              generated += "\n\timul";
              break;
            case ParserTreeConstants.JJTAND:
              generated += "\n\tiand";
              break;
            case ParserTreeConstants.JJTNEGATION:
              generated += "\n\tineg";
              break;
            case ParserTreeConstants.JJTLESSTHAN:
              generated += "\n\tif_icmple l";
              generated += labelCounter;
              nl();
              tab();
              generated += "iconst_0";
              nl();
              tab();
              generated += "goto l" + (labelCounter +1);
              nl();
              generated += "l" + labelCounter + ":\n\t" + "iconst_1" ;
              nl();
              generated += "l" + (labelCounter + 1) + ":";
              labelCounter += 2;

              break;
        }
    }


    private static void loadVariable(String name) {
        nl();
        tab();
        generated += "iload ";
        generated += Integer.toString(getFunctionLocals(name));
    }

    private void methodReturn(Node returnNode, TypeEnum typeReturn) {
        SimpleNode node = (SimpleNode) returnNode;
        if(((SimpleNode)node.jjtGetChild(0)).id == ParserTreeConstants.JJTFUNC_METHOD) {  //ver se retornar numero
            addMethodCall(((SimpleNode)node.jjtGetChild(0)));
            // storeLocal(((ASTIDENT)((SimpleNode)((SimpleNode)node.jjtGetChild(0)).jjtGetChild(0))).name);
            // storeLocal(((ASTIDENT)candidate.jjtGetChild(0)).name);
        }
        else if (((SimpleNode)node.jjtGetChild(0)).id == ParserTreeConstants.JJTIDENT) {
            loadVariable(((ASTIDENT)((SimpleNode)node.jjtGetChild(0))).name);
        } else if (((SimpleNode)node.jjtGetChild(0)).id == ParserTreeConstants.JJTNUM) {
            // System.out.println(((ASTNUM)(((SimpleNode)node.jjtGetChild(0)))).value);
            nl();
            tab();
            generated += "bipush ";
            generated += ((ASTNUM)(((SimpleNode)node.jjtGetChild(0)))).value;
        } else if(((SimpleNode)node.jjtGetChild(0)).id == ParserTreeConstants.JJTBOOL) { //ver se retornar numero
            nl();
            tab();
            generated += "iconst";
            if (((ASTBOOL)node.jjtGetChild(0)).truth_value)
                generated += "_1";
            else generated += "_0";

        } else if(((SimpleNode)node.jjtGetChild(0)).id != ParserTreeConstants.JJTADD || 
        ((SimpleNode)node.jjtGetChild(0)).id != ParserTreeConstants.JJTSUB || 
        ((SimpleNode)node.jjtGetChild(0)).id != ParserTreeConstants.JJTMUL ||
        ((SimpleNode)node.jjtGetChild(0)).id != ParserTreeConstants.JJTDIV) {
            //ver se retornar numero
            makeOperation(node.jjtGetChild(0));
        }
        nl();
        nl();
        tab();
        if(typeReturn == TypeEnum.INT || typeReturn == TypeEnum.BOOL) 
                generated += "ireturn";
        if (typeReturn == TypeEnum.VOID)
              generated += "return";
        nl();
        generated += ".end method";
    }

    private static int checkIfClassVar(SimpleNode candidate){
        for(int i = 0; i < 99; i++){
            if(classVars[i] != null){
                if(classVars[i].equals(((ASTIDENT) candidate).name)){
                    return i;
                }
            }    
        }
        return -1;
    }

    private void printTypes(ArrayList<TypeEnum> argsType) {
        for(TypeEnum type: argsType){
            getJType(type);
        }
        generated += ')';
    }

    public static String getJType(TypeEnum type) {
        switch(type) {
            case INT:
                generated += "I";
                break;
            case BOOL:
                generated += "Z";
                break;
            case VOID:
                generated += "V";
                break;
        }
        return "";
    }

    private ArrayList<TypeEnum> getArgsType(Node argNode) {
        ArrayList<TypeEnum> buff = new ArrayList<TypeEnum>();
        SimpleNode args = (SimpleNode) argNode;
        Node[] argsChildren = args.jjtGetChildren();
        for(Node n : argsChildren){
            SimpleNode node = (SimpleNode) n;
            Node type =  node.jjtGetChild(0);
            buff.add(((ASTTYPE) type).typeID);
        }
        return buff;
    }

    static Node[] addClass(Node node) {
        SimpleNode classSimpleNode = (SimpleNode) node;
        Node[] classChilds = classSimpleNode.jjtGetChildren();

        classIdent = ((ASTIDENT) classChilds[0]).name;
        generated += ".class public " + classIdent;
        nl();
        // generated += ".super java/lang/Object";
        // nl();
        generated += ".super ";

        if(classChilds.length > 1 && classChilds[1].getId() == ParserTreeConstants.JJTEXTENDS) {
            SimpleNode extNode = (SimpleNode) classChilds[1];
            Node[] className = extNode.jjtGetChildren();
            extendedClass = ((ASTIDENT) className[0]).name;
        }
        else {
            extendedClass = "java/lang/Object";
        }

        generated += extendedClass;
        nl();
        return classChilds;
    }


    static void addVariables(Node node[]) {
        for (Node n : node) {
            SimpleNode simpleN = (SimpleNode) n;
            

            if (simpleN.toString().equals("MAINMETHOD")) {
                Node[] candidates = ((SimpleNode)simpleN.jjtGetChild(1)).jjtGetChildren();
                storeVariables(candidates);

                // Node[] candidates = ((SimpleNode)simpleN.jjtGetChild(1)).jjtGetChildren();
                // for (Node candidate: candidates) {
                //     if (candidate.toString() == "VARIABLE") {
                //         locals[localsCounter] = ((ASTIDENT)candidate.jjtGetChild(1)).name;
                //         localsCounter++;
                //     }
                // }


            //   classVars[classIndex] = ((ASTIDENT)simpleN.jjtGetChild(1)).name;
            //   classIndex++;
            //   generated += '\n' + ".field public" + ((ASTIDENT)simpleN.jjtGetChildren()[1]).name;
            //   space();

            //   ASTTYPE typeN = (ASTTYPE)simpleN.jjtGetChildren()[0];
            //   switch (typeN.typeID) {
            //   case STRING:
            //     generated += "V";
            //     nl();
            //     break;
            //   case INT:
            //     generated += "I";
            //     nl();
            //     break;
            //   case BOOL:
            //     generated += "Z";
            //     nl();
            //     break;
            //     }
            }

                if (simpleN.toString().equals("VARIABLE")) {
                   
                    classVars[classIndex] = ((ASTIDENT)simpleN.jjtGetChild(1)).name;
                    classIndex++;
                    generated += '\n' + ".field public " + ((ASTIDENT)simpleN.jjtGetChildren()[1]).name;
                    space();
                    getJType(getClassVarType(classVars[classIndex -1]));
                    space();
                }
            
        }
    }


    static void storeVariables(Node[] candidates) {
                for (Node candidate: candidates) {
                    if (candidate.toString() == "VARIABLE") {
                        locals[localsCounter] = ((ASTIDENT)candidate.jjtGetChild(1)).name;
                        localsCounter++;
                    }
                }

    }


    static void addMainMethod(Node node[]) {
        for (Node n : node){
            SimpleNode simpleN = (SimpleNode) n;
            if (simpleN.toString().equals("MAINMETHOD")) {
                Node mainMethodChilds[] = simpleN.jjtGetChildren();
                for (Node mainBodyCandidate: mainMethodChilds) {
                    SimpleNode mainSimpleNode = (SimpleNode) mainBodyCandidate;
                    if (mainSimpleNode.toString().equals("METHOD_BODY")) {
                        methodBody(mainSimpleNode.jjtGetChildren());
                    }
                }
            }
        }
        nl();
        tab();
        generated += "return";
        nl();
        generated += ".end method\n";
    }


    private void addMethodArguments(Node[] args) {
        // for (int i=0; i < args.length; i++) {
        //     if (((ASTIDENT)args[i].jjtGetChild(1)).name == name)
        //         return i;
        // }

        // return -1;

        this.locals = new String[99];

        for (int i = 0; i < args.length; i++) {
            this.locals[i+1] = ((ASTIDENT)((SimpleNode)((SimpleNode)args[i]).jjtGetChild(1))).name;
            this.localsCounter++;
        }
    }


    static void methodBody(Node methodBodyChilds[]) {
        for (Node candidate : methodBodyChilds)
            if (((SimpleNode) candidate).id == ParserTreeConstants.JJTASSIGN) {
            switch (
                  ((SimpleNode)candidate.jjtGetChild(1)).id) {
              case ParserTreeConstants.JJTNEW:
                addNew(candidate.jjtGetChild(1));
                storeAddress(((ASTIDENT)candidate.jjtGetChild(0)).name);
                break;
              case ParserTreeConstants.JJTSUB:
                makeOperation(((SimpleNode)candidate).jjtGetChild(1));
                storeLocal(((ASTIDENT)candidate.jjtGetChild(0)).name);
                break;
              case ParserTreeConstants.JJTADD:
                makeOperation(((SimpleNode)candidate).jjtGetChild(1));
                storeLocal(((ASTIDENT)candidate.jjtGetChild(0)).name);
                break;
              case ParserTreeConstants.JJTMUL:
                makeOperation(((SimpleNode)candidate).jjtGetChild(1));
                storeLocal(((ASTIDENT)candidate.jjtGetChild(0)).name);
                break;
              case ParserTreeConstants.JJTDIV:
                makeOperation(((SimpleNode)candidate).jjtGetChild(1));
                storeLocal(((ASTIDENT)candidate.jjtGetChild(0)).name);
                break;
              case ParserTreeConstants.JJTAND:
                makeOperation(((SimpleNode) candidate).jjtGetChild(1));
                storeLocal(((ASTIDENT)candidate.jjtGetChild(0)).name);
                break;
              case ParserTreeConstants.JJTLESSTHAN:
                makeOperation(((SimpleNode)candidate).jjtGetChild(1));
                storeLocal(((ASTIDENT)candidate.jjtGetChild(0)).name);
                break;
              case ParserTreeConstants.JJTNEGATION:
                handleNeg(((SimpleNode)candidate).jjtGetChild(1));
                storeLocal(((ASTIDENT)candidate.jjtGetChild(0)).name);
                break;
              case ParserTreeConstants.JJTFUNC_METHOD:
                addMethodCall(candidate.jjtGetChild(1));
                storeLocal(((ASTIDENT)candidate.jjtGetChild(0)).name);
                break;
              case ParserTreeConstants.JJTBOOL:
                addBoolean(candidate.jjtGetChild(1));
                storeLocal(((ASTIDENT)candidate.jjtGetChild(0)).name);
                break;
              case ParserTreeConstants.JJTNUM:
                addVariableAllocation(candidate);
                break;
              default:
                break;
              }
            } else if (((SimpleNode) candidate).id == ParserTreeConstants.JJTFUNC_METHOD) {
                addMethodCall(candidate);
                popReturn(candidate);
            }
            else if (((SimpleNode) candidate).id == ParserTreeConstants.JJTNEW) {
                addNew(candidate);
            }
    }

    

    static void addBoolean(Node bool) {
        if (((ASTBOOL)bool).truth_value) {
            nl();
            tab();
            generated += "iconst_1";

        } else {
            nl();
            tab();
            generated += "iconst_0";

        }
    }

    static void handleNeg(Node nodeNeg){
        Node n = ((SimpleNode) nodeNeg).jjtGetChild(0);
        if(((SimpleNode)n).id == ParserTreeConstants.JJTBOOL){
            addBoolean(n);
            generated += "\n\tineg";
            return;
        }else if(((SimpleNode)n).id == ParserTreeConstants.JJTIDENT) {
          Node[] tmp = {n};

          ArrayList<String> localVars = getFunctionLocals(tmp);

          nl();
          for (String s : localVars) {
            tab();
            generated += "iload ";
            generated += s;
            nl();
          }
        }else{
            makeOperation(n);  //é uma operação ou função
            generated += "\n\tineg";
        }
    }

    static void popReturn(Node funcMethod) {
        if (getMethodReturnType(funcMethod) == TypeEnum.VOID) return;

        tab();
        generated += "pop";
        nl();
    }


    static void storeAddress(String addr) {
        int index = getFunctionLocals(addr);
        tab();
        generated += "astore ";
        generated += index;
        nl();
    }

    static void storeLocal(String id) {

      for (int i = 0; i < 99; i++) {
        if (classVars[i] != null) {
          if (classVars[i].equals(id)) {

            generated += "\n\tputfield " + getClassName() + "/" + ((ASTIDENT)((SimpleNode)root.jjtGetChild(1)).jjtGetChild(0)).name;
            space();
            getJType(getClassVarType(((ASTIDENT)((SimpleNode)root.jjtGetChild(1)).jjtGetChild(0)).name));
            space();
            return;
            }
        }
        }

        nl();
        tab();
        int index = getFunctionLocals(id);

        if (index == -1) {
            return;
        } else {
            generated += "istore " + index;
        }
        nl();
    }

    private static void addOperation(Node candidate) {
        SimpleNode operation = (SimpleNode)candidate.jjtGetChild(1);
        tab();
        generated += "ldc " + ((ASTNUM) operation.jjtGetChild(0)).value;
        nl();
        tab();
        generated += "ldc " + ((ASTNUM) operation.jjtGetChild(1)).value;
        nl();
        tab();
        generated += "iadd"; //TODO verificar o tipo de variavel e operação
        nl();
        tab();
        
        int index = getFunctionLocals(((ASTIDENT)candidate.jjtGetChild(0)).name);
        generated += "istore " + index;
        nl();
    }


    static void addNew(Node candidate) {
        String classIdent = ((ASTIDENT)((SimpleNode)candidate).jjtGetChild(0)).name;

        nl();
        tab();
        generated += "new";
        space();
        generated += classIdent;
        nl();
        tab();
        generated += "dup";
        nl();
        tab();
        generated += "invokespecial";
        space();
        generated += classIdent;
        generated += "/<init>()V";
        nl();        
    }


    static void parseMethodArgumentsType(Node node) {
            switch(node.getId()) {
                case ParserTreeConstants.JJTIDENT:
                    String name = ((ASTIDENT) node).name;
                    SymbolTable st = ((SimpleNode)node).symbolTable;
                    Symbol symbol = st.getSymbol(name);
                    generated += parseType(symbol.getType());
                break;
                case ParserTreeConstants.JJTNUM:
                case ParserTreeConstants.JJTADD:
                case ParserTreeConstants.JJTSUB:
                case ParserTreeConstants.JJTMUL:
                case ParserTreeConstants.JJTDIV:
                    generated += "I";
                    break;
                case ParserTreeConstants.JJTAND:
                case ParserTreeConstants.JJTLESSTHAN:
                case ParserTreeConstants.JJTNEGATION:
                case ParserTreeConstants.JJTBOOL:
                    generated += "Z";
                    break;
                case ParserTreeConstants.JJTVOID:
                    generated += "V";
                    break;
                case ParserTreeConstants.JJTFUNC_METHOD:
                    generated += parseType(getMethodReturnType(node));
                    break;
            }
        }



    static void addMethodCall(Node funcMethod) {

        Node callNode = funcMethod.jjtGetChild(1);
        String funcName = ((ASTIDENT)callNode.jjtGetChild(0)).name;

        //If method is nonstatic, load address
        if(!checkIfStatic(funcMethod)) {
            nl();
            tab();
            generated += "aload";
            if (((SimpleNode)(funcMethod.jjtGetChild(0))).id == ParserTreeConstants.JJTTHIS) {
                // space();
                generated += "_0"; 
            } else if (((SimpleNode)(funcMethod.jjtGetChild(0))).id == ParserTreeConstants.JJTIDENT) {

                String key = ((ASTIDENT)(funcMethod.jjtGetChild(0))).name;
                String[] output = key.split("\\.");
                int index = getFunctionLocals(output[0]);

                space();
                generated += Integer.toString(index); 
            }


        }

        Node[] args = ((SimpleNode)callNode.jjtGetChild(1)).jjtGetChildren();

        for (Node n: args) {
            switch(((SimpleNode)n).id) {
                case ParserTreeConstants.JJTIDENT:
                    String name = ((ASTIDENT)n).name;
                    int ind = getFunctionLocals(name);

                    if(ind == -1){
                      int classVarI = checkIfClassVar((SimpleNode)n);

                      if (classVarI != -1) {
                        tab();
                        generated += "getfield ";
                        space();
                        generated += getClassName() + "/";
                        generated += classVars[classVarI];
                        space();
                        getJType(getClassVarType(classVars[classVarI]));
                        nl();
                      }
                    }else{

                    nl();
                    tab();
                    generated += "iload ";
                    generated += Integer.toString(ind);
                    nl();
                    }
                    break;
                case ParserTreeConstants.JJTADD:
                case ParserTreeConstants.JJTSUB:
                case ParserTreeConstants.JJTMUL:
                case ParserTreeConstants.JJTDIV:
                case ParserTreeConstants.JJTAND:
                case ParserTreeConstants.JJTLESSTHAN:
                    makeOperation(n);
                    break;
                case ParserTreeConstants.JJTNEGATION:
                    handleNeg(n);
                    break;
                case ParserTreeConstants.JJTFUNC_METHOD:
                    addMethodCall(n);
                    break;
                case ParserTreeConstants.JJTNUM:
                    generated += "\n\tbipush " + ((ASTNUM) n).value;
                    break;
                case ParserTreeConstants.JJTBOOL:
                    if (((ASTBOOL)n).truth_value)
                        generated += "\n\ticonst_1";
                    else
                        generated += "\n\ticonst_0";
                    break;
            }
        }

        // ArrayList<String> localVars = getFunctionLocals(args);

        // nl();
        // for(String s: localVars) {
        //     tab();
        //     generated += "iload ";
        //     generated += s;
        //     nl();
        // }

        tab();
        String key = ((ASTFUNC_METHOD)funcMethod).call;
       	String[] output = key.split("\\.");

        if(checkIfStatic(funcMethod)) {
            generated += "invokestatic";

        } else {
            nl();
            tab();
            generated += "invokevirtual";

        }
        
        space();
        generated += output[0];
        generated += "/";
        generated += funcName;
        generated += "(";

        for (int i=0; i < args.length; i++) {
            parseMethodArgumentsType(args[i]);
        }


        generated += ")";

        TypeEnum ret = getMethodReturnType(funcMethod);
        generated += parseType(ret);
    }

    private static String parseType(TypeEnum returnType) {
        switch(returnType) {
            case INT:
                return "I";
            //TODO
            case VOID:
                return "V";
            case BOOL:
                return "Z";
            default:
                return "";
        }
    }

    static TypeEnum getMethodReturnType(Node funcMethod) {
        String key = ((ASTFUNC_METHOD)funcMethod).call;
        ArrayList<TypeEnum> args = ((ASTFUNC_METHOD)funcMethod).arguments;
        
        SymbolTable st = ((SimpleNode)funcMethod).symbolTable;
        if (st.existsMethodSymbol(key)) {
            MethodSymbol symbol = (MethodSymbol) st.getSymbol(key);
            return symbol.getReturnType(args);
        }
        return null;
    }

    static boolean checkIfStatic(Node funcMethod) {
        String key = ((ASTFUNC_METHOD)funcMethod).call;
        ArrayList<TypeEnum> args = ((ASTFUNC_METHOD)funcMethod).arguments;
        SymbolTable st = ((SimpleNode)funcMethod).symbolTable;

        if (st.existsMethodSymbol(key)) {
            MethodSymbol symbol = (MethodSymbol) st.getSymbol(key);
            return symbol.isStatic(args);
        }

        return false;
    }


    static ArrayList<String> getFunctionLocals(Node args[]) {
        ArrayList<String> localVars = new ArrayList<String>();
        int counter = 0;
        for (Node n: args) {
            for (int i= 1; i < 99; i++) {
                if (locals[i] != null)
                    if (((ASTIDENT)n).name.equals(locals[i])) {
                        localVars.add(Integer.toString(i));
                        counter++;
                    }
            }
        }
        return localVars;
    }

    static int getFunctionLocals(String arg) {
        for (int i= 1; i < 99; i++) {
            if (locals[i] != null) {
                if (locals[i].equals(arg))
                    return i;
            }

        }
        return -1;
    }


    private static void subOperation(Node candidate) {
        SimpleNode operation = (SimpleNode)candidate.jjtGetChild(1);
        tab();
        generated += "ldc " + ((ASTNUM) operation.jjtGetChild(0)).value;
        nl();
        tab();
        generated += "ldc " + ((ASTNUM) operation.jjtGetChild(1)).value;
        nl();
        tab();
        generated += "isub"; //TODO verificar o tipo de variavel e operação
        nl();
        tab();
        int index = getFunctionLocals(((ASTIDENT)candidate.jjtGetChild(0)).name);
        generated += "istore " + index;
        nl();
    }


    
    public static void addVariableAllocation(Node assign) {
        Node value = assign.jjtGetChild(1);
        int valueString = ((ASTNUM) value).value;

        nl();
        tab();
        generated += "bipush";
        space();
        generated += valueString;

        Node identificationNode = assign.jjtGetChild(0);
        String identification = ((ASTIDENT)identificationNode).name;

        for(int i = 1; classVars[i] != null; i++){
            if(classVars[i].equals(identification)){
              nl();
              tab();
              generated += "putfield " + getClassName() + "/" + identification;
              space();
              getJType(getClassVarType(identification));
              nl();
              return;
            }
        }
        
        storeLocal(identification);

        
    }


    public static void addStandardInitializer() {
        nl();
        generated += ".method <init>()V";
        nl();
        tab();
        generated += "aload_0";
        nl();
        tab();
        generated = generated + "invokenonvirtual " + extendedClass + "/<init>()V";
        nl();
        tab();
        generated += "return";
        nl();
        generated += ".end method";
        nl();
    }


    public static void addMainHeader() {
        nl();
        generated += ".method public static main([Ljava/lang/String;)V";
        nl();
        tab();
        generated += ".limit stack 99";
        nl();
        tab();
        generated += ".limit locals 99";
        nl();
    }


    public void print() {
        System.out.println("\n\n" + this.generated);
    }


    public static void space() {
        generated += " ";
    }


    public static void nl() {
        generated += "\n";
    }


    public static void tab() {
        generated += "\t";
    }


    public PrintWriter fileToWrite() throws IOException {

        // File dir = new File("jvm");
        // if (!dir.exists()) dir.mkdirs();

        File file = new File("test/fixtures/libs/compiled/Simple.j");

        if(!file.exists())
            file.createNewFile();

        PrintWriter writer = new PrintWriter(file);
        return writer;
    }

    static void printLocals() {
        for (String local: locals)
            if (local != null)
                System.out.println(local);
    }

    static String getClassName() {
      return ( (ASTIDENT)((SimpleNode) classNode).jjtGetChild(0)).name;
    }

    static TypeEnum getClassVarType(String name){
        SimpleNode nodeClass = null;
        for (Node child : root.jjtGetChildren()) {
          if (child.getId() == ParserTreeConstants.JJTCLASS)
            nodeClass = (SimpleNode)child;
        }

          for (Node n : nodeClass.jjtGetChildren()){
            if( ((SimpleNode)n).id == ParserTreeConstants.JJTVARIABLE){
                if((((ASTIDENT)((SimpleNode) n).jjtGetChild(1)).name).equals(name)){
                  return ((ASTTYPE)((SimpleNode) n).jjtGetChild(0)).typeID;
                }
            }
        }

        return null;
        
      }
}