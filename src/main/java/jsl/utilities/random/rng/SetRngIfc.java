package jsl.utilities.random.rng;

public interface SetRngIfc {

    /**
     * Sets the underlying random number generator
     *
     * @param rng the reference to the random number generator, must not be null
     */
    void setRandomNumberGenerator(RngIfc rng);
}
