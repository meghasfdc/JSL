package jsl.utilities.random.rvariable;

import jsl.utilities.GetValueIfc;
import jsl.utilities.random.SampleIfc;
import jsl.utilities.random.rng.*;

import java.util.ArrayList;
import java.util.List;

/** An interface for defining random variables
 *
 */
public interface MVRVariableIfc extends RandomStreamIfc,
        MVSampleIfc, GetRngIfc, SetRngIfc {

    /**
     * @param rng the RngIfc to use
     * @return a new instance with same parameter value
     */
    MVRVariableIfc newInstance(RngIfc rng);

    /**
     * @return a new instance with same parameter value
     */
    default MVRVariableIfc newInstance() {
        return newInstance(RNStreamFactory.getDefault().getStream());
    }

}
