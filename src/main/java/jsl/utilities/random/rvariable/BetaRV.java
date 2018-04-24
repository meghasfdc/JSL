package jsl.utilities.random.rvariable;

import jsl.utilities.random.distributions.Beta;
import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RngIfc;

/**
 *  Beta(alpha1, alpha2) random variable, range (0,1)
 */
public final class BetaRV extends AbstractRVariable {

    private final Beta myBeta;

    public BetaRV(double alpha1, double alpha2){
        this(alpha1, alpha2, RNStreamFactory.getDefault().getStream());
    }

    public BetaRV(double alpha1, double alpha2, RngIfc rng){
        super(rng);
        myBeta = new Beta(alpha1, alpha2);
    }

    /**
     *
     * @param rng the RngIfc to use
     * @return a new instance with same parameter value
     */
    public final BetaRV newInstance(RngIfc rng){
        return new BetaRV(getAlpha1(), getAlpha2(), rng);
    }

    @Override
    public String toString() {
        return "BetaRV{" +
                "alpha1=" + myBeta.getAlpha1() +
                ", alpha2=" + myBeta.getAlpha2() +
                '}';
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
        double v = myBeta.invCDF(myRNG.randU01());
        return v;
    }
}
