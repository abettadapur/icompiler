package edu.gatech.generation;

import com.sun.java.swing.plaf.motif.MotifPopupMenuSeparatorUI;
import edu.gatech.facade.ITable;
import edu.gatech.icompiler.Binding;
import edu.gatech.icompiler.DeclaredType;
import edu.gatech.intermediate.IntermediateOperation;
import edu.gatech.util.Util;

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
        generateDataSegment(instructionStream, irStream, table);
        instructionStream.add(new MipsOperation("", MipsOperator.TEXT,"","",""));
        instructionStream.add(new MipsOperation("", MipsOperator.GLOBL, "main","",""));
        int i = 0;
        for(IntermediateOperation op: irStream)
        {
            if(op.getLabel().equals("main"))
                break;
            i++;

        }
        for(i=i;i<irStream.size(); i++)
        {
            IntermediateOperation intermediate = irStream.get(i);
            switch(intermediate.getOp())
            {
                case ADD:

                    if(!Util.isNumeric(intermediate.getY()) && !Util.isNumeric(intermediate.getZ()))
                        instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.ADD, intermediate.getX(), intermediate.getY(), intermediate.getZ()));
                    else{
                        if(Util.isNumeric(intermediate.getY()))
                            instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.ADDI, intermediate.getX(), intermediate.getZ(), intermediate.getY()));
                        else
                            instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.ADDI, intermediate.getX(), intermediate.getY(), intermediate.getZ()));
                    }
                    break;

                case SUB:
                    if(!Util.isNumeric(intermediate.getY()) && !Util.isNumeric(intermediate.getZ()))
                        instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.SUB, intermediate.getX(), intermediate.getY(), intermediate.getZ()));
                    else
                    {
                        if(Util.isNumeric(intermediate.getY()))
                        {
                            instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.ADDI, "$16", "$0", intermediate.getY()));
                            instructionStream.add(new MipsOperation("", MipsOperator.SUB, intermediate.getX(), "$16", intermediate.getZ()));
                        }
                        else
                        {
                            instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.ADDI, "$16", "$0", intermediate.getZ()));
                            instructionStream.add(new MipsOperation("", MipsOperator.SUB, intermediate.getX(), intermediate.getY(), "$16"));
                        }
                    }
                    break;

                case MULT:
                    if(!Util.isNumeric(intermediate.getY()) && !Util.isNumeric(intermediate.getZ()))
                        instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.MUL, intermediate.getX(), intermediate.getY(), intermediate.getZ()));
                    else
                    {
                        if(Util.isNumeric(intermediate.getY()))
                        {
                            instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.ADDI, "$16", "$0", intermediate.getY()));
                            instructionStream.add(new MipsOperation("", MipsOperator.MUL, intermediate.getX(), "$16", intermediate.getZ()));
                        }
                        else
                        {
                            instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.ADDI, "$16", "$0", intermediate.getZ()));
                            instructionStream.add(new MipsOperation("", MipsOperator.MUL, intermediate.getX(), intermediate.getY(), "$16"));
                        }
                    }
                    break;

                case DIV:
                    if(!Util.isNumeric(intermediate.getY()) && !Util.isNumeric(intermediate.getZ()))
                        instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.DIV, intermediate.getX(), intermediate.getY(), intermediate.getZ()));
                    else
                    {
                        if(Util.isNumeric(intermediate.getY()))
                        {
                            instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.ADDI, "$16", "$0", intermediate.getY()));
                            instructionStream.add(new MipsOperation("", MipsOperator.DIV, intermediate.getX(), "$16", intermediate.getZ()));
                        }
                        else
                        {
                            instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.ADDI, "$16", "$0", intermediate.getZ()));
                            instructionStream.add(new MipsOperation("", MipsOperator.DIV, intermediate.getX(), intermediate.getY(), "$16"));
                        }
                    }
                    break;

                case AND:
                    instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.AND, intermediate.getX(), intermediate.getY(), intermediate.getZ()));
                    break;

                case OR:
                    instructionStream.add(new MipsOperation(intermediate.getLabel(),  MipsOperator.OR, intermediate.getX(), intermediate.getY(), intermediate.getZ()));
                    break;

                case LOAD:
                    instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.LA, "$27", intermediate.getY(), ""));
                    instructionStream.add(new MipsOperation("", MipsOperator.LW, intermediate.getX(), "0", "$27"));
                    break;

                case STORE:
                    instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.LA, "$27", intermediate.getY(), ""));
                    instructionStream.add(new MipsOperation("", MipsOperator.SW, intermediate.getX(), "0", "$27"));
                    break;

                case BREQ:
                    if(!Util.isNumeric(intermediate.getX())&&!Util.isNumeric(intermediate.getY()))
                        instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.BEQ, intermediate.getX(), intermediate.getY(), intermediate.getZ()));
                    else
                    {
                        if(Util.isNumeric(intermediate.getX()))
                        {
                            instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.ADDI, "$16", "$0", intermediate.getX()));
                            instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.BEQ, "$16", intermediate.getY(), intermediate.getZ()));
                        }
                        else
                        {
                            instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.ADDI, "$16", "$0", intermediate.getY()));
                            instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.BEQ, intermediate.getX(), "$16", intermediate.getZ()));
                        }
                    }
                    break;

                case BRNEQ:
                    if(!Util.isNumeric(intermediate.getX())&&!Util.isNumeric(intermediate.getY()))
                        instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.BNE, intermediate.getX(), intermediate.getY(), intermediate.getZ()));
                    else
                    {
                        if(Util.isNumeric(intermediate.getX()))
                        {
                            instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.ADDI, "$16", "$0", intermediate.getX()));
                            instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.BNE, "$16", intermediate.getY(), intermediate.getZ()));
                        }
                        else
                        {
                            instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.ADDI, "$16", "$0", intermediate.getY()));
                            instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.BNE, intermediate.getX(), "$16", intermediate.getZ()));
                        }
                    }
                    break;

                case BRGEQ:
                    if(!Util.isNumeric(intermediate.getX())&&!Util.isNumeric(intermediate.getY()))
                        instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.BGE, intermediate.getX(), intermediate.getY(), intermediate.getZ()));
                    else
                    {
                        if(Util.isNumeric(intermediate.getX()))
                        {
                            instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.ADDI, "$16", "$0", intermediate.getX()));
                            instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.BGE, "$16", intermediate.getY(), intermediate.getZ()));
                        }
                        else
                        {
                            instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.ADDI, "$16", "$0", intermediate.getY()));
                            instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.BGE, intermediate.getX(), "$16", intermediate.getZ()));
                        }
                    }
                    break;

                case BRLEQ:
                    if(!Util.isNumeric(intermediate.getX())&&!Util.isNumeric(intermediate.getY()))
                        instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.BLE, intermediate.getX(), intermediate.getY(), intermediate.getZ()));
                    else
                    {
                        if(Util.isNumeric(intermediate.getX()))
                        {
                            instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.ADDI, "$16", "$0", intermediate.getX()));
                            instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.BLE, "$16", intermediate.getY(), intermediate.getZ()));
                        }
                        else
                        {
                            instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.ADDI, "$16", "$0", intermediate.getY()));
                            instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.BLE, intermediate.getX(), "$16", intermediate.getZ()));
                        }
                    }
                    break;


                case BRGT:
                    if(!Util.isNumeric(intermediate.getX())&&!Util.isNumeric(intermediate.getY()))
                        instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.BGT, intermediate.getX(), intermediate.getY(), intermediate.getZ()));
                    else
                    {
                        if(Util.isNumeric(intermediate.getX()))
                        {
                            instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.ADDI, "$16", "$0", intermediate.getX()));
                            instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.BGT, "$16", intermediate.getY(), intermediate.getZ()));
                        }
                        else
                        {
                            instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.ADDI, "$16", "$0", intermediate.getY()));
                            instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.BGT, intermediate.getX(), "$16", intermediate.getZ()));
                        }
                    }
                    break;

                case BRLT:
                    if(!Util.isNumeric(intermediate.getX())&&!Util.isNumeric(intermediate.getY()))
                        instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.BLT, intermediate.getX(), intermediate.getY(), intermediate.getZ()));
                    else
                    {
                        if(Util.isNumeric(intermediate.getX()))
                        {
                            instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.ADDI, "$16", "$0", intermediate.getX()));
                            instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.BLT, "$16", intermediate.getY(), intermediate.getZ()));
                        }
                        else
                        {
                            instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.ADDI, "$16", "$0", intermediate.getY()));
                            instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.BLT, intermediate.getX(), "$16", intermediate.getZ()));
                        }
                    }
                    break;

                case ASSIGN:

                    if(!Util.isNumeric(intermediate.getY()) )
                    {
                      instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.LA, "$27", intermediate.getX(),""));
                      instructionStream.add(new MipsOperation("", MipsOperator.SW, intermediate.getY(), "0", "$27"));
                    }
                    else
                    {

                        instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.LA, "$27", intermediate.getX(),""));
                        instructionStream.add(new MipsOperation("", MipsOperator.ADDI, "$26", "$0", intermediate.getY()));
                        instructionStream.add(new MipsOperation("", MipsOperator.SW, "$26", "0", "$27"));
                    }

                    break;

                case ARRAY_LOAD:
                    instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.LA, "$27", intermediate.getY(),"" ));
                    instructionStream.add(new MipsOperation("", MipsOperator.ADDI, "$23", "$0", "4"));
                    instructionStream.add(new MipsOperation("", MipsOperator.MUL, intermediate.getZ(), "$23", intermediate.getZ()));
                    instructionStream.add(new MipsOperation("", MipsOperator.ADD, "$27", "$27", intermediate.getZ()));
                    instructionStream.add(new MipsOperation("", MipsOperator.LW, intermediate.getX(), "0","$27"));
                    break;

                case ARRAY_STORE:
                    instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.LA, "$27", intermediate.getX(),"" ));
                    instructionStream.add(new MipsOperation("", MipsOperator.ADDI, "$23", "$0", "4"));
                    instructionStream.add(new MipsOperation("", MipsOperator.MUL, intermediate.getY(), "$23", intermediate.getY()));
                    instructionStream.add(new MipsOperation("", MipsOperator.ADD, "$27", "$27", intermediate.getY()));
                    instructionStream.add(new MipsOperation("", MipsOperator.SW, intermediate.getZ(), "0","$27"));

                case CALL:
                    if(intermediate.getX().equals("printi"))
                    {
                        instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.LI, "$v0", "1",""));
                        instructionStream.add(new MipsOperation("", MipsOperator.MOVE, "$a0", intermediate.getParameters().get(0),""));
                        instructionStream.add(new MipsOperation("", MipsOperator.SYSCALL, "", "",""));


                    }
                    break;
                case GOTO:
                    instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.J, intermediate.getX(),"",""));
                    break;
                case END:
                    instructionStream.add(new MipsOperation(intermediate.getLabel(), MipsOperator.JR, "$ra", "",""));
                    break;
                default:
                    break;


            }
        }



        return instructionStream;
    }
    public static void generateDataSegment(List<MipsOperation> instructionStream, List<IntermediateOperation> irStream,  ITable table)
    {
        List<IntermediateOperation> assignments = new ArrayList<>();
        for(IntermediateOperation op: irStream)
        {
            if(op.getLabel().equals("main"))
                break;
            assignments.add(op);
        }
        List<Binding> vars = table.getVars();
        instructionStream.add(new MipsOperation("", MipsOperator.DATA,"","",""));
        for(Binding b: vars)
        {
            if(table.findPrimitive(b.getName(), b.getScope())== DeclaredType.integer)
            {
                int initialize = 0;
                for(IntermediateOperation op:assignments)
                {
                    if(op.getX().equals(b.getName()))
                    {
                        initialize = Integer.parseInt(op.getY());
                        break;
                    }
                }
                instructionStream.add(new MipsOperation(b.getName(), MipsOperator.WORD, initialize+"","",""));
            }
            if(table.findPrimitive(b.getName(), b.getScope()).isArray())
            {
                int initialize = 0;
                for(IntermediateOperation op:assignments)
                {
                    if(op.getX().equals(b.getName()))
                    {
                        initialize = Integer.parseInt(op.getZ());
                        break;
                    }
                }
                if(b.getType().getDimensionCount()==1)
                {
                    StringBuilder intialization = new StringBuilder();
                    for(int i=0; i<b.getType().getDimensionX(0); i++)
                    {
                        intialization.append(initialize);
                        if(i!=b.getType().getDimensionX(0)-1)
                            intialization.append(",");
                    }

                    instructionStream.add(new MipsOperation(b.getName(), MipsOperator.WORD, intialization.toString(),"",""));
                }
                //TODO: MULTIARRAYS
            }


        }
    }
}
