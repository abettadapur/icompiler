package edu.gatech.testing;

import edu.gatech.facade.IScanner;
import edu.gatech.icompiler.Scanner;
import org.junit.Before;
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

        //TODO: figure out failure conditions

        scanner = new Scanner ("type intArray = array of int\n");



        scanner = new Scanner("var row := intArray [ N ] of 0");


    }

    @Test
    public void testRemove() throws Exception {

    }

    @Test
    public void testClose() throws Exception {

    }
}
