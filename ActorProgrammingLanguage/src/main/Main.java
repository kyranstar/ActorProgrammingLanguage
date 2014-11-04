/*
 * @author Kyran Adams
 */
package main;

import interpreter.Interpreter;

import java.util.Map;

import machine.Context;
import type.APValue;

// TODO: Auto-generated Javadoc
/**
 * The Class Main.
 * @author Kyran Adams
 * @version $Revision: 1.0 $
 */
public final class Main {

    /**
     * Instantiates a new main.
     */
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
        final Context context = interpreter.interpret("a= println (3);");
        
        for (final Map.Entry<String, APValue> a : context.getVariables()
                .entrySet()) {
            System.out.println(a.getKey() + " -> " + a.getValue().getValue());
        }
    }
}
