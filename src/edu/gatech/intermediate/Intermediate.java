package edu.gatech.intermediate;

import edu.gatech.facade.ITable;
import edu.gatech.icompiler.*;
import edu.gatech.util.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created by Stefano on 3/10/14.
 */
public class Intermediate {

    private List<IntermediateOperation> intermediates;
    private ITable table;

    private int labelCount =1;
    private int tempCount =1;

    public Intermediate(Node<Type> root, ITable table){
        this.table = table;

        intermediates = new ArrayList<>();


        intermediates.addAll(getStatements(root));

    }

    private List<IntermediateOperation> getStatements(Node<Type> root){


        List<IntermediateOperation> out = new ArrayList<>();

        List<Node<Type>> open = new ArrayList<>();

        open.add(root);

        //DFS
        while(!open.isEmpty()){

            Node<Type> temp = open.remove(0);

            if(temp.hasChildOfType(RuleType.OPTIONAL_INIT)){
                List<Node<Type>> foo = temp.getChildren();

                Node<Type> identifierList = null;
                Node<Type> initialization = null;

                for(Node<Type > bar : foo)
                    if(bar.getData().equals(RuleType.ID_LIST))
                        identifierList = bar;
                    else if(bar.getData().equals(RuleType.OPTIONAL_INIT))
                        initialization = bar;

                out.addAll(0,generateInitializationList(identifierList, initialization));

            }
            else if(temp.hasChildOfType(TokenType.FOR)){

                out.addAll(generateLoop(temp));

            }
            else if(temp.hasChildOfType(RuleType.STAT_ASSIGN)){

                Node<Type> identifierNode = temp.getFirstChildOfType(TokenType.ID);

                Binding identifier = table.findByNameScope(  ((Terminal)identifierNode.getNextChild().getData()).getContent() ,identifierNode.getScope());

                if(!identifier.isFunction()){

                    Node<Type> foo= temp.getFirstChildOfType(RuleType.STAT_ASSIGN);


                    out.addAll(generateExpression(identifier.getName(), foo.getFirstChildOfType(RuleType.EXPR_OR_ID)));
                }else{
                    //TODO: finish
                }
            }
            else{
                open.addAll(temp.getChildren());
            }

        }

        return out;
    }

    private List<IntermediateOperation> generateInitializationList(Node<Type> identifierList, Node<Type> initialization){

        String value = null;

        List<Node<Type>> identifiers = new ArrayList<>();

        {
            List<Node<Type>> open = new ArrayList<>();
            open.addAll(identifierList.getChildren());

            //dump all identifiers in ID_LIST to an actual list
            while(!open.isEmpty()){
                Node<Type> foo = open.remove(0);

               if(foo.getData().isTerminal() && ! foo.getData().equals(","))
                   identifiers.add(foo);
               else
                   open.addAll(foo.getChildren());
            }
        }

        Node<Type> assignment = null;
        for (Node<Type> foo : initialization.getChildren())
            if(foo.getData().equals(RuleType.CONST ))
                assignment = foo;

        {
            List<Node<Type>> open = new ArrayList<>();

            open.add(assignment);

            while(!open.isEmpty()){

                Node<Type> foo = open.remove(0);

                if(foo.getData().isTerminal())
                    value = ((Terminal) foo.getData()).getContent();

                open.addAll(foo.getChildren());
            }
        }


        List<IntermediateOperation> out = new ArrayList<>();


        for(Node<Type> foo : identifiers){

            Terminal bar = (Terminal) foo.getData();

            Binding baz = table.findByNameScope(bar.getContent(), foo.getScope());


            if(baz != null){
                DeclaredType bam = baz.getType();

                if(bam.isArray()){

                    int arraySize = 1;

                    for(int i=0; i < bam.getDimensionCount(); i++)
                        arraySize *= bam.getDimensionX(i);

                    IntermediateOperation qux =new IntermediateOperation(Operator.ASSIGN, baz.getName(), arraySize+"", value, "", null);
                    out.add(qux);

                }else{
                   out.add(new IntermediateOperation(Operator.ASSIGN, baz.getName(),value, "", "", null  ));

                }
            }
        }

        return out;
    }

    private String generateLabel(){
        return "label"+labelCount++;
    }

    private Binding generateTemp(DeclaredType type, String scope){
        return new Binding( "t"+tempCount++, type, scope);

    }

