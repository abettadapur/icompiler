package edu.gatech;

import edu.gatech.icompiler.Parser;
import java.io.File;

/**
 * Created by Stefano on 2/23/14.
 */
public class Driver {

    public static void main(String[] args){

        boolean debug = false;

        if(args.length ==0 || args.length >2){
            System.out.println("Usage: [fileToParse] <debug flag>\n\t\tAdd flag -d to see debugging output");
            return;
        }

        if(args.length ==2 && ! (args[1].equals("-d"))){
            System.out.println("Please use -d as a debug flag");
            return;
        }

        if(args.length == 2 && args[1].equals("-d"))
            debug = true;

        Parser parser = new Parser(debug);

        try{
            parser.parse(new File(args[0]));
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
