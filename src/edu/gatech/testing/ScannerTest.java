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

        assertEquals(scanner.peek(), new Token(TokenType.VAR, "var"));

        assertEquals(scanner.next(), new Token(TokenType.VAR, "var"));

        assertEquals(scanner.peek(), new Token(TokenType.ID, "N" ));

        assertEquals(scanner.next(), new Token(TokenType.ID, "N" ));

        assertEquals(scanner.peek(), new Token(TokenType.ASSIGN, ":=" ));

        assertEquals(scanner.next(), new Token(TokenType.ASSIGN, ":=" ));

        assertEquals(scanner.peek(), new Token(TokenType.INTLIT, "8"));

        assertEquals(scanner.next(), new Token(TokenType.INTLIT, "8"));

        assertEquals(scanner.peek(), new Token(TokenType.AND, "&"));

        assertEquals(scanner.next(), new Token(TokenType.AND, "&"));

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
    @Test
    public void fileTest() throws Exception
    {
        Scanner scanner = new Scanner(new File("SampleProgram.tg"));

        assertEquals(scanner.next(), new Token(TokenType.LET, "let"));

        assertEquals(scanner.next(), new Token(TokenType.VAR, "var"));

        assertEquals(scanner.next(), new Token(TokenType.ID, "X"));

        assertEquals(scanner.next(), new Token(TokenType.COMMA, ","));

        assertEquals(scanner.next(), new Token(TokenType.ID, "Y"));

        assertEquals(scanner.next(), new Token(TokenType.COLON, ":"));

        assertEquals(scanner.next(), new Token(TokenType.ID, "ArrayInt"));

        assertEquals(scanner.next(), new Token(TokenType.ASSIGN, ":="));

        assertEquals(scanner.next(), new Token(TokenType.INTLIT, "10"));

        assertEquals(scanner.next(), new Token(TokenType.SEMI, ";"));

        assertEquals(scanner.next(), new Token(TokenType.IN, "in"));

        assertEquals(scanner.next(), new Token(TokenType.FOR, "for"));

        assertEquals(scanner.next(), new Token(TokenType.ID, "i"));

        assertEquals(scanner.next(), new Token(TokenType.ASSIGN, ":="));

        assertEquals(scanner.next(), new Token(TokenType.INTLIT, "1"));

        assertEquals(scanner.next(), new Token(TokenType.TO, "to"));

        assertEquals(scanner.next(), new Token(TokenType.INTLIT, "100"));

        assertEquals(scanner.next(), new Token(TokenType.DO, "do"));

        assertEquals(scanner.next(), new Token(TokenType.COMMENT, "/*comment*/"));

        assertEquals(scanner.next(), new Token(TokenType.ID, "sum"));

        assertEquals(scanner.next(), new Token(TokenType.ASSIGN, ":="));

        assertEquals(scanner.next(), new Token(TokenType.ID, "sum"));

        assertEquals(scanner.next(), new Token(TokenType.PLUS, "+"));

        assertEquals(scanner.next(), new Token(TokenType.ID, "X"));

        assertEquals(scanner.next(), new Token(TokenType.LBRACK, "["));

        assertEquals(scanner.next(), new Token(TokenType.ID, "i"));

        assertEquals(scanner.next(), new Token(TokenType.RBRACK, "]"));

        assertEquals(scanner.next(), new Token(TokenType.MULT, "*"));

        assertEquals(scanner.next(), new Token(TokenType.ID, "Y"));

        assertEquals(scanner.next(), new Token(TokenType.LBRACK, "["));

        assertEquals(scanner.next(), new Token(TokenType.ID, "i"));

        assertEquals(scanner.next(), new Token(TokenType.RBRACK, "]"));

        assertEquals(scanner.next(), new Token(TokenType.SEMI, ";"));

        assertEquals(scanner.next(), new Token(TokenType.ENDDO, "enddo"));

        assertEquals(scanner.next(), new Token(TokenType.SEMI, ";"));

        assertEquals(scanner.next(), new Token(TokenType.ID, "printi"));

        assertEquals(scanner.next(), new Token(TokenType.LPAREN, "("));

        assertEquals(scanner.next(), new Token(TokenType.ID, "sum"));

        assertEquals(scanner.next(), new Token(TokenType.RPAREN, ")"));

        assertEquals(scanner.next(), new Token(TokenType.SEMI, ";"));

    }
    @Test
    public void eqTest()
    {
        Scanner scanner = new Scanner("<>");
        assertEquals(scanner.next(), new Token(TokenType.EQ, "<>"));
    }

}
