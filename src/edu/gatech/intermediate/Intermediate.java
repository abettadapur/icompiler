package edu.gatech.intermediate;

import edu.gatech.facade.ITable;
import edu.gatech.icompiler.*;
import edu.gatech.util.Node;

import java.util.*;

/**
 * Created by Stefano on 3/10/14.
 */
public class Intermediate {

    //TODO: add temporaries to table
    //TODO: while loops
    //TODO: else blocks
    //TODO: multidimensional arrays
    //TODO: function calls with returns

    private List<IntermediateOperation> intermediates;
    private ITable table;
    private Node<Type> root;
    private Map<String, Integer> operatorPrecedence;
    private boolean debug = false;
    private int labelCount =1;
    private int tempCount =1;

    public Intermediate(Node<Type> root, ITable table){
        this(false, root, table);
    }

    public Intermediate(boolean debug, Node<Type> root, ITable table){
        this.table = table;

        intermediates = new ArrayList<>();
        this.debug = debug;
        this.root = root;
        operatorPrecedence = new HashMap<>();
        operatorPrecedence.put("|", 5);
        operatorPrecedence.put("&", 5);
        operatorPrecedence.put("<=", 4);
        operatorPrecedence.put(">=", 4);
        operatorPrecedence.put("<", 4);
        operatorPrecedence.put(">", 4);
        operatorPrecedence.put("<>", 3);
        operatorPrecedence.put("=",3);
        operatorPrecedence.put("+", 2);
        operatorPrecedence.put("-", 2);
        operatorPrecedence.put("/", 1);
        operatorPrecedence.put("*", 1);


        generateIntermediates();


        removeRedundancy();


    }

    public List<IntermediateOperation> getIntermediates(){
        return intermediates;
    }

