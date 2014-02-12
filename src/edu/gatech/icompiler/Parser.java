package edu.gatech.icompiler;

import org.junit.Rule;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;

/**
 * Created by Alex on 2/12/14.
 */
public class Parser
{
    private Stack<Entity> stack;
    private Scanner scanner;
    private List<List<List<Entity>>> parserTable;
    private HashMap<TokenType, Integer> tokenColumns;
    private HashMap<RuleType, Integer> ruleRows;



    public boolean parse(String program)
    {
        scanner = new Scanner(program);
        stack.push(new Entity<RuleType>(RuleType.TIGER_PROGRAM, ""));
        while(scanner.hasNext())
        {

            Entity<TokenType> token = scanner.peek();
            Entity top = stack.pop();

            if(top.isToken())
            {
                //match tokens
            }
            else
            {
                int row = ruleRows.get(top.TYPE);
                int col = tokenColumns.get(token.TYPE);
                //index to table
                List<Entity> contents = parserTable.get(row).get(col);
                if(contents.get(0).TYPE == RuleType.FAIL)
                {
                        //error
                }
                else if(contents.get(0).TYPE!=RuleType.EPSILON)
                {
                    for(int i=contents.size()-1; i>=0; i--)
                    {
                        stack.push(contents.get(i));
                    }
                }
            }


            //quit, or push
        }
        return false;

    }
}
