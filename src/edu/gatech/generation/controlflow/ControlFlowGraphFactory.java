package edu.gatech.generation.controlflow;

import edu.gatech.intermediate.IntermediateOperation;
import edu.gatech.intermediate.OperationType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stefano on 4/14/14.
 */
public class ControlFlowGraphFactory {

    public static void printGraph(List<BasicBlock> blocks){
        int i=0;
        for(BasicBlock block : blocks){

            System.out.println("Starting block : " +i++);

            for(IntermediateOperation op : block.getContents())
                System.out.println(op.toString());
            System.out.println("Block has out edges to blocks starting with: ");

            for(BasicBlock link : block.getNextBlocks()){
                System.out.println(link.getFirstOp());
            }

            System.out.println();


        }
    }


    public static List<BasicBlock> getBasicBlocks(List<IntermediateOperation> irStream)
    {
        List<BasicBlock> blocks = new ArrayList<>();

        List<Integer> leaders = new ArrayList<>();
        int start = 0;
        for(IntermediateOperation op:irStream)
        {
            if("main".equals(op.getLabel()))
                break;
            start++;
        }
        leaders.add(start);
        //find leaders
        for(int i=start; i<irStream.size(); i++)
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

        leaders.add(irStream.size());

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

            BasicBlock currentBlock = blocks.get(i);

            IntermediateOperation lastOp  = currentBlock.getLastOp();


            if(lastOp.getType() == OperationType.GOTO){
                //search for label

                for(BasicBlock block : blocks)
                    if(block.getFirstOp().getLabel().equals(lastOp.getX()))
                    {
                        currentBlock.addNext(block);
                        block.addPrev(currentBlock);
                    }

            }else if(lastOp.getType() == OperationType.BRANCH){
                //search for label and add next block
                if(i+1 < blocks.size())
                {
                    currentBlock.addNext(blocks.get(i + 1));
                    blocks.get(i + 1).addPrev(currentBlock);
                }

                for(BasicBlock block : blocks)
                    if(block.getFirstOp().getLabel().equals(lastOp.getZ()))
                    {
                    	currentBlock.addNext(block);
                    	block.addPrev(currentBlock);
                    }
                        

            }
            else{
                //add next block
                if(i+1 < blocks.size()){
                    currentBlock.addNext(blocks.get(i+1));
                    blocks.get(i + 1).addPrev(currentBlock);
                }
            }

        }


        return blocks;
    }

}
