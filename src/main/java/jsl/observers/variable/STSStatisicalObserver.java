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
import jsl.modeling.elements.variable.ResponseVariable;
import jsl.observers.ModelElementObserver;
import jsl.utilities.reporting.JSL;
import jsl.utilities.statistic.StandardizedTimeSeriesStatistic;
import jsl.utilities.statistic.Statistic;

/**
 *
 */
public class STSStatisicalObserver extends ModelElementObserver {

    protected StandardizedTimeSeriesStatistic myStatistic;

    protected Statistic myAcrossRepStat;

    protected ResponseVariable myResponseVariable;

    protected Model myModel;

    /**
     *
     */
    public STSStatisicalObserver() {
        this(StandardizedTimeSeriesStatistic.BATCH_SIZE, null);
    }

    /**
     * @param name the name of statistic
     */
    public STSStatisicalObserver(String name) {
        this(StandardizedTimeSeriesStatistic.BATCH_SIZE, name);
    }

    /**
     * @param batchSize the batch size
     * @param name the name
     */
    public STSStatisicalObserver(int batchSize, String name) {
        super(name);
        myStatistic = new StandardizedTimeSeriesStatistic(batchSize);
        myAcrossRepStat = new Statistic();
    }

    public void resetStatistics() {
        myStatistic.reset();
    }

    public String toString() {

        StringBuffer sb = new StringBuffer();

        if (myStatistic != null) {
            sb.append("-------------------------------------------------\n");
            sb.append("STS Statistic:\n");
            sb.append("-------------------------------------------------\n");
            sb.append(myStatistic);
            sb.append("-------------------------------------------------\n");
            sb.append("\n");
        }

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
        myResponseVariable = (ResponseVariable) m;
        myModel = myResponseVariable.getModel();
        myStatistic.setName("STS Stat " + myResponseVariable.getStringLabel());
        myAcrossRepStat.setName("Across Rep Stat " + myResponseVariable.getStringLabel());
        myAcrossRepStat.reset();
        resetStatistics();
    }

    protected void beforeReplication(ModelElement m, Object arg) {
        resetStatistics();
    }

    protected void initialize(ModelElement m, Object arg) {
    }

    protected void warmUp(ModelElement m, Object arg) {
        resetStatistics();
    }

    protected void update(ModelElement m, Object arg) {
        myResponseVariable = (ResponseVariable) m;
        myModel = myResponseVariable.getModel();
        myStatistic.collect(myResponseVariable.getValue());
    }

    protected void afterReplication(ModelElement m, Object arg) {
        myAcrossRepStat.collect(myStatistic.getWeightedAverage());
    }

    protected void afterExperiment(ModelElement m, Object arg) {
//		JSL.out.println(toString());
    }

    protected void removedFromModel(ModelElement m, Object arg) {
        myStatistic = null;
        myAcrossRepStat = null;
        myResponseVariable = null;
        myModel = null;
    }
}
