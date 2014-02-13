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
    private List<List<List<Type>>> parserTable;
    private Map<TokenType, Integer> tokenColumns;
    private Map<RuleType, Integer> ruleRows;

    public Parser(){

        parserTable = new ArrayList<>();
        tokenColumns = new HashMap<>();
        ruleRows = new HashMap<>();

        initializeTable();
    }

    private void initializeTable()
    {
        try
        {
            File csv = new File("ParseTable.csv");
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
        foo = foo.trim();
        if(foo.equals("E"))
        {
            out.add(RuleType.EPSILON);
            return out;
        }
        if(foo.equals("-1"))
        {
            out.add(RuleType.FAIL);
            return out;
        }

        String[] rules = foo.split(" ");
        for(String bar:rules)
            if(bar.startsWith("<"))
                out.add(RuleType.getFromString(bar.trim().replace("<", "").replace(">", "")));
            else
                out.add(TokenType.getFromString(bar.trim()))  ;
        
        return out;
    }

    public boolean parse(String program)
    {
        Stack<Type> stack = new Stack<>();
        Scanner scanner = new Scanner(program);

        stack.push(RuleType.TIGER_PROGRAM);

        while(scanner.hasNext() && stack.size()!=0){

            TokenType token = scanner.peek().TYPE;

            Type currentType = stack.pop();

            if(currentType.isToken() && token == currentType)
                scanner.next();

            else if(currentType.isToken() && token != currentType)
                return false;

            else if(!currentType.isToken()){

                //TODO: Remove after DEBUG:
                if(!tokenColumns.containsKey(token))
                    System.err.printf("Unexpected token: %s\n", token.toString());

                //TODO: Remove after DEBUG;
                if(!ruleRows.containsKey(currentType))
                    System.err.printf("Unexpected rule: %s\n", currentType.toString());

                int row = ruleRows.get(currentType);
                int col = tokenColumns.get(token);

                List<Type> nextStates = parserTable.get(row).get(col);

                if(nextStates.size() == 1 && nextStates.get(0) == RuleType.FAIL){
                    //TODO: log errors
                    return false;
                }

                if(!(nextStates.size() == 1 && nextStates.get(0) == RuleType.EPSILON))
                    for(int i=nextStates.size()-1; i>= 0; i--)
                        stack.push(nextStates.get(i));

            }

        }


        return stack.empty();

    }
}
