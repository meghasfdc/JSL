package jsl.utilities.random.rvariable;

import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RngIfc;

/**
 *  Continuous uniform(min, max) random variable
 */
public final class UniformRV extends AbstractRVariable {

    private final double min;
    private final double max;

    public UniformRV(double min, double max){
        this(min, max, RNStreamFactory.getDefault().getStream());
    }

    public UniformRV(double min, double max, RngIfc rng){
        super(rng);
        if (min >= max) {
            throw new IllegalArgumentException("Lower limit must be < upper limit. lower limit = " + min + " upper limit = " + max);
        }
        this.min = min;
        this.max = max;
    }

    /**
     *
     * @param rng the RngIfc to use
     * @return a new instance with same parameter value
     */
    public final UniformRV newInstance(RngIfc rng){
        return new UniformRV(this.min, this.max, rng);
    }

    @Override
    public String toString() {
        return "UniformRV{" +
                "min=" + min +
                ", max=" + max +
                '}';
    }

    /** Gets the lower limit
     * @return The lower limit
     */
    public final double getMinimum() {
        return (min);
    }

    /** Gets the upper limit
     * @return The upper limit
     */
    public final double getMaximum() {
        return (max);
    }

    @Override
    protected final double generate() {
        double v = JSLRandom.rUniform(min, max, myRNG);
        return v;
    }
}
