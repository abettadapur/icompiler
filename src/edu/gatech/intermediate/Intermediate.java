package edu.gatech.intermediate;

import edu.gatech.facade.ITable;
import edu.gatech.icompiler.*;
import edu.gatech.util.Node;

import java.util.*;

/**
 * Created by Stefano on 3/10/14.
 */
public class Intermediate {

    //TODO: while loops
    //TODO: else blocks
    //TODO: multidimensional arrays
    //TODO: function calls

    private List<IntermediateOperation> intermediates;
    private ITable table;

    private Map<String, Integer> operatorPrecedence;

    private int labelCount =1;
    private int tempCount =1;

    public Intermediate(Node<Type> root, ITable table){
        this.table = table;

        intermediates = new ArrayList<>();

        operatorPrecedence = new HashMap<>();

        operatorPrecedence.put("+", 2);
        operatorPrecedence.put("-", 2);
        operatorPrecedence.put("/", 1);
        operatorPrecedence.put("*", 1);

        intermediates.addAll(getStatements(root));

    }

    private List<IntermediateOperation> getStatements(Node<Type> root){


        List<IntermediateOperation> out = new ArrayList<>();

        List<Node<Type>> open = new ArrayList<>();

        open.add(root);

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
            else if(temp.hasChildOfType(TokenType.WHILE)){

                out.addAll(generateWhile(temp));

            }
            else if(temp.getData().equals(RuleType.FUNCT_DECLARATION)){

                out.addAll(generateFunctionDeclaration(temp));

            }
            else if(temp.hasChildOfType(RuleType.STAT_ASSIGN)){

                Node<Type> identifierNode = temp.getFirstChildOfType(TokenType.ID);

                Binding identifier = table.findByNameScope(  ((Terminal)identifierNode.getNextChild().getData()).getContent() ,identifierNode.getScope());

                Node<Type> foo= temp.getFirstChildOfType(RuleType.STAT_ASSIGN);

                if(!identifier.isFunction()){

                    out.addAll(generateExpression(identifier.getName(), foo.getFirstChildOfType(RuleType.EXPR_OR_ID)));

                }else{


                    List<Node<Type>> parameterExpressions = new ArrayList<>();

                    List<Node<Type>> tempOpen = new ArrayList<>();
                    tempOpen.add(foo.getFirstChildOfType(RuleType.EXPR_LIST));


                    //find all parameters
                    while(!tempOpen.isEmpty()){

                        Node<Type> bar = tempOpen.remove(0);

                        List<Node<Type>> baz = bar.getChildren();

                        for(Node<Type> child : baz){
                            if(child.getData().equals(RuleType.EXPR))
                                parameterExpressions.add(child);
                            else
                                tempOpen.add(child);
                        }
                    }


                    List<String> parameters = new ArrayList<>();

                    for(Node<Type> bar : parameterExpressions){

                        Binding baz = generateTemp(DeclaredType.integer, bar.getScope());

                        List<IntermediateOperation> qux = generateExpression(baz.getName(), bar);

                        out.addAll(qux);

                        parameters.add(baz.getName());

                    }

                    IntermediateOperation call = new IntermediateOperation( Operator.CALL, identifier.getName(), "", "", "", parameters);

                    out.add(call);

                }
            }
            else{
                open.addAll(temp.getChildren());
            }

        }

