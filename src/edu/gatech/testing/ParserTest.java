package edu.gatech.testing;

import edu.gatech.icompiler.Parser;
import junit.framework.TestCase;

/**
 * Created by Stefano on 2/12/14.
 */
public class ParserTest extends TestCase {

    public void testParse() throws Exception {

        Parser parser = new Parser( );

        assertEquals(parser.parse("let var i, n: int = 0; in printi(i); printi(n); end"), true);

    }

}
