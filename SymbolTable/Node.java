import java.util.ArrayList;


public class Node {
	String Type;
	ArrayList<String> parameters;
	
	public Node(String Type)
	{
		Type = this.Type;
	}
	
	public Node(ArrayList<String> parameters)
	{
		Type="FUNC";
		parameters= this.parameters;
	}
}
