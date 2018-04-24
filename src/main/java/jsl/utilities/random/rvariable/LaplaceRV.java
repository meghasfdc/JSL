package jsl.utilities.random.rvariable;

import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RngIfc;

/**
 *  Normal(mean, variance)
 */
public final class LaplaceRV extends AbstractRVariable {

    private final double myMean;

    private final double myScale;

    public LaplaceRV(double mean, double scale){
        this(mean, scale, RNStreamFactory.getDefault().getStream());
    }

    public LaplaceRV(double mean, double scale, RngIfc rng){
        super(rng);
        myMean = mean;
        if (scale <= 0) {
            throw new IllegalArgumentException("Scale must be positive");
        }
        myScale = scale;
    }

    /**
     *
     * @param rng the RngIfc to use
     * @return a new instance with same parameter value
     */
    public final LaplaceRV newInstance(RngIfc rng){
        return new LaplaceRV(this.myMean, this.myScale, rng);
    }

    @Override
    public String toString() {
        return "LaplaceRV{" +
                "mean=" + myMean +
                ", scale=" + myScale +
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
     * @return the scale parameter
     */
    public final double getScale() {
        return myScale;
    }

    @Override
    protected final double generate() {
        double v = JSLRandom.rLaplace(myMean, myScale, myRNG);
        return v;
    }
}
