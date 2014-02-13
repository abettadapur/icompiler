package edu.gatech.testing;

import edu.gatech.facade.IScanner;
import edu.gatech.icompiler.*;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Created by Stefano on 1/31/14.
 */
public class ScannerTest {

    //TODO: implement testing battery

    @Test
    public void testNext() throws Exception {

        IScanner scanner = new Scanner("var N := 8 &");

        assertEquals(scanner.peek(), new Entity<>(TokenType.VAR, "var"));

        assertEquals(scanner.next(), new Entity<>(TokenType.VAR, "var"));

        assertEquals(scanner.peek(), new Entity<>(TokenType.ID, "N" ));

        assertEquals(scanner.next(), new Entity<>(TokenType.ID, "N" ));

        assertEquals(scanner.peek(), new Entity<>(TokenType.ASSIGN, ":=" ));

        assertEquals(scanner.next(), new Entity<>(TokenType.ASSIGN, ":=" ));

        assertEquals(scanner.peek(), new Entity<>(TokenType.INTLIT, "8"));

        assertEquals(scanner.next(), new Entity<>(TokenType.INTLIT, "8"));

        assertEquals(scanner.peek(), new Entity<>(TokenType.AND, "&"));

        assertEquals(scanner.next(), new Entity<>(TokenType.AND, "&"));

        scanner = new Scanner ("type intArray := array of int\n");

        assertEquals(scanner.next(), new Entity<>(TokenType.TYPE, "type"));

        assertEquals(scanner.next(), new Entity<>(TokenType.ID, "intArray"));

        assertEquals(scanner.next(), new Entity<>(TokenType.ASSIGN, ":="));

        assertEquals(scanner.next(), new Entity<>(TokenType.ARRAY, "array"));

        assertEquals(scanner.next(), new Entity<>(TokenType.OF, "of"));

        assertEquals(scanner.next(), new Entity<>(TokenType.ID, "int"));  //what is int?

        scanner = new Scanner("var row := intArray [ N ] of 0");

        assertEquals(scanner.next(), new Entity<>(TokenType.VAR, "var"));

        assertEquals(scanner.next(), new Entity<>(TokenType.ID, "row"));

        assertEquals(scanner.next(), new Entity<>(TokenType.ASSIGN, ":="));

        assertEquals(scanner.next(), new Entity<>(TokenType.ID, "intArray"));

        assertEquals(scanner.next(), new Entity<>(TokenType.LBRACK, "["));

        assertEquals(scanner.next(), new Entity<>(TokenType.ID, "N"));

        assertEquals(scanner.next(), new Entity<>(TokenType.RBRACK, "]"));

        assertEquals(scanner.next(), new Entity<>(TokenType.OF, "of"));

        assertEquals(scanner.next(), new Entity<>(TokenType.INTLIT, "0"));

        scanner = new Scanner("endif identifier begin end enddo");

        assertEquals(scanner.next(), new Entity<>(TokenType.ENDIF, "endif"));

        assertEquals(scanner.next(), new Entity<>(TokenType.ID, "identifier"));

        assertEquals(scanner.next(), new Entity<>(TokenType.BEGIN, "begin"));

        assertEquals(scanner.next(), new Entity<>(TokenType.END, "end"));

        assertEquals(scanner.next(), new Entity<>(TokenType.ENDDO, "enddo"));

    }
    @Test
    public void fileTest() throws Exception
    {
        Scanner scanner = new Scanner(new File("SampleProgram.tg"));

        assertEquals(scanner.next(), new Entity<>(TokenType.FOR, "for"));

        assertEquals(scanner.next(), new Entity<>(TokenType.ID, "i"));

        assertEquals(scanner.next(), new Entity<>(TokenType.ASSIGN, ":="));

        assertEquals(scanner.next(), new Entity<>(TokenType.INTLIT, "1"));

        assertEquals(scanner.next(), new Entity<>(TokenType.TO, "to"));

        assertEquals(scanner.next(), new Entity<>(TokenType.INTLIT, "100"));

        assertEquals(scanner.next(), new Entity<>(TokenType.DO, "do"));

        assertEquals(scanner.next(), new Entity<>(TokenType.COMMENT, "/*comment*/"));

        assertEquals(scanner.next(), new Entity<>(TokenType.ID, "sum"));

        assertEquals(scanner.next(), new Entity<>(TokenType.ASSIGN, ":="));

        assertEquals(scanner.next(), new Entity<>(TokenType.ID, "sum"));

        assertEquals(scanner.next(), new Entity<>(TokenType.PLUS, "+"));

        assertEquals(scanner.next(), new Entity<>(TokenType.ID, "X"));

        assertEquals(scanner.next(), new Entity<>(TokenType.LBRACK, "["));

        assertEquals(scanner.next(), new Entity<>(TokenType.ID, "i"));

        assertEquals(scanner.next(), new Entity<>(TokenType.RBRACK, "]"));

        assertEquals(scanner.next(), new Entity<>(TokenType.MULT, "*"));

        assertEquals(scanner.next(), new Entity<>(TokenType.ID, "Y"));

        assertEquals(scanner.next(), new Entity<>(TokenType.LBRACK, "["));

        assertEquals(scanner.next(), new Entity<>(TokenType.ID, "i"));

        assertEquals(scanner.next(), new Entity<>(TokenType.RBRACK, "]"));

        assertEquals(scanner.next(), new Entity<>(TokenType.SEMI, ";"));

        assertEquals(scanner.next(), new Entity<>(TokenType.ENDDO, "enddo"));

        assertEquals(scanner.next(), new Entity<>(TokenType.ID, "printi"));

        assertEquals(scanner.next(), new Entity<>(TokenType.LPAREN, "("));

        assertEquals(scanner.next(), new Entity<>(TokenType.ID, "sum"));

        assertEquals(scanner.next(), new Entity<>(TokenType.RPAREN, ")"));

        assertEquals(scanner.next(), new Entity<>(TokenType.SEMI, ";"));

    }

}
