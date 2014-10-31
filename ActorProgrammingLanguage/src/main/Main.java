/*
 * @author Kyran Adams
 */
package main;

import interpreter.Interpreter;

import java.util.Map;

import machine.Context;
import parser.ExpressionNode;

// TODO: Auto-generated Javadoc
/**
 * The Class Main.
 */
public final class Main {
    private Main() {
    }

    /**
     * The main method.
     *
     * @param args
     *            the arguments
     */
    public static void main(final String[] args) {
        final Interpreter interpreter = new Interpreter(System.out);
        final Context context = interpreter.interpret("a= print (3);");
        
        for (final Map.Entry<String, ExpressionNode> a : context.getContext()
                .entrySet()) {
            System.out.println(a.getKey() + " -> "
                    + a.getValue().getValue(context));
        }
    }
}
