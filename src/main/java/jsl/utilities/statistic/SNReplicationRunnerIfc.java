package jsl.utilities.statistic;

import jsl.utilities.random.rvariable.RVariableIfc;

import java.util.Map;

public interface SNReplicationRunnerIfc {

    double runReplications(int numReplications, Map<String, RVariableIfc> inputs);
}
