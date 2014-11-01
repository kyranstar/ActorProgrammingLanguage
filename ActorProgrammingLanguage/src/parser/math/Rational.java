/*
 * @author Kyran Adams
 */
package parser.math;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

// TODO: Auto-generated Javadoc
/**
 * Fractions (rational numbers). They are divisions of two BigInteger numbers,
 * reduced to coprime numerator and denominator.
 *
 * @author Richard J. Mathar
 * @since 2006-06-25
 */
public class Rational implements Cloneable, Comparable<Rational> {
    
    /** numerator. */
    BigInteger a;
    
    /**
     * denominator, always larger than zero.
     */
    BigInteger b;
    
    /**
     * The maximum and minimum value of a standard Java integer, 2^31.
     * 
     * @since 2009-05-18
     */
    static public BigInteger MAX_INT = new BigInteger("2147483647");
    
    /** The min int. */
    static public BigInteger MIN_INT = new BigInteger("-2147483648");
    
    /**
     * The constant 1.
     */
    static Rational ONE = new Rational(1, 1);
    /**
     * The constant 0.
     */
    static public Rational ZERO = new Rational();
    
    /**
     * The constant 1/2.
     *
     * @since 2010-05-25
     */
    static public Rational HALF = new Rational(1, 2);
    
    /**
     * Default ctor, which represents the zero.
     * 
     * @since 2007-11-17
     */
    public Rational() {
        a = BigInteger.ZERO;
        b = BigInteger.ONE;
    }
    
    /**
     * ctor from a numerator and denominator.
     * 
     * @param a
     *            the numerator.
     * @param b
     *            the denominator.
     */
    public Rational(final BigInteger a, final BigInteger b) {
        this.a = a;
        this.b = b;
        normalize();
    }
    
    /**
     * ctor from a numerator.
     * 
     * @param a
     *            the BigInteger.
     */
    public Rational(final BigInteger a) {
        this.a = a;
        b = new BigInteger("1");
    }
    
    /**
     * ctor from a numerator and denominator.
     * 
     * @param a
     *            the numerator.
     * @param b
     *            the denominator.
     */
    public Rational(final int a, final int b) {
        this(new BigInteger("" + a), new BigInteger("" + b));
    }
    
    /**
     * ctor from an integer.
     * 
     * @param n
     *            the integer to be represented by the new instance.
     * @since 2010-07-18
     */
    public Rational(final int n) {
        this(n, 1);
    }
    
    /**
     * ctor from a string representation.
     *
     * @param str
     *            the string. This either has a slash in it, separating two
     *            integers, or, if there is no slash, is representing the
     *            numerator with implicit denominator equal to 1. Warning: this
     *            does not yet test for a denominator equal to zero
     * @throws NumberFormatException
     *             the number format exception
     */
    public Rational(final String str) throws NumberFormatException {
        this(str, 10);
    }
    
    /**
     * ctor from a string representation in a specified base.
     *
     * @param str
     *            the string. This either has a slash in it, separating two
     *            integers, or, if there is no slash, is just representing the
     *            numerator.
     * @param radix
     *            the number base for numerator and denominator Warning: this
     *            does not yet test for a denominator equal to zero
     * @throws NumberFormatException
     *             the number format exception
     */
    public Rational(final String str, final int radix)
            throws NumberFormatException {
        final int hasslah = str.indexOf("/");
        if (hasslah == -1) {
            a = new BigInteger(str, radix);
            b = new BigInteger("1", radix);
            /* no normalization necessary here */
        } else {
            /*
             * create numerator and denominator separately
             */
            a = new BigInteger(str.substring(0, hasslah), radix);
            b = new BigInteger(str.substring(hasslah + 1), radix);
            normalize();
        }
    }
    
    /**
     * Create a copy.
     *
     * @return the rational
     * @since 2008-11-07
     */
    @Override
    public Rational clone() {
        /*
         * protected access means this does not work return new
         * Rational(a.clone(), b.clone()) ;
         */
        final BigInteger aclon = new BigInteger("" + a);
        final BigInteger bclon = new BigInteger("" + b);
        return new Rational(aclon, bclon);
    } /* Rational.clone */
    
