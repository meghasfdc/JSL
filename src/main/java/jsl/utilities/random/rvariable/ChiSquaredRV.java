package jsl.utilities.random.rvariable;

import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RngIfc;

/**
 *  Chi-Squared(degrees of freedom) random variable
 */
public final class ChiSquaredRV extends AbstractRVariable {

    private final double dof;

    public ChiSquaredRV(double dof){
        this(dof, RNStreamFactory.getDefault().getStream());
    }

    public ChiSquaredRV(double dof, RngIfc rng){
        super(rng);
        if (dof <= 0.0) {
            throw new IllegalArgumentException("Chi-Squared degrees of freedom must be > 0.0");
        }
        this.dof = dof;
    }

    /**
     *
     * @param rng the RngIfc to use
     * @return a new instance with same parameter value
     */
    public final ChiSquaredRV newInstance(RngIfc rng){
        return new ChiSquaredRV(this.dof, rng);
    }

    @Override
    public String toString() {
        return "ChiSquaredlRV{" +
                "dof=" + dof +
                '}';
    }

    /**
     *
     * @return the dof value
     */
    public final double getDegreesOfFreedom() {
        return dof;
    }

    @Override
    protected final double generate() {
        double v = JSLRandom.rChiSquared(dof, myRNG);
        return v;
    }
}
