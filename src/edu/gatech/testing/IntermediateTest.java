package edu.gatech.testing;


import edu.gatech.icompiler.Parser;
import edu.gatech.icompiler.Semantics;
import edu.gatech.icompiler.SymbolTable;
import edu.gatech.icompiler.Type;
import edu.gatech.intermediate.Intermediate;
import edu.gatech.util.Node;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;

/**
 * Created by Stefano on 3/19/14.
 */
public class IntermediateTest {

    @Test
    public void testIntermediate1(){

        Parser parser = new Parser( false);
        File f = new File("ex1.tiger");
        File bar = new File("ex1.ir");
        try
        {
            Node<Type> node = parser.parse(f);
            if(node!=null)
            {
                SymbolTable table = new SymbolTable();
                table.populateTable(node.getChildren().get(1));

                System.out.println(table);
                Semantics s = new Semantics(node,table);
                List<String> errors = s.performChecks();
                System.out.println("Output should look like this: ");

                Scanner scan = new Scanner(bar);

                while(scan.hasNext())
                    System.out.println(scan.nextLine());

                Intermediate i = new Intermediate(node,table);

                System.out.println("Output is actually this: ");

                System.out.println(i);
                }



        }
        catch(Exception ex)
        {ex.printStackTrace();}
    }

        //TODO: test branching
        public void testIntermediate2(){

        }

        //TODO: test function calls
        public void testIntermediate3(){

        }

        //TODO: test looping
        public void testIntermediate4(){

        }

        //should output nothing on incorrect code?
        public void testIntermediateWithError(){

        }
}

