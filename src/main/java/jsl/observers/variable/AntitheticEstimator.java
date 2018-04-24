/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 *
 * Copyright (c) Manuel D. Rossetti (rossetti@uark.edu)
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

import jsl.modeling.Experiment;
import jsl.modeling.ExperimentGetIfc;
import jsl.modeling.ModelElement;
import jsl.modeling.elements.variable.ResponseVariable;
import jsl.observers.ModelElementObserver;
import jsl.utilities.reporting.JSL;
import jsl.utilities.statistic.Statistic;
import jsl.utilities.statistic.WeightedStatisticIfc;

/**
 * The purpose of this class is to observe response variables in order to form
 * pairs across replications and to compute statistics across the pairs.
 *
 * If Y(1), Y(2), .., Y(j), .., Y(n) represent observations from the jth
 * replication, then this class averages adjacent pairs
 *
 * X(1) = (Y(1)+Y(2))/2, X(2) = (Y(3)+Y(4))/2, for m = floor(n/2) pairs
 *
 * X(i) = (Y(2j-1) + Y(2j))/2 for j = 1, 2, 3, ... floor(n/2)
 *
 * If the experiment has been set to control the streams using antithetic
 * streams for odd and even replications, then the resulting estimate from this
 * class will implement the variance reduction technique called antithetic
 * variates.
 *
 * The class is designed as an observer that can be attached to individual
 * response variables
 *
 * @author rossetti
 */
public class AntitheticEstimator extends ModelElementObserver {

    private Statistic myStat;

    private double myOdd;

    private ResponseVariable myResponse;

    private ExperimentGetIfc myExp;

    public AntitheticEstimator() {
        this(null);
    }

    public AntitheticEstimator(String name) {
        super(name);
        myStat = new Statistic();
    }

    /**
     * Returns the experiment for the simulation
     *
     * @return the experiment
     */
    protected final ExperimentGetIfc getExperiment() {
        return myExp;
    }

    @Override
    protected void beforeExperiment(ModelElement m, Object arg) {
        myResponse = (ResponseVariable) m;
        myExp = m.getExperiment();
        myStat.setName("Antithetic Estimator for " + myResponse.getName());
        if (myExp.getAntitheticOption() != true) {
            StringBuilder sb = new StringBuilder();
            sb.append("The antithetic option is not on. \n");
            sb.append("And there were AntitheticEstimator instances used.");
            JSL.LOGGER.warning(sb.toString());
        }
    }

    @Override
    protected void replicationEnded(ModelElement m, Object arg) {
        if ((getExperiment().getCurrentReplicationNumber() % 2) == 0) {
            // get the even replication average
            WeightedStatisticIfc s = myResponse.getWithinReplicationStatistic();
            double myEven = s.getAverage();
            // collect the average of the pair
            myStat.collect((myEven + myOdd)/2.0);
        } else {
            // remember the odd replication
            WeightedStatisticIfc s = myResponse.getWithinReplicationStatistic();
            myOdd = s.getAverage();
        }
    }
    
    public final Statistic getStatistic(){
        return myStat.newInstance();
    }
}
