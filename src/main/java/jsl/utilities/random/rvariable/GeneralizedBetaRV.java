package jsl.utilities.random.rvariable;

import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RngIfc;

/**
 *  JohnsonB(alpha1, alpha2, min, max) random variable
 */
public final class GeneralizedBetaRV extends AbstractRVariable {

    private final double myMin;

    private final double myMax;

    private final BetaRV myBeta;

    public GeneralizedBetaRV(double alpha1, double alpha2, double min, double max){
        this(alpha1, alpha2, min, max, RNStreamFactory.getDefault().getStream());
    }

    public GeneralizedBetaRV(double alpha1, double alpha2, double min, double max, RngIfc rng){
        super(rng);
        myBeta = new BetaRV(alpha1, alpha2, rng);
        if (max <= min) {
            throw new IllegalArgumentException("the min must be < than the max");
        }
        myMin = min;
        myMax = max;
    }

    /**
     *
     * @param rng the RngIfc to use
     * @return a new instance with same parameter value
     */
    public final GeneralizedBetaRV newInstance(RngIfc rng){
        return new GeneralizedBetaRV(getAlpha1(), getAlpha2(), myMin, myMax, rng);
    }

    @Override
    public String toString() {
        return "GeneralizedBetaRV{" +
                "alpha1=" + myBeta.getAlpha1() +
                ", alpha2=" + myBeta.getAlpha2() +
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
        return myBeta.getAlpha1();
    }

    /**
     *
     * @return the second shape parameter
     */
    public final double getAlpha2() {
        return myBeta.getAlpha2();
    }

    @Override
    protected final double generate() {
        double x = myBeta.getValue();
        double v = myMin + (myMax - myMin) * x;
        return v;
    }
}
