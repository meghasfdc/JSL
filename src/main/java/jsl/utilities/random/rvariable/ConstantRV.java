package jsl.utilities.random.rvariable;

import jsl.utilities.Identity;
import jsl.utilities.IdentityIfc;
import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RngIfc;

/**
 *  Allows a constant to pretend to be a random variable
 */
public final class ConstantRV implements RVariableIfc, IdentityIfc {

    /**
     * A constant to represent zero for sharing
     */
    public final static ConstantRV ZERO = new ConstantRV(0.0);
    /**
     * A constant to represent one for sharing
     */
    public final static ConstantRV ONE = new ConstantRV(1.0);

    /**
     * A constant to represent two for sharing
     */
    public final static ConstantRV TWO = new ConstantRV(2.0);

    /**
     * A constant to represent positive infinity for sharing
     */
    public final static ConstantRV POSITIVE_INFINITY = new ConstantRV(Double.POSITIVE_INFINITY);

    private final double value;
    private final Identity myIdentity;

    public ConstantRV(double value){
        myIdentity = new Identity();
        this.value = value;
    }

    /**
     *
     * @param rng the RngIfc to use
     * @return a new instance with same parameter value
     */
    public final ConstantRV newInstance(RngIfc rng){
        return new ConstantRV(this.value);
    }

    /**
     *
     * @return a new instance with same parameter value
     */
    public final ConstantRV newInstance(){
        return new ConstantRV(this.value);
    }

    @Override
    public String toString() {
        return "ConstantRV{" +
                "value=" + value +
                '}';
    }

    @Override
    public final String getName() {
        return myIdentity.getName();
    }

    @Override
    public final long getId() {
        return myIdentity.getId();
    }

    /** Sets the name
     * @param str The name as a string.
     */
    public final void setName(String str) {
        myIdentity.setName(str);
    }


    @Override
    public final double sample() {
        return value;
    }

    @Override
    public final void resetStartStream() {

    }

    @Override
    public final void resetStartSubstream() {

    }

    @Override
    public final void advanceToNextSubstream() {

    }

    @Override
    public final void setAntitheticOption(boolean flag) {

    }

    @Override
    public final boolean getAntitheticOption() {
        return false;
    }

    @Override
    public final double getPreviousValue() {
        return value;
    }

    @Override
    public final RVariableIfc newAntitheticInstance() {
        return new ConstantRV(this.value);
    }
}
