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
                    instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.ADD, intermediate.getZ(), intermediate.getX(), intermediate.getY()));
                    break;

                case SUB:
                    instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.SUB, intermediate.getZ(), intermediate.getX(), intermediate.getY()));
                    break;

                case MULT:
                    instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.MULT, intermediate.getZ(), intermediate.getX(), intermediate.getY()));
                    break;

                case DIV:
                    instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.DIV, intermediate.getZ(), intermediate.getX(), intermediate.getY()));
                    break;

                case AND:
                    instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.AND, intermediate.getZ(), intermediate.getX(), intermediate.getY()));
                    break;

                case OR:
                    instructionStream.add(new MipsOperation(intermediate.getLabel(),  MipsOperator.OR, intermediate.getZ(), intermediate.getX(), intermediate.getY()));
                    break;

                case LOAD:
                    instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.LA, intermediate.getX(), intermediate.getY(), ""));
                    instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.LW, intermediate.getX(), "0", intermediate.getX()));
                    break;

                case STORE:
                    instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.LA, intermediate.getX(), intermediate.getY(), ""));
                    instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.SW, intermediate.getX(), "0", intermediate.getX()));
                    break;

                case BREQ:
                    instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.BEQ, intermediate.getX(), intermediate.getY(), intermediate.getZ()));
                    break;

                case BRNEQ:
                    instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.BNE, intermediate.getX(), intermediate.getY(), intermediate.getZ()));
                    break;

                case BRGEQ:
                    instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.BGE, intermediate.getX(), intermediate.getY(), intermediate.getZ()));
                    break;

                case BRLEQ:
                    instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.BLE, intermediate.getX(), intermediate.getY(), intermediate.getZ()));
                    break;


                case BRGT:
                    instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.BGT, intermediate.getX(), intermediate.getY(), intermediate.getZ()));
                    break;

                case BRLT:
                    instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.BLT, intermediate.getX(), intermediate.getY(), intermediate.getZ()));
                    break;

                case ASSIGN:
                    instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.ADDI, intermediate.getX(), intermediate.getY(), "0"));

                default:
                    break;


            }
        }

        return instructionStream;
    }
}
