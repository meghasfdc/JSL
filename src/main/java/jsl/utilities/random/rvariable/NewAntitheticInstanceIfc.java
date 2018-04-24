package jsl.utilities.random.rvariable;

import jsl.utilities.random.rng.RngIfc;

public interface NewAntitheticInstanceIfc {

    /**
     * @return a new instance with same parameter value, but that has antithetic variates
     */
    RVariableIfc newAntitheticInstance();
}
