package edu.gatech.icompiler;

import java.io.*;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.gatech.facade.IScanner;
/**
 * Created by Alex on 1/30/14.
 */

public class Scanner implements Iterator<String>, Closeable, AutoCloseable, IScanner {

    private CharBuffer tokenBuffer;
    private Reader charStream;
    private List<List<Integer>> selectionTable;

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


    }

    public void initializeTable()
    {
        try
        {
            File csv = new File("ScannerTable.csv");
            BufferedReader reader = new BufferedReader(new FileReader(csv));
            String line="";
            int row = -1;
            while((line=reader.readLine())!=null)
            {
                if(row==-1)
                {
                    row++;
                    continue;
                }
                selectionTable.add(new ArrayList<Integer>());
                String[] items = line.split(",");
                for(int i=1; i<items.length; i++)
                {
                    //add items
                    selectionTable.get(row).add(Integer.parseInt(items[i]));
                }
                row++;
            }
        }
        catch(FileNotFoundException fex)
        {}
        catch(IOException ioex)
        {

        }
    }

    public boolean hasNext(){
        //TODO: implement
        return false;
    }

    public String next(){
        //TODO: implement
        return null;
    }

    public void remove(){
        //TODO: implement

    }

    public void close(){
        //TODO: implement
    }



}
