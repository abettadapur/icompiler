package edu.gatech.generation;

import edu.gatech.intermediate.Intermediate;
import edu.gatech.intermediate.IntermediateOperation;
import edu.gatech.intermediate.OperationType;
import edu.gatech.intermediate.Operator;

import java.util.HashSet;
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
            OperationType type = operation.getType();
            HashSet<String> operands = new HashSet<String>();
            if(type!=null)

            {
                switch(type)
                {
                    case BINARY:
                        operands.add(operation.getX());
                        operands.add(operation.getY());
                        operands.add(operation.getZ());
                        break;
                    case ASSIGN:
                        break; //TODO
                    case GOTO:
                        break;
                    case BRANCH:
                        operands.add(operation.getX());
                        operands.add(operation.getY());
                        break;
                    case RETURN:
                        operands.add(operation.getX());
                    case FUNCTION:
                        break; //TODO
                    case ARRAYSTORE:
                        break; //TODO
                    case ARRAYLOAD:
                        break; //TODO
                }
                int register = 2;
                for(String s: operands)
                {
                    IntermediateOperation store = new IntermediateOperation(Operator.STORE, "$"+register, s, "","",null);
                    IntermediateOperation load = new IntermediateOperation(Operator.LOAD, "$"+register, s, "","",null);
                    stream.add(i+1, store);
                    stream.add(i, load);
                    register++;
                    i++;
                }
            }



        }
        for(IntermediateOperation op:stream)
        {
            System.out.println(op);
        }
    }
}
