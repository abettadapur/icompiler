package edu.gatech.generation;

import edu.gatech.intermediate.IntermediateOperation;

import java.util.List;

/**
 * Created by Alex on 4/4/14.
 */
public interface IAllocator
{
    public void annotateIr(List<IntermediateOperation> io);
}
