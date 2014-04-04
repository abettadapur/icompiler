package edu.gatech.generation;

import edu.gatech.intermediate.Intermediate;
import edu.gatech.intermediate.IntermediateOperation;
import edu.gatech.intermediate.Operator;

import java.util.List;

/**
 * Created by Alex on 4/4/14.
 */
public class NaiveAllocator implements IAllocator {
    @Override
    public void annotateIr(List<IntermediateOperation> stream)
    {
        for(int i=0; i<stream.size(); i++)
        {
            IntermediateOperation operation = stream.get(i);
            if(operation.getOp()!= Operator.ASSIGN)
            {

            }
        }
    }
}