    /**
     * Multiply by another fraction.
     * 
     * @param val
     *            a second rational number.
     * @return the product of this with the val.
     */
    public Rational multiply(final Rational val) {
        final BigInteger num = a.multiply(val.a);
        final BigInteger deno = b.multiply(val.b);
        /*
         * Normalization to an coprime format will be done inside the ctor() and
         * is not duplicated here.
         */
        return new Rational(num, deno);
    } /* Rational.multiply */
    
    /**
     * Multiply by a BigInteger.
     * 
     * @param val
     *            a second number.
     * @return the product of this with the value.
     */
    public Rational multiply(final BigInteger val) {
        final Rational val2 = new Rational(val, BigInteger.ONE);
        return multiply(val2);
    } /* Rational.multiply */
    
    /**
     * Multiply by an integer.
     * 
     * @param val
     *            a second number.
     * @return the product of this with the value.
     */
    public Rational multiply(final int val) {
        final BigInteger tmp = new BigInteger("" + val);
        return multiply(tmp);
    } /* Rational.multiply */
    
    /**
     * Power to an integer.
     * 
     * @param exponent
     *            the exponent.
     * @return this value raised to the power given by the exponent. If the
     *         exponent is 0, the value 1 is returned.
     */
    public Rational pow(final int exponent) {
        if (exponent == 0) {
            return new Rational(1, 1);
        }
        
        final BigInteger num = a.pow(Math.abs(exponent));
        final BigInteger deno = b.pow(Math.abs(exponent));
        if (exponent > 0) {
            return new Rational(num, deno);
        } else {
            return new Rational(deno, num);
        }
    } /* Rational.pow */
    
    /**
     * Power to an integer.
     *
     * @param exponent
     *            the exponent.
     * @return this value raised to the power given by the exponent. If the
     *         exponent is 0, the value 1 is returned.
     * @throws NumberFormatException
     *             the number format exception
     * @since 2009-05-18
     */
    public Rational pow(final BigInteger exponent) throws NumberFormatException {
        /* test for overflow */
        if (exponent.compareTo(MAX_INT) == 1) {
            throw new NumberFormatException("Exponent " + exponent.toString()
                    + " too large.");
        }
        if (exponent.compareTo(MIN_INT) == -1) {
            throw new NumberFormatException("Exponent " + exponent.toString()
                    + " too small.");
        }
        
        /* promote to the simpler interface above */
        return pow(exponent.intValue());
    } /* Rational.pow */
    
    /**
     * r-th root.
     *
     * @param r
     *            the inverse of the exponent. 2 for the square root, 3 for the
     *            third root etc
     * @return this value raised to the inverse power given by the root
     *         argument, this^(1/r).
     * @throws NumberFormatException
     *             the number format exception
     * @since 2009-05-18
     */
    public Rational root(final BigInteger r) throws NumberFormatException {
        /* test for overflow */
        if (r.compareTo(MAX_INT) == 1) {
            throw new NumberFormatException("Root " + r.toString()
                    + " too large.");
        }
        if (r.compareTo(MIN_INT) == -1) {
            throw new NumberFormatException("Root " + r.toString()
                    + " too small.");
        }
        
        final int rthroot = r.intValue();
        /* cannot pull root of a negative value with even-valued root */
        if (compareTo(ZERO) == -1 && rthroot % 2 == 0) {
            throw new NumberFormatException("Negative basis " + toString()
                    + " with odd root " + r.toString());
        }
        
        /*
         * extract a sign such that we calculate |n|^(1/r), still r carrying any
         * sign
         */
        final boolean flipsign = compareTo(ZERO) == -1 && rthroot % 2 != 0 ? true
                : false;
        
        /*
         * delegate the main work to ifactor#root()
         */
        final Ifactor num = new Ifactor(a.abs());
        final Ifactor deno = new Ifactor(b);
        final Rational resul = num.root(rthroot).divide(deno.root(rthroot));
        if (flipsign) {
            return resul.negate();
        } else {
            return resul;
        }
    } /* Rational.root */
    
