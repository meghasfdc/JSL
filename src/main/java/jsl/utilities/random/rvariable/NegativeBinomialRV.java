package jsl.utilities.random.rvariable;

import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RngIfc;

/**
 *  NegativeBinomial(probability of success, number of successes)
 */
public final class NegativeBinomialRV extends AbstractRVariable {

    private double myProbSuccess;

    private double myNumSuccesses;

    public NegativeBinomialRV(double prob, double numSuccess){
        this(prob, numSuccess, RNStreamFactory.getDefault().getStream());
    }

    public NegativeBinomialRV(double prob, double numSuccess, RngIfc rng){
        super(rng);
        if ((prob < 0.0) || (prob > 1.0)) {
            throw new IllegalArgumentException("Success Probability must be [0,1]");
        }
        if (numSuccess <= 0) {
            throw new IllegalArgumentException("Number of successes must be > 0");
        }
        myProbSuccess = prob;
        myNumSuccesses = numSuccess;
    }

    /**
     *
     * @param rng the RngIfc to use
     * @return a new instance with same parameter value
     */
    public final NegativeBinomialRV newInstance(RngIfc rng){
        return new NegativeBinomialRV(this.myProbSuccess, this.myNumSuccesses, rng);
    }

    @Override
    public String toString() {
        return "NegativeBinomialRV{" +
                "probSuccess=" + myProbSuccess +
                ", numSuccesses=" + myNumSuccesses +
                '}';
    }

    /**
     * Gets the success probability
     *
     * @return The success probability
     */
    public final double getProbabilityOfSuccess() {
        return (myProbSuccess);
    }

    /**
     * Gets the desired number of successes
     *
     * @return the number of success
     */
    public final double getDesiredNumberOfSuccesses() {
        return (myNumSuccesses);
    }


    @Override
    protected final double generate() {
        double v = JSLRandom.rNegBinomial(myProbSuccess, myNumSuccesses, myRNG);
        return v;
    }
}
