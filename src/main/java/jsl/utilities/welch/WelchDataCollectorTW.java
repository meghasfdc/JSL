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
package jsl.utilities.welch;

import java.io.File;
import java.io.PrintWriter;
import jsl.modeling.ModelElement;
import jsl.modeling.elements.variable.TimeWeighted;
import jsl.observers.ModelElementObserver;
import jsl.utilities.reporting.JSL;
import jsl.utilities.statistic.Statistic;
import jsl.utilities.statistic.WeightedStatistic;

/**
 * Collects Welch data for a particular TimeWeighted response.
 *
 * The TimeWeighted variable is observed every delta T time units.

 The time average within each delta T time units is observed. Thus, each
 observation is an average over delta T and the number of potential
 observations will depend on the replication length divided by delta T.

 The user can specify the number of replications to be observed.

 The data is stored in an array. There are memory implications if there are a
 large number of observations and/or a large number of replications.

 The user specifies the delta T observation interval. The default is 10.0 time
 units.

 The user specifies the maximum number of observations to be collected for
 each replication. The default is 1000.

 If observations are not available to reach the maximum, then the values will
 be Double.NaN

 Example usage:

 WelchDataCollectorTW wdc = new WelchDataCollectorTW(10.0, 2000, 5);
 TimeWeighted tw = new TimeWeighted(this, "State Variable");
 tw.addObserver(wdc);
 *
 * @author rossetti
 */
public class WelchDataCollectorTW extends ModelElementObserver {

    private int myMaxNumObs;

    private int myMaxNumReps;

    private TimeWeighted myResponse;

    private double[][] myData;

    private int myObsCount = 0;

    private int myRepCount = 0;

    private double myDeltaT;

    private double myTotalArea;

    private WeightedStatistic myWithinRepStats;

    public WelchDataCollectorTW() {
        this(10.0, 1000, 0, null);
    }

    public WelchDataCollectorTW(double deltaT) {
        this(deltaT, 1000, 0, null);
    }

    public WelchDataCollectorTW(double deltaT, int maxNumObs) {
        this(deltaT, maxNumObs, 0, null);
    }

    public WelchDataCollectorTW(double deltaT, int maxNumObs, int maxNumReps) {
        this(deltaT, maxNumObs, maxNumReps, null);
    }

    public WelchDataCollectorTW(double deltaT, int maxNumObs, int maxNumReps, String name) {
        super(name);

        if (maxNumObs <= 0) {
            throw new IllegalArgumentException("The maximum number of observations must be > 0");
        }

        if (deltaT <= 0) {
            throw new IllegalArgumentException("The delta T (update interval) must be > 0");
        }

        myMaxNumObs = maxNumObs;
        myMaxNumReps = maxNumReps;
        setDeltaT(deltaT);
        myTotalArea = 0.0;
        myWithinRepStats = new WeightedStatistic(getName() + "shadow");

    }

    public final double getDeltaT() {
        return myDeltaT;
    }

    private void setDeltaT(double deltaT) {
        if (deltaT <= 0) {
            throw new IllegalArgumentException("The batching interval must be > 0");
        }
        myDeltaT = deltaT;
    }

    /**
     * Sets all the data to zero
     *
     */
    public final void clearData() {
        for (int r = 0; r < myData.length; r++) {
            for (int c = 0; c < myData[r].length; c++) {
                myData[r][c] = Double.NaN;
            }
        }
    }

    /**
     * Welch average is across each replication for each observation
     *
     * @return an array of the Welch averages
     */
    public final double[] getWelchAvg() {
        double[] w = new double[myMaxNumObs];
        Statistic s = new Statistic();
        for (int r = 0; r < myData.length; r++) {
            s.collect(myData[r]);
            w[r] = s.getAverage();
            s.reset();
        }
        return w;
    }

    /**
     * Gets an array that contains the cumulative average over the Welch
     * Averages
     *
     * @return returns an array that contains the cumulative average
     */
    public final double[] getCumAvg() {
        double[] cs = new double[myMaxNumObs];
        double[] w = getWelchAvg();
        Statistic s = new Statistic();
        for (int r = 0; r < myData.length; r++) {
            s.collect(w[r]);
            cs[r] = s.getAverage();
        }
        return cs;
    }

    /**
     *
     * @return a copy of the data
     */
    public final double[][] getData() {
        double[][] data = new double[myMaxNumObs][myMaxNumReps];
        for (int r = 0; r < myData.length; r++) {
            System.arraycopy(myData[r], 0, data[r], 0, myData[r].length);
        }
        return data;
    }

    /**
     * Makes the file containing the data
     *
     * @return
     */
    protected PrintWriter makeDataFile() {
        String directory = myResponse.getSimulation().getName();
        File subDirectory = JSL.makeOutputSubDirectory(directory);
        String fName = myResponse.getName() + "_WelchData";
        PrintWriter out = JSL.makePrintWriter(subDirectory, fName, "csv");

        for (int c = 0; c < myMaxNumReps; c++) {
            out.print("Rep" + (c + 1));
            out.print(",");
        }
        out.print("Avg");
        out.print(",");
        out.println("CumAvg");

        double[] w = getWelchAvg();
        double[] ca = getCumAvg();

        for (int r = 0; r < myData.length; r++) {
            for (int c = 0; c < myData[r].length; c++) {
                out.print(myData[r][c]);
                out.print(",");
            }
            out.print(w[r]);
            out.print(",");
            out.print(ca[r]);
            out.println();
        }
        return out;
    }

    @Override
    protected void beforeExperiment(ModelElement m, Object arg) {
        myResponse = (TimeWeighted) m;
        if (myResponse.getWarmUpOption()) {
            myResponse.setWarmUpOption(false);
        }
        myResponse.setTimedUpdateInterval(myDeltaT);
        myRepCount = 0;
        if (myMaxNumReps <= 0) {
            myMaxNumReps = myResponse.getExperiment().getNumberOfReplications();
        }
        myData = new double[myMaxNumObs][myMaxNumReps];
        clearData();
    }

    @Override
    protected void beforeReplication(ModelElement m, Object arg) {
        myObsCount = 0;
        myTotalArea = 0.0;
        myWithinRepStats.reset();
    }

    @Override
    protected void update(ModelElement m, Object arg) {
        double v = myResponse.getValue();
        double w = myResponse.getWeight();
//        System.out.println(v + "," + w);
        myWithinRepStats.collect(v, w);
    }

    @Override
    protected void timedUpdate(ModelElement m, Object arg) {
        if ((myObsCount < myMaxNumObs) && (myRepCount < myMaxNumReps)) {
            // get the underlying weighted statistic accumulator
            //WeightedStatisticIfc s = myResponse.getWithinReplicationStatistic();
            // computes the area within the timed update interval
            // current cumulative area minus cumulative area at last update
            double deltaArea = myWithinRepStats.getWeightedSum() - myTotalArea;
            // correct for warm up
            // remembers new cumulative area
            myTotalArea = myWithinRepStats.getWeightedSum();
            // computes and records the average for this update interval
            // area within update interval divided by update interval
            myData[myObsCount][myRepCount] = deltaArea / myDeltaT;
            myObsCount++;
        }
    }

    @Override
    protected void afterReplication(ModelElement m, Object arg) {
        myRepCount++;
    }

    @Override
    protected void afterExperiment(ModelElement m, Object arg) {
        if (myResponse.getWarmUpOption() == false) {
            myResponse.setWarmUpOption(true);
        }
        PrintWriter out = makeDataFile();
        out.close();
    }
}
