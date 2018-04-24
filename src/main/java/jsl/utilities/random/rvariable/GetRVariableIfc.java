package jsl.utilities.random.rvariable;

import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RngIfc;

public interface GetRVariableIfc {

    RVariableIfc getRandomVariable(RngIfc rng);

    default RVariableIfc getRandomVariable(){
        return getRandomVariable(RNStreamFactory.getDefault().getStream());
    }
}