    /**
     * Raise to a rational power.
     *
     * @param exponent
     *            The exponent.
     * @return This value raised to the power given by the exponent. If the
     *         exponent is 0, the value 1 is returned.
     * @throws NumberFormatException
     *             the number format exception
     * @since 2009-05-18
     */
    public Rational pow(final Rational exponent) throws NumberFormatException {
        if (exponent.a.compareTo(BigInteger.ZERO) == 0) {
            return new Rational(1, 1);
        }
        
        /*
         * calculate (a/b)^(exponent.a/exponent.b) as
         * ((a/b)^exponent.a)^(1/exponent.b) = tmp^(1/exponent.b)
         */
        final Rational tmp = pow(exponent.a);
        return tmp.root(exponent.b);
    } /* Rational.pow */
    
    /**
     * Divide by another fraction.
     * 
     * @param val
     *            A second rational number.
     * @return The value of this/val
     */
    public Rational divide(final Rational val) {
        if (val.compareTo(Rational.ZERO) == 0) {
            throw new ArithmeticException("Dividing " + toString()
                    + " through zero.");
        }
        final BigInteger num = a.multiply(val.b);
        final BigInteger deno = b.multiply(val.a);
        /*
         * Reduction to a coprime format is done inside the ctor, and not
         * repeated here.
         */
        return new Rational(num, deno);
    } /* Rational.divide */
    
    /**
     * Divide by an integer.
     * 
     * @param val
     *            a second number.
     * @return the value of this/val
     */
    public Rational divide(final BigInteger val) {
        if (val.compareTo(BigInteger.ZERO) == 0) {
            throw new ArithmeticException("Dividing " + toString()
                    + " through zero.");
        }
        final Rational val2 = new Rational(val, BigInteger.ONE);
        return divide(val2);
    } /* Rational.divide */
    
    /**
     * Divide by an integer.
     * 
     * @param val
     *            A second number.
     * @return The value of this/val
     */
    public Rational divide(final int val) {
        if (val == 0) {
            throw new ArithmeticException("Dividing " + toString()
                    + " through zero.");
        }
        final Rational val2 = new Rational(val, 1);
        return divide(val2);
    } /* Rational.divide */
    
    /**
     * Add another fraction.
     * 
     * @param val
     *            The number to be added
     * @return this+val.
     */
    public Rational add(final Rational val) {
        final BigInteger num = a.multiply(val.b).add(b.multiply(val.a));
        final BigInteger deno = b.multiply(val.b);
        return new Rational(num, deno);
    } /* Rational.add */
    
    /**
     * Add another integer.
     * 
     * @param val
     *            The number to be added
     * @return this+val.
     */
    public Rational add(final BigInteger val) {
        final Rational val2 = new Rational(val, BigInteger.ONE);
        return add(val2);
    } /* Rational.add */
    
    /**
     * Add another integer.
     * 
     * @param val
     *            The number to be added
     * @return this+val.
     * @since May 26 2010
     */
    public Rational add(final int val) {
        final BigInteger val2 = a.add(b.multiply(new BigInteger("" + val)));
        return new Rational(val2, b);
    } /* Rational.add */
    
    /**
     * Compute the negative.
     * 
     * @return -this.
     */
    public Rational negate() {
        return new Rational(a.negate(), b);
    } /* Rational.negate */
    
    /**
     * Subtract another fraction.
     * 
     * @param val
     *            the number to be subtracted from this
     * @return this - val.
     */
    public Rational subtract(final Rational val) {
        final Rational val2 = val.negate();
        return add(val2);
    } /* Rational.subtract */
    
    /**
     * Subtract an integer.
     * 
     * @param val
     *            the number to be subtracted from this
     * @return this - val.
     */
    public Rational subtract(final BigInteger val) {
        final Rational val2 = new Rational(val, BigInteger.ONE);
        return subtract(val2);
    } /* Rational.subtract */
    
    /**
     * Subtract an integer.
     * 
     * @param val
     *            the number to be subtracted from this
     * @return this - val.
     */
    public Rational subtract(final int val) {
        final Rational val2 = new Rational(val, 1);
        return subtract(val2);
    } /* Rational.subtract */
    
