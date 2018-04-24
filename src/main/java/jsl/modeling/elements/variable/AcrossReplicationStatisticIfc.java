package jsl.modeling.elements.variable;

import jsl.utilities.statistic.StatisticAccessorIfc;

public interface AcrossReplicationStatisticIfc {

    /** Returns a StatisticAccessorIfc for the across replication statistics
     *  that have been collected on this Counter
     * 
     * @return
     */
    public StatisticAccessorIfc getAcrossReplicationStatistic();

    /** A convenience method to get the across replication average from
     *  the underlying CounterObserver For other statistics use
     *  getAcrossReplicationStatistic()
     * 
     * @return
     */
    public double getAcrossReplicationAverage();

    /**
     * @param name
     */
    public void setAcrossReplicationStatisticName(String name);
}