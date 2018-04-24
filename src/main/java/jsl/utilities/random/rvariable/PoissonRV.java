package jsl.utilities.random.rvariable;

import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RngIfc;

/**
 *  Poisson(mean) random variable
 */
public final class PoissonRV extends AbstractRVariable {

    private final double mean;

    public PoissonRV(double mean){
        this(mean, RNStreamFactory.getDefault().getStream());
    }

    public PoissonRV(double mean, RngIfc rng){
        super(rng);
        if (mean <= 0.0) {
            throw new IllegalArgumentException("Poisson mean must be > 0.0");
        }
        this.mean = mean;
    }

    /**
     *
     * @param rng the RngIfc to use
     * @return a new instance with same parameter value
     */
    public final PoissonRV newInstance(RngIfc rng){
        return new PoissonRV(this.mean, rng);
    }

    @Override
    public String toString() {
        return "PoissonRV{" +
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
        double v = JSLRandom.rPoisson(mean, myRNG);
        return v;
    }
}
