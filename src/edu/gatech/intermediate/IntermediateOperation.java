package edu.gatech.intermediate;

import edu.gatech.icompiler.Binding;

import java.util.List;

/**
 * Created by Stefano on 3/10/14.
 */
public class IntermediateOperation {

    public Operator getOp() {
        return op;
    }

    public String getX() {
        return x;
    }

    public String getY() {
        return y;
    }

    public String getZ() {
        return z;
    }

    public String getLabel() {
        return label;
    }

    public List<String> getParameters() {
        return parameters;
    }

    private Operator op;
    private String  x;
    private String  y;
    private String  z;

    private String label;
    private List<String> parameters;

    public IntermediateOperation(Operator op, String x, String y, String z, String label, List<String> parameters){

        this.op = op;
        this.x = x;
        this.y =y;
        this.z = z;

        this.label = label != null && !label.isEmpty() ? label+"\t" : "\t\t";
        this.parameters = parameters;

    }

    @Override
    public String toString(){

        if(op == Operator.UNSUPPORTED)
            return label;

        if(null!= parameters){
            String out = op.name() +", "+ x;
            for(String s : parameters)
                out+=", " + s;
            return out;
        }

        return label + op.name().toLowerCase() + ", " + x + ", " + y +", " + z;

    }
    public void registerReplace(String param, String register)
    {
        if(param.equals(x))
            x = register;
        if(param.equals(y))
            y = register;
        if(param.equals(z))
            z = register;
    }

    public OperationType getType()
    {
        if(op==Operator.ADD||op==Operator.SUB||op==Operator.MULT||op==Operator.DIV||op==Operator.AND||op==Operator.OR)
            return OperationType.BINARY;
        if(op==Operator.ASSIGN)
            return OperationType.ASSIGN;
        if(op==Operator.GOTO)
            return OperationType.GOTO;
        if(op==Operator.BREQ||op==Operator.BRNEQ||op==Operator.BRLT||op==Operator.BRGT||op==Operator.BRGEQ||op==Operator.BRLEQ)
            return OperationType.BRANCH;
        if(op==Operator.RETURN)
            return OperationType.RETURN;
        if(op==Operator.CALLR||op==Operator.CALL)
            return OperationType.FUNCTION;
        if(op==Operator.ARRAY_LOAD)
            return OperationType.ARRAYLOAD;
        if(op==Operator.ARRAY_STORE)
            return OperationType.ARRAYSTORE;
        return null;
    }

}
