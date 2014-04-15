package edu.gatech.generation.controlflow;

import edu.gatech.intermediate.Intermediate;
import edu.gatech.intermediate.IntermediateOperation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Alex on 4/8/14.
 */
public class BasicBlock
{
    private List<IntermediateOperation> contents;
    private int startIndex;
    private int lastIndex;
    private List<BasicBlock> nextBlocks;
    private Set<String> in,out;

    public BasicBlock(int startIndex, int lastIndex)
    {
        this.startIndex=startIndex;
        this.lastIndex=lastIndex;
        this.nextBlocks = new ArrayList<>();
        this.contents = new ArrayList<>();
        in = new HashSet<>();
        out = new HashSet<>();
    }

    public List<IntermediateOperation> getContents(){
        return contents;
    }

    public IntermediateOperation getFirstOp(){
        return contents.get(0);
    }

    public IntermediateOperation getLastOp(){
        return contents.get(contents.size()-1);
    }

    public List<BasicBlock> getNextBlocks(){
        return nextBlocks;
    }

    public void addOperation(IntermediateOperation op ){
        contents.add(op);
    }

    public void addNext(BasicBlock b)
    {
        nextBlocks.add(b);
    }

    public Set<String> getIn() {
        return in;
    }

    public Set<String> getOut() {
        return out;
    }
}
