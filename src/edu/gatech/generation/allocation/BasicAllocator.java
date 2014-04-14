package edu.gatech.generation.allocation;

import edu.gatech.generation.controlflow.BasicBlock;
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
        entryPoints = buildBlocks(irStream);
    }

    private List<BasicBlock> buildBlocks(List<IntermediateOperation> irStream)
    {
        List<BasicBlock> blocks = new ArrayList<>();

        List<Integer> leaders = new ArrayList<>();

        leaders.add(0);
        //find leaders
        for(int i=0; i<irStream.size(); i++)
        {
            IntermediateOperation operation = irStream.get(i);

                if(operation.getLabel()!=null&&!operation.getLabel().equals(""))
            {
                if(!leaders.contains(i))
                    leaders.add(i);

            }else if(operation.getType() == OperationType.BRANCH || operation.getType()==OperationType.GOTO ){
                if(!leaders.contains(i+1))
                    leaders.add(i+1);
            }
        }

        //set block leaders
        //TODO: fix last block
        for(int i = 1; i < leaders.size(); i++){

            int leader = leaders.get(i-1);
            int end = leaders.get(i); //last index in sublist is exclusive

            BasicBlock block = new BasicBlock(leader, end);
            blocks.add( block);

            List<IntermediateOperation> foo = irStream.subList( leader, end );

            for(IntermediateOperation bar : foo)
                block.addOperation(bar);

        }

        for(int i=0; i < blocks.size(); i++){

            IntermediateOperation lastOp  = blocks.get(i).getLastOp();

            if(lastOp.getType() == OperationType.GOTO){
                //search for label

                for(BasicBlock block : blocks)
                    if(block.getFirstOp().getLabel().equals(lastOp.getX()))
                        blocks.get(i).addNext(block);


            }else if(lastOp.getType() == OperationType.BRANCH){
                //search for label and add next block
                if(i+1 < blocks.size())
                    blocks.get(i).addNext(blocks.get(i+1));

                for(BasicBlock block : blocks)
                    if(block.getFirstOp().getLabel().equals(lastOp.getZ()))
                        blocks.get(i).addNext(block);

            }
            else{
                //add next block
                if(i+1 < blocks.size())
                    blocks.get(i).addNext(blocks.get(i+1));
            }

        }



        return blocks;
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
