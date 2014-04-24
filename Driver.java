package edu.gatech;

import edu.gatech.generation.Generator;
import edu.gatech.generation.MipsOperation;
import edu.gatech.generation.allocation.BasicAllocator;
import edu.gatech.generation.allocation.ExtendedAllocator;
import edu.gatech.generation.allocation.IAllocator;
import edu.gatech.generation.allocation.NaiveAllocator;
import edu.gatech.icompiler.Parser;
import edu.gatech.icompiler.Semantics;
import edu.gatech.icompiler.SymbolTable;
import edu.gatech.icompiler.Type;
import edu.gatech.intermediate.Intermediate;
import edu.gatech.intermediate.IntermediateOperation;
import edu.gatech.util.Node;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

/**
 * Created by Stefano on 2/23/14.
 */
public class Driver {

    public static void main(String[] args){

        boolean debug = false;
        IAllocator allocator = null;

        if(args.length ==0 || args.length<2){
            System.out.println("Usage: [fileToParse] [-n|-b] <debug flag>\n\t\tAdd flag -d to see debugging output");
            return;
        }


        if(args.length ==3 && ! (args[2].equals("-d"))){
            System.out.println("Please use -d as a debug flag");
            return;
        }

        if(args.length==3&&!(args[1].equals("-n"))&&!(args[1].equals("-b"))&&!(args[1].equals("-e")))
        {
            System.out.println("Please use -n to use the naive allocator, -b to use the basic block allocator, and -e to use the basic block allocator");
            return;
        }

        if(args.length == 3 && args[2].equals("-d"))
        {
            debug = true;
            if(args[1].equals("-n"))
                allocator = new NaiveAllocator();
            else if(args[1].equals("-b"))
                allocator = new BasicAllocator();
            else
            	allocator = new ExtendedAllocator();
            
        }

        Parser parser = new Parser(debug);

        try{
            Node<Type> parseTree =  parser.parse(new File(args[0]));
            if(parseTree!=null)
            {
                SymbolTable table = new SymbolTable();
                List<String> errors = table.populateTable(parseTree.getChildren().get(1));
                if(errors.size()==0)
                {
                    if(debug)
                        System.out.println(table.toString());

                    Semantics checker = new Semantics(parseTree, table);
                    errors = checker.performChecks();
                    if(errors.size()!=0)
                    {
                        for(String s:errors)
                            System.err.println(s);
                    }
                    System.err.println(errors.size()+ " ERRORS");

                    if(errors.size() == 0){


                        Intermediate intermediate = new Intermediate(debug, parseTree, table );
                        List<IntermediateOperation> irstream = intermediate.getIntermediates();
                        if(debug)
                        {

                            for(IntermediateOperation operation: irstream)
                                System.out.println(operation);
                        }
                        allocator.annotateIr(irstream);
                        List<MipsOperation> program = Generator.generateCode(irstream, table);
                        if(debug)
                        {
                            System.out.println("----Compiled Program----");
                            for(MipsOperation operation: program)
                                System.out.println(operation);
                        }

                        String  filePrefix = args[0].split("\\.")[0];
                        File file = new File(filePrefix+".asm");
                        file.createNewFile();

                        FileWriter fw = new FileWriter(file.getAbsoluteFile());
                        BufferedWriter bw = new BufferedWriter(fw);
                        for(MipsOperation operation: program)
                        {
                            bw.write(operation.toString());
                            bw.newLine();
                            bw.flush();
                        }



                    }
                }
                else
                {

                    for(String s: errors)
                    {
                        System.err.println(s);
                    }
                    System.err.println(errors.size()+ "ERRORS");
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
