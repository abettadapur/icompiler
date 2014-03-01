import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Stack;


public class SymbolTable {

	static Hashtable<String, ArrayList<Attributes>> table =new Hashtable<String, ArrayList<Attributes>>();
	static int scopeLevel = 0;
	static Stack<ScopeElement> activeScope= new Stack<ScopeElement>();
	
	
	public static void enterNewScope()
	{
		scopeLevel++;
	}
	
	public int closeScope()
	{
		// removing all elements of current scope level:
		int currentScopeLevel = activeScope.peek().scope;
		if( currentScopeLevel == -1)
			return -1;
		while( !activeScope.isEmpty() && activeScope.peek().scope == currentScopeLevel)
		{
			ScopeElement temp = activeScope.pop();
			
			//case where there is only one symbol with this name
			if(table.get(temp.name).size()==1)
				table.remove(temp.name);
			// find and delete attributes element with current scope
			else
			{
				ArrayList<Attributes> list= table.get(temp.name);
				for(int i=0;i<list.size();i++)
				{
					if(list.get(i).scope == temp.scope)
					{
						list.remove(i);
						break;
					}
				}
				//overwritingList
				table.put(temp.name, list);				
			}
		}
		
		// finally decrementing scopeLevel to indicate scope has been closed
		return --scopeLevel;		
	}
	
	public Attributes enterSymbol(String name)
	{
		Node temp = new Node("ID");
		Attributes curAttributes = new Attributes(temp, scopeLevel);
		if(table.get(name)!= null)
		{
			for(Attributes scopesInUse: table.get(name))
			{
			// Checking to make sure current symbol does not have same scope as existing symbol with same name
				if(scopesInUse.scope == scopeLevel)
					return null;
			}
			// adding inner scope symbols to the front
			table.get(name).add(0,curAttributes);
		}
        // adding first symbol with this name
		else
		{
			ArrayList<Attributes> attributeList = new ArrayList<Attributes>();
			attributeList.add(curAttributes);
			table.put(name, attributeList);
		}
		
		// adding to active scope stack
		activeScope.push(new ScopeElement(name,scopeLevel));
		return curAttributes;
	}
	
	public Attributes enterSymbol(String name, ArrayList<String> parameters)
	{
		Node temp = new Node(parameters);
		Attributes curAttributes = new Attributes(temp, scopeLevel);
		if(table.get(name)!= null)
		{
			for(Attributes scopesInUse: table.get(name))
			{
			// Checking to make sure current symbol does not have same scope as existing symbol with same name
				if(scopesInUse.scope == scopeLevel)
					return null;
			}
			// adding inner scope symbols to the front
			table.get(name).add(0,curAttributes);
		}
		// adding first symbol with this name 
		else
		{
			ArrayList<Attributes> attributeList = new ArrayList<Attributes>();
			attributeList.add(curAttributes);
			table.put(name, attributeList);
		}
		
		// adding to active scope stack
				activeScope.push(new ScopeElement(name,scopeLevel));
				return curAttributes;
	}
	
	public Attributes find(String name)
	{
		if(name==null || table.get(name) == null )
			return null;
		
		// finding if symbol has been declared in this scope or higher scope starting from inner scope
		Attributes curr = null;
		Stack<ScopeElement> searchStack= activeScope;
		ScopeElement found=null;
		while(!searchStack.isEmpty())
			if((found=searchStack.pop()).name.equals(name))
				break;
		if(found == null)
			return null;
		// if symbol is valid return its attributes
		for(Attributes a: table.get(name))
		{
			if(a.scope == found.scope)
				return a;
		}
		
		return null;
	}
	
	
}


