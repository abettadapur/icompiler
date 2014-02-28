package edu.gatech.testing;

import edu.gatech.icompiler.Parser;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.File;
import static org.junit.Assert.*;
/**
 * Created by Stefano on 2/12/14.
 */
public class ParserTest  {

    @Test
    public void testParse() throws Exception {

        Parser parser = new Parser(true );

        assertEquals(parser.parse("let var i, n: int := 0; in printi(i); printi(n); end"), true);


    }
    @Test
    public void fileParse() throws Exception
    {
        Parser parser = new Parser( true);
        File f = new File("ex1.tiger");
        assertEquals(parser.parse(f),true);

    }
    @Test
    public void fileParse2() throws Exception
    {
        Parser parser = new Parser(true);
        File f = new File("ex2.tiger");
        assertEquals(parser.parse(f),true);
    }
    @Test
    public void fileParse3() throws Exception
    {
        Parser parser = new Parser(true );
        File f = new File("ex3.tiger");
        assertEquals(parser.parse(f),true);
    }
    @Test
    public void fileParse4() throws Exception
    {
        Parser parser = new Parser(false);
        File f = new File("ex4.tiger");
        assertEquals(parser.parse(f),false);
    }
    @Test
    public void fileParse5() throws Exception
    {
        Parser parser = new Parser(false);
        File f = new File("ex5.tiger");
        assertEquals(parser.parse(f),true);
    }
    @Test
    public void fileParse6() throws Exception
    {
        Parser parser = new Parser(false);
        File f = new File("ex6.tiger");
        assertEquals(parser.parse(f),false);
    }
    @Test
    public void fileParse7() throws Exception
    {
        Parser parser = new Parser(false);
        File f = new File("ex7.tiger");
        assertEquals(parser.parse(f),false);
    }

    @Test
    public void masterTest() throws Exception
    {
        Parser parser = new Parser(true);
        File f = new File("tictactoe.tiger");
        assertEquals(parser.parse(f),true);
    }


}
