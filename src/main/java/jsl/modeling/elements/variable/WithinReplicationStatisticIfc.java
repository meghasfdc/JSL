package jsl.modeling.elements.variable;

import jsl.utilities.statistic.WeightedStatisticIfc;


/** An interface for accessing within replication statistics
 *  Within replication statistics can be observation or time weighted.
 *  No variance information is provided due to the fact that the standard
 *  generate variance estimator is likely to be biased because of within
 *  replication correlation.
 *
 * @author rossetti
 */
public interface WithinReplicationStatisticIfc {

    /** Returns a reference to the underlying WeightedStatistic
     *
     * @return
     */
    WeightedStatisticIfc getWithinReplicationStatistic();

    /** Allows the name of the statistic to be changed
     *
     * @param name
     */
    void setWithinReplicationStatisticName(String name);
}
