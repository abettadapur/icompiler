package edu.gatech.intermediate;

import edu.gatech.fallback.Binding;

import java.util.List;

/**
 * Created by Stefano on 3/10/14.
 */
public class IntermediateOperation {

    private Operator op;
    private Binding x;
    private Binding y;
    private Binding z;

    private String label;
    private List<Binding> parameters;



    public IntermediateOperation(Operator op, Binding x, Binding y, Binding z, String label, List<Binding> parameters){
        this.op = op;
        this.x = x;
        this.y =y;
        this.z = z;

        this.label = label;
        this.parameters = parameters;


    }

}