    private List<IntermediateOperation> generateIntermediates()
    {
        intermediates.addAll(getStatements(root));
        intermediates.add(new IntermediateOperation(Operator.END, "","","","",null));
        coalesceLabels(intermediates);
        for(IntermediateOperation intermediate: intermediates)
            intermediate.populateDefUse();
        if(debug)
            System.out.println(this);
        return intermediates;
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
            else if(temp.getData().equals(TokenType.IN)){

                out.add(new IntermediateOperation(Operator.UNSUPPORTED, "", "", "", "main", null));

            }
            else if(temp.hasChildOfType(TokenType.FOR)){

                out.addAll(generateLoop(temp));

            }
            else if(temp.hasChildOfType(TokenType.WHILE)){

                out.addAll(generateWhile(temp));

            }
            else if(temp.getData().equals(RuleType.FUNCT_DECLARATION)){

                out.addAll(0,generateFunctionDeclaration(temp));

            }
            else if(temp.hasChildOfType(TokenType.IF)){

                out.addAll(generateIf(temp));

            }
            else if(temp.hasChildOfType(TokenType.RETURN)){

                Binding foo = generateTemp(DeclaredType.integer, "");
                out.addAll(generateExpression( foo.getName(),temp.getFirstChildOfType(RuleType.EXPR) ));

                out.add(new IntermediateOperation(Operator.RETURN, foo.getName(), "", "", "", null));
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
                //System.out.println(temp);
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


    private List<IntermediateOperation> generateIf(Node<Type> ifParent){

        List<IntermediateOperation> out = new ArrayList<>();

        Node<Type> switchingExpression = ifParent.getFirstChildOfType(RuleType.EXPR);

        Binding entryPoint = generateTemp(DeclaredType.integer, switchingExpression.getScope());

        String ifLabel = generateLabel();

        String end = generateLabel();

        out.addAll(generateExpression(entryPoint.getName(), switchingExpression) );

        out.add(new IntermediateOperation(Operator.BRNEQ, entryPoint.getName(), "0", ifLabel, "", null ));

        Node<Type> elseBlock = ifParent.getFirstChildOfType(RuleType.STAT_ELSE);

        out.addAll(getStatements(elseBlock));

        out.add(new IntermediateOperation(Operator.GOTO, end, "", "" ,"", null));

        out.add(new IntermediateOperation(Operator.UNSUPPORTED, "", "", "" ,ifLabel, null));

        Node<Type> ifBlock = ifParent.getFirstChildOfType(RuleType.STAT_SEQ);

        out.addAll(getStatements(ifBlock));

        out.add(new IntermediateOperation(Operator.UNSUPPORTED, "", "", "" ,end, null));

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

               if(foo.getData().isTerminal() )
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

        //out.add(new IntermediateOperation(Operator.UNSUPPORTED, "","","","data", null));
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
        if(scope==null)
            scope="";



        String tempName = "t"+tempCount;

        while( table.find(tempName) != null ){
            tempName  = "t" + ++tempCount;
        }

        table.addVariable(tempName, type, scope);
        return table.findByNameScope(tempName, scope);

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

        IntermediateOperation loopBack = new IntermediateOperation(Operator.GOTO, startLabel, "", "", "", null);

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

                String foo = calculationStack.pop();

                String bar = calculationStack.pop();

                String tempName = generateTemp(DeclaredType.integer, expression.getScope()).getName();

                out.addAll(generateOperation(token, foo, bar , tempName));

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

            if("+*-/<=>=<>".contains(token)){

                while(!operators.isEmpty() && !operators.peek().equals("(") && operatorPrecedence.get(operators.peek()) < operatorPrecedence.get(token))
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

    private List<IntermediateOperation> generateOperation(String operand, String a, String b, String result){
        List<IntermediateOperation> out = new ArrayList<>();

        Operator op = Operator.getFromString(operand);

        if(!op.equals(Operator.UNSUPPORTED)){
            out.add(new IntermediateOperation(op, result, a, b, "", null ));

        }else{

            String foo = generateLabel();

            switch(operand){

                case ">":


                    out.add(new IntermediateOperation(Operator.ASSIGN, result, "1", "", "", null));
                    out.add(new IntermediateOperation(Operator.BRGT, a, b, foo, "",null));
                    out.add(new IntermediateOperation(Operator.ASSIGN, result, "0", "", "", null));
                    out.add(new IntermediateOperation(Operator.UNSUPPORTED, "", "" ,"", foo, null));

                    break;


                case "<":

                    out.add(new IntermediateOperation(Operator.ASSIGN, result, "1", "", "", null));
                    out.add(new IntermediateOperation(Operator.BRLT, a, b, foo, "",null));
                    out.add(new IntermediateOperation(Operator.ASSIGN, result, "0", "", "", null));
                    out.add(new IntermediateOperation(Operator.UNSUPPORTED, "", "" ,"", foo, null));

                    break;
                case "=":

                    out.add(new IntermediateOperation(Operator.ASSIGN, result, "1", "", "", null));
                    out.add(new IntermediateOperation(Operator.BREQ, a, b, foo, "",null));
                    out.add(new IntermediateOperation(Operator.ASSIGN, result, "0", "", "", null));
                    out.add(new IntermediateOperation(Operator.UNSUPPORTED, "", "" ,"", foo, null));


                    break;

                case "<>":

                    out.add(new IntermediateOperation(Operator.ASSIGN, result, "1", "", "", null));
                    out.add(new IntermediateOperation(Operator.BREQ, a, b, foo, "",null));
                    out.add(new IntermediateOperation(Operator.ASSIGN, result, "0", "", "", null));
                    out.add(new IntermediateOperation(Operator.UNSUPPORTED, "", "" ,"", foo, null));
                    break;

                case "<=":

                    out.add(new IntermediateOperation(Operator.ASSIGN, result, "1", "", "", null));
                    out.add(new IntermediateOperation(Operator.BRLEQ, a, b, foo, "",null));
                    out.add(new IntermediateOperation(Operator.ASSIGN, result, "0", "", "", null));
                    out.add(new IntermediateOperation(Operator.UNSUPPORTED, "", "" ,"", foo, null));

                    break;

                case ">=":

                    out.add(new IntermediateOperation(Operator.ASSIGN, result, "1", "", "", null));
                    out.add(new IntermediateOperation(Operator.BRGEQ, a, b, foo, "",null));
                    out.add(new IntermediateOperation(Operator.ASSIGN, result, "0", "", "", null));
                    out.add(new IntermediateOperation(Operator.UNSUPPORTED, "", "" ,"", foo, null));

                    break;

            }

        }

        return out;
    }

    public void coalesceLabels(List<IntermediateOperation> out)
    {
        for(int i=0; i<out.size(); i++)
        {
            IntermediateOperation operation = out.get(i);
            if(operation.getOp()==Operator.UNSUPPORTED)
            {
                if(i!=out.size()-1)
                {
                    IntermediateOperation labeledOp = out.get(i+1);
                    labeledOp.setLabel(operation.getLabel());
                    i--;
                    out.remove(operation);
                }
            }
        }
    }


    private void removeRedundancy(){

        //TODO: remove redundant temporaries

    }

    public String toString(){

        String out = "";
        for(IntermediateOperation foo : intermediates)
            out += foo.toString() + "\n";
        return out;
    }
}
