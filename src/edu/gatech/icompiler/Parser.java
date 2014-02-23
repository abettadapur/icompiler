package edu.gatech.icompiler;

import org.junit.Rule;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * Created by Alex on 2/12/14.
 */
public class Parser
{
    private List<List<List<Type>>> parserTable;
    private Map<TokenType, Integer> tokenColumns;
    private Map<RuleType, Integer> ruleRows;
    private final boolean DEBUG;

    public Parser(){
    this(false);
}

    public Parser(boolean DEBUG){

        this.DEBUG = DEBUG;
        parserTable = new ArrayList<>();
        tokenColumns = new HashMap<>();
        ruleRows = new HashMap<>();

        initializeTable();
    }

    private void initializeTable()
    {
        try
        {
            InputStream stream = this.getClass().getResourceAsStream("/ParseTable.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
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
            {
                RuleType type = RuleType.getFromString(bar.trim().replace("<", "").replace(">", ""));
                if(type!=null)
                    out.add(RuleType.getFromString(bar.trim().replace("<", "").replace(">", "")));
                else
                    System.err.println("TABLE FAILURE: "+bar);
            }
            else
                out.add(TokenType.getFromString(bar.trim()))  ;
        
        return out;
    }

    public boolean parse(File f) throws FileNotFoundException, IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader(f));
        StringBuilder sb = new StringBuilder();
        String line = "";
        while((line = reader.readLine())!=null)
        {
            sb.append(line+"\n");
        }
       return parse(sb.toString());

    }

    public boolean parse(String program)
    {
        boolean error = false;

        Stack<Type> stack = new Stack<>();
        Scanner scanner = new Scanner(program, DEBUG);

        stack.push(RuleType.TIGER_PROGRAM);

        while(scanner.hasNext() && stack.size()!=0)
        {
            if(!error)
            {


                Token token = scanner.peek();

                TokenType tokenType = token.TYPE;

                if(token.TOKEN_CONTENT.equals("init_win_cond"))
                {
                    int a = 0;
                }

                Type currentType = stack.pop();

                if(tokenType==TokenType.COMMENT)
                {
                    stack.push(currentType);
                    scanner.next();
                    continue;
                }

                if(tokenType==TokenType.ERROR)
                {

                    //scanner.next();
                    System.err.println("LEXICAL ERROR "+scanner.getLineCount()+": "+token.TOKEN_CONTENT);
                    error = true;
                    continue;
                }

                if(currentType.isToken() && tokenType == currentType)
                {

                    //System.out.println(stack);
                    scanner.next();
                }


                else if(currentType.isToken() && tokenType != currentType)
                {
                    System.out.println("TOKEN MISMATCH "+scanner.getLineCount()+": "+tokenType.name()+"instead of " +((TokenType)currentType).name());
                    error = true;
                    continue;
                }

                else if(!currentType.isToken()){

                    int row = ruleRows.get(currentType);
                    int col = tokenColumns.get(tokenType);

                    List<Type> nextStates = parserTable.get(row).get(col);

                    if(nextStates.size() == 1 && nextStates.get(0) == RuleType.FAIL)
                    {
                        System.err.println("RULE TOKEN MISMATCH: "+tokenType.name()+" matched with <"+((RuleType)currentType).name()+">");

                        error=true;
                        //scanner.next();
                        continue;
                    }

                    if(!(nextStates.size() == 1 && nextStates.get(0) == RuleType.EPSILON))
                        for(int i=nextStates.size()-1; i>= 0; i--)
                            stack.push(nextStates.get(i));

                }

            }
            else
            {
                Token token = scanner.next();
                //if(token.TYPE!=TokenType.ERROR)
                  //  System.err.println(token.TYPE.name() + ": "+ token.TOKEN_CONTENT);
            }
        }

        if(!error)
            System.out.println("Successful Parse");
        return !error;

    }
}