    /**
     * binomial (n choose m).
     *
     * @author Richard J. Mathar
     * @param n
     *            the numerator. Equals the size of the set to choose from.
     * @param m
     *            the denominator. Equals the number of elements to select.
     * @return the binomial coefficient.
     * @since 2006-06-27
     */
    public static Rational binomial(final Rational n, final BigInteger m) {
        if (m.compareTo(BigInteger.ZERO) == 0) {
            return Rational.ONE;
        }
        Rational bin = n;
        for (BigInteger i = new BigInteger("2"); i.compareTo(m) != 1; i = i
                .add(BigInteger.ONE)) {
            bin = bin.multiply(n.subtract(i.subtract(BigInteger.ONE)))
                    .divide(i);
        }
        return bin;
    } /* Rational.binomial */
    
    /**
     * binomial (n choose m).
     *
     * @author Richard J. Mathar
     * @param n
     *            the numerator. Equals the size of the set to choose from.
     * @param m
     *            the denominator. Equals the number of elements to select.
     * @return the binomial coefficient.
     * @since 2009-05-19
     */
    public static Rational binomial(final Rational n, final int m) {
        if (m == 0) {
            return Rational.ONE;
        }
        Rational bin = n;
        for (int i = 2; i <= m; i++) {
            bin = bin.multiply(n.subtract(i - 1)).divide(i);
        }
        return bin;
    } /* Rational.binomial */
    
    /**
     * Hankel's symbol (n,k).
     *
     * @author Richard J. Mathar
     * @param n
     *            the first parameter.
     * @param k
     *            the second parameter, greater or equal to 0.
     * @return Gamma(n+k+1/2)/k!/GAMMA(n-k+1/2)
     * @since 2010-07-18
     */
    public static Rational hankelSymb(final Rational n, final int k) {
        if (k == 0) {
            return Rational.ONE;
        } else if (k < 0) {
            throw new ArithmeticException("Negative parameter " + k);
        }
        Rational nkhalf = n.subtract(k).add(Rational.HALF);
        nkhalf = nkhalf.Pochhammer(2 * k);
        final Factorial f = new Factorial();
        return nkhalf.divide(f.at(k));
    } /* Rational.binomial */
    
    /**
     * Get the numerator.
     * 
     * @return The numerator of the reduced fraction.
     */
    public BigInteger numer() {
        return a;
    }
    
    /**
     * Get the denominator.
     * 
     * @return The denominator of the reduced fraction.
     */
    public BigInteger denom() {
        return b;
    }
    
    /**
     * Absolute value.
     * 
     * @return The absolute (non-negative) value of this.
     */
    public Rational abs() {
        return new Rational(a.abs(), b.abs());
    }
    
    /**
     * floor(): the nearest integer not greater than this.
     * 
     * @return The integer rounded towards negative infinity.
     */
    public BigInteger floor() {
        /*
         * is already integer: return the numerator
         */
        if (b.compareTo(BigInteger.ONE) == 0) {
            return a;
        } else if (a.compareTo(BigInteger.ZERO) > 0) {
            return a.divide(b);
        } else {
            return a.divide(b).subtract(BigInteger.ONE);
        }
    } /* Rational.floor */
    
    /**
     * ceil(): the nearest integer not smaller than this.
     * 
     * @return The integer rounded towards positive infinity.
     * @since 2010-05-26
     */
    public BigInteger ceil() {
        /*
         * is already integer: return the numerator
         */
        if (b.compareTo(BigInteger.ONE) == 0) {
            return a;
        } else if (a.compareTo(BigInteger.ZERO) > 0) {
            return a.divide(b).add(BigInteger.ONE);
        } else {
            return a.divide(b);
        }
    } /* Rational.ceil */
    
    /**
     * Remove the fractional part.
     * 
     * @return The integer rounded towards zero.
     */
    public BigInteger trunc() {
        /*
         * is already integer: return the numerator
         */
        if (b.compareTo(BigInteger.ONE) == 0) {
            return a;
        } else {
            return a.divide(b);
        }
    } /* Rational.trunc */
    
    /**
     * Compares the value of this with another constant.
     * 
     * @param val
     *            the other constant to compare with
     * @return -1, 0 or 1 if this number is numerically less than, equal to, or
     *         greater than val.
     */
    @Override
    public int compareTo(final Rational val) {
        /*
         * Since we have always kept the denominators positive, simple
         * cross-multiplying works without changing the sign.
         */
        final BigInteger left = a.multiply(val.b);
        final BigInteger right = val.a.multiply(b);
        return left.compareTo(right);
    } /* Rational.compareTo */
    
