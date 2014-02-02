package edu.gatech.icompiler;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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

    private final int INITIAL_BUFFER_SIZE = 11; //primes are good, right?

    public Scanner(String foo){
        this(new StringReader(foo));
    }

    public Scanner(File foo) throws FileNotFoundException{
        this(new FileReader(foo));
    }

    public Scanner(Reader charStream){

        this.charStream = new PeekBackReader(charStream);
        this.tokenBuffer = new ArrayList<>();
        this.selectionTable = new ArrayList<List<Integer>>();
        this.stateNames = new HashMap<>();
        this.symbolColumns = new HashMap<>();
        initializeTable();

    }

    public void initializeTable()
    {
        try
        {
            File csv = new File("ScannerTable.csv");
            BufferedReader reader = new BufferedReader(new FileReader(csv));
            String line="";
            int row = 0;

            String[] symbols = reader.readLine().split(",");

            for(int i=1; i < symbols.length; i++)
            {
                if(symbols[i].equals("~"))
                {
                    symbols[i]=",";
                }
                symbolColumns.put(symbols[i], i-1 );
            }

            while((line=reader.readLine())!=null)
            {
                selectionTable.add(new ArrayList<Integer>());
                String[] items = line.split(",");

                //index state names by row
                stateNames.put(row, TokenType.getFromString(items[0]));

                //add items
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

    public boolean hasNext(){

        boolean out = false;

        try{
            out = charStream.hasNext();
        }catch(IOException e){
            e.printStackTrace();
        }

        return out;
    }

    //TODO: implement && test
    public Token next(){

        tokenBuffer.clear();




        int currentColumn =-1;
        int currentRow = 0;

        TokenType currentState = null;


        try{
            while(charStream.peek()==' '||charStream.peek()=='\r'||charStream.peek()=='\n'||charStream.peek()=='\t')
            {
                charStream.read();
            }


            while(charStream.hasNext()){

                lastCharacter = (char)charStream.read();
                System.out.println(lastCharacter);
                System.out.println(symbolColumns.get("" + lastCharacter));
                if(!symbolColumns.containsKey(""+lastCharacter))
                {
                    currentColumn = symbolColumns.get("OTHERS");
                }
                else
                    currentColumn = symbolColumns.get( "" + lastCharacter );

                currentRow = selectionTable.get(currentRow).get(currentColumn);

                if(currentRow < 0) {

                    if(null != currentState)
                    {
                        charStream.unread((char)lastCharacter);
                        return new Token(currentState, Util.stringFromList(tokenBuffer));
                    }

                    else
                        throw new Error("Lexical Error?");

                }

                currentState = stateNames.get(currentRow);
                tokenBuffer.add(lastCharacter);
            }
            if(null!=currentState)
            {
                return new Token(currentState, Util.stringFromList(tokenBuffer));
            }
            else
                throw new Error("Lexical Error?");

        }catch(IOException e){
            e.printStackTrace();
        }

        return null;
    }

    public void remove(){
        //TODO: implement

    }

    public void close(){

        try{
            charStream.close();
        }catch(IOException e){
            e.printStackTrace();
        }

    }



}
