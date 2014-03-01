package edu.gatech.facade;

import edu.gatech.icompiler.Entry;

import java.util.List;

/**
 * Created by Alex on 3/1/14.
 */
public interface ITable
{
    public List<Entry> find(String name);
    public Entry findByNameScope(String name, String scope);
}

