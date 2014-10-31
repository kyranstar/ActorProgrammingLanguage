/*
 * @author Kyran Adams
 */
package interpreter.library;

import java.util.Arrays;

import machine.Context;
import machine.Function;
import parser.ExpressionNode;
import parser.ExpressionNode.OrNode.VariableNode;

// TODO: Auto-generated Javadoc
/**
 * The Class LibraryFunction.
 */
public final class LibraryFunction {

    /**
     * Instantiates a new library function.
     */
    private LibraryFunction() {
    }

    /**
     * Apply library functions.
     *
     * @param context
     *            the context
     * @return the context
     */
    public static Context applyLibraryFunctions(final Context context) {

        context.putFunction("println",
                new Function("println", Arrays.asList(new VariableNode("a")),
                        new ExpressionNode.PrintlnNode(new VariableNode("a"))));

        return context;
    }
}
