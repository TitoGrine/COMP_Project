public class ControlVars {
    public static final boolean ANALYSE_SEMANTICS = true;
    public static final boolean ANALYSE_SCOPE_VAR_INIT = true;
    public static final boolean THROW_VAR_INIT_ERROR = true;
    public static final boolean THROW_WARNING_EXCEPTION = false;
    public static final boolean PRINT_SYMBOLTABLE = false;
    public static final boolean PRINT_AST = true;
    public static final boolean RUN_CUSTOM_TESTS = true;
    public static final boolean GENERATE_JASMIN_CODE = true;
    public static final boolean SAVE_JASMIN_CODE = true;

    // Related to -r optimization
    public static final boolean PRINT_LIVENESS = false;
    public static final boolean PRINT_NODE_TABLE = false;
    public static final boolean PRINT_FLOW_GRAPH = false;
    public static final boolean PRINT_ALLOCATED_REGISTERS = false;
    public static final boolean PRINT_RI_GRAPH = false;

    // Extra optimizations
    public static final boolean IGNORE_USELESS_ASSIGNS = true;

    // Var Types
    public static final String BOOL = "BOOL";
    public static final String INT = "INT";
    public static final String ARRAY = "ARRAY";
    public static final String METHOD = "METHOD";
    public static final String STRING = "STRING";
    public static final String VOID = "VOID";

    // Terminal Colors
    public static final String RESET = "\033[0m";  // Text Reset

    public static final String PURPLE = "\033[0;35m";  // PURPLE
    public static final String BLUE = "\033[0;34m";    // BLUE
    public static final String CYAN = "\033[0;36m";    // CYAN
    public static final String GREEN = "\033[0;32m"; // GREEN

    public static final String RED_BRIGHT = "\033[0;91m";    // RED
    public static final String GREEN_BRIGHT = "\033[0;92m";  // GREEN

    public static final String RED_BOLD = "\033[1;31m";    // RED
    public static final String YELLOW_BOLD = "\033[1;33m"; // YELLOW
    public static final String WHITE_BOLD = "\033[1;37m";  // WHITE

    public static final String RED_UNDERLINED = "\033[4;31m";    // RED
    public static final String YELLOW_UNDERLINED = "\033[4;33m";  // WHITE
}
