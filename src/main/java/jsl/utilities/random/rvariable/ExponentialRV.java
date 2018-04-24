package jsl.utilities.random.rvariable;

import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RngIfc;

/**
 *  Exponential(mean) random variable
 */
public final class ExponentialRV extends AbstractRVariable {

    private final double mean;

    public ExponentialRV(double mean){
        this(mean, RNStreamFactory.getDefault().getStream());
    }

    public ExponentialRV(double mean, RngIfc rng){
        super(rng);
        if (mean <= 0.0) {
            throw new IllegalArgumentException("Exponential mean must be > 0.0");
        }
        this.mean = mean;
    }

    /**
     *
     * @param rng the RngIfc to use
     * @return a new instance with same parameter value
     */
    public final ExponentialRV newInstance(RngIfc rng){
        return new ExponentialRV(this.mean, rng);
    }

    @Override
    public String toString() {
        return "ExponentialRV{" +
                "mean=" + mean +
                '}';
    }

    /**
     *
     * @return the mean value
     */
    public final double getMean() {
        return mean;
    }

    @Override
    protected final double generate() {
        double v = JSLRandom.rExponential(mean, myRNG);
        return v;
    }
}
