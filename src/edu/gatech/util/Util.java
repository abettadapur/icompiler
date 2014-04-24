package edu.gatech.util;

import edu.gatech.generation.MipsOperation;
import edu.gatech.generation.MipsOperator;

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

    public static boolean isInstruction(String id)
    {
        for(MipsOperator op:MipsOperator.values())
            if(op.name().toLowerCase().equals(id))
                return true;
        return false;

    }
}
