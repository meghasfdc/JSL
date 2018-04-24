package jsl.utilities.random.rvariable;

import jsl.utilities.Identity;
import jsl.utilities.IdentityIfc;
import jsl.utilities.random.rng.RngIfc;

abstract public class AbstractMVRVariable implements MVRVariableIfc, IdentityIfc {

    private final Identity myIdentity;

    /**
     * myRNG provides a reference to the underlying stream of random numbers
     */
    protected RngIfc myRNG;

    public AbstractMVRVariable(RngIfc rng) {
        myIdentity = new Identity();
        setRandomNumberGenerator(rng);
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

    /**
     *
     * @return the underlying random number source
     */
    public final RngIfc getRandomNumberGenerator() {
        return (myRNG);
    }

    /**
     * Sets the underlying random number source
     *
     * @param rng the reference to the random number generator, must not be null
     */
    public final void setRandomNumberGenerator(RngIfc rng) {
        if (rng == null) {
            throw new NullPointerException("RngIfc rng must be non-null");
        }
        myRNG = rng;
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
