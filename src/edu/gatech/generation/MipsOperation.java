package edu.gatech.generation;

/**
 * Created by Alex on 4/6/14.
 */
public class MipsOperation
{
    private MipsOperator operator;
    private String label;
    private String x;
    private String y;
    private String z;

    public MipsOperation(String label, MipsOperator operator, String x, String y, String z)
    {
        this.label=label;
        this.operator=operator;
        this.x=x;
        this.y=y;
        this.z=z;
    }

    public MipsOperator getOperator() {
        return operator;
    }

    public String getLabel() {
        return label;
    }

    public String getX() {
        return x;
    }

    public String getY() {
        return y;
    }

    public String getZ() {
        return z;
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        if(!label.equals(""))
            sb.append(label+": ");
        sb.append(operator.name().toLowerCase()+" ");
        sb.append(x);
        if(operator==MipsOperator.LW||operator==MipsOperator.SW)
        {
            sb.append(", "+y+"("+z+")");
        }
        else
        {
            sb.append(", "+y);
            if(!z.equals(""))
                sb.append(", "+z);
        }
        return sb.toString();



    }
}
