package jsl.utilities.random.rvariable;

import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RngIfc;
import jsl.utilities.random.robj.DPopulation;

/**
 *  A random variable that samples from the provided data
 */
public final class EmpiricalRV extends AbstractRVariable {

    private final DPopulation myPop;

    public EmpiricalRV(double[] data){
        this(data, RNStreamFactory.getDefault().getStream());
    }

    public EmpiricalRV(double [] data, RngIfc rng) {
        super(rng);
        if (rng == null) {
            throw new IllegalArgumentException("The supplied RngIfc was null");
        }
        if (data == null) {
            throw new IllegalArgumentException("The supplied data array was null");
        }
        if (data.length == 0){
            throw new IllegalArgumentException("The supplied data array had no elements.");
        }
        myPop = new DPopulation(data);
    }

    @Override
    public RVariableIfc newInstance(RngIfc rng) {
        return new EmpiricalRV(myPop.getParameters(), myRNG);
    }

    @Override
    protected final double generate() {
        double v = myPop.getValue();
        return v;
    }
}
