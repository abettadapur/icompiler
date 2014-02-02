package edu.gatech.testing;

import edu.gatech.facade.IScanner;
import edu.gatech.icompiler.*;
import org.junit.Test;

/**
 * Created by Stefano on 1/31/14.
 */
public class ScannerTest {

    //TODO: implement testing battery

    @Test
    public void testHasNext() throws Exception {

    }

    @Test
    public void testNext() throws Exception {
        IScanner scanner = new Scanner("var N := 8");

        assert(scanner.next().equals(new Token(TokenType.VAR, "var")));

        assert(scanner.next().equals( new Token(TokenType.ID, "N" )));

        assert(scanner.next().equals( new Token(TokenType.ASSIGN, ":=" )));

        assert(scanner.next().equals(new Token(TokenType.INTLIT, "8")));

        scanner = new Scanner ("type intArray := array of int\n");

        assert(scanner.next().equals(new Token(TokenType.TYPE, "type")));

        assert(scanner.next().equals(new Token(TokenType.ID, "intArray")));

        assert(scanner.next().equals(new Token(TokenType.ASSIGN, ":=")));

        assert(scanner.next().equals(new Token(TokenType.ARRAY, "array")));

        assert(scanner.next().equals(new Token(TokenType.OF, "of")));

        assert(scanner.next().equals(new Token(TokenType.TYPE, "int")));

        scanner = new Scanner("var row := intArray [ N ] of 0");

        assert(scanner.next().equals(new Token(TokenType.VAR, "var")));

        assert(scanner.next().equals(new Token(TokenType.ID, "row")));
        assert(scanner.next().equals(new Token(TokenType.ASSIGN, ":=")));
        assert(scanner.next().equals(new Token(TokenType.ID, "intArray")));
        assert(scanner.next().equals(new Token(TokenType.LBRACK, "[")));
        assert(scanner.next().equals(new Token(TokenType.ID, "N")));
        assert(scanner.next().equals(new Token(TokenType.RBRACK, "]")));
        assert(scanner.next().equals(new Token(TokenType.OF, "of")));
        assert(scanner.next().equals(new Token(TokenType.INTLIT, "0")));


    }

    @Test
    public void testRemove() throws Exception {

    }

    @Test
    public void testClose() throws Exception {

    }
}
