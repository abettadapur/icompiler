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

        Parser parser = new Parser( );

        assertEquals(parser.parse("let var i, n: int := 0; in printi(i); printi(n); end"), true);

    }
    @Test
    public void fileParse() throws Exception
    {
        Parser parser = new Parser();
        File f = new File("SampleProgram.tg");
        assertEquals(parser.parse(f),true);

    }
    @Test
    public void fileParse2() throws Exception
    {
        Parser parser = new Parser();
        File f = new File("ex1.tiger");
        assertEquals(parser.parse(f),true);
    }

}
