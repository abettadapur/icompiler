package edu.gatech.generation.controlflow;

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

    public BasicBlock(int startIndex, int lastIndex)
    {
        this.startIndex=startIndex;
        this.lastIndex=lastIndex;
        this.nextBlocks = new ArrayList<>();
    }

    public void addNext(BasicBlock b)
    {
        nextBlocks.add(b);
    }

}
