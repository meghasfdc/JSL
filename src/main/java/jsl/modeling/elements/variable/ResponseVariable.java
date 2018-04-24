/*
 * Copyright (c) 2007, Manuel D. Rossetti (rossetti@uark.edu)
 *
 * Contact:
 *	Manuel D. Rossetti, Ph.D., P.E.
 *	Department of Industrial Engineering
 *	University of Arkansas
 *	4207 Bell Engineering Center
 *	Fayetteville, AR 72701
 *	Phone: (479) 575-6756
 *	Email: rossetti@uark.edu
 *	Web: www.uark.edu/~rossetti
 *
 * This file is part of the JSL (a Java Simulation Library). The JSL is a framework
 * of Java classes that permit the easy development and execution of discrete event
 * simulation programs.
 *
 * The JSL is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * The JSL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the JSL (see file COPYING in the distribution);
 * if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor,
 * Boston, MA  02110-1301  USA, or see www.fsf.org
 *
 */
package jsl.modeling.elements.variable;

import jsl.modeling.ExperimentGetIfc;
import jsl.modeling.ModelElement;
import jsl.utilities.statistic.Statistic;
import jsl.utilities.statistic.StatisticAccessorIfc;
import jsl.utilities.statistic.WeightedStatistic;
import jsl.utilities.statistic.WeightedStatisticIfc;

/**
 *
 */
