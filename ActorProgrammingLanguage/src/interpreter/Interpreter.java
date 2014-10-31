package interpreter;

import java.io.PrintStream;
import java.util.List;

import lexer.Lexer;
import machine.Context;
import parser.ExpressionNode;
import parser.Parser;

public class Interpreter {
    private final PrintStream printStream;
    
    public Interpreter(final PrintStream printStream) {
        this.printStream = printStream;
    }

    public Context interpret(final String s) {
        final Context context = new Context(printStream);
        final Lexer lexer = new Lexer(s);
        final List<ExpressionNode> nodes = new Parser(lexer.lex())
                .parse(context);
        for (final ExpressionNode node : nodes) {
            node.getValue(context);
        }
        return context;
    }
}
