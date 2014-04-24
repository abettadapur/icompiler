package edu.gatech.generation.allocation;

import edu.gatech.intermediate.IntermediateOperation;
import edu.gatech.intermediate.OperationType;
import edu.gatech.intermediate.Operator;
import edu.gatech.util.Util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Alex on 4/4/14.
 */
public class NaiveAllocator implements IAllocator {
    @Override
    public void annotateIr(List<IntermediateOperation> stream, boolean debug)
    {
        int i = 0;
        for(IntermediateOperation op: stream)
        {
            if(op.getLabel().equals("main"))
                break;
            i++;

        }
        for(i=i; i<stream.size(); i++)
        {
            IntermediateOperation operation = stream.get(i);
            OperationType type = operation.getType();
            Set<String> operands = new HashSet<>();
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
                       // operands.add(operation.getX());
                        operands.add(operation.getY());
                        break;
                    case GOTO:
                        break;
                    case BRANCH:
                        operands.add(operation.getX());
                        operands.add(operation.getY());
                        break;
                    case RETURN:
                        operands.add(operation.getX());
                    case FUNCTION:
                        if(operation.getParameters().size()!=0)
                        {
                            for(String s:operation.getParameters())
                                operands.add(s);
                        }
                        break;
                    case FUNCTIONR:
                        operands.add(operation.getX());
                        if(operation.getParameters().size()!=0)
                        {
                            for(String s:operation.getParameters())
                                operands.add(s);
                        }
                        break;
                    case ARRAYSTORE:
                        operands.add(operation.getX());
                        operands.add(operation.getY());
                        operands.add(operation.getZ());
                        break;
                    case ARRAYLOAD:
                        operands.add(operation.getX());
                       // operands.add(operation.getY());
                        operands.add(operation.getZ());
                        break;
                }
                int register = 8;
                for(String s: operands)
                {
                    if(!Util.isNumeric(s))
                    {
                        operation.registerReplace(s, "$"+register);
                        IntermediateOperation store = new IntermediateOperation(Operator.STORE, "$"+register, s, "","",null);
                        IntermediateOperation load = new IntermediateOperation(Operator.LOAD, "$"+register, s, "","",null);
                        if(register==8)
                        {
                            load.setLabel(operation.getLabel());
                            operation.setLabel("");
                        }
                        stream.add(i+1, store);
                        stream.add(i, load);
                        register++;
                        i++;
                    }
                }
            }



        }

    }

}
