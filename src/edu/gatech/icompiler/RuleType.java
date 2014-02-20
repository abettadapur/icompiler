package edu.gatech.icompiler;

/**
 * Created by Alex on 2/11/14.
 */
public enum RuleType implements Type
{
    TIGER_PROGRAM, DECLARATION_SEGMENT, TYPE_DECLARATION_LIST, VAR_DECLARATION_LIST,
    FUNCT_DECLARATION_LIST, TYPE_DECLARATION, TYPE, TYPE_DIM, TYPE_ID, VAR_DECLARATION, ID_LIST,
    ID_LIST_TAIL, OPTIONAL_INIT, FUNCT_DECLARATION, PARAM_LIST, PARAM_LIST_TAIL, RET_TYPE,
    PARAM, STAT, STAT_SEQ, STAT_SEQ_TAIL, STAT_ELSE, STAT_ASSIGN, EXPR, MULTEXPR, MULT_TAIL, ADDEXPR,
    ADD_TAIL, BINEXPR, BIN_TAIL, MULTOP, ADDOP, BINOP, CONST, EXPR_LIST, EXPR_LIST_TAIL,
    LVALUE, LVALUE_TAIL, EPSILON, FAIL, EXPR_OR_ID, EXPR_OR_FUNC, EXPR_NO_LVALUE, OP_EXPR, EXPR_TAIL;

    public static RuleType getFromString(String foo)
    {

        if( null != foo)
        {
            if(foo.equals("-1"))
                return FAIL;
            if(foo.equals("E"))
                return EPSILON;

            for(RuleType t : RuleType.values())
                if(foo.equalsIgnoreCase(t.name()))
                    return t;
        }
        return null;
    }

    public boolean isToken(){return false;}

}
