package edu.gatech.icompiler;

import org.junit.Rule;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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


    private void initializeTable()
    {
        try
        {
            File csv = new File("ScannerTable.csv");
            BufferedReader reader = new BufferedReader(new FileReader(csv));
            String line= "";

        }
        catch(IOException ioex)
        {

        }
    }

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
                if(top.TYPE!=token.TYPE)
                {
                    //error
                }
                else
                {
                    //consume token, move on
                    scanner.next();
                }
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
                    for(int i=contents.size()-1; i --> 0;)
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