        return out;
    }

    private List<IntermediateOperation> generateFunctionDeclaration(Node<Type> functionDeclaration){

        List<IntermediateOperation> out = new ArrayList<>();



        String functionName = ((Terminal)functionDeclaration.getFirstChildOfType(TokenType.ID).getNextChild().getData()).getContent();

        IntermediateOperation funcStart = new IntermediateOperation(Operator.UNSUPPORTED, "", "", "", functionName, null);

        List<IntermediateOperation> funcBody =  getStatements(functionDeclaration.getFirstChildOfType(RuleType.STAT_SEQ));

        out.add(funcStart);
        out.addAll(funcBody);

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

    private List<IntermediateOperation> generateWhile(Node<Type> expression){

        List<IntermediateOperation> out = new ArrayList<>();

        Binding temp = generateTemp(DeclaredType.integer, expression.getScope());

        String startLabel = generateLabel();

        String skipLabel = generateLabel();

        IntermediateOperation loopStart = new IntermediateOperation(Operator.UNSUPPORTED, "","","", startLabel, null);

        IntermediateOperation check = new IntermediateOperation(Operator.BREQ, temp.getName(), "0", skipLabel, "", null  );

        List<IntermediateOperation> precondition = generateExpression( temp.getName(), expression.getFirstChildOfType(RuleType.EXPR));

        List<IntermediateOperation> loopBody = getStatements(expression.getFirstChildOfType(RuleType.STAT_SEQ));

        IntermediateOperation loopBack = new IntermediateOperation(Operator.GOTO, "startLabel", "", "", "", null);

        IntermediateOperation loopSkip = new IntermediateOperation(Operator.UNSUPPORTED, "", "", "", skipLabel, null);

        out.add(loopStart);

        out.add(check);
        out.addAll(precondition);
        out.addAll(loopBody);
        out.add(loopBack);
        out.add(loopSkip);

        return out;
    }

    //do the dumb thing first
    private List<IntermediateOperation> generateExpression(String initialVariable, Node<Type> expression){

        //TODO: change initial to binding

        List<IntermediateOperation> out = new ArrayList<>();

        List<Node<Type>> open = new ArrayList<>();

        List<Node<Type>> closed = new ArrayList<>();

        List<String> terminals = new ArrayList<>();

        open.add(expression);

        while(! open.isEmpty()){
            Node<Type> temp = open.remove(0);

            if(temp.hasChildOfType(TokenType.ID) ){
                Node<Type> bam = temp.getFirstChildOfType(TokenType.ID);

                Terminal foo = (Terminal)(bam.getChildren().get(0).getData());

                Binding bar = table.findByNameScope(foo.getContent(), bam.getScope());

                if(bar.getType().isArray()){

                    //TODO: handle multidimensional arrays

                    closed.add(bam);

                    Binding index = generateTemp(DeclaredType.integer, bar.getScope());

                    Node<Type> lvalue = temp.getFirstChildOfType(RuleType.LVALUE_TAIL);

                    Node<Type> indexingExpression =  lvalue.getFirstChildOfType(RuleType.EXPR);

                    closed.add(lvalue);

                    out.addAll(generateExpression( index.getName() , indexingExpression ));

                    Binding contents = generateTemp( bar.getType().getContainer() ,bam.getScope());

                    out.add(new IntermediateOperation(Operator.ARRAY_LOAD, contents.getName(), bar.getName(), index.getName(), "", null));

                    terminals.add(contents.getName());

                    //TODO: verify
                }

            }
            else if(temp.getData().isTerminal())
                terminals.add(((Terminal) temp.getData()).getContent());

           List<Node<Type>> tempList = new ArrayList<>();

           for(Node<Type> node : temp.getChildren())
               if(! closed.contains(node))
                   tempList.add(node);

            open.addAll(0,tempList);

        }





        List<String> postFixTerminals = orderOperations( terminals );

        Stack<String> calculationStack = new Stack<>();

        while(!postFixTerminals.isEmpty()){

            String token = postFixTerminals.remove(0);

            if(token.matches("[\\d\\w]*")){
                calculationStack.push(token);
            }else{

                Operator op = Operator.getFromString(token);

                String foo = calculationStack.pop();

                String bar = calculationStack.pop();

                String tempName = generateTemp(DeclaredType.integer, expression.getScope()).getName();

                IntermediateOperation temp = new IntermediateOperation(op, tempName, foo, bar, "", null );
                out.add(temp);

                calculationStack.push(tempName);


            }


        }

        IntermediateOperation assignment = new IntermediateOperation(Operator.ASSIGN, initialVariable, calculationStack.pop(), "", "", null);

        out.add(assignment);

        return out;
    }



    private List<String> orderOperations( List<String> in ){


        //We put our ops in postfix notation (shunting yard algorithm)
        List<String> out = new ArrayList<>();

        Stack<String> operators = new Stack<>();

        while(!in.isEmpty()){

            String token = in.remove(0);

            if(token.matches("[\\d\\w]*"))
                out.add(token);

            if("+*-/".contains(token)){

                while(!operators.isEmpty() && operatorPrecedence.get(operators.peek()) < operatorPrecedence.get(token))
                    out.add(operators.pop());

                operators.push(token);

            }

            if(token.equals("(")){
                //these are just stored for precedence, they never wind up in the queue
                operators.push(token);
            }

            if(token.equals(")")){

                while(!operators.peek() .equals("("))
                    out.add(operators.pop());

                //discard parentheses
                operators.pop();

            }


        }

        while(! operators.isEmpty())
            out.add(operators.pop());


        return out;

    }


    public String toString(){

        String out = "";
        for(IntermediateOperation foo : intermediates)
            out += foo.toString() + "\n";
        return out;
    }
}
