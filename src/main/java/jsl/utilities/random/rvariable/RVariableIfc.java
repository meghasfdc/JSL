package jsl.utilities.random.rvariable;

import jsl.utilities.GetValueIfc;
import jsl.utilities.PreviousValueIfc;
import jsl.utilities.random.SampleIfc;
import jsl.utilities.random.rng.*;

/**
 * An interface for defining random variables. The methods sample() and getValue() gets a new
 * value of the random variable sampled accordingly.  The method getPreviousValue() returns
 * the value from the last call to sample()/getValue(). The value returned by getPreviousValue() stays
 * the same until the next call to sample()/getValue().  The methods sample()/getValue() always get
 * the next random value.  If sample()/getValue() is never called then getPreviousValue() returns Double.NaN.
 * Use sample()/getValue() to get a new random value and use getPreviousValue() to get the last sampled value.
 *
 * The preferred approach to creating random variables is to sub-class AbstractRVariable.
 */
public interface RVariableIfc extends GetValueIfc, RandomStreamIfc,
        SampleIfc, NewAntitheticInstanceIfc, PreviousValueIfc {

    /**
     * @return returns a sampled values
     */
    default double getValue() {
        return sample();
    }

    /**
     * @param rng the RngIfc to use
     * @return a new instance with same parameter value
     */
    RVariableIfc newInstance(RngIfc rng);

    /**
     * @return a new instance with same parameter value
     */
    default RVariableIfc newInstance() {
        return newInstance(RNStreamFactory.getDefault().getStream());
    }

}
