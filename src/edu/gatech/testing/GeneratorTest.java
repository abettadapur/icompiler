package edu.gatech.testing;

import edu.gatech.generation.Generator;
import edu.gatech.generation.MipsOperation;
import edu.gatech.generation.allocation.IAllocator;
import edu.gatech.generation.allocation.NaiveAllocator;
import edu.gatech.icompiler.Parser;
import edu.gatech.icompiler.Semantics;
import edu.gatech.icompiler.SymbolTable;
import edu.gatech.icompiler.Type;
import edu.gatech.intermediate.Intermediate;
import edu.gatech.intermediate.IntermediateOperation;
import edu.gatech.util.Node;
import org.junit.Test;

import java.io.File;
import java.util.List;

/**
 * Created by Stefano on 4/11/14.
 */
public class GeneratorTest {

    @Test
    public void naiveGenerationTest(){

        boolean debug = false;

        Parser parser = new Parser(debug);

        try{
            Node<Type> parseTree =  parser.parse(new File("ex1.tiger"));
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
                        IAllocator allocator = new NaiveAllocator();
                        allocator.annotateIr(irstream);
                        List<MipsOperation> program = Generator.generateCode(irstream, table);
                        System.out.println("----Compiled Program----");
                        for(MipsOperation operation: program)
                            System.out.println(operation);


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

    @Test
    public void basicAllocationTest(){

    }

    @Test
    public void extendedAllocationTest(){

    }

}