    /**
     * Compares the value of this with another constant.
     * 
     * @param val
     *            the other constant to compare with
     * @return -1, 0 or 1 if this number is numerically less than, equal to, or
     *         greater than val.
     */
    public int compareTo(final BigInteger val) {
        final Rational val2 = new Rational(val, BigInteger.ONE);
        return compareTo(val2);
    } /* Rational.compareTo */
    
    /**
     * Return a string in the format number/denom. If the denominator equals 1,
     * print just the numerator without a slash.
     * 
     * @return the human-readable version in base 10
     */
    @Override
    public String toString() {
        if (b.compareTo(BigInteger.ONE) != 0) {
            return a.toString() + "/" + b.toString();
        } else {
            return a.toString();
        }
    } /* Rational.toString */
    
    /**
     * Return a double value representation.
     * 
     * @return The value with double precision.
     * @since 2008-10-26
     */
    public double doubleValue() {
        /*
         * To meet the risk of individual overflows of the exponents of a
         * separate invocation a.doubleValue() or b.doubleValue(), we divide
         * first in a BigDecimal environment and convert the result.
         */
        final BigDecimal adivb = new BigDecimal(a).divide(new BigDecimal(b),
                MathContext.DECIMAL128);
        return adivb.doubleValue();
    } /* Rational.doubleValue */
    
    /**
     * Return a float value representation.
     * 
     * @return The value with single precision.
     * @since 2009-08-06
     */
    public float floatValue() {
        final BigDecimal adivb = new BigDecimal(a).divide(new BigDecimal(b),
                MathContext.DECIMAL128);
        return adivb.floatValue();
    } /* Rational.floatValue */
    
    /**
     * Return a representation as BigDecimal.
     * 
     * @param mc
     *            the mathematical context which determines precision, rounding
     *            mode etc
     * @return A representation as a BigDecimal floating point number.
     * @since 2008-10-26
     */
    public BigDecimal BigDecimalValue(final MathContext mc) {
        /*
         * numerator and denominator individually rephrased
         */
        final BigDecimal n = new BigDecimal(a);
        final BigDecimal d = new BigDecimal(b);
        /*
         * the problem with n.divide(d,mc) is that the apparent precision might
         * be smaller than what is set by mc if the value has a precise
         * truncated representation. 1/4 will appear as 0.25, independent of mc
         */
        return BigDecimalMath.scalePrec(n.divide(d, mc), mc);
    } /* Rational.BigDecimalValue */
    
    /**
     * Return a string in floating point format.
     * 
     * @param digits
     *            The precision (number of digits)
     * @return The human-readable version in base 10.
     * @since 2008-10-25
     */
    public String toFString(final int digits) {
        if (b.compareTo(BigInteger.ONE) != 0) {
            final MathContext mc = new MathContext(digits, RoundingMode.DOWN);
            final BigDecimal f = new BigDecimal(a)
                    .divide(new BigDecimal(b), mc);
            return f.toString();
        } else {
            return a.toString();
        }
    } /* Rational.toFString */
    
    /**
     * Compares the value of this with another constant.
     * 
     * @param val
     *            The other constant to compare with
     * @return The arithmetic maximum of this and val.
     * @since 2008-10-19
     */
    public Rational max(final Rational val) {
        if (compareTo(val) > 0) {
            return this;
        } else {
            return val;
        }
    } /* Rational.max */
    
    /**
     * Compares the value of this with another constant.
     * 
     * @param val
     *            The other constant to compare with
     * @return The arithmetic minimum of this and val.
     * @since 2008-10-19
     */
    public Rational min(final Rational val) {
        if (compareTo(val) < 0) {
            return this;
        } else {
            return val;
        }
    } /* Rational.min */
    
