package edu.gatech.icompiler;

import org.junit.Rule;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by Alex on 2/12/14.
 */
public class Parser
{
    private Stack<Type> stack;
    private Scanner scanner;
    private List<List<List<Type>>> parserTable;
    private Map<TokenType, Integer> tokenColumns;
    private Map<RuleType, Integer> ruleRows;

    public Parser(){
        initializeTable();
    }

    private void initializeTable()
    {
        try
        {
            File csv = new File("ParserTable.csv");
            BufferedReader reader = new BufferedReader(new FileReader(csv));
            String line= "";
            tokenColumns = new HashMap<>();
            ruleRows = new HashMap<>();

            int row =0;
            String[] symbols = reader.readLine().split(",");

            for(int i=1; i < symbols.length; i++)
            {
                if(symbols[i].equals("~"))
                    symbols[i]=",";

                tokenColumns.put(TokenType.getFromString(symbols[i]), i-1 );
            }

            while((line=reader.readLine())!=null)
            {
                parserTable.add(new ArrayList<List<Type>>());
                String[] items = line.split(",");

                ruleRows.put( RuleType.getFromString(items[0].trim()), row);

                for(int i=1; i<items.length; i++)
                    parserTable.get(row).add(rulesListFromString(items[i]));

                row++;
            }
        }
        catch(IOException ioex)
        {
            System.err.println("Missing ParseTable.csv");
            ioex.printStackTrace();
        }
    }

    private List<Type> rulesListFromString(String foo){
        List<Type> out = new ArrayList<>();

        for(String bar: foo.split(" ") )
            if(bar.startsWith("<"))
                out.add(RuleType.getFromString(bar.trim().replace("<", "").replace(">", "")));
            else
                out.add(TokenType.getFromString(bar.trim()))  ;
        
        return out;
    }

    public boolean parse(String program)
    {
        stack = new Stack<>();
        scanner = new Scanner(program);
        stack.push(RuleType.TIGER_PROGRAM);


        while(scanner.hasNext())
        {

            Entity<TokenType> token = scanner.peek();
            Type top = stack.pop();

            if(top.isToken())
            {
                if(top != token.TYPE)
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
                int row = ruleRows.get(top);
                int col = tokenColumns.get(token);
                //index to table
                List<Type> contents = parserTable.get(row).get(col);
                if(!contents.get(0).isToken())
                {
                        //error
                }
                else if(contents.get(0)!=RuleType.EPSILON)
                {
                    for(int i=contents.size()-1; i --> 0;)
                        stack.push(contents.get(i));

                }
            }


            //quit, or push
        }
        return false;

    }
}
