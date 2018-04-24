package jsl.utilities.random.distributions;

import jsl.utilities.Interval;

/**
 *  Used to represent the set of possible values for continuous distributions
 */
public interface DomainIfc {

    Interval getDomain();
}
