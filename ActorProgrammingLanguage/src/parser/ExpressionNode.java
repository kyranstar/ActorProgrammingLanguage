package parser;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import machine.Context;
import type.APValue;
import type.APValue.APValueInt;

public abstract class ExpressionNode<T> {

	private final ExpressionNodeType type;

	private final List<ExpressionNode<T>> terms;

	public ExpressionNode(final ExpressionNodeType type,
			final List<ExpressionNode<T>> terms) {
		this.type = type;
		this.terms = terms;
	}

	public ExpressionNodeType getType() {
		return type;
	}

	protected ExpressionNode<T> getTerm(final int i) {
		return terms.get(i);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "<" + terms + ">";
	}

	public abstract APValue<T> getValue(Context context);

	public static class ConstantNode extends ExpressionNode<BigDecimal> {

		BigDecimal v;

		public ConstantNode(final BigDecimal bigDecimal) {
			super(ExpressionNodeType.CONSTANT_NODE, null);
			v = bigDecimal;
		}

		@Override
		public String toString() {
			return ConstantNode.class.getSimpleName() + "<" + v + ">";
		}

		@Override
		public APValue<BigDecimal> getValue(final Context c) {
			return new APValue.APValueInt(v);
		}

	}

	// public static class FunctionDefNode extends ExpressionNode<BigDecimal>{}
	//
	// public static class FunctionCallNode extends ExpressionNode<BigDecimal> {
	//
	// private final ExpressionNode.VariableNode variable;
	// private final ExpressionNode<BigDecimal> expression;
	// private final ExpressionNode parameter;
	//
	// public FunctionCallNode(final VariableNode expr,
	// final ExpressionNode parameter,
	// final ExpressionNode<BigDecimal> assigned) {
	// super(ExpressionNodeType.ASSIGNMENT_NODE, null);
	// this.variable = expr;
	// this.parameter = parameter;
	// this.expression = assigned;
	// }
	//
	// @Override
	// public String toString() {
	// return variable + "(" + parameter + ") = " + expression;
	// }
	//
	// @Override
	// public APValue<BigDecimal> getValue(final Context context) {
	// final Context c = context.getChild();
	// c.
	// final APValue<BigDecimal> expr = this.expression.getValue(c);
	//
	// return expr;
	// }
	//
	// }

	public static class AssignmentNode extends ExpressionNode<BigDecimal> {

		private final parser.ExpressionNode.VariableNode variable;
		private final ExpressionNode<BigDecimal> expression;

		public AssignmentNode(final VariableNode expr,
				final ExpressionNode<BigDecimal> assigned) {
			super(ExpressionNodeType.ASSIGNMENT_NODE, null);
			this.variable = expr;
			this.expression = assigned;
		}

		@Override
		public String toString() {
			return variable + " = " + expression;
		}

		@Override
		public APValue<BigDecimal> getValue(final Context context) {
			final APValue<BigDecimal> expr = this.expression.getValue(context);
			context.put(variable.name, expression);
			return expr;
		}

	}

	public static class AdditionNode extends ExpressionNode<BigDecimal> {
		public AdditionNode(final ExpressionNode<BigDecimal> n1,
				final ExpressionNode<BigDecimal> n2) {
			super(ExpressionNodeType.ADDITION_NODE, Arrays.asList(n1, n2));
		}

		@Override
		public APValueInt getValue(final Context c) {
			final BigDecimal t1 = getTerm(0).getValue(c).getValue();
			final BigDecimal t2 = getTerm(1).getValue(c).getValue();

			return new APValue.APValueInt(t1.add(t2));
		}
	}

	public static class SubtractionNode extends ExpressionNode<BigDecimal> {
		public SubtractionNode(final ExpressionNode<BigDecimal> n1,
				final ExpressionNode<BigDecimal> n2) {
			super(ExpressionNodeType.SUBTRACTION_NODE, Arrays.asList(n1, n2));
		}

		@Override
		public APValueInt getValue(final Context c) {
			final BigDecimal t1 = getTerm(0).getValue(c).getValue();
			final BigDecimal t2 = getTerm(1).getValue(c).getValue();

			return new APValue.APValueInt(t1.subtract(t2));
		}

	}

	public static class MultiplicationNode extends ExpressionNode<BigDecimal> {
		public MultiplicationNode(final ExpressionNode<BigDecimal> n1,
				final ExpressionNode<BigDecimal> n2) {
			super(ExpressionNodeType.MULTIPLICATION_NODE, Arrays.asList(n1, n2));
		}

		@Override
		public APValue<BigDecimal> getValue(final Context c) {
			final BigDecimal t1 = getTerm(0).getValue(c).getValue();
			final BigDecimal t2 = getTerm(1).getValue(c).getValue();

			return new APValue.APValueInt(t1.multiply(t2));
		}
	}

	public static class DivisionNode extends ExpressionNode<BigDecimal> {
		public DivisionNode(final ExpressionNode<BigDecimal> n1,
				final ExpressionNode<BigDecimal> n2) {
			super(ExpressionNodeType.DIVISION_NODE, Arrays.asList(n1, n2));
		}

		@Override
		public APValue<BigDecimal> getValue(final Context c) {
			final BigDecimal t1 = getTerm(0).getValue(c).getValue();
			final BigDecimal t2 = getTerm(1).getValue(c).getValue();

			return new APValue.APValueInt(t1.divide(t2));
		}
	}

	public static class ExponentiationNode extends ExpressionNode<BigDecimal> {
		public ExponentiationNode(final ExpressionNode<BigDecimal> n1,
				final ExpressionNode<BigDecimal> n2) {
			super(ExpressionNodeType.EXPONENTIATION_NODE, Arrays.asList(n1, n2));
		}

		@Override
		public APValue<BigDecimal> getValue(final Context c) {
			final BigDecimal t1 = getTerm(0).getValue(c).getValue();
			final BigDecimal t2 = getTerm(1).getValue(c).getValue();

			return new APValue.APValueInt(t1.pow(t2.intValue()));
		}
	}

	public static class VariableNode extends ExpressionNode {
		private final String name;

		public VariableNode(final String s) {
			super(ExpressionNodeType.EXPONENTIATION_NODE, null);
			this.name = s;
		}

		@Override
		public String toString() {
			return VariableNode.class.getSimpleName() + "<" + name + ">";
		}

		@Override
		public APValue getValue(final Context c) {
			// TODO: Use map for variables
			return c.get(name).getValue(c);
		}
	}

}
