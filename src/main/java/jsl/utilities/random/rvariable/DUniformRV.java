package jsl.utilities.random.rvariable;

import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RngIfc;

/**
 *  discrete uniform(min, max) random variable
 */
public final class DUniformRV extends AbstractRVariable {

    private final int min;
    private final int max;

    public DUniformRV(int min, int max){
        this(min, max, RNStreamFactory.getDefault().getStream());
    }

    public DUniformRV(int min, int max, RngIfc rng){
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
    public final DUniformRV newInstance(RngIfc rng){
        return new DUniformRV(this.min, this.max, rng);
    }

    @Override
    public String toString() {
        return "DUniformRV{" +
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
        double v = JSLRandom.rDUniform(min, max, myRNG);
        return v;
    }
}
