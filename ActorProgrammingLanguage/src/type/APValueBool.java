package type;

public class APValueBool extends APValue<Boolean> {
    
    /** The Constant TYPE. */
    private static final Class<Boolean> TYPE = Boolean.class;
    
    /**
     * Instantiates a new AP value bool.
     *
     * @param expressionNode
     *            the expression node
     */
    public APValueBool(final Boolean expressionNode) {
        setValue(expressionNode);
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "APValueBool<" + getValue() + ">";
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see type.APValue#getType()
     */
    @Override
    public Class<Boolean> getType() {
        return TYPE;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see type.APValue#callMethod(type.APValue.Methods, type.APValue)
     */
    @Override
    public APValue callMethod(final Operators method, final APValue arg) {
        if (!arg.getType().equals(TYPE)) {
            throw new MismatchedMethodException(method
                    + " must take two bool types. Was " + TYPE + " and "
                    + arg.getType());
        }
        
        switch (method) {
            case AND:
                return new APValueBool(getValue()
                        && ((APValueBool) arg).getValue());
            case OR:
                return new APValueBool(getValue()
                        || ((APValueBool) arg).getValue());
        }
        throw new MismatchedMethodException("Can't call method " + method
                + " on type bool!");
    }
}
