package jsl.utilities.random.rvariable;

import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RngIfc;

/**
 *  Lognormal(mean, variance). The mean and variance are for the lognormal random variables
 */
public final class LognormalRV extends AbstractRVariable {

    private final double myMean;

    private final double myVar;

    public LognormalRV(double mean, double variance){
        this(mean, variance, RNStreamFactory.getDefault().getStream());
    }

    public LognormalRV(double mean, double variance, RngIfc rng){
        super(rng);
        if (mean <= 0) {
            throw new IllegalArgumentException("Mean must be positive");
        }
        myMean = mean;

        if (variance <= 0) {
            throw new IllegalArgumentException("Variance must be positive");
        }
        myVar = variance;
    }

    /**
     *
     * @param rng the RngIfc to use
     * @return a new instance with same parameter value
     */
    public final LognormalRV newInstance(RngIfc rng){
        return new LognormalRV(this.myMean, this.myVar, rng);
    }

    @Override
    public String toString() {
        return "LognormalRV{" +
                "mean=" + myMean +
                ", variance=" + myVar +
                '}';
    }

    /**
     *
     * @return mean of the random variable
     */
    public final double getMean() {
        return myMean;
    }

    /**
     *
     * @return variance of the random variable
     */
    public final double getVariance() {
        return myVar;
    }

    @Override
    protected final double generate() {
        double v = JSLRandom.rLogNormal(myMean, myVar, myRNG);
        return v;
    }
}
