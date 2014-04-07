package edu.gatech.util;

import java.util.List;

/**
 * Created by Alex on 2/2/14.
 */
public class Util {
    public static String stringFromList(List<Character> list)
    {
        StringBuilder sb = new StringBuilder();
        for(Character c: list)
            sb.append(c);

        return sb.toString();
    }

    public static boolean isNumeric(String str)
    {
        return str.matches("-?\\d+(\\.\\d+)?");
    }
}
