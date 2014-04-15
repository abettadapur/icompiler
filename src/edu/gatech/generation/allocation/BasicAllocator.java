package edu.gatech.generation.allocation;

import edu.gatech.generation.controlflow.BasicBlock;
import edu.gatech.generation.controlflow.ControlFlowGraphFactory;
import edu.gatech.intermediate.IntermediateOperation;
import edu.gatech.intermediate.OperationType;
import edu.gatech.util.Util;

import java.util.*;

/**
 * Created by Alex on 4/4/14.
 */
public class BasicAllocator implements IAllocator
{

    private List<BasicBlock> entryPoints;

    @Override
    public void annotateIr(List<IntermediateOperation> irStream)
    {
        entryPoints = ControlFlowGraphFactory.getBasicBlocks(irStream);

        ControlFlowGraphFactory.printGraph(entryPoints);

    }

    private void fillInOutSets(BasicBlock block)
    {
        Set<String> previousIn=null;
        for(int i=block.getContents().size()-1; i>-1; i--)
        {
            IntermediateOperation operation = block.getContents().get(i);
            if(previousIn!=null)
            {
                operation.getOut().addAll(previousIn); //out[b] = in[b+1]
            }

            Set<String> operands = getOperands(operation);

        }
    }

    private Set<String> getOperands(IntermediateOperation op)
    {
        Set<String> variables = new HashSet<>();
        if(op.getType()!=null)
        {
            switch(op.getType())
            {
                case BINARY:
                    if(!Util.isNumeric(op.getX()))
                        variables.add(op.getX());
                    if(!Util.isNumeric(op.getY()))
                        variables.add(op.getY());
                    if(!Util.isNumeric(op.getZ()))
                        variables.add(op.getZ());
                    break;
                case ASSIGN:
                    if(!Util.isNumeric(op.getX()))
                        variables.add(op.getX());
                    if(!Util.isNumeric(op.getY()))
                        variables.add(op.getY());
                    break;
                case GOTO:
                    break;
                case BRANCH:
                    if(!Util.isNumeric(op.getX()))
                        variables.add(op.getX());
                    if(!Util.isNumeric(op.getY()))
                        variables.add(op.getY());
                    break;
                case RETURN:
                    if(!Util.isNumeric(op.getX()))
                        variables.add(op.getX());
                case FUNCTION:
                    if(op.getParameters().size()!=0)
                    {
                        for(String s:op.getParameters())
                            if(!Util.isNumeric(s))
                                variables.add(s);
                    }
                    break;
                case FUNCTIONR:
                    if(!Util.isNumeric(op.getX()))
                        variables.add(op.getX());
                    if(op.getParameters().size()!=0)
                    {
                        for(String s:op.getParameters())
                            if(!Util.isNumeric(s))
                                variables.add(s);
                    }
                    break;
                case ARRAYSTORE:
                    if(!Util.isNumeric(op.getX()))
                        variables.add(op.getX());
                    if(!Util.isNumeric(op.getY()))
                        variables.add(op.getY());
                    if(!Util.isNumeric(op.getZ()))
                        variables.add(op.getZ());
                    break;
                case ARRAYLOAD:
                    if(!Util.isNumeric(op.getX()))
                        variables.add(op.getX());

                    if(!Util.isNumeric(op.getZ()))
                        variables.add(op.getZ());
                    break;
            }
        }
        return variables;
    }


}
