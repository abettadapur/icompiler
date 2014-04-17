package edu.gatech.generation.allocation;

import edu.gatech.generation.controlflow.BasicBlock;
import edu.gatech.generation.controlflow.ControlFlowGraphFactory;
import edu.gatech.intermediate.Intermediate;
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
        for(BasicBlock b: entryPoints)
        {
            fillInOutSets(b);
            annotateBlock(b);
        }


    }

    private void annotateBlock(BasicBlock b)
    {
        InterferenceGraph graph = new InterferenceGraph();
        HashMap<String, Integer> table = new HashMap<>();
        for(IntermediateOperation op:b.getContents())
        {
            Set<String> in = op.getIn();
            for(String s:in)
            {
                graph.addNode(s);
            }

        }
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

            operation.getIn().addAll(operation.getOut());
            operation.getIn().removeAll(operation.getDef());
            operation.getIn().addAll(operation.getUse());
            previousIn = operation.getIn();
        }
        block.getIn().addAll(previousIn);
    }

    private Set<String> getVariables(BasicBlock b)
    {
        Set<String> variables = new HashSet<>();
        for(IntermediateOperation op:b.getContents())
        {
            variables.addAll(getOperands(op));
        }
        return variables;
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