public class ResponseVariable extends Variable implements
        DefaultReportingOptionIfc, ResponseStatisticsIfc {

    /**
     * indicates the count when the simulation should stop *
     */
    protected long myCountStopLimit = 0;

    /**
     * Can be used by the reports to indicate whether or not the response should
     * appear The default is true
     *
     */
    protected boolean myDefaultReportingOption = true;

    /**
     * The within replication statistics for the response variable
     *
     */
    protected WeightedStatistic myWithinRepStats;

    /**
     * The across replication statistics for the response variable
     *
     */
    protected Statistic myAcrossRepStats;

    /**
     * Used to collect across replication averages over the maximum of the
     * response variable
     *
     */
    protected ResponseVariable myMaxResponse;

    /**
     * The within interval statistics for the response variable
     *
     */
    protected WeightedStatistic myWithinIntervalStats;

    /**
     * For collecting across interval statistics
     *
     */
    protected ResponseVariable myAcrossIntervalResponse;

    /**
     * Time of last update interval
     */
    protected double myLastUpdateTime;

    /**
     * The time of the warm up if it occurs, 0.0 otherwise
     */
    protected double myTimeOfWarmUp;

    /**
     * Creates a ResponseVariable with the given parent with initial value 0.0
     * over the range [Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY]
     *
     * @param parent the variable's parent model element.
     */
    public ResponseVariable(ModelElement parent) {
        this(parent, 0.0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, null);
    }

    /**
     * Creates a ResponseVariable with the given name and initial value over the
     * range [Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY]
     *
     * @param parent the variable's parent model element.
     * @param initialValue The initial value of the variable.
     */
    public ResponseVariable(ModelElement parent, double initialValue) {
        this(parent, initialValue, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, null);
    }

    /**
     * Creates a ResponseVariable with the given name and initial value, 0.0,
     * over the range [Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY]
     *
     * @param parent the variable's parent model element.
     * @param name The name of the variable.
     */
    public ResponseVariable(ModelElement parent, String name) {
        this(parent, 0.0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, name);
    }

    /**
     * Creates a ResponseVariable with the given name and initial value over the
     * supplied range The default range is [Double.NEGATIVE_INFINITY,
     * Double.POSITIVE_INFINITY]
     *
     * @param parent the variable's parent model element
     * @param initialValue The initial value of the variable.
     * @param name The name of the variable.
     */
    public ResponseVariable(ModelElement parent, double initialValue, String name) {
        this(parent, initialValue, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, name);
    }

    /**
     * Creates a ResponseVariable with the given name and initial value over the
     * supplied range [lowerLimit, Double.POSITIVE_INFINITY]
     *
     * @param parent the variable's parent model element
     * @param initialValue The initial value of the variable.
     * @param lowerLimit the lower limit on the range for the variable, must be
     * &lt; upperLimit
     * @param name The name of the variable.
     */
    public ResponseVariable(ModelElement parent, double initialValue, double lowerLimit, String name) {
        this(parent, initialValue, lowerLimit, Double.POSITIVE_INFINITY, name);
    }

    /**
     * Creates a ResponseVariable with the initial value over the supplied range
     * [lowerLimit, Double.POSITIVE_INFINITY]
     *
     * @param parent the variable's parent model element
     * @param initialValue The initial value of the variable.
     * @param lowerLimit the lower limit on the range for the variable, must be
     * &lt; upperLimit
     */
    public ResponseVariable(ModelElement parent, double initialValue, double lowerLimit) {
        this(parent, initialValue, lowerLimit, Double.POSITIVE_INFINITY, null);
    }

    /**
     * Creates a ResponseVariable with the initial value over the supplied range
     * [lowerLimit, upperLimit]
     *
     * @param parent the variable's parent model element
     * @param initialValue The initial value of the variable.
     * @param lowerLimit the lower limit on the range for the variable, must be
     * &lt; upperLimit
     * @param upperLimit the upper limit on the range for the variable
     */
    public ResponseVariable(ModelElement parent, double initialValue, double lowerLimit, double upperLimit) {
        this(parent, initialValue, lowerLimit, upperLimit, null);
    }

    /**
     * Creates a ResponseVariable with the given name and initial value over the
     * supplied range [lowerLimit, upperLimit]
     *
     * @param parent the variable's parent model element
     * @param initialValue The initial value of the variable. Must be within the
     * range.
     * @param lowerLimit the lower limit on the range for the variable, must be
     * &lt; upperLimit
     * @param upperLimit the upper limit on the range for the variable
     * @param name The name of the variable.
     */
    public ResponseVariable(ModelElement parent, double initialValue, double lowerLimit, double upperLimit, String name) {
        super(parent, initialValue, lowerLimit, upperLimit, name);
        myWithinRepStats = new WeightedStatistic(getName());
        myLastUpdateTime = 0.0;
        myTimeOfWarmUp = 0.0;
    }

    /**
     * Allows for the collection of across replication statistics on the average
     * maximum number observed
     *
     */
    public final void turnOnAcrossReplicationMaxCollection() {
        if (myMaxResponse == null) {
            myMaxResponse = new ResponseVariable(this, getName() + ":Max");
        }
    }

    /**
     * If the time interval collection is turned on a ResponseVariable is
     * created for capturing statistics across the intervals. This returns this
     * value or null if time interval collection has not been turned on
     *
     * @return the response or null
     */
    public final ResponseVariable getAcrossIntervalResponse() {
        return myAcrossIntervalResponse;
    }

    /**
     * Turns on the collection of statistics across intervals of time, defined
     * by the interval length
     *
     * @param interval
     */
    public final void turnOnTimeIntervalCollection(double interval) {
        setTimedUpdateInterval(interval);
        if (myAcrossIntervalResponse == null) {
            String s = String.format("_DT(%d)", (int) interval);
            myAcrossIntervalResponse = new ResponseVariable(this, getName() + s);
            myWithinIntervalStats = new WeightedStatistic();
        }
    }

    /**
     * Turns on tracing to a file of the time interval response if and only if
     * time interval collection has been turned on
     *
     */
    public final void turnOnTimeIntervalTrace() {
        if (myAcrossIntervalResponse != null) {
            myAcrossIntervalResponse.turnOnTrace();
        }
    }

    /**
     * Turns on tracing to a file of the time interval response if and only if
     * time interval collection has been turned on
     *
     * @param header true means include the header
     */
    public final void turnOnTimeIntervalTrace(boolean header) {
        if (myAcrossIntervalResponse != null) {
            myAcrossIntervalResponse.turnOnTrace(header);
        }
    }

    /**
     * Turns on tracing to a file of the time interval response if and only if
     * time interval collection has been turned on
     *
     * @param fileName the name of the file to write the trace
     */
    public final void turnOnTimeIntervalTrace(String fileName) {
        if (myAcrossIntervalResponse != null) {
            myAcrossIntervalResponse.turnOnTrace(fileName);
        }
    }

    /**
     * Turns on tracing to a file of the time interval response if and only if
     * time interval collection has been turned on
     *
     * @param fileName the name of the file to write the trace
     * @param header true means include the header
     */
    public final void turnOnTimeIntervalTrace(String fileName, boolean header) {
        if (myAcrossIntervalResponse != null) {
            myAcrossIntervalResponse.turnOnTrace(fileName, header);
        }
    }

    @Override
    protected void replicationEnded() {
        super.replicationEnded();
        if (myMaxResponse != null) {
            double max = getWithinReplicationStatistic().getMax();
            myMaxResponse.setValue(max);
        }
    }

    /**
     * Sets the default reporting option. True means the response will appear on
     * default reports
     *
     * @param flag
     */
    @Override
    public void setDefaultReportingOption(boolean flag) {
        myDefaultReportingOption = flag;
    }

    /**
     * Returns the default reporting option. True means that the response should
     * appear on the default reports
     *
     * @return
     */
    @Override
    public boolean getDefaultReportingOption() {
        return myDefaultReportingOption;
    }

    /**
     * Every Variable must implement the getValue method. This method simply
     * returns the value of the variable.
     *
     * @return The value of the variable.
     */
    @Override
    public double getValue() {
        return (myValue);
    }

    /**
     * Gets the within replication statistics
     *
     * @return
     */
    @Override
    public final WeightedStatisticIfc getWithinReplicationStatistic() {
        return myWithinRepStats;
    }

    @Override
    public void setWithinReplicationStatisticName(String name) {
        myWithinRepStats.setName(name);
    }

    public void resetWithinReplicationStatistics() {
        myWithinRepStats.reset();
    }

    @Override
    protected void removedFromModel() {
        super.removedFromModel();
        myWithinRepStats = null;
        myAcrossRepStats = null;
        myWithinIntervalStats = null;
        myAcrossIntervalResponse = null;
    }

    @Override
    public void setValue(double value) {
        super.setValue(value);
        collectStatistics();
        if (myCountStopLimit > 0) { // a limit has been set, so check it
            if (myWithinRepStats.getCount() >= (double) myCountStopLimit) {
                stopExecutive();
            }
        }
    }

    protected void collectStatistics() {
        myWithinRepStats.collect(getValue(), getWeight());
        if (myWithinIntervalStats != null) {
            myWithinIntervalStats.collect(getValue(), getWeight());
        }
    }

    @Override
    protected void timedUpdate() {
        if (myWithinIntervalStats != null) {
            if (!((myLastUpdateTime < myTimeOfWarmUp) && (myTimeOfWarmUp < getTime()))) {
                // if the warm up did not occur during the interval, then collect the average
                myAcrossIntervalResponse.setValue(myWithinIntervalStats.getAverage());
            }
            myWithinIntervalStats.reset();
        }
        myLastUpdateTime = getTime();
    }

    /**
     * Sets the count limit for determining when the count based stopping should
     * occur If countLimit is less than or equal to current count when set then
     * the action will occur the next time that the variable changes
     *
     * @param countLimit must be &gt;=0, zero implies no count limit
     */
    public final void setCountBasedStopLimit(long countLimit) {
        if (countLimit < 0) {
            throw new IllegalArgumentException("Count Limit must be >= 0.");
        }

        myCountStopLimit = countLimit;
    }

    /**
     * Gets the stopping limit based on counts, zero means no limit set
     *
     * @return the stopping limit for observations
     */
    public final long getCountStopLimit() {
        return myCountStopLimit;
    }

    @Override
    protected void beforeExperiment() {
        super.beforeExperiment();
        myWithinRepStats.reset();
        if (myAcrossRepStats != null) {
            myAcrossRepStats.reset();
        }
        if (myWithinIntervalStats != null) {
            myWithinIntervalStats.reset();
        }
        myLastUpdateTime = 0.0;
        myTimeOfWarmUp = 0.0;
    }

    @Override
    protected void beforeReplication() {
        super.beforeReplication();
        myWithinRepStats.reset();
        if (myWithinIntervalStats != null) {
            myWithinIntervalStats.reset();
        }
        myLastUpdateTime = 0.0;
        myTimeOfWarmUp = 0.0;
    }

    @Override
    protected void warmUp() {
        super.warmUp();
        myTimeOfWarmUp = getTime();
        myWithinRepStats.reset();
    }

    @Override
    protected void afterReplication() {
        super.afterReplication();
        ExperimentGetIfc e = getExperiment();
        if (e != null) {
            if (e.getNumberOfReplications() >= 1) {
                if (myAcrossRepStats == null) {
                    myAcrossRepStats = new Statistic(getName());
                }
                myAcrossRepStats.collect(myWithinRepStats.getAverage());
            }
        }

    }

    /**
     * Gets a copy of the statistics that have been accumulated across all
     * replications for this variable.
     *
     * @return A StatisticAccessorIfc representing the across replication
     * statistics.
     */
    @Override
    public final StatisticAccessorIfc getAcrossReplicationStatistic() {
        if (myAcrossRepStats == null) {
            myAcrossRepStats = new Statistic(getName());
        }
        return myAcrossRepStats;
    }

    /**
     * A convenience method to set the name of the underlying Statistic for
     * tabulating across replication statistics
     *
     * @param name
     */
    @Override
    public final void setAcrossReplicationStatisticName(String name) {
        if (myAcrossRepStats == null) {
            myAcrossRepStats = new Statistic();
        }

        myAcrossRepStats.setName(name);
    }

    @Override
    public String asString(){
        StringBuilder sb = new StringBuilder();
        sb.append(toString());
        sb.append(myWithinRepStats.toString());
        return sb.toString();
    }

    @Override
    public double getAcrossReplicationAverage() {
        if (myAcrossRepStats == null) {
            myAcrossRepStats = new Statistic(getName());
        }
        return myAcrossRepStats.getAverage();
    }

}
