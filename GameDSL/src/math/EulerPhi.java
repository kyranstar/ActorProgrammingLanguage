/*
 * @author Kyran Adams
 */
package math;

import java.math.BigInteger;

// TODO: Auto-generated Javadoc
/**
 * Euler totient function.
 *
 * @author Richard J. Mathar
 * @see <a href="http://oeis.org/A000010">A000010</a> in the OEIS.
 * @since 2008-10-14
 * @since 2012-03-04 Adapted to new Ifactor representation.
 */
public class EulerPhi {
    /**
     * Default constructor. Does nothing().
     */
    public EulerPhi() {
    }
    
    /**
     * Compute phi(n).
     * 
     * @param n
     *            The positive argument of the function.
     * @return phi(n)
     */
    public BigInteger at(final int n) {
        return at(new BigInteger("" + n));
    } /* at */
    
    /**
     * Compute phi(n).
     * 
     * @param n
     *            The positive argument of the function.
     * @return phi(n)
     */
    public BigInteger at(final BigInteger n) {
        if (n.compareTo(BigInteger.ZERO) <= 0) {
            throw new ArithmeticException("negative argument " + n
                    + " of EulerPhi");
        }
        final Ifactor prFact = new Ifactor(n);
        BigInteger phi = n;
        if (n.compareTo(BigInteger.ONE) > 0) {
            for (int i = 0; i < prFact.primeexp.size(); i += 2) {
                final BigInteger p = new BigInteger(prFact.primeexp
                        .elementAt(i).toString());
                final BigInteger p_1 = p.subtract(BigInteger.ONE);
                phi = phi.multiply(p_1).divide(p);
            }
        }
        return phi;
    } /* at */
    
    /**
     * Test program. It takes one argument n and prints the value phi(n).<br>
     * java -cp . org.nevec.rjm.EulerPhi n<br>
     *
     * @param args
     *            the arguments
     * @throws ArithmeticException
     *             the arithmetic exception
     * @since 2006-08-14
     */
    public static void main(final String[] args) throws ArithmeticException {
        final EulerPhi a = new EulerPhi();
        final int n = new Integer(args[0]).intValue();
        System.out.println("phi(" + n + ") = " + a.at(n));
    }
} /* EulerPhi */
