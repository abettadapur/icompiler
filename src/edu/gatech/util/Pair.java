package edu.gatech.util;

/**
 * Created by Alex on 4/18/14.
 */
public class Pair<T,U>
{
    private T T;
    private U U;

    public Pair(T data1, U data2)
    {
        this.T = data1;
        this.U = data2;
    }

    public T getT() {
        return T;
    }

    public U getU() {
        return U;
    }

    public void setT(T t) {
        T = t;
    }

    public void setU(U u) {
        U = u;
    }
    public String toString()
    {
        return T+", "+U;
    }
}
