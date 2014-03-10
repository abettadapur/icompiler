package edu.gatech.intermediate;

import edu.gatech.icompiler.Type;
import edu.gatech.util.Node;

import java.util.List;

/**
 * Created by Stefano on 3/10/14.
 */
public class Intermediate {

    private List<IntermediateOperation> intermediates;

    private static Intermediate entireRepresentation;

    private Intermediate(Node<Type> root){



        //TODO: build representation

    }

    private static Intermediate getRepresentation(Node root){

        if(null == entireRepresentation)
            entireRepresentation = new Intermediate((root));

        return entireRepresentation;
    }

}
