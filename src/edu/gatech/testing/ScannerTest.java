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

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.VAR, "var"));

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.ID, "N" ));

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.ASSIGN, ":=" ));

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.INTLIT, "8"));

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.AND, "&"));

        scanner = new Scanner ("type intArray := array of int");

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.TYPE, "type"));

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.ID, "intArray"));

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.ASSIGN, ":="));

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.ARRAY, "array"));

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.OF, "of"));

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.ID, "int"));  //what is int?

        scanner = new Scanner("var row := intArray [ N ] of 0");

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.VAR, "var"));

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.ID, "row"));

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.ASSIGN, ":="));

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.ID, "intArray"));

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.LBRACK, "["));

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.ID, "N"));

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.RBRACK, "]"));

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.OF, "of"));

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.INTLIT, "0"));

        scanner = new Scanner("endif identifier begin end enddo");

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.ENDIF, "endif"));

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.ID, "identifier"));

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.BEGIN, "begin"));

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.END, "end"));

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.ENDDO, "enddo"));




    }
    @Test
    public void fileTest() throws Exception
    {
        Scanner scanner = new Scanner(new File("SampleProgram.tg"));

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.FOR, "for"));

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.ID, "i"));

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.ASSIGN, ":="));

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.INTLIT, "1"));

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.TO, "to"));

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.INTLIT, "100"));

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.DO, "do"));

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.COMMENT, "/*comment*/"));

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.ID, "sum"));

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.ASSIGN, ":="));

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.ID, "sum"));

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.PLUS, "+"));

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.ID, "X"));

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.LBRACK, "["));

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.ID, "i"));

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.RBRACK, "]"));

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.MULT, "*"));

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.ID, "Y"));

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.LBRACK, "["));

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.ID, "i"));

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.RBRACK, "]"));

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.SEMI, ";"));

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.ENDDO, "enddo"));

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.ID, "printi"));

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.LPAREN, "("));

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.ID, "sum"));

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.RPAREN, ")"));

        assertEquals(scanner.next(), new Entity<TokenType>(TokenType.SEMI, ";"));


    }

}
