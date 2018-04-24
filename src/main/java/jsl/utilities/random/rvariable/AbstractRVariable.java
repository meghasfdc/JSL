package jsl.utilities.random.rvariable;

import jsl.utilities.Identity;
import jsl.utilities.IdentityIfc;
import jsl.utilities.random.rng.RngIfc;
import java.util.Objects;

/**
 *  An abstract base class for building random variables.  Implement
 *  the random generation procedure in the method generate().
 */
abstract public class AbstractRVariable implements RVariableIfc, IdentityIfc {

    private final Identity myIdentity;

    private double myPrevValue;

    /**
     * myRNG provides a reference to the underlying stream of random numbers
     */
    protected final RngIfc myRNG;

    /**
     *
     * @param rng the source of the randomness
     * @throws NullPointerException if rng is null
     *
     */
    public AbstractRVariable(RngIfc rng) {
        myIdentity = new Identity();
        myRNG = Objects.requireNonNull(rng,"RngIfc rng must be non-null" );
        myPrevValue = Double.NaN;
    }

    /** Makes a new instance.  False allows the new instance to keep using
     * the same underlying source of random numbers.
     *
     * @param newRNG true mean use new stream. This is same as newInstance(). False
     *               means clone uses same underlying source of randomness
     * @return a new instance configured based on current instance
     */
    public final RVariableIfc newInstance(boolean newRNG){
        if (newRNG){
            return newInstance();
        } else {
            return newInstance(this.myRNG);
        }
    }

    /**
     *
     * @return the randomly generated variate
     */
    abstract protected double generate();

    /** Sets the last (previous) randomly generated value. Used within sample()
     *
     * @param value the value to assign
     */
    protected final void setPreviousValue(double value){
        myPrevValue = value;
    }

    @Override
    public final double sample(){
        double x = generate();
        setPreviousValue(x);
        return x;
    }

    @Override
    public final double getValue(){
        return sample();
    }

    @Override
    public final double getPreviousValue() {
        return myPrevValue;
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
    public final RVariableIfc newAntitheticInstance() {
        return newInstance(myRNG.newAntitheticInstance());
    }

    @Override
    public final void resetStartStream() {
        myRNG.resetStartStream();
    }

    @Override
    public final void resetStartSubstream() {
        myRNG.resetStartSubstream();
    }

    @Override
    public final void advanceToNextSubstream() {
        myRNG.advanceToNextSubstream();
    }

    @Override
    public final void setAntitheticOption(boolean flag) {
        myRNG.setAntitheticOption(flag);
    }

    @Override
    public final boolean getAntitheticOption() {
        return myRNG.getAntitheticOption();
    }
}
