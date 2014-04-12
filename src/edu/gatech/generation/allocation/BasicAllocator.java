package edu.gatech.generation.allocation;

import edu.gatech.generation.controlflow.BasicBlock;
import edu.gatech.intermediate.IntermediateOperation;
import edu.gatech.intermediate.OperationType;

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
        entryPoints = buildBlocks(irStream);
    }

    private List<BasicBlock> buildBlocks(List<IntermediateOperation> irStream)
    {
        List<BasicBlock> blocks = new ArrayList<>();

        List<Integer> leaders = new ArrayList<>();

        for(int i=0; i<irStream.size(); i++)
        {
            IntermediateOperation operation = irStream.get(i);

            if(operation.getLabel()!=null&&!operation.getLabel().equals(""))
            {
                leaders.add(i);

            }else if(operation.getType() == OperationType.BRANCH || operation.getType()==OperationType.GOTO ){
                leaders.add(i+1);
            }

        }
        return blocks;
    }
}
