package edu.gatech.testing;

import edu.gatech.facade.IScanner;
import edu.gatech.icompiler.*;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by Stefano on 1/31/14.
 */
public class ScannerTest {

    //TODO: implement testing battery

    @Test
    public void testNext() throws Exception {

        IScanner scanner = new Scanner("var N := 8");

        assertEquals(scanner.next(), new Token(TokenType.VAR, "var"));

        assertEquals(scanner.next(), new Token(TokenType.ID, "N" ));

        assertEquals(scanner.next(), new Token(TokenType.ASSIGN, ":=" ));

        assertEquals(scanner.next(), new Token(TokenType.INTLIT, "8"));

        scanner = new Scanner ("type intArray := array of int\n");

        assertEquals(scanner.next(), new Token(TokenType.TYPE, "type"));

        assertEquals(scanner.next(), new Token(TokenType.ID, "intArray"));

        assertEquals(scanner.next(), new Token(TokenType.ASSIGN, ":="));

        assertEquals(scanner.next(), new Token(TokenType.ARRAY, "array"));

        assertEquals(scanner.next(), new Token(TokenType.OF, "of"));

        assertEquals(scanner.next(), new Token(TokenType.ID, "int"));  //what is int?

        scanner = new Scanner("var row := intArray [ N ] of 0");

        assertEquals(scanner.next(), new Token(TokenType.VAR, "var"));

        assertEquals(scanner.next(), new Token(TokenType.ID, "row"));

        assertEquals(scanner.next(), new Token(TokenType.ASSIGN, ":="));

        assertEquals(scanner.next(), new Token(TokenType.ID, "intArray"));

        assertEquals(scanner.next(), new Token(TokenType.LBRACK, "["));

        assertEquals(scanner.next(), new Token(TokenType.ID, "N"));

        assertEquals(scanner.next(), new Token(TokenType.RBRACK, "]"));

        assertEquals(scanner.next(), new Token(TokenType.OF, "of"));

        assertEquals(scanner.next(), new Token(TokenType.INTLIT, "0"));

        scanner = new Scanner("endif identifier begin end enddo");

        assertEquals(scanner.next(), new Token(TokenType.ENDIF, "endif"));

        assertEquals(scanner.next(), new Token(TokenType.ID, "identifier"));

        assertEquals(scanner.next(), new Token(TokenType.BEGIN, "begin"));

        assertEquals(scanner.next(), new Token(TokenType.END, "end"));

        assertEquals(scanner.next(), new Token(TokenType.ENDDO, "enddo"));


    }

}
