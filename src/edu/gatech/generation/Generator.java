package edu.gatech.generation;

import edu.gatech.intermediate.Intermediate;
import edu.gatech.intermediate.IntermediateOperation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 4/6/14.
 */
public class Generator
{
    public static List<MipsOperation> generateCode(List<IntermediateOperation> irStream)
    {
        List<MipsOperation> instructionStream = new ArrayList<>();
        for(IntermediateOperation intermediate: irStream)
        {
            switch(intermediate.getOp())
            {
                case ADD:
                    instructionStream.add(new MipsOperation("", MipsOperator.ADD, intermediate.getZ(), intermediate.getX(), intermediate.getY()));
                    break;

                case SUB:
                    instructionStream.add(new MipsOperation("", MipsOperator.SUB, intermediate.getZ(), intermediate.getX(), intermediate.getY()));
                    break;

                case MULT:
                    instructionStream.add(new MipsOperation("", MipsOperator.MULT, intermediate.getZ(), intermediate.getX(), intermediate.getY()));
                    break;

                case DIV:
                    instructionStream.add(new MipsOperation("", MipsOperator.DIV, intermediate.getZ(), intermediate.getX(), intermediate.getY()));
                    break;

                case AND:
                    instructionStream.add(new MipsOperation("", MipsOperator.AND, intermediate.getZ(), intermediate.getX(), intermediate.getY()));
                    break;

                case OR:
                    instructionStream.add(new MipsOperation("", MipsOperator.OR, intermediate.getZ(), intermediate.getX(), intermediate.getY()));
                    break;

                case LOAD:
                    instructionStream.add(new MipsOperation("", MipsOperator.LA, intermediate.getX(), intermediate.getY(), ""));
                    instructionStream.add(new MipsOperation("", MipsOperator.LW, intermediate.getX(), "0", intermediate.getX()));
                    break;

                case STORE:
                    instructionStream.add(new MipsOperation("", MipsOperator.LA, intermediate.getX(), intermediate.getY(), ""));
                    instructionStream.add(new MipsOperation("", MipsOperator.SW, intermediate.getX(), "0", intermediate.getX()));
                    break;

                case BREQ:
                    instructionStream.add(new MipsOperation("", MipsOperator.BEQ, intermediate.getX(), intermediate.getY(), intermediate.getZ()));
                    break;

                case BRNEQ:
                    instructionStream.add(new MipsOperation("", MipsOperator.BNE, intermediate.getX(), intermediate.getY(), intermediate.getZ()));
                    break;

                case BRGEQ:
                    instructionStream.add(new MipsOperation("", MipsOperator.BGE, intermediate.getX(), intermediate.getY(), intermediate.getZ()));
                    break;

                case BRLEQ:
                    instructionStream.add(new MipsOperation("", MipsOperator.BLE, intermediate.getX(), intermediate.getY(), intermediate.getZ()));
                    break;


                case BRGT:
                    instructionStream.add(new MipsOperation("", MipsOperator.BGT, intermediate.getX(), intermediate.getY(), intermediate.getZ()));
                    break;

                case BRLT:
                    instructionStream.add(new MipsOperation("", MipsOperator.BLT, intermediate.getX(), intermediate.getY(), intermediate.getZ()));
                    break;

                case ASSIGN:
                    instructionStream.add(new MipsOperation("", MipsOperator.ADDI, intermediate.getX(), intermediate.getY(), "0"));

                default:
                    break;


            }
        }

        return instructionStream;
    }
}
