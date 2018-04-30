package jsl.utilities.random.rvariable;

import jsl.utilities.controls.Controls;
import jsl.utilities.random.rng.RNStreamIfc;

import java.util.Objects;

public abstract class RVControls extends Controls {

    private final RVariableIfc.RVType myType;

    public RVControls(RVariableIfc.RVType type) {
        Objects.requireNonNull(type, "The random variable type must not be null");
        this.myType = type;
    }

    /**
     *
     * @return the type of the random variable
     */
    public final RVariableIfc.RVType getType() {
        return myType;
    }

    /**
     *
     * @return an instance of the random variable based on the current control parameters,
     * with a new stream
     */
    public final RVariableIfc makeRVariable(){
        return makeRVariable(JSLRandom.getRNStream());
    }

    /**
     *
     * @param rnStream the stream to use
     * @return an instance of the random variable based on the current control parameters
     */
    abstract RVariableIfc makeRVariable(RNStreamIfc rnStream);
}
