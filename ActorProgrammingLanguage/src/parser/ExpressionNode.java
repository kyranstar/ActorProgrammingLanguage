/*
 * @author Kyran Adams
 */
package parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import machine.Context;
import machine.Context.VariableMapping;
import machine.ContextException;
import machine.Function;
import type.APNumber;
import type.APValue;
import type.APValue.Operators;
import type.APValueData;
import type.APValueFunction;
import type.APValueList;
import type.APValueNum;

// TODO: Auto-generated Javadoc
/**
 * An ExpressionNode is a expression in the language that evaluates to T.
 *
 * @author Kyran Adams
 * @version $Revision: 1.0 $
 * @param <T>
 *            the generic type
 */
public abstract class ExpressionNode<T> {
    
    /** The Constant VOID. */
    public static final ExpressionNode<Void> VOID = new ExpressionNode<Void>(
            null) {

        @Override
        public APValue<Void> getValue(final Context context) {
            return APValue.VOID;
        }
    };

    /** The terms of an expression. In "3+4" the terms are 3 and 4. */
    private final List<ExpressionNode<T>> terms;

    /**
     * Instantiates a new expression node.
     *
     * @param terms
     *            the terms
     */
    public ExpressionNode(final List<ExpressionNode<T>> terms) {
        this.terms = terms;
    }

    /**
     * Gets the term.
     *
     * @param i
     *            the index
     *
     * @return the term
     */
    protected ExpressionNode<T> getTerm(final int i) {
        return terms.get(i);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "<" + terms + ">";
    }

    /**
     * Gets the value of the expression in the context.
     *
     * @param context
     *            the context
     *
     * @return the expressions value in context
     */
    public abstract APValue<T> getValue(Context context);

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (terms == null ? 0 : terms.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ExpressionNode<?> other = (ExpressionNode<?>) obj;
        if (terms == null) {
            if (other.terms != null) {
                return false;
            }
        } else if (!terms.equals(other.terms)) {
            return false;
        }
        return true;
    }

    /**
     * The Class ConstantNode. Represents a constant, for example "true" or "3"
     *
     * @author Kyran Adams
     * @version $Revision: 1.0 $
     */
    public static class ConstantNode<T> extends ExpressionNode<T> {

        /** The value. */
        private final APValue<T> v;

        /**
         * Instantiates a new constant node.
         *
         * @param apValue
         *            the ap value
         */
        public ConstantNode(final APValue<T> apValue) {
            super(null);
            v = apValue;
        }

        /*
         * (non-Javadoc)
         *
         * @see parser.ExpressionNode#toString()
         */
        @Override
        public String toString() {
            return v.toString();
        }

