package edu.gatech.intermediate;

import edu.gatech.icompiler.Entry;
import edu.gatech.icompiler.Token;

import java.util.List;

/**
 * Created by Stefano on 3/10/14.
 */
public class IntermediateOperation {

    private Operator op;
    private Entry x;
    private Entry y;
    private Entry z;

    private String label;
    private List<Entry> parameters;



    public IntermediateOperation(Operator op, Entry x, Entry y, Entry z, String label, List<Entry> parameters){
        this.op = op;
        this.x = x;
        this.y =y;
        this.z = z;

        this.label = label;
        this.parameters = parameters;


    }

}
