======================= Compiler for the Java-- language to Java Bytecodes =======================

GROUP: 2A

NAME1: Eduardo João Santana Macedo      NR1: up201703658  GRADE1: <0 to 20 value>  CONTRIBUTION1: 25%
NAME2: José Miguel da Costa Simões      NR2: up201704317  GRADE2: <0 to 20 value>  CONTRIBUTION2: 25%
NAME3: Mark Timothy Vasconcelos Meehan  NR3: up201704581  GRADE3: <0 to 20 value>  CONTRIBUTION3: 25%
NAME4: Tito Alexandre Trindade Griné    NR4: up201706732  GRADE4: 20 CONTRIBUTION4: 25%


GLOBAL Grade of the project: 19.5


SUMMARY:

This project is a compiler for the Java-- language to Java Bytecodes. Some of its main features include:
 - Ability to parse code written in Java-- with LL(1).
 - Syntactical errors detection with recovery mechanisms.
 - Semantic analysis.
 - JVM code generation capable of being read by Jasmin.
 - Register allocation optimization using liveness analysis and graph coloring algorithm.
 - While loops optimized to minimize jumps.
 - Constant propagation optimization within scopes.
 - Constant folding for simple operations.
 - Optimize code generation for useless assigns.


EXECUTE:

For this project, gradle should be installed. (https://gradle.org/install/)

Project setup

### Run

To run there are two options: Run the ".class" files or run the JAR.

### Run ".class"

To run the ".class" files, do the following:

        java -cp "./build/classes/java/main/" <class_name> <arguments>

Where <class_name> is the name of the class you want to run and <arguments> are the arguments to be passed to "main()".

### Run ".jar"

To run the JAR, do the following command:

        java -jar comp2020-2a.jar <arguments>

Where <arguments> are the arguments to be passed to "main()".

## Test

To test the program, run "gradle test".
This will execute the build, and run the JUnit tests in the "test" folder.
The flag "-i" can be used to see the output from the tests. (i.e., "gradle test -i").

## Execution Control

In the "javacc" folder there is a class "ControlVars.java" that holds some variables that control the execution of the parser,
allowing for some costumization. Here are the description of each of those variables:

+---------------------------+------------------------------------------------------------------------------------------------------------+
|          Variable         |                                                 Description                                                |
+---------------------------+------------------------------------------------------------------------------------------------------------+
|     ANALYSE_SEMANTICS     | If **true**, it performs a semantic analysis of the code.                                                  |
+---------------------------+------------------------------------------------------------------------------------------------------------+
|   ANALYSE_SCOPE_VAR_INIT  | If **true**, it will check whether variables are only initialized within if, else or while scopes and      |
|                           | therefore might not be initialized when used.                                                              |
+---------------------------+------------------------------------------------------------------------------------------------------------+
|    THROW_VAR_INIT_ERROR   | If **true**, variables that are used but are not initialized will throw an error instead of a warning.     |
+---------------------------+------------------------------------------------------------------------------------------------------------+
|  THROW_WARNING_EXCEPTION  | If **true**, warnings will throw an exception causing the semantic analysis to fail.                       |
+---------------------------+------------------------------------------------------------------------------------------------------------+
|     PRINT_SYMBOL_TABLE    | If **true**, it prints the symbol table of each meaningful scope in the code.                              |
+---------------------------+------------------------------------------------------------------------------------------------------------+
|         PRINT_AST         | If **true**, it prints the Abstract Syntax Tree.                                                           |
+---------------------------+------------------------------------------------------------------------------------------------------------+
|      RUN_CUSTOM_TESTS     | If **true**, it will run tests for custom made ".jmm" files.                                               |
+---------------------------+------------------------------------------------------------------------------------------------------------+
|      SAVE_JASMIN_CODE     | If **true**, it will save the generated Jasmin code in a file, otherwise it will print it to the terminal. |
+---------------------------+------------------------------------------------------------------------------------------------------------+
|    GENERATE_JASMIN_CODE   | If **true**, it will generate the JVM bytecode instructions, readable by Jasmin, for the code.             |
|                           | Successful generation is only guaranteed if Semantic Analysis is performed before.                         |
+---------------------------+------------------------------------------------------------------------------------------------------------+
|       PRINT_LIVENESS      | If **true** and -r optimization is requested, it will print, for each of the optimized methods, the        |
|                           | nodes where each variable is "live".                                                                       |
+---------------------------+------------------------------------------------------------------------------------------------------------+
|      PRINT_NODE_TABLE     | If **true** and -r optimization is requested, it will print, for each of the optimized methods, each node  |
|                           | of the Flow Graph and the corresponding IN and OUT sets.                                                   |
+---------------------------+------------------------------------------------------------------------------------------------------------+
|      PRINT_FLOW_GRAPH     | If **true** and -r optimization is requested, it will print, for each of the optimized methods, each node  |
|                           | of the Flow Graph and the corresponding successors.                                                        |
+---------------------------+------------------------------------------------------------------------------------------------------------+
| PRINT_ALLOCATED_REGISTERS | If **true** and -r optimization is requested, it will print, for each of the optimized methods, the        |
|                           | registers allocated for each of the local variables.                                                       |
+---------------------------+------------------------------------------------------------------------------------------------------------+
|       PRINT_RI_GRAPH      | If **true** and -r optimization is requested, it will print, for each of the optimized methods, the        |
|                           | Register Interference Graph with the corresponding assigned "colors".                                      |
+---------------------------+------------------------------------------------------------------------------------------------------------+


DEALING WITH SYNTACTIC ERRORS:

The syntactic error detection is performed for up to MAX_ERRORS (currently set to 10) in while expression
before stopping the program.
An error recovery mechanism is implemented where terminal symbols are ignored if an error is detected, until a safe
condition is met, continuing from that point detecting errors.
With each error, a message is shown containing information about the line and column where the error occurred followed
by the token it found and the tokens it was expecting.


SEMANTIC ANALYSIS:

To perform semantic analysis, each node contains an evaluation function that it calls for each of its children an checks
if any semantic errors, that can be detected in the node, are present.
Some of the most relevant semantic errors detected are:
 - Non static imports must have the class imported beforehand.
 - If a class extends another, the extended class must have been imported beforehand.
 - Variable declarations are checked if they haven't been previously declared, this doesn't include class and local variables
   with the same name (which is allowed), and if the identifier is valid.
 - When assigning variables, the variable type matches the type of the right hand side of an assign.
 - When using a variable, the variable must have been instantiated before. If it was instantiated only inside a while loop
   or only in one of the if/else branches, a warning/error is thrown. Instantiations in both the if and else scopes are
   considered valid instantiations.
 - When performing an operation (comparisons, arithmetic and logical operations) both operands must be of the correct type.
 - When a method is called, the method must exists, that is, it has to be either imported, belongs to the extended class, or
   id defined somewhere in the class (the order in which methods are defined does not matter).
 - When calling methods, the parameters accepted by the method must match the given arguments. Method overloading is allowed
   and correctly dealt with.
 - A method return must be of the same type as declared in the method header.
 - In if and while conditions, the result of the expression must be a boolean.

With each error a message is added to a list containing useful information about the error, such as, the line and column
where it occurred, and more detailed information depending on the type of error. After the AST is fully traversed all
error messages are displayed and an exception is thrown.


INTERMEDIATE REPRESENTATIONS (IRs):

Other than the AST, no other representation was constructed. However, auxiliary structures where implemented to aid on
some of the functionalities. This includes:
 - SymbolTable - a structure that holds the symbol table for a given scope and contains a reference to the previous scope
                 in which its nested, if it has one. For example, the SymbolTable of a method contains all symbols in the method's
                 scope and a reference to the class scope which it self has a reference to the file scope (i.e. imports).
 - FlowGraph - a structure used to analyse and represent the liveness of each variable in a method's scope, as well as having
               functions to perform register allocation.
 - RIGraph - a structure representing the Register Interference Graph of a methods scope that helps in register allocation
             optimization by allowing the use of graph coloring algorithms to find a suitable register allocation.


CODE GENERATION:

While traversing the AST to perform semantic analysis, some information is included in each node to later help the
generation of code, together with the SymbolTable of each node.
The code is generate for each method by traversing the necessary nodes and translating each segment into its appropriate
set of bytecode instructions.
Besides the normal generation of code, some optimizations are made if certain conditions are met, such has:
 - While loops are optimized to avoid jumping to the condition at the end of the loop body.
 - Variables assigned a constant are replaced by the constant within the scope until they are reassigned a new value.
 - Instructions regarding constant are chosen depending on their value (e.g. 2 -> iconst_2; -300 -> bipush -300; 1000000 -> ldc 1000000).
 - If an operation is done with just constants, the operation code is replaced by the constant equivalent to the result of that operation.
 - AND operations are represented in code such that the left side is evaluated before the right side, avoiding evaluating the right side
   if the left side is already false.
 - A call to a function is followed by a "pop" instruction if its value is not used.
 - Class variables can have names which are

 Given the size of the code generator, it's difficult to assess whether everything is working well even in edge cases.
 For that reason one of the main disadvantages is both finding and fixing bugs which require a lot of trial and error
 before the root of the bug is found. Tied to this is the fact that adding further optimizations is difficult, since
 a simple optimization may require changing a lot of code or even its logic.


OVERVIEW:

Our tool relies heavily on the AST, both for semantic analysis, code generation and some optimizations.
The algorithms used for the -r optimization where the ones described in the lecture slides. This includes the
algorithm used to calculate a variable's liveness, and the vertex coloring algorithm including the taught heuristic
used to assign registers for each variable of the RIGraph.
Besides jasmin and javacc, no other libraries where used to complete this project.


TASK DISTRIBUTION:


PROS:

Besides all the requirements that were asked for this project, both optimizations where implemented in some form.
In addition, the following features were included:
 - An extensive set of flags (present in ControlVars) are included which allow to see a terminal representation of some
   of the structures that are used along the compiler, including the AST, SymbolTable, FlowGraph, RIGraph.
 - Various code generation optimizations previously described.
 - Detection of uninitialized variables with the distinction for conditional branches and while loops.
 - Assigns to variables which are not used are removed from the generated code, except when a function is called, in which
   case the call to the function is kept but not the storage of the return value.

CONS:













