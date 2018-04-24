package jsl.utilities.random.rvariable;

import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RngIfc;

/**
 *  Geometric(probability of success) random variable, range 0, 1, 2, ..
 */
public final class GeometricRV extends AbstractRVariable {

    private final double myProbSuccess;

    public GeometricRV(double prob){
        this(prob, RNStreamFactory.getDefault().getStream());
    }

    public GeometricRV(double prob, RngIfc rng){
        super(rng);
        if ((prob < 0.0) || (prob > 1.0)) {
            throw new IllegalArgumentException("Probability must be [0,1]");
        }
        myProbSuccess = prob;
    }

    /**
     *
     * @param rng the RngIfc to use
     * @return a new instance with same parameter value
     */
    public final GeometricRV newInstance(RngIfc rng){
        return new GeometricRV(this.myProbSuccess, rng);
    }

    @Override
    public String toString() {
        return "GeometricRV{" +
                "probSuccess=" + myProbSuccess +
                '}';
    }

    /** Gets the success probability
     * @return The success probability
     */
    public final double getProbabilityOfSuccess() {
        return (myProbSuccess);
    }

    @Override
    protected final double generate() {
        double v = JSLRandom.rGeometric(myProbSuccess, myRNG);
        return v;
    }
}
