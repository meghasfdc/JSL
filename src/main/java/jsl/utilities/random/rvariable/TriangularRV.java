package jsl.utilities.random.rvariable;

import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RngIfc;

/**
 *  Triangularmin, mode, max) random variable
 */
public final class TriangularRV extends AbstractRVariable {

    private final double myMin;

    private final double myMax;

    private final double myMode;

    public TriangularRV(double min, double mode, double max){
        this(min, mode, max, RNStreamFactory.getDefault().getStream());
    }

    public TriangularRV(double min, double mode, double max, RngIfc rng){
        super(rng);
        if (min > mode) {
            throw new IllegalArgumentException("min must be <= mode");
        }

        if (min >= max) {
            throw new IllegalArgumentException("min must be < max");
        }

        if (mode > max) {
            throw new IllegalArgumentException("mode must be <= max");
        }

        myMode = mode;
        myMin = min;
        myMax = max;
    }

    /**
     *
     * @param rng the RngIfc to use
     * @return a new instance with same parameter value
     */
    public final TriangularRV newInstance(RngIfc rng){
        return new TriangularRV(myMin, myMode, myMax, rng);
    }

    /**
     *
     * @return the minimum
     */
    public final double getMinimum() {
        return (myMin);
    }

    /**
     *
     * @return the mode or most likely value
     */
    public final double getMode() {
        return (myMode);
    }

    /**
     *
     * @return the maximum
     */
    public final double getMaximum() {
        return (myMax);
    }

    @Override
    protected final double generate() {
        double v = JSLRandom.rTriangular(myMin, myMode, myMax, myRNG);
        return v;
    }
}
