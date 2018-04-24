package jsl.utilities.random.rvariable;

import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RngIfc;

/**
 *  JohnsonB(alpha1, alpha2, min, max) random variable
 */
public final class JohnsonBRV extends AbstractRVariable {

    private final double myAlpha1;

    private final double myAlpha2;

    private final double myMin;

    private final double myMax;

    public JohnsonBRV(double alpha1, double alpha2, double min, double max){
        this(alpha1, alpha2, min, max, RNStreamFactory.getDefault().getStream());
    }

    public JohnsonBRV(double alpha1, double alpha2, double min, double max, RngIfc rng){
        super(rng);
        if (alpha2 <= 0) {
            throw new IllegalArgumentException("alpha2 must be > 0");
        }

        if (max <= min) {
            throw new IllegalArgumentException("the min must be < than the max");
        }
        myAlpha1 = alpha1;
        myAlpha2 = alpha2;
        myMin = min;
        myMax = max;
    }

    /**
     *
     * @param rng the RngIfc to use
     * @return a new instance with same parameter value
     */
    public final JohnsonBRV newInstance(RngIfc rng){
        return new JohnsonBRV(getAlpha1(), getAlpha2(), myMin, myMax, rng);
    }

    @Override
    public String toString() {
        return "JohnsonBRV{" +
                "alpha1=" + myAlpha1 +
                ", alpha2=" + myAlpha2 +
                ", min=" + myMin +
                ", max=" + myMax +
                '}';
    }

    /** Gets the lower limit
     * @return The lower limit
     */
    public final double getMinimum() {
        return (myMin);
    }

    /** Gets the upper limit
     * @return The upper limit
     */
    public final double getMaximum() {
        return (myMax);
    }

    /**
     *
     * @return the first shape parameter
     */
    public final double getAlpha1() {
        return myAlpha1;
    }

    /**
     *
     * @return the second shape parameter
     */
    public final double getAlpha2() {
        return myAlpha2;
    }

    @Override
    protected final double generate() {
        double v = JSLRandom.rJohnsonB(myAlpha1, myAlpha2, myMin, myMax, myRNG);
        return v;
    }
}
