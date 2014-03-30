package edu.gatech.intermediate;

import edu.gatech.icompiler.Binding;

import java.util.List;

/**
 * Created by Stefano on 3/10/14.
 */
public class IntermediateOperation {

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

}
