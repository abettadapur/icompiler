package edu.gatech.icompiler;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.HeaderTokenizer;
import edu.gatech.facade.IScanner;
import edu.gatech.util.PeekBackReader;
import edu.gatech.util.Util;

/**
 * Created by Alex on 1/30/14.
 */

public class Scanner implements Iterator<Token>, Closeable, AutoCloseable, IScanner {

    private List<Character> tokenBuffer;
    private PeekBackReader charStream;
    private List<List<Integer>> selectionTable;
    private HashMap<Integer, TokenType> stateNames;
    private HashMap<String, Integer> symbolColumns;
    private Character lastCharacter = null;
    private final int FAIL = -1;
    private int lineCount;

    private Token currentToken;

    public Scanner(String foo){
        this(new StringReader(foo));
    }

    public Scanner(File foo) throws FileNotFoundException{
        this(new FileReader(foo));
    }

    public Scanner(Reader charStream){

        this.charStream = new PeekBackReader(charStream);
        this.tokenBuffer = new ArrayList<>();
        this.selectionTable = new ArrayList<>();
        this.stateNames = new HashMap<>();
        this.symbolColumns = new HashMap<>();
        initializeTable();

        currentToken = loadNext();
        lineCount=1;
    }

    //Takes the CSV table, makes a transition table and creates a symbol lookup table and state name lookup table
    private void initializeTable()
    {
        try
        {
            File csv = new File("ScannerTable.csv");
            BufferedReader reader = new BufferedReader(new FileReader(csv));
            String line= "";
            int row = 0;

            String[] symbols = reader.readLine().split(",");

            //first line is the symbol lookup table
            for(int i=1; i < symbols.length; i++)
            {
                if(symbols[i].equals("~"))
                    symbols[i]=",";

                symbolColumns.put(symbols[i], i-1 );
            }

            //other lines are the transitions
            while((line=reader.readLine())!=null)
            {
                selectionTable.add(new ArrayList<Integer>());
                String[] items = line.split(",");

                //first item is the state name. store that information
                stateNames.put(row, TokenType.getFromString(items[0].trim()));

                //other items are transitions. Populate table
                for(int i=1; i<items.length; i++)
                    selectionTable.get(row).add(Integer.parseInt(items[i]));

                row++;
            }
        }
        catch(FileNotFoundException fex)
        {
            System.err.println("Scanner Table Missing!");
            fex.printStackTrace();
        }
        catch(IOException ioex)
        {
            ioex.printStackTrace();
        }

    }

    //are there more tokens?
    public boolean hasNext(){

        boolean out = false;

        try{
            out = charStream.hasNext();
        }catch(IOException e){
            e.printStackTrace();
        }

        return out;
    }

    private Token loadNext(){
        tokenBuffer.clear();

        //currentCol is the current input char, currentRow is the current state
        int currentColumn =FAIL;
        int currentRow = 0;

        //null unless we have an end state
        TokenType currentState = null;

        try{
            //eliminate whitespace tokens
            while(charStream.peek()==' ' ||charStream.peek()=='\r'||charStream.peek()=='\n'||charStream.peek()=='\t')
            {
               if(charStream.peek()=='\n')
               {
                    lineCount++;
               }
                charStream.read();
            }

            //while there are things in the stream, loop
            while(charStream.hasNext()){

                lastCharacter = (char)charStream.read();

                //if input is not in our language, default to something
                if(!symbolColumns.containsKey("" + lastCharacter))
                    currentColumn = symbolColumns.get("OTHERS");
                else
                    currentColumn = symbolColumns.get("" + lastCharacter );

                //get next state
                currentRow = selectionTable.get(currentRow).get(currentColumn);

                //if there is no next state (i.e state==-1)
                if(currentRow < 0)
                    //remember, currentState is not null only if there is an end state
                    return checkState(currentState);

                //update current state using state names (non accepting states are null in the map)
                currentState = stateNames.get(currentRow);

                //add to the running token buffer
                tokenBuffer.add(lastCharacter);
            }
            //we've run out of characters, need to see if we can accept
            return checkState(currentState);

        }catch(IOException e){
            e.printStackTrace();
        }

        return null;
    }

    private Token checkState(TokenType currentState) throws IOException {
        if(null != currentState)
        {
            String content = Util.stringFromList(tokenBuffer);
            charStream.unread(lastCharacter);
            if(currentState==TokenType.ID)
            {
                TokenType type = TokenType.getFromString(content);

                if(type!=null)
                    currentState=type;
            }

            //these types are no longer supported, easier to check than change the table :/
            if(currentState==TokenType.PERIOD||currentState==TokenType.LBRACE||currentState==TokenType.RBRACE)
            {
                return new Token(TokenType.ERROR,"Lexical Error: "+tokenBuffer.toString()+" was an unexpected sequence");
            }

            return new Token(currentState, content);
        }

        else if(tokenBuffer.size()!=0)
            return new Token(TokenType.ERROR,"Lexical Error: "+tokenBuffer.toString()+" was an unexpected sequence");
        else return null;
    }

    //get the next token
    public Token next(){

        Token temp = currentToken;

        currentToken = loadNext();

        return temp;

    }

    public Token peek(){
        if(!hasNext())
            return null;
        return currentToken;
    }

    public void remove(){
        //TODO: implement
        throw new UnsupportedOperationException();

    }

    public void close(){

        try{
            charStream.close();
        }catch(IOException e){
            e.printStackTrace();
        }

    }

    public int getLineCount()
    {
        return lineCount;
    }
}




