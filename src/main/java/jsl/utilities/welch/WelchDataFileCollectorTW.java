/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsl.utilities.welch;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jsl.modeling.ModelElement;
import jsl.utilities.statistic.WeightedStatistic;

/**
 * Makes files related to Welch Data Analysis for a TimeWeighted variable
 *
 * The "wdf" extension is used to indicate a Welch Data File, which is a file
 * containing each observation for each replication of the simulation run. The
 * observations are written sequentially using writeDouble()
 *
 * The "wdfmd" extension is used to indicate a text file holding the meta data
 * for the Welch plot analysis. This includes the number of observations in each
 * replication and the number of replications. The first line of the file is the
 * number of replications. Each following line is the number of observations for
 * each replication.
 *
 * The TimeWeighted variable is observed every delta T time units.
 *
 * The time average within each delta T time units is observed. Thus, each
 * observation is an average over delta T and the number of potential
 * observations will depend on the replication length divided by delta T.
 *
 * The user specifies the delta T observation interval. The default is 10.0 time
 * units.
 *
 * Example usage:
 *
 * TimeWeighted tw = new TimeWeighted(this, "State Variable");
 * WelchDataFileCollectorTW wdc = new WelchDataFileCollectorTW(10.0, "fileName"); 
 * tw.addObserver(wdc);
 *
 * @author rossetti
 */
public class WelchDataFileCollectorTW extends WelchDataFileCollector {

    private double myDeltaT;

    private double myTotalArea;

    private WeightedStatistic myWithinRepStats;

    public WelchDataFileCollectorTW(String name) {
        this(10.0, null, name);
    }

    public WelchDataFileCollectorTW(double deltaT, String name) {
        this(deltaT, null, name);
    }

    public WelchDataFileCollectorTW(File directory, String name) {
        this(10.0, directory, name);
    }

    public WelchDataFileCollectorTW(double deltaT, File directory, String name) {
        super(directory, name);
        setDeltaT(deltaT);
        myWithinRepStats = new WeightedStatistic(getName() + "shadow");
    }

    private void setDeltaT(double deltaT) {
        if (deltaT <= 0) {
            throw new IllegalArgumentException("The batching interval must be > 0");
        }
        myDeltaT = deltaT;
    }

    public final double getDeltaT() {
        return myDeltaT;
    }

    @Override
    protected void beforeExperiment(ModelElement m, Object arg) {
        super.beforeExperiment(m, arg);
        myResponse.setTimedUpdateInterval(getDeltaT());
    }

    @Override
    protected void beforeReplication(ModelElement m, Object arg) {
        super.beforeReplication(m, arg);
        myTotalArea = 0.0;
        myWithinRepStats.reset();
    }

    @Override
    protected void update(ModelElement m, Object arg) {
        double v = myResponse.getValue();
        double w = myResponse.getWeight();
        //System.out.println(v + "," + w);
        myWithinRepStats.collect(v, w);
    }

    @Override
    protected void timedUpdate(ModelElement m, Object arg) {
        // get the underlying weighted statistic accumulator
       // WeightedStatisticIfc s = myResponse.getWithinReplicationStatistic();
        // computes the area within the timed update interval
        // current cumulative area minus cumulative area at last update
        double deltaArea = myWithinRepStats.getWeightedSum() - myTotalArea;
        // remembers new cumulative area
        myTotalArea = myWithinRepStats.getWeightedSum();
        // computes and records the average for this update interval
        // area within update interval divided by update interval
        try {
            double obs = deltaArea / getDeltaT();
            myStats.collect(obs);
            myData.writeDouble(obs);
        } catch (IOException ex) {
            Logger.getLogger(WelchDataFileCollector.class.getName()).log(Level.SEVERE, null, ex);
        }
        myObsCount++;

    }

}