    /**
     * Compute Pochhammer's symbol (this)_n.
     * 
     * @param n
     *            The number of product terms in the evaluation.
     * @return Gamma(this+n)/Gamma(this) = this*(this+1)*...*(this+n-1).
     * @since 2008-10-25
     */
    public Rational Pochhammer(final BigInteger n) {
        if (n.compareTo(BigInteger.ZERO) < 0) {
            return null;
        } else if (n.compareTo(BigInteger.ZERO) == 0) {
            return Rational.ONE;
        } else {
            /*
             * initialize results with the current value
             */
            Rational res = new Rational(a, b);
            BigInteger i = BigInteger.ONE;
            for (; i.compareTo(n) < 0; i = i.add(BigInteger.ONE)) {
                res = res.multiply(add(i));
            }
            return res;
        }
    } /* Rational.pochhammer */
    
    /**
     * Compute pochhammer's symbol (this)_n.
     * 
     * @param n
     *            The number of product terms in the evaluation.
     * @return Gamma(this+n)/GAMMA(this).
     * @since 2008-11-13
     */
    public Rational Pochhammer(final int n) {
        return Pochhammer(new BigInteger("" + n));
    } /* Rational.pochhammer */
    
    /**
     * True if the value is integer. Equivalent to the indication whether a
     * conversion to an integer can be exact.
     *
     * @return true, if is big integer
     * @since 2010-05-26
     */
    public boolean isBigInteger() {
        return b.abs().compareTo(BigInteger.ONE) == 0;
    } /* Rational.isBigInteger */
    
    /**
     * True if the value is integer and in the range of the standard integer.
     * Equivalent to the indication whether a conversion to an integer can be
     * exact.
     *
     * @return true, if is integer
     * @since 2010-05-26
     */
    public boolean isInteger() {
        if (!isBigInteger()) {
            return false;
        }
        return a.compareTo(MAX_INT) <= 0 && a.compareTo(MIN_INT) >= 0;
    } /* Rational.isInteger */
    
    /**
     * Conversion to an integer value, if this can be done exactly.
     *
     * @return the int
     * @since 2011-02-13
     */
    int intValue() {
        if (!isInteger()) {
            throw new NumberFormatException("cannot convert " + toString()
                    + " to integer.");
        }
        return a.intValue();
    }
    
    /**
     * Conversion to a BigInteger value, if this can be done exactly.
     *
     * @return the big integer
     * @since 2012-03-02
     */
    BigInteger BigIntegerValue() {
        if (!isBigInteger()) {
            throw new NumberFormatException("cannot convert " + toString()
                    + " to BigInteger.");
        }
        return a;
    }
    
    /**
     * True if the value is a fraction of two integers in the range of the
     * standard integer.
     *
     * @return true, if is integer frac
     * @since 2010-05-26
     */
    public boolean isIntegerFrac() {
        return a.compareTo(MAX_INT) <= 0 && a.compareTo(MIN_INT) >= 0
                && b.compareTo(MAX_INT) <= 0 && b.compareTo(MIN_INT) >= 0;
    } /* Rational.isIntegerFrac */
    
    /**
     * The sign: 1 if the number is >0, 0 if ==0, -1 if <0.
     *
     * @return the signum of the value.
     * @since 2010-05-26
     */
    public int signum() {
        return b.signum() * a.signum();
    } /* Rational.signum */
    
    /**
     * Common lcm of the denominators of a set of rational values.
     * 
     * @param vals
     *            The list/set of the rational values.
     * @return LCM(denom of first, denom of second, ..,denom of last)
     * @since 2012-03-02
     */
    static public BigInteger lcmDenom(final Rational[] vals) {
        BigInteger l = BigInteger.ONE;
        for (int v = 0; v < vals.length; v++) {
            l = BigIntegerMath.lcm(l, vals[v].b);
        }
        return l;
    } /* Rational.lcmDenom */
    
    /**
     * Normalize to coprime numerator and denominator. Also copy a negative sign
     * of the denominator to the numerator.
     * 
     * @since 2008-10-19
     */
    protected void normalize() {
        /*
         * compute greatest common divisor of numerator and denominator
         */
        final BigInteger g = a.gcd(b);
        if (g.compareTo(BigInteger.ONE) > 0) {
            a = a.divide(g);
            b = b.divide(g);
        }
        if (b.compareTo(BigInteger.ZERO) == -1) {
            a = a.negate();
            b = b.negate();
        }
    } /* Rational.normalize */
} /* Rational */
