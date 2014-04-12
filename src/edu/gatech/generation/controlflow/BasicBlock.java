package edu.gatech.generation.controlflow;

import edu.gatech.intermediate.Intermediate;
import edu.gatech.intermediate.IntermediateOperation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 4/8/14.
 */
public class BasicBlock
{
    private List<IntermediateOperation> contents;
    private int startIndex;
    private int lastIndex;
    private List<BasicBlock> nextBlocks;

    private String entryLabel;

    public BasicBlock(int startIndex, int lastIndex, String entryLabel)
    {
        this.entryLabel = entryLabel;
        this.startIndex=startIndex;
        this.lastIndex=lastIndex;
        this.nextBlocks = new ArrayList<>();
    }

    public List<IntermediateOperation> getContents(){
        return contents;
    }

    public void addOperation(IntermediateOperation op ){
        contents.add(op);
    }

    public void addNext(BasicBlock b)
    {
        nextBlocks.add(b);
    }

}
