package edu.gatech.generation;

import edu.gatech.facade.ITable;
import edu.gatech.icompiler.Binding;
import edu.gatech.icompiler.DeclaredType;
import edu.gatech.icompiler.SymbolTable;
import edu.gatech.intermediate.Intermediate;
import edu.gatech.intermediate.IntermediateOperation;
import edu.gatech.intermediate.OperationType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 4/6/14.
 */
public class Generator
{
    public static List<MipsOperation> generateCode(List<IntermediateOperation> irStream, ITable table)
    {
        List<MipsOperation> instructionStream = new ArrayList<>();
        generateDataSegment(instructionStream, table);
        instructionStream.add(new MipsOperation("", MipsOperator.TEXT,"","",""));
        for(IntermediateOperation intermediate: irStream)
        {
            switch(intermediate.getOp())
            {
                case ADD:
                    instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.ADD, intermediate.getX(), intermediate.getY(), intermediate.getZ()));
                    break;

                case SUB:
                    instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.SUB, intermediate.getX(), intermediate.getY(), intermediate.getZ()));
                    break;

                case MULT:
                    instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.MUL, intermediate.getX(), intermediate.getY(), intermediate.getZ()));
                    break;

                case DIV:
                    instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.DIV, intermediate.getX(), intermediate.getY(), intermediate.getZ()));
                    break;

                case AND:
                    instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.AND, intermediate.getX(), intermediate.getY(), intermediate.getZ()));
                    break;

                case OR:
                    instructionStream.add(new MipsOperation(intermediate.getLabel(),  MipsOperator.OR, intermediate.getX(), intermediate.getY(), intermediate.getZ()));
                    break;

                case LOAD:
                    instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.LA, intermediate.getX(), intermediate.getY(), ""));
                    instructionStream.add(new MipsOperation("", MipsOperator.LW, intermediate.getX(), "0", intermediate.getX()));
                    break;

                case STORE:
                    instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.LA, intermediate.getX(), intermediate.getY(), ""));
                    instructionStream.add(new MipsOperation("", MipsOperator.SW, intermediate.getX(), "0", intermediate.getX()));
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
                    instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.ADDI, intermediate.getX(), "$0", intermediate.getY()));
                    break;

                case ARRAY_LOAD:
                    instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.LA, "$31", intermediate.getY(),"" ));
                    instructionStream.add(new MipsOperation("", MipsOperator.ADD, "$31", "$31", intermediate.getZ()));
                    instructionStream.add(new MipsOperation("", MipsOperator.LW, intermediate.getX(), "0","$31"));
                    break;

                default:
                    break;


            }
        }

        return instructionStream;
    }
    public static void generateDataSegment(List<MipsOperation> instructionStream, ITable table)
    {
        List<Binding> vars = table.getVars();
        instructionStream.add(new MipsOperation("", MipsOperator.DATA,"","",""));
        for(Binding b: vars)
        {
            if(table.findPrimitive(b.getName(), b.getScope())== DeclaredType.integer)
            {
                instructionStream.add(new MipsOperation(b.getName(), MipsOperator.WORD, "0","",""));
            }
            if(table.findPrimitive(b.getName(), b.getScope()).isArray())
            {
                if(b.getType().getDimensionCount()==1)
                {
                    instructionStream.add(new MipsOperation(b.getName(), MipsOperator.SPACE, b.getType().getDimensionX(0)+"","",""));
                }
                //TODO: MULTIARRAYS
            }


        }
    }
}
