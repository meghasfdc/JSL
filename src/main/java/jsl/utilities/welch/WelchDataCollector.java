/*
 * Copyright (c) 2018. Manuel D. Rossetti, rossetti@uark.edu
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package jsl.utilities.welch;

import java.io.File;
import java.io.PrintWriter;
import jsl.modeling.ModelElement;
import jsl.modeling.elements.variable.ResponseVariable;
import jsl.observers.ModelElementObserver;
import jsl.utilities.reporting.JSL;
import jsl.utilities.statistic.Statistic;

/**
 * Collects Welch data for a particular ResponseVariable (each observation for
 * each replication)
 *
 * The data is stored in an array. There are memory implications if there are a
 * large number of observations.
 *
 * The user specifies the maximum number of observations to be collected for
 * each replication. If observations are not available, then the values will be
 * Double.NaN
 * 
 * Example usage:
 * 
 * WelchDataCollector wdc = new WelchDataCollector(2000, 5);
 * ResponseVariable r = new ResponseVariable(this, "Response");
 * r.addObserver(wdc);

 * @author rossetti
 */
public class WelchDataCollector extends ModelElementObserver {

    private int myMaxNumObs;

    private int myMaxNumReps;

    private ResponseVariable myResponse;

    private double[][] myData;

    private int myObsCount = 0;

    private int myRepCount = 0;

    public WelchDataCollector() {
        this(1000, 0, null);
    }

    public WelchDataCollector(int maxNumObs) {
        this(maxNumObs, 0, null);
    }

    public WelchDataCollector(int maxNumObs, int maxNumReps) {
        this(maxNumObs, maxNumReps, null);
    }

    public WelchDataCollector(int maxNumObs, int maxNumReps, String name) {
        super(name);

        if (maxNumObs <= 0) {
            throw new IllegalArgumentException("The maximum number of observations must be > 0");
        }

        myMaxNumObs = maxNumObs;
        myMaxNumReps = maxNumReps;

    }

    /**
     * Sets all the data to Double.NaN
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

    /** Columns are the replications, rows are the data
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
     *
     * @param repNum the replication number 1, 2, etc
     * @return the within replication data for the indicated replication
     */
    public final double[] getReplicationData(int repNum){
        if (repNum > myMaxNumReps){
            return new double[0];
        }
        double[] data = new double[myData.length];
        for(int r= 0; r< myData.length; r++){
            data[r] = myData[r][repNum-1];
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
        myResponse = (ResponseVariable)m;
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
    }

    @Override
    protected void update(ModelElement m, Object arg) {
        if ((myObsCount < myMaxNumObs) && (myRepCount < myMaxNumReps)) {
            myData[myObsCount][myRepCount] = myResponse.getValue();
            myObsCount++;
        }
    }

    @Override
    protected void afterReplication(ModelElement m, Object arg) {
        myRepCount++;
    }

    @Override
    protected void afterExperiment(ModelElement m, Object arg) {
        PrintWriter out = makeDataFile();
        out.close();
    }
}
