package edu.gatech.generation.allocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import edu.gatech.generation.controlflow.BasicBlock;
import edu.gatech.generation.controlflow.ControlFlowGraphFactory;
import edu.gatech.intermediate.IntermediateOperation;
import edu.gatech.intermediate.OperationType;
import edu.gatech.intermediate.Operator;
import edu.gatech.util.Pair;
import edu.gatech.util.Util;

/**
 * Created by Alex on 4/4/14.
 */
public class ExtendedAllocator extends BasicAllocator implements IAllocator{
	
	private List<BasicBlock> entryPoints;
	private List<BasicBlock> EBBEntryPoints;

	public List<BasicBlock> extendedBasicBlocks(List<BasicBlock> BB)
	{
		List<BasicBlock> EBBs = new ArrayList<>();		
		List<BasicBlock> visited = BB;
		
		while(!visited.isEmpty())
		{
			List<BasicBlock> BBsInEBB = new ArrayList<>();
			Queue<BasicBlock> breadthFirstList = new LinkedList<>();
			breadthFirstList.add(visited.get(0));
			
			while(!breadthFirstList.isEmpty())
			{
				BasicBlock curr = breadthFirstList.remove();
				//removing curr from visited
				for(int i=0;i<visited.size();i++)
				{
					if(curr.getStartIndex() == visited.get(i).getStartIndex()  && curr.getLastIndex() == visited.get(i).getLastIndex())
					{
						visited.remove(i);
						break;
					}
				}
				
				// adding direct non-join succesors of curr to breadthFistList
				for(BasicBlock b: curr.getNextBlocks())
				{
					// if successor is not a join node, add it to breadthFistList
					if(b.getPrevBlocks().size()<=1)
					{
						breadthFirstList.add(b);
					}
				}
				
				BBsInEBB.add(curr);
			}
			
			// make EBB and add to EBBs list
			EBBs.add(makeEBB(BBsInEBB));
		}
		
		return EBBs;
	}
	
	public BasicBlock makeEBB(List<BasicBlock> BBs)
	{
		
		BasicBlock EBB = new BasicBlock(0,0); // hack - not using indexes for allocation right?
		for(BasicBlock b:BBs)
			for(IntermediateOperation i:b.getContents())
				EBB.addOperation(i);

		return EBB;
	}
	
	@Override
	public void annotateIr(List<IntermediateOperation> irStream)
    {
        List<IntermediateOperation> assignments = new ArrayList<>();
        for(IntermediateOperation op: irStream)
        {
            if(op.getLabel().equals("main"))
                break;
            assignments.add(op);
        }
        entryPoints = ControlFlowGraphFactory.getBasicBlocks(irStream);
        ControlFlowGraphFactory.printGraph(entryPoints);
        
        EBBEntryPoints = extendedBasicBlocks(entryPoints);
        
        		
        ControlFlowGraphFactory.printGraph(EBBEntryPoints);
        for(BasicBlock b: EBBEntryPoints){
            fillInOutSets(b);
            annotateBlock(b);
        }

        irStream.clear();
        irStream.addAll(assignments);
        for(BasicBlock b: EBBEntryPoints)
        {
            irStream.addAll(b.getContents());
        }

    }
	
}
