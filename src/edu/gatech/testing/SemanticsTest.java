package edu.gatech.testing;

import edu.gatech.icompiler.SymbolTable;
import edu.gatech.icompiler.Parser;
import edu.gatech.icompiler.Semantics;
import edu.gatech.icompiler.Type;
import edu.gatech.util.Node;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Alex on 3/14/14.
 */
public class SemanticsTest
{
    @Test
    public void test1()
    {
        Parser parser = new Parser( false);
        File f = new File("ex1.tiger");
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
                for(String error:errors)
                {
                    System.out.println(error);
                }
                System.out.println(errors.size()+" errors");
                assertEquals(errors.size(), 0);
            }
        }
        catch(Exception ex)
        {}
    }

    @Test
    public void test2()
    {
        Parser parser = new Parser( false);
        File f = new File("ex2.tiger");
        try
        {
            Node<Type> node = parser.parse(f);
            if(node!=null)
            {
                SymbolTable table = new SymbolTable();
                table.populateTable(node.getChildren().get(1));
                Semantics s = new Semantics(node,table);
                List<String> errors = s.performChecks();
                for(String error:errors)
                {
                    System.out.println(error);
                }
                System.out.println(errors.size()+" errors");
                assertEquals(errors.size(), 0);
            }
        }
        catch(Exception ex)
        {}
    }

    @Test
    public void test3()
    {
        Parser parser = new Parser( false);
        File f = new File("ex3.tiger");
        try
        {
            Node<Type> node = parser.parse(f);
            if(node!=null)
            {
                SymbolTable table = new SymbolTable();
                table.populateTable(node.getChildren().get(1));
                Semantics s = new Semantics(node,table);
                List<String> errors = s.performChecks();
                for(String error:errors)
                {
                    System.out.println(error);
                }
                System.out.println(errors.size()+" errors");
                assertEquals(errors.size(), 0);
            }
        }
        catch(Exception ex)
        {}
    }

    @Test
    public void test4()
    {
        Parser parser = new Parser( false);
        File f = new File("ex4.tiger");
        try
        {
            Node<Type> node = parser.parse(f);
            if(node!=null)
            {
                SymbolTable table = new SymbolTable();
                table.populateTable(node.getChildren().get(1));
                Semantics s = new Semantics(node,table);
                List<String> errors = s.performChecks();
                for(String error:errors)
                {
                    System.out.println(error);
                }
                System.out.println(errors.size()+" errors");
                assertEquals(errors.size(), 2);
            }
        }
        catch(Exception ex)
        {}
    }

    @Test
    public void test5()
    {
        Parser parser = new Parser( false);
        File f = new File("ex5.tiger");
        try
        {
            Node<Type> node = parser.parse(f);
            if(node!=null)
            {
                SymbolTable table = new SymbolTable();
                table.populateTable(node.getChildren().get(1));

                Semantics s = new Semantics(node,table);
                List<String> errors = s.performChecks();
                for(String error:errors)
                {
                    System.out.println(error);
                }
                System.out.println(errors.size()+" errors");
                assertEquals(errors.size(), 1);
            }
        }
        catch(Exception ex)
        {}
    }


}
