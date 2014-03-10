package edu.gatech;

import edu.gatech.icompiler.Parser;
import edu.gatech.icompiler.Semantics;
import edu.gatech.icompiler.Type;
import edu.gatech.util.Node;

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
            Node<Type> parseTree =  parser.parse(new File(args[0]));
            if(parseTree!=null)
            {
                Semantics checker = new Semantics(parseTree, null); //MUST CHANGE
                checker.performChecks();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
