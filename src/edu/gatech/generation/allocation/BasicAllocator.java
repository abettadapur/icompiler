package edu.gatech.generation.allocation;

import edu.gatech.generation.controlflow.BasicBlock;
import edu.gatech.intermediate.IntermediateOperation;
import edu.gatech.intermediate.OperationType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 4/4/14.
 */
public class BasicAllocator implements IAllocator
{
    @Override
    public void annotateIr(List<IntermediateOperation> irStream)
    {
        List<BasicBlock> basicBlocks = buildBlocks(irStream);
    }

    private List<BasicBlock> buildBlocks(List<IntermediateOperation> irStream)
    {
        List<BasicBlock> blocks = new ArrayList<>();
        int leaderIndex = 0;
        for(int i=1; i<irStream.size(); i++)
        {
            IntermediateOperation operation = irStream.get(i);
            if(operation.getLabel()!=null&&!operation.getLabel().equals("")&&!operation.getLabel().equals("main"))
            {
                blocks.add(new BasicBlock(leaderIndex, i-1));
                leaderIndex=i;
            }
            else if(operation.getType() == OperationType.BRANCH)
            {
                blocks.add(new BasicBlock(leaderIndex, i));
                leaderIndex=i+1;
            }
        }
        return blocks;
    }
}
