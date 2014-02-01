package edu.gatech.icompiler;

import java.io.*;
import java.nio.CharBuffer;
import java.util.Iterator;
import edu.gatech.facade.IScanner;
/**
 * Created by Alex on 1/30/14.
 */

public class Scanner implements Iterator<String>, Closeable, AutoCloseable, IScanner {

    private CharBuffer tokenBuffer;
    private Reader charStream;

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