        /*
         * (non-Javadoc)
         *
         * @see parser.ExpressionNode#getValue(machine.Context)
         */
        @Override
        public APValue<T> getValue(final Context c) {
            return v;
        }
        
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + (v == null ? 0 : v.hashCode());
            return result;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!super.equals(obj)) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ConstantNode other = (ConstantNode) obj;
            if (v == null) {
                if (other.v != null) {
                    return false;
                }
            } else if (!v.equals(other.v)) {
                return false;
            }
            return true;
        }

    }

    /**
     * The Class FunctionCallNode. Represents a function call.
     *
     * @author Kyran Adams
     * @version $Revision: 1.0 $
     */
    public static class FunctionCallNode extends ExpressionNode {

        /** The function. */
        private final ExpressionNode function;

        /** The parameters. */
        private final List<ExpressionNode> parameters;

        /**
         * Instantiates a new function call node.
         *
         * @param expr
         *            the name
         * @param parameters
         *            the parameters
         */
        public FunctionCallNode(final ExpressionNode expr,
                final List<ExpressionNode> parameters) {
            super(null);
            this.function = expr;
            this.parameters = parameters;
        }

        /*
         * (non-Javadoc)
         *
         * @see parser.ExpressionNode#toString()
         */
        @Override
        public String toString() {
            final StringBuilder b = new StringBuilder("func").append("(");
            for (final ExpressionNode node : parameters) {
                b.append(node).append(',');
            }
            return b.substring(0, b.length() - 1) + ")";
        }

        /*
         * (non-Javadoc)
         *
         * @see parser.ExpressionNode#getValue(machine.Context)
         */
        @Override
        public APValue getValue(final Context context) {
            
            final Context c = new Context(context.getOutputStream());
            // Add all functions of outer scope, but we have to add this
            // function individually to avoid stackoverflow.
            final Map<String, VariableMapping> oldVariables = new HashMap<>(
                    context.getVariables());
            
            c.setVariables(oldVariables);
            final APValue valueFunction = function.getValue(context);
            if (valueFunction == null) {
                throw new ParserException("Undefined function");
            }
            
            final Function func = (Function) valueFunction.getValue();
            // give it access to itself
            if (!c.getVariables().containsKey(func.name)) {
                c.putFunction(func, false);
            }

            if (parameters.size() != func.parameters.size()) {
                throw new ParserException("You gave " + parameters.size()
                        + " parameter(s), function " + func.name + " requires "
                        + func.parameters.size() + " parameter(s).");
            }

            // Put all parameters in function scope
            for (int i = 0; i < parameters.size(); i++) {
                final ExpressionNode given = parameters.get(i);
                final String name = func.parameters.get(i).name;
                // over write outside parameters
                if (c.getVariable(name) != null) {
                    c.getVariables().remove(name);
                    
                }
                c.putVariable(name, given.getValue(context), true);
            }
            final APValue returnVal = func.body.getValue(c);
            // The reason we have to simplify a list before we return it is if
            // the list uses the parameters. This means that if you return
            // [a,a,a], the caller cannot simplify it because it has no access
            // to parameters anymore.
            if (returnVal instanceof APValueList) {
                final List<ExpressionNode> nodes = (List<ExpressionNode>) returnVal
                        .getValue();
                final List<ExpressionNode> simplifiedNodes = new ArrayList<>();
                for (final ExpressionNode node : nodes) {
                    simplifiedNodes.add(new ConstantNode<>(node.getValue(c)));
                }
                return new APValueList(simplifiedNodes);
            }
            return returnVal;
        }
    }

    /**
     * The Class AssignmentNode.
     *
     * @author Kyran Adams
     * @version $Revision: 1.0 $
     */
    public static class AssignmentNode extends ExpressionNode {

        /** The variable to assign to. */
        private final VariableNode variable;

        /** The expression to assign to the variable. */
        private final ExpressionNode expression;
        
        /** The is mutable. */
        private final boolean isMutable;

        /**
         * Instantiates a new assignment node.
         *
         * @param expr
         *            the variable to assign to
         * @param assigned
         *            the assigned expression
         * @param mutable
         *            the mutable
         */
        public AssignmentNode(final VariableNode expr,
                final ExpressionNode<APNumber> assigned, final boolean mutable) {
            super(null);
            variable = expr;
            this.expression = assigned;
            this.isMutable = mutable;
        }

        /*
         * (non-Javadoc)
         *
         * @see parser.ExpressionNode#toString()
         */
        @Override
        public String toString() {
            return getVariable() + " = " + getExpression();
        }

        /*
         * (non-Javadoc)
         *
         * @see parser.ExpressionNode#getValue(machine.Context)
         */
        @Override
        public APValue getValue(final Context context) {
            final APValue expr = this.getExpression().getValue(context);
            context.putVariable(getVariable().name, expr, isMutable);
            return expr;
        }

        /**
         * Gets the variable.
         *
         * @return the variable
         */
        public VariableNode getVariable() {
            return variable;
        }

        /**
         * Gets the expression.
         *
         * @return the expression
         */
        public ExpressionNode getExpression() {
            return expression;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result
                    + (expression == null ? 0 : expression.hashCode());
            result = prime * result + (isMutable ? 1231 : 1237);
            result = prime * result
                    + (variable == null ? 0 : variable.hashCode());
            return result;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!super.equals(obj)) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final AssignmentNode other = (AssignmentNode) obj;
            if (expression == null) {
                if (other.expression != null) {
                    return false;
                }
            } else if (!expression.equals(other.expression)) {
                return false;
            }
            if (isMutable != other.isMutable) {
                return false;
            }
            if (variable == null) {
                if (other.variable != null) {
                    return false;
                }
            } else if (!variable.equals(other.variable)) {
                return false;
            }
            return true;
        }

    }
    
    /**
     * The Class IndexAssignmentNode.
     */
    public static class IndexAssignmentNode extends ExpressionNode {

        /** The variable. */
        private final ExpressionNode<List> variable;

        /** The inside curlies. */
        private final ExpressionNode insideCurlies;

        /** The rh expr. */
        private final ExpressionNode rhExpr;

        /**
         * Instantiates a new index assignment node.
         *
         * @param expr
         *            the expr
         * @param insideCurlies
         *            the inside curlies
         * @param rh
         *            the rh
         */
        public IndexAssignmentNode(final ExpressionNode expr,
                final ExpressionNode insideCurlies, final ExpressionNode rh) {
            super(null);
            this.variable = expr;
            this.insideCurlies = insideCurlies;
            this.rhExpr = rh;
        }
        
        /*
         * (non-Javadoc)
         * 
         * @see parser.ExpressionNode#getValue(machine.Context)
         */
        @Override
        public APValue getValue(final Context context) {
            variable.getValue(context)
                    .getValue()
                    .set(((APNumber) getInsideCurlies().getValue(context)
                            .getValue()).intValueExact(),
                            new ConstantNode(getRightHand().getValue(context)));
            return APValue.VOID;
        }
        
        public ExpressionNode<List> getLeftHand() {
            return variable;
        }
        
        public ExpressionNode getInsideCurlies() {
            return insideCurlies;
        }
        
        public ExpressionNode getRightHand() {
            return rhExpr;
        }

    }
    
    /**
     * The Class FieldAssignmentNode.
     *
     * @author Kyran Adams
     * @version $Revision: 1.0 $
     */
    public static class FieldAssignmentNode extends ExpressionNode {

        /** The variable to assign to. */
        private final VariableNode variable;

        /** The field in the variable to assign to. */
        private final VariableNode field;

        /** The expression to assign to the variable. */
        private final ExpressionNode expression;

        /**
         * Instantiates a new assignment node.
         *
         * @param expr
         *            the variable to assign to
         * @param field
         *            the field
         * @param assigned
         *            the assigned expression
         */
        public FieldAssignmentNode(final VariableNode expr,
                final VariableNode field, final ExpressionNode assigned) {
            super(null);
            this.field = field;
            variable = expr;
            this.expression = assigned;
        }

        /*
         * (non-Javadoc)
         *
         * @see parser.ExpressionNode#toString()
         */
        @Override
        public String toString() {
            return getVariable() + "." + field + " = " + getExpression();
        }

        /*
         * (non-Javadoc)
         *
         * @see parser.ExpressionNode#getValue(machine.Context)
         */
        @Override
        public APValue getValue(final Context context) {
            final APValue lh = context.getVariables().get(variable.getName()).variable;
            if (!(lh instanceof APValueData)) {
                throw new ParserException("Can't access field of non data type");
            }
            ((APValueData) lh).getValue().fields.put(field.getName(),
                    new ConstantNode(expression.getValue(context)));
            return APValue.VOID;
        }

        /**
         * Gets the variable.
         *
         * @return the variable
         */
        public VariableNode getVariable() {
            return variable;
        }

        /**
         * Gets the expression.
         *
         * @return the expression
         */
        public ExpressionNode getExpression() {
            return expression;
        }

    }

    /**
     * The Class FieldAssignmentNode.
     *
     * @author Kyran Adams
     * @version $Revision: 1.0 $
     */
    public static class SequenceNode extends ExpressionNode {

        /** The expression to assign to the variable. */
        private final ExpressionNode expression;

        /** The statements. */
        private final List<ExpressionNode> statements;

        /**
         * Instantiates a new Sequence node.
         *
         * @param statements
         *            the statements
         * @param expression
         *            the expression
         */
        public SequenceNode(final List<ExpressionNode> statements,
                final ExpressionNode expression) {
            super(null);
            this.statements = statements;
            this.expression = expression;
        }

        /*
         * (non-Javadoc)
         *
         * @see parser.ExpressionNode#toString()
         */
        @Override
        public String toString() {
            final StringBuilder b = new StringBuilder("seq((");
            for (final ExpressionNode node : statements) {
                b.append(node).append(',');
            }
            return b.substring(0, b.length() - 1) + ")" + expression + ")";
        }

        /*
         * (non-Javadoc)
         *
         * @see parser.ExpressionNode#getValue(machine.Context)
         */
        @Override
        public APValue getValue(final Context context) {
            for (final ExpressionNode statement : statements) {
                statement.getValue(context);
            }
            return expression.getValue(context);
        }
        
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result
                    + (expression == null ? 0 : expression.hashCode());
            result = prime * result
                    + (statements == null ? 0 : statements.hashCode());
            return result;
        }
        
        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!super.equals(obj)) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final SequenceNode other = (SequenceNode) obj;
            if (expression == null) {
                if (other.expression != null) {
                    return false;
                }
            } else if (!expression.equals(other.expression)) {
                return false;
            }
            if (statements == null) {
                if (other.statements != null) {
                    return false;
                }
            } else if (!statements.equals(other.statements)) {
                return false;
            }
            return true;
        }

    }

    /**
     * The Class IfNode. Represents if then else expression
     *
     * @author Kyran Adams
     * @version $Revision: 1.0 $
     */
    public static class IfNode extends ExpressionNode {

        /**
         * Instantiates a new if node.
         *
         * @param ifExpr
         *            the if expr
         * @param thenExpr
         *            the then expr
         * @param elseExpr
         *            the else expr
         */
        public IfNode(final ExpressionNode<Boolean> ifExpr,
                final ExpressionNode thenExpr, final ExpressionNode elseExpr) {
            super(Arrays.asList(ifExpr, thenExpr, elseExpr));
        }

        /*
         * (non-Javadoc)
         *
         * @see parser.ExpressionNode#getValue(machine.Context)
         */
        @Override
        public APValue getValue(final Context context) {
            boolean result;
            try {
                result = (boolean) getTerm(0).getValue(context).getValue();
            } catch (final ClassCastException e) {
                throw new ParserException(
                        "If expression requires a boolean expression, was "
                                + getTerm(0).getValue(context).getClass(), e);
            }
            if (result) {
                return getTerm(1).getValue(context);
            } else {
                return getTerm(2).getValue(context);
            }
        }

        /*
         * (non-Javadoc)
         *
         * @see parser.ExpressionNode#toString()
         */
        @Override
        public String toString() {
            return "if (" + getTerm(0) + ")" + getTerm(1) + " else "
                    + getTerm(2);
        }
    }
    
    /**
     * The Class AssignmentNode.
     *
     * @author Kyran Adams
     * @version $Revision: 1.0 $
     */
    public static class LambdaNode extends ExpressionNode {

        /** The variable to assign to. */
        private final Function func;

        /**
         * Instantiates a new assignment node.
         *
         * @param func
         *            the func
         */
        public LambdaNode(final Function func) {
            super(null);
            this.func = func;
        }

        /*
         * (non-Javadoc)
         *
         * @see parser.ExpressionNode#toString()
         */
        @Override
        public String toString() {
            return func.toString();
        }

        /*
         * (non-Javadoc)
         *
         * @see parser.ExpressionNode#getValue(machine.Context)
         */
        @Override
        public APValue getValue(final Context context) {
            return new APValueFunction(func);
        }

    }
    
    /**
     * The Class EqualNode.
     *
     * @author Kyran Adams
     * @version $Revision: 1.0 $
     */
    public static class EqualNode extends ExpressionNode {

        /**
         * Instantiates a new equal (comparison) node.
         *
         * @param firstTerm
         *            the firstTerm
         * @param secondTerm
         *            the secondTerm
         */
        public EqualNode(final ExpressionNode<? extends Comparable> firstTerm,
                final ExpressionNode<? extends Comparable> secondTerm) {
            super(Arrays.asList(firstTerm, secondTerm));
        }

        /*
         * (non-Javadoc)
         *
         * @see parser.ExpressionNode#getValue(machine.Context)
         */
        @Override
        public APValue getValue(final Context c) {
            final APValue termOne = getTerm(0).getValue(c);
            final APValue termTwo = getTerm(1).getValue(c);

            return termOne.callMethod(Operators.EQUAL, termTwo);
        }
        
        /*
         * (non-Javadoc)
         * 
         * @see parser.ExpressionNode#toString()
         */
        @Override
        public String toString() {
            return "(" + getTerm(0) + " = " + getTerm(1) + ")";
        }
    }

    /**
     * The Class GreaterThanEqualNode.
     *
     * @author Kyran Adams
     * @version $Revision: 1.0 $
     */
    public static class GreaterThanEqualNode extends ExpressionNode {

        /**
         * Instantiates a new greater than equal node.
         *
         * @param firstTerm
         *            the firstTerm
         * @param secondTerm
         *            the secondTerm
         */
        public GreaterThanEqualNode(
                final ExpressionNode<? extends Comparable> firstTerm,
                final ExpressionNode<? extends Comparable> secondTerm) {
            super(Arrays.asList(firstTerm, secondTerm));
        }

        /*
         * (non-Javadoc)
         *
         * @see parser.ExpressionNode#getValue(machine.Context)
         */
        @Override
        public APValue getValue(final Context c) {
            final APValue termOne = getTerm(0).getValue(c);
            final APValue termTwo = getTerm(1).getValue(c);

            return termOne.callMethod(Operators.GREATER_EQUAL, termTwo);
        }
        
        /*
         * (non-Javadoc)
         * 
         * @see parser.ExpressionNode#toString()
         */
        @Override
        public String toString() {
            return "(" + getTerm(0) + " >= " + getTerm(1) + ")";
        }
    }

    /**
     * The Class LessThanEqualNode.
     *
     * @author Kyran Adams
     * @version $Revision: 1.0 $
     */
    public static class LessThanEqualNode extends ExpressionNode {

        /**
         * Instantiates a new less than equal node.
         *
         * @param firstTerm
         *            the first term
         * @param secondTerm
         *            the second term
         */
        public LessThanEqualNode(
                final ExpressionNode<? extends Comparable> firstTerm,
                final ExpressionNode<? extends Comparable> secondTerm) {
            super(Arrays.asList(firstTerm, secondTerm));
        }

        /*
         * (non-Javadoc)
         *
         * @see parser.ExpressionNode#getValue(machine.Context)
         */
        @Override
        public APValue getValue(final Context c) {
            final APValue termOne = getTerm(0).getValue(c);
            final APValue termTwo = getTerm(1).getValue(c);

            return termOne.callMethod(Operators.LESS_EQUAL, termTwo);
        }
        
        /*
         * (non-Javadoc)
         * 
         * @see parser.ExpressionNode#toString()
         */
        @Override
        public String toString() {
            return "(" + getTerm(0) + " <= " + getTerm(1) + ")";
        }
    }

    /**
     * The Class GreaterThanNode.
     *
     * @author Kyran Adams
     * @version $Revision: 1.0 $
     */
    public static class GreaterThanNode extends ExpressionNode {

        /**
         * Instantiates a new greater than node.
         *
         * @param firstTerm
         *            the first term
         * @param secondTerm
         *            the second term
         */
        public GreaterThanNode(
                final ExpressionNode<? extends Comparable> firstTerm,
                final ExpressionNode<? extends Comparable> secondTerm) {
            super(Arrays.asList(firstTerm, secondTerm));
        }

        /*
         * (non-Javadoc)
         *
         * @see parser.ExpressionNode#getValue(machine.Context)
         */
        @Override
        public APValue getValue(final Context c) {
            final APValue termOne = getTerm(0).getValue(c);
            final APValue termTwo = getTerm(1).getValue(c);

            return termOne.callMethod(Operators.GREATER, termTwo);
        }
        
        /*
         * (non-Javadoc)
         * 
         * @see parser.ExpressionNode#toString()
         */
        @Override
        public String toString() {
            return "(" + getTerm(0) + " > " + getTerm(1) + ")";
        }
    }

    /**
     * The Class LessThanNode.
     *
     * @author Kyran Adams
     * @version $Revision: 1.0 $
     */
    public static class LessThanNode extends ExpressionNode {

        /**
         * Instantiates a new less than node.
         *
         * @param firstTerm
         *            the firstTerm
         * @param secondTerm
         *            the secondTerm
         */
        public LessThanNode(
                final ExpressionNode<? extends Comparable> firstTerm,
                final ExpressionNode<? extends Comparable> secondTerm) {
            super(Arrays.asList(firstTerm, secondTerm));
        }

        /*
         * (non-Javadoc)
         *
         * @see parser.ExpressionNode#getValue(machine.Context)
         */
        @Override
        public APValue getValue(final Context c) {
            final APValue termOne = getTerm(0).getValue(c);
            final APValue termTwo = getTerm(1).getValue(c);

            return termOne.callMethod(Operators.LESS, termTwo);
        }
        
        /*
         * (non-Javadoc)
         * 
         * @see parser.ExpressionNode#toString()
         */
        @Override
        public String toString() {
            return "(" + getTerm(0) + " < " + getTerm(1) + ")";
        }
    }

    /**
     * The Class ListIndexNode.
     *
     * @author Kyran Adams
     * @version $Revision: 1.0 $
     */
    public static class ListIndexNode extends ExpressionNode {

        /** The list. */
        private final ExpressionNode list;
        
        /** The index. */
        private final ExpressionNode index;

        /**
         * Instantiates a new list index node.
         *
         * @param expr
         *            the expr
         * @param insideParens
         *            the inside parens
         */
        public ListIndexNode(final ExpressionNode expr,
                final ExpressionNode insideParens) {
            super(null);
            this.list = expr;
            this.index = insideParens;
        }

        /*
         * (non-Javadoc)
         *
         * @see parser.ExpressionNode#getValue(machine.Context)
         */
        @Override
        public APValue getValue(final Context context) {
            final APValueList apValueList = (APValueList) this.list
                    .getValue(context);
            final int indexValue = ((APNumber) index.getValue(context)
                    .getValue()).intValueExact();
            final ExpressionNode expressionNode = (ExpressionNode) apValueList
                    .getValue().get(indexValue);
            return expressionNode.getValue(context);
        }
        
        /*
         * (non-Javadoc)
         * 
         * @see parser.ExpressionNode#toString()
         */
        @Override
        public String toString() {
            return "(" + list + "[" + index + "])";
        }
    }

    /**
     * The Class FieldAccessNode.
     *
     * @author Kyran Adams
     * @version $Revision: 1.0 $
     */
    public static class FieldAccessNode extends ExpressionNode {

        /** The data structure. */
        private final ExpressionNode dataStructure;
        
        /** The field in the data structure. */
        private final VariableNode field;

        /**
         * Instantiates a new list index node.
         *
         * @param expr
         *            the data structure
         * @param field
         *            the field being accessed
         */
        public FieldAccessNode(final ExpressionNode expr,
                final VariableNode field) {
            super(null);
            assert expr != null;
            assert field != null;
            this.dataStructure = expr;
            this.field = field;
        }

        /*
         * (non-Javadoc)
         *
         * @see parser.ExpressionNode#getValue(machine.Context)
         */
        @Override
        public APValue getValue(final Context context) {
            final APValueData apValueData = (APValueData) this.dataStructure
                    .getValue(context);
            if (apValueData == null) {
                throw new ParserException("Undefined data structure "
                        + this.dataStructure);
            }

            final ExpressionNode fieldValue = apValueData.getValue().fields
                    .get(field.name);

            if (fieldValue == null) {
                throw new ParserException("Undefined field " + field.name);
            }

            return fieldValue.getValue(context);
        }
        
        /*
         * (non-Javadoc)
         * 
         * @see parser.ExpressionNode#toString()
         */
        @Override
        public String toString() {
            return "(" + dataStructure + "." + field + ")";
        }
    }
    
    /**
     * The Class AndNode.
     *
     * @author Kyran Adams
     * @version $Revision: 1.0 $
     */
    public static class AndNode extends ExpressionNode<Boolean> {

        /**
         * Instantiates a new and node.
         *
         * @param firstTerm
         *            the firstTerm
         * @param secondTerm
         *            the secondTerm
         */
        public AndNode(final ExpressionNode<Boolean> firstTerm,
                final ExpressionNode<Boolean> secondTerm) {
            super(Arrays.asList(firstTerm, secondTerm));
        }

        /*
         * (non-Javadoc)
         *
         * @see parser.ExpressionNode#getValue(machine.Context)
         */
        @Override
        public APValue getValue(final Context c) {
            final APValue termOne = getTerm(0).getValue(c);
            final APValue termTwo = getTerm(1).getValue(c);

            return termOne.callMethod(Operators.AND, termTwo);
        }
        
        /*
         * (non-Javadoc)
         * 
         * @see parser.ExpressionNode#toString()
         */
        @Override
        public String toString() {
            return "(" + getTerm(0) + " && " + getTerm(1) + ")";
        }
    }

    /**
     * The Class OrNode.
     *
     * @author Kyran Adams
     * @version $Revision: 1.0 $
     */
    public static class OrNode extends ExpressionNode<Boolean> {

        /**
         * Instantiates a new or node.
         *
         * @param firstTerm
         *            the firstTerm
         * @param secondTerm
         *            the secondTerm
         */
        public OrNode(final ExpressionNode<Boolean> firstTerm,
                final ExpressionNode<Boolean> secondTerm) {
            super(Arrays.asList(firstTerm, secondTerm));
        }

        /*
         * (non-Javadoc)
         *
         * @see parser.ExpressionNode#getValue(machine.Context)
         */
        @Override
        public APValue getValue(final Context c) {
            final APValue termOne = getTerm(0).getValue(c);
            final APValue termTwo = getTerm(1).getValue(c);

            return termOne.callMethod(Operators.OR, termTwo);

        }
        
        /*
         * (non-Javadoc)
         * 
         * @see parser.ExpressionNode#toString()
         */
        @Override
        public String toString() {
            return "(" + getTerm(0) + " || " + getTerm(1) + ")";
        }
    }

    /**
     * The Class AdditionNode.
     *
     * @author Kyran Adams
     * @version $Revision: 1.0 $
     */
    public static class AdditionNode extends ExpressionNode<APNumber> {

        /**
         * Instantiates a new addition node.
         *
         * @param firstTerm
         *            the firstTerm
         * @param secondTerm
         *            the secondTerm
         */
        public AdditionNode(final ExpressionNode<APNumber> firstTerm,
                final ExpressionNode<APNumber> secondTerm) {
            super(Arrays.asList(firstTerm, secondTerm));
        }

        /*
         * (non-Javadoc)
         *
         * @see parser.ExpressionNode#getValue(machine.Context)
         */
        @Override
        public APValue getValue(final Context context) {
            final APValue termOne = getTerm(0).getValue(context);
            final APValue termTwo = getTerm(1).getValue(context);

            return termOne.callMethod(Operators.ADD, termTwo);
        }
        
        /*
         * (non-Javadoc)
         * 
         * @see parser.ExpressionNode#toString()
         */
        @Override
        public String toString() {
            return "(" + getTerm(0) + " + " + getTerm(1) + ")";
        }
    }

    /**
     * The Class SubtractionNode.
     *
     * @author Kyran Adams
     * @version $Revision: 1.0 $
     */
    public static class SubtractionNode extends ExpressionNode {

        /**
         * Instantiates a new subtraction node.
         *
         * @param firstTerm
         *            the firstTerm
         * @param secondTerm
         *            the secondTerm
         */
        public SubtractionNode(final ExpressionNode firstTerm,
                final ExpressionNode secondTerm) {
            super(Arrays.asList(firstTerm, secondTerm));
        }

        /*
         * (non-Javadoc)
         *
         * @see parser.ExpressionNode#getValue(machine.Context)
         */
        @Override
        public APValue getValue(final Context c) {
            final APValue termOne = getTerm(0).getValue(c);
            final APValue termTwo = getTerm(1).getValue(c);

            return termOne.callMethod(Operators.SUBTRACT, termTwo);
        }
        
        /*
         * (non-Javadoc)
         * 
         * @see parser.ExpressionNode#toString()
         */
        @Override
        public String toString() {
            return "(" + getTerm(0) + " - " + getTerm(1) + ")";
        }
    }

    /**
     * The Class MultiplicationNode.
     *
     * @author Kyran Adams
     * @version $Revision: 1.0 $
     */
    public static class MultiplicationNode extends ExpressionNode {

        /**
         * Instantiates a new multiplication node.
         *
         * @param firstTerm
         *            the firstTerm
         * @param secondTerm
         *            the secondTerm
         */
        public MultiplicationNode(final ExpressionNode firstTerm,
                final ExpressionNode secondTerm) {
            super(Arrays.asList(firstTerm, secondTerm));
        }

        /*
         * (non-Javadoc)
         *
         * @see parser.ExpressionNode#getValue(machine.Context)
         */
        @Override
        public APValue getValue(final Context context) {
            final APValue termOne = getTerm(0).getValue(context);
            final APValue termTwo = getTerm(1).getValue(context);

            return termOne.callMethod(Operators.MULTIPLY, termTwo);
        }
        
        /*
         * (non-Javadoc)
         * 
         * @see parser.ExpressionNode#toString()
         */
        @Override
        public String toString() {
            return "(" + getTerm(0) + " * " + getTerm(1) + ")";
        }
    }
    
    /**
     * The Class ModNode.
     */
    public static class ModNode extends ExpressionNode {

        /**
         * Instantiates a new modulo node.
         *
         * @param firstTerm
         *            the firstTerm
         * @param secondTerm
         *            the secondTerm
         */
        public ModNode(final ExpressionNode firstTerm,
                final ExpressionNode secondTerm) {
            super(Arrays.asList(firstTerm, secondTerm));
        }

        /*
         * (non-Javadoc)
         *
         * @see parser.ExpressionNode#getValue(machine.Context)
         */
        @Override
        public APValue getValue(final Context c) {
            final APValue termOne = getTerm(0).getValue(c);
            final APValue termTwo = getTerm(1).getValue(c);

            return termOne.callMethod(Operators.MOD, termTwo);
        }
        
        /*
         * (non-Javadoc)
         * 
         * @see parser.ExpressionNode#toString()
         */
        @Override
        public String toString() {
            return "(" + getTerm(0) + " % " + getTerm(1) + ")";
        }
    }

    /**
     * The Class DivisionNode.
     *
     * @author Kyran Adams
     * @version $Revision: 1.0 $
     */
    public static class DivisionNode extends ExpressionNode {

        /**
         * Instantiates a new division node.
         *
         * @param firstTerm
         *            the firstTerm
         * @param secondTerm
         *            the secondTerm
         */
        public DivisionNode(final ExpressionNode firstTerm,
                final ExpressionNode secondTerm) {
            super(Arrays.asList(firstTerm, secondTerm));
        }

        /*
         * (non-Javadoc)
         *
         * @see parser.ExpressionNode#getValue(machine.Context)
         */
        @Override
        public APValue getValue(final Context c) {
            final APValue termOne = getTerm(0).getValue(c);
            final APValue termTwo = getTerm(1).getValue(c);

            return termOne.callMethod(Operators.DIVIDE, termTwo);
        }
        
        /*
         * (non-Javadoc)
         * 
         * @see parser.ExpressionNode#toString()
         */
        @Override
        public String toString() {
            return "(" + getTerm(0) + " / " + getTerm(1) + ")";
        }
    }

    /**
     * The Class ExponentiationNode.
     *
     * @author Kyran Adams
     * @version $Revision: 1.0 $
     */
    public static class ExponentiationNode extends ExpressionNode<APNumber> {

        /**
         * Instantiates a new exponentiation node.
         *
         * @param firstTerm
         *            the firstTerm
         * @param secondTerm
         *            the secondTerm
         */
        public ExponentiationNode(final ExpressionNode<APNumber> firstTerm,
                final ExpressionNode<APNumber> secondTerm) {
            super(Arrays.asList(firstTerm, secondTerm));
        }

        /*
         * (non-Javadoc)
         *
         * @see parser.ExpressionNode#getValue(machine.Context)
         */
        @Override
        public APValue<APNumber> getValue(final Context c) {
            final APValue termOne = getTerm(0).getValue(c);
            final APValue termTwo = getTerm(1).getValue(c);

            return termOne.callMethod(Operators.POWER, termTwo);
        }
        
        /*
         * (non-Javadoc)
         * 
         * @see parser.ExpressionNode#toString()
         */
        @Override
        public String toString() {
            return "(" + getTerm(0) + " ^ " + getTerm(1) + ")";
        }
    }
    
    /**
     * The Class RangeNode.
     *
     * @author Kyran Adams
     * @version $Revision: 1.0 $
     */
    public static class RangeNode extends ExpressionNode {

        /**
         * Instantiates a new exponentiation node.
         *
         * @param firstTerm
         *            the firstTerm
         * @param secondTerm
         *            the secondTerm
         */
        public RangeNode(final ExpressionNode firstTerm,
                final ExpressionNode secondTerm) {
            super(Arrays.asList(firstTerm, secondTerm));
        }

        /*
         * (non-Javadoc)
         *
         * @see parser.ExpressionNode#getValue(machine.Context)
         */
        @Override
        public APValue getValue(final Context c) {
            final APValue termOne = getTerm(0).getValue(c);
            final APValue termTwo = getTerm(1).getValue(c);

            final List<ExpressionNode> nodes = new ArrayList<>();
            if (termOne instanceof APValueNum && termTwo instanceof APValueNum) {
                APNumber first = (APNumber) termOne.getValue();
                final APNumber second = (APNumber) termTwo.getValue();
                
                for (; first.compareTo(second) <= 0; first = first
                        .add(APNumber.ONE)) {
                    nodes.add(new ConstantNode(new APValueNum(first)));
                }
            } else {
                throw new ParserException("Cannot create range of types "
                        + termOne.getClass() + " and " + termTwo.getClass());
            }

            return new APValueList(nodes);
        }
        
        /*
         * (non-Javadoc)
         * 
         * @see parser.ExpressionNode#toString()
         */
        @Override
        public String toString() {
            return "(" + getTerm(0) + " to " + getTerm(1) + ")";
        }
    }

    /**
     * The Class VariableNode.
     *
     * @author Kyran Adams
     * @version $Revision: 1.0 $
     */
    public static class VariableNode extends ExpressionNode {

        /** The name. */
        private final String name;

        /**
         * Instantiates a new variable node.
         *
         * @param s
         *            the s
         */
        
        public VariableNode(final String s) {
            super(null);
            this.name = s;
        }

        /*
         * (non-Javadoc)
         *
         * @see parser.ExpressionNode#toString()
         */
        @Override
        public String toString() {
            return name;
        }

        /*
         * (non-Javadoc)
         *
         * @see parser.ExpressionNode#getValue(machine.Context)
         */
        @Override
        public APValue getValue(final Context c) {
            final APValue function = c.getVariable(name);
            if (function == null) {
                throw new ContextException("Undefined function: " + name);
            }
            return function;
        }

        /**
         * Gets the name.
         *
         *
         * @return the name
         */
        public String getName() {
            return name;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = super.hashCode();
            result = prime * result + (name == null ? 0 : name.hashCode());
            return result;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (!super.equals(obj)) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final VariableNode other = (VariableNode) obj;
            if (name == null) {
                if (other.name != null) {
                    return false;
                }
            } else if (!name.equals(other.name)) {
                return false;
            }
            return true;
        }
        
    }

}