    private List<IntermediateOperation> generateLoop(Node<Type> loop){

        String label = generateLabel();
        String skipLabel = generateLabel();

        List<Node<Type>> children = new ArrayList<>();

        children.addAll(loop.getChildren());

        Node<Type> probe = children.remove(0);

        while(!probe.getData().equals(TokenType.ID)) probe = children.remove(0);

        Node<Type> loopVarNode = probe;

        String loopVar = ((Terminal)loopVarNode.getNextChild().getData()).getContent();

        while(!probe.getData().equals(RuleType.EXPR)) probe = children.remove(0);

        Node<Type> startExpr = probe;


        probe = children.remove(0);

        while(!probe.getData().equals(RuleType.EXPR)) probe = children.remove(0);

        Node<Type> endExpr = probe;

        while(!probe.getData().equals(RuleType.STAT_SEQ)) probe = children.remove(0);

        Node<Type> statements = probe;

        List<IntermediateOperation> out = new ArrayList<>();

        IntermediateOperation loopStart = new IntermediateOperation(Operator.UNSUPPORTED, "", "", "", label, null);

        List<IntermediateOperation> preExpression = generateExpression(loopVar, startExpr);

        Binding upperBound = generateTemp(DeclaredType.integer, endExpr.getScope());

        List<IntermediateOperation> endExpression = generateExpression(upperBound.getName() ,endExpr);

        List<IntermediateOperation> body = getStatements(statements);

        out.addAll(preExpression);
        out.add(loopStart);
        out.addAll(endExpression);

        out.add(new IntermediateOperation(Operator.BRGEQ, loopVar, upperBound.getName(), skipLabel, "", null));

        out.addAll(body);

        out.add(new IntermediateOperation(Operator.ADD, loopVar ,loopVar, "1", "", null  ));
        out.add(new IntermediateOperation(Operator.GOTO, label, "", "", "", null ));
        out.add(new IntermediateOperation(Operator.UNSUPPORTED, "", "", "", skipLabel, null));

        return out;
    }

    //do the dumb thing first
    private List<IntermediateOperation> generateExpression(String initialVariable, Node<Type> expression){

        //TODO: change initial to binding

        List<IntermediateOperation> out = new ArrayList<>();

        List<Node<Type>> open = new ArrayList<>();

        List<String> terminals = new ArrayList<>();

        open.add(expression);

        while(! open.isEmpty()){
            Node<Type> temp = open.remove(0);

            if(temp.hasChildOfType(TokenType.ID) ){
                Node<Type> bam = temp.getFirstChildOfType(TokenType.ID);

                Terminal foo = (Terminal)(bam.getChildren().get(0).getData());

                Binding bar = table.findByNameScope(foo.getContent(), temp.getScope());

                if(bar.getType().isArray()){

                    //TODO: handle multidimensional arrays

                    Binding index = generateTemp(DeclaredType.integer, bar.getScope());

                    Node<Type> indexingExpression =  temp.getFirstChildOfType(RuleType.LVALUE_TAIL).getFirstChildOfType(RuleType.EXPR);

                    out.addAll(generateExpression( index.getName() , indexingExpression ));

                    Binding contents = generateTemp( bar.getType().getContainer() ,bam.getScope());

                    out.add(new IntermediateOperation(Operator.ARRAY_LOAD, contents.getName(), bar.getName(), index.getName(), "", null));

                    terminals.add(contents.getName());

                    //TODO: verify
                }

            }
            else if(temp.getData().isTerminal())
                terminals.add(((Terminal) temp.getData()).getContent());

            open.addAll(0, temp.getChildren());

        }

        List<Binding> temporaries = new ArrayList<>();

        Stack<String> stack= new Stack<>();

         for(int i = terminals.size()-1; i>=0; i--)
            stack.push(terminals.get(i));

        //TODO: fix precedence

        //do not add temporaries if this is just an assignment
        while(stack.size() > 1){

            String foo = stack.pop();

            Operator op = Operator.getFromString(stack.pop());

            String bar = stack.pop();

            String tempName = generateTemp(DeclaredType.integer, expression.getScope()).getName();

            IntermediateOperation temp = new IntermediateOperation(op, tempName, foo, bar, "", null );
            out.add(temp);

            stack.push(tempName);
        }

        IntermediateOperation assignment = new IntermediateOperation(Operator.ASSIGN, initialVariable, stack.pop(), "", "", null);

        out.add(assignment);

        return out;
    }


    public String toString(){

        String out = "";
        for(IntermediateOperation foo : intermediates)
            out += foo.toString() + "\n";
        return out;
    }
}
