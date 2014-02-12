package edu.gatech.icompiler;

/**
 * Created by Alex on 2/11/14.
 */
public enum RuleType
{
    TIGER_PROGRAM, DECLARATION_STATEMENT, TYPE_DECLARATION_LIST, VAR_DECLARATION_LIST,
    FUNCT_DECLARATION_LIST, TYPE_DECLARATION, TYPE, TYPE_ID, VAR_DECLARATION, ID_LIST,
    ID_LIST_TAIL, OPTIONAL_INIT, FUNCT_DECLARATION, PARAM_LIST, PARAM_LIST_TAIL, RET_TYPE,
    PARAM, STAT, STAT_SEQ, STAT_SEQ_TAIL, STAT_ASSIGN, EXPR, MULTEXPR, MULT_TAIL, ADDEXPR,
    ADD_TAIL, BINEXPR, BIN_TAIL, MULTOP, ADDOP, BINOP, CONST, EXPR_LIST, EXPR_LIST_TAIL,
    LVALUE, LVALUE_TAIL;

    public static RuleType getFromString(String foo){
        //anglebrackets???
        if( null != foo)
        {
            for(RuleType t : RuleType.values())
                if(foo.equalsIgnoreCase(t.name()))
                    return t;
        }
        return null;
    }
}
