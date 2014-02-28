package edu.gatech.icompiler;

import edu.gatech.util.Node;

import java.util.*;

/**
 * Created by Stefano on 2/27/14.
 */
public class SymbolTable {

    Map backingTable = new LinkedHashMap();

    //every time we declare a type add it to this set for maintenance
    Set<DeclaredType> declaredTypes = new HashSet<DeclaredType>();


    public SymbolTable(Node<Type> root){



    }
    /*
      Entries
        variable names
        defined constants
        defined types
        procedure and function names
        literal constants and strings
        source text labels
        compiler­generated temporaries

     */

    /*
    Details per entry:
        textual name
        data type
        dimension information (for aggregates)
        declaring procedure
        lexical level of declaration
        storage class (base address)
        offset in storage
        if record, pointer to structure table
        if parameter, by­reference or by­value?
        can it be aliased? to what other names?
        number and type of arguments to functions
     */
    private static class Entry{

        private String name;
        private DeclaredType type;
        private int dimensions;
        private DeclaredType contentType;//arrays only
        //how to do declaring procedure? is it even necessary?
        private boolean referenceP;
        private List<DeclaredType> parameters;

        //For single variables
        public static Entry getVariableInstance(String name, DeclaredType type){
            return new Entry(name, type, 0, null, false, null );
        }

        //For arrays
        public static Entry getArrayInstance(String name, DeclaredType type, int dimensions, DeclaredType contentType){
            return new Entry(name, type, dimensions, contentType, true, null );
        }

        //For functions
        public static Entry getFunctionInstance(String name, DeclaredType type, List<DeclaredType> parameters){
            return new Entry(name, type, 0, null, false, parameters );
        }

        private Entry(String name, DeclaredType type,
                     int dimensions, DeclaredType contentType, boolean referenceP, List<DeclaredType> parameters){
            this.name = name;
            this.type = type;
            this.dimensions = dimensions;
            this.referenceP = referenceP;
            this.parameters = parameters;
        }


        public String toString(){
            return "["+name + " " +type+"]";
        }
    }

    //I don't know that there's any point to this but it lets
    // me not use strings for everything so yeah
    private class DeclaredType{
        String typeName;

        public DeclaredType(String typeName){
            this.typeName = typeName;
        }

        public int hashCode(){
            return typeName.hashCode();
        }

        public boolean equals(Object other ){
            if(null == other) return false;
            if(this == other) return true;
            if(!(other instanceof DeclaredType)) return false;
            DeclaredType temp = (DeclaredType) other;
            return temp.typeName.equals(typeName);
        }
    }
}
