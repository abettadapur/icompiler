package edu.gatech.icompiler;

import java.io.*;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import edu.gatech.facade.IScanner;
/**
 * Created by Alex on 1/30/14.
 */

public class Scanner implements Iterator<Token>, Closeable, AutoCloseable, IScanner {

    private CharBuffer tokenBuffer;
    private Reader charStream;
    private List<List<Integer>> selectionTable;
    private HashMap<Integer, TokenType> stateNames;
    private HashMap<String, Integer> symbolColumns;

    private final int INITIAL_BUFFER_SIZE = 11; //primes are good, right?

    public Scanner(String foo){
        this(new StringReader(foo));
    }

    public Scanner(File foo) throws FileNotFoundException{
        this(new FileReader(foo));
    }

    public Scanner(Reader charStream){

        this.charStream = charStream;
        this.tokenBuffer = CharBuffer.allocate(INITIAL_BUFFER_SIZE);
        this.selectionTable = new ArrayList<List<Integer>>();
        this.stateNames = new HashMap<>();
        this.symbolColumns = new HashMap<>();

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
                symbolColumns.put(symbols[i], i );

            while((line=reader.readLine())!=null)
            {
                selectionTable.add(new ArrayList<Integer>());
                String[] items = line.split(",");

                //index state names by row
                if(TokenType.getFromString(items[0]) != null)
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
            out = charStream.ready();
        }catch(IOException e){
            e.printStackTrace();
        }

        return out;
    }

    public Token next(){
        //TODO: implement
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
