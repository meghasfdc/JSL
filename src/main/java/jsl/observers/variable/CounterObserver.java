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
package jsl.observers.variable;

import jsl.modeling.Model;
import jsl.modeling.ModelElement;
import jsl.modeling.elements.variable.Counter;
import jsl.observers.ModelElementObserver;
import jsl.utilities.reporting.JSL;
import jsl.utilities.statistic.*;

/**
 *
 */
public class CounterObserver extends ModelElementObserver {

    /** The Counter that is being observed
     */
    protected Counter myCounter;

    /** The Model that holds the Counter being observed
     */
    protected Model myModel;

    /** Statistic used to collect the across replication statistics on the final
     *  value of the counter during each replication
     */
    protected Statistic myAcrossRepStat;

    /** A Counter can have a timed update action during a replication
     *  This Statistic collects the average of the total count
     *  during each timed update interval.  For example, a timed
     *  update interval of 1 hour can be set.  Each hour this statistic
     *  will observe the count for that hour and collect averages across
     *  the timede update intervals
     */
    protected Statistic myAcrossTimedUpdateStatistic;

    /** If the timed update statistics are collected during a replication
     *  This Statistic will be used to collect the average across the
     *  averages over the update intervals
     */
    protected Statistic myAcrossRepTimedUpdateStatistic;

    /**
     *
     */
    public CounterObserver() {
        this(null);
    }

    /**
     * @param name the name
     */
    public CounterObserver(String name) {
        super(name);
        myAcrossRepStat = new Statistic();
    }

    /** Resets any statistics collected across the timed update intervals
     *  for within a replication
     */
    public final void resetTimedUpdateStatistics() {

        if (myAcrossTimedUpdateStatistic != null) {
            myAcrossTimedUpdateStatistic.reset();
        }
    }

    /** Returns a StatisticAccessorIfc for the statistics collected across
     *  timed update intervals within a replication.
     *
     * @return Returns the statistic
     */
    public final StatisticAccessorIfc getAcrossTimedUpdateStatistic() {
        return myAcrossTimedUpdateStatistic;
    }

    /** Returns a StatisticAccessorIfc for the statistics collected across
     *  timed update intervals across the replications
     *
     * @return Returns the statistic
     */
    public final StatisticAccessorIfc getAcrossRepTimedUpdateStatistic() {
        return myAcrossRepTimedUpdateStatistic;
    }

    /** Gets the statistics that have been accumulated across all replications
     *  for this counter.
     *
     * @return A StatisticAccessorIfc representing the across replication statistics.
     */
    public final StatisticAccessorIfc getAcrossReplicationStatistic() {
        return (myAcrossRepStat);
    }

    /** A convenience method to set the name of the underlying Statistic
     *  for tabulating across replication statistics
     *
     * @param name the name
     */
    public void setAcrossReplicationStatisticName(String name) {
        myAcrossRepStat.setName(name);
    }

    public String toString() {

        StringBuffer sb = new StringBuffer();

        if (myAcrossRepStat.getCount() >= 2.0) {
            sb.append("-------------------------------------------------\n");
            sb.append("Across Replication Statistic:\n");
            sb.append("-------------------------------------------------\n");
            sb.append(myAcrossRepStat);
            sb.append("-------------------------------------------------\n");
            sb.append("\n");
        }

        return (sb.toString());
    }

    protected void beforeExperiment(ModelElement m, Object arg) {
        myCounter = (Counter) m;
        myModel = myCounter.getModel();
        myAcrossRepStat.setName("Across Rep Stat " + myCounter.getStringLabel());
        myAcrossRepStat.reset();
        resetTimedUpdateStatistics();
        if (myCounter.getTimedUpdateInterval() > 0) { // if timed update option is on, create the statistic for collecting on the intervals
            // counter uses a statistic to collect across timed updates
            myAcrossTimedUpdateStatistic = new Statistic("AcrossTimedUpdate Stat " + myCounter.getStringLabel());
            myAcrossRepTimedUpdateStatistic = new Statistic("AcrossRepTimedUpdate Stat " + myCounter.getStringLabel());
        }
    }

    protected void beforeReplication(ModelElement m, Object arg) {
        myCounter = (Counter) m;
        myModel = myCounter.getModel();
        resetTimedUpdateStatistics();
    }

    protected void initialize(ModelElement m, Object arg) {
        myCounter = (Counter) m;
        myModel = myCounter.getModel();
        resetTimedUpdateStatistics();
    }

    protected void warmUp(ModelElement m, Object arg) {
        resetTimedUpdateStatistics();
    }

    protected void timedUpdate(ModelElement m, Object arg) {
        // determine the count since last timed update
        if (myAcrossTimedUpdateStatistic != null) {
            Counter c = (Counter) m;
            double count = c.getTotalDuringTimedUpdate();
            myAcrossTimedUpdateStatistic.collect(count);
        }
    }

    protected void afterReplication(ModelElement m, Object arg) {
        Counter c = (Counter) m;
        myAcrossRepStat.collect(c.getValue());
        if (myAcrossRepTimedUpdateStatistic != null) {
            myAcrossRepTimedUpdateStatistic.collect(myAcrossTimedUpdateStatistic.getAverage());
        }
    }

    protected void afterExperiment(ModelElement m, Object arg) {
//	    if(myAcrossTimedUpdateStatistic != null){
//	    	JSL.out.println("Timed update results for Counter .....");
//	    	JSL.out.println(myAcrossRepTimedUpdateStatistic);
//	    }
        //JSL.out.println(toString());
    }

    protected void removedFromModel(ModelElement m, Object arg) {
        myCounter = null;
        myAcrossRepStat = null;
        myAcrossTimedUpdateStatistic = null;
        myAcrossRepTimedUpdateStatistic = null;
        myModel = null;
    }
}
