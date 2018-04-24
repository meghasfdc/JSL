/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsl.utilities.welch;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import jsl.modeling.ModelElement;
import jsl.modeling.elements.variable.ResponseVariable;
import jsl.observers.ModelElementObserver;
import jsl.utilities.reporting.JSL;
import static jsl.utilities.reporting.JSL.LOGGER;
import jsl.utilities.statistic.Statistic;

/**
 * Makes files related to Welch Data Analysis.
 *
 * The "wdf" extension is used to indicate a Welch Data File, which is a
 * RandomAccessFile containing each observation for each replication of the
 * simulation run. The observations are written sequentially using writeDouble()
 *
 * The "wdfmd" extension is used to indicate a text file holding the meta data
 * for the Welch plot analysis. This includes the number of observations in each
 * replication and the number of replications. The first line of the file is the
 * number of replications. Each following line is the number of observations for
 * each replication.
 *
 * @author rossetti
 */
public class WelchDataFileCollector extends ModelElementObserver {
    
    protected ResponseVariable myResponse;
    
    protected File myDataFile;
    
    protected File myMetaDataFile;
    
    protected RandomAccessFile myData;//TODO does not have to be RandomAccessFile

    protected PrintWriter myMetaData;

    /**
     * Counts the observations for a replication
     */
    protected long myObsCount = 0;
    
    private File myDirectory;
    
    private String myFileName;

    /**
     * Holds the number of observations for each of the replications zero is the
     * first replication
     */
    private ArrayList<Long> myObsCounts;

    /**
     * Holds the average time between observations
     */
    private ArrayList<Double> myTimePerObs;

    /**
     * Used to collect the average for each replication
     */
    protected Statistic myStats;

    /**
     * holds the replication averages
     */
    private ArrayList<Double> myAvgs;
    
    public WelchDataFileCollector(String name) {
        this(null, name);
    }
    
    public WelchDataFileCollector(File directory, String name) {
        myObsCounts = new ArrayList<>();
        myTimePerObs = new ArrayList<>();
        myAvgs = new ArrayList<>();
        myStats = new Statistic();
        myDirectory = directory;
        myFileName = name;
        myDataFile = JSL.makeFile(directory, name, "wdf");
        myMetaDataFile = JSL.makeFile(directory, name, "wdfmd");
        myMetaData = JSL.makePrintWriter(myMetaDataFile);
        try {
            myData = new RandomAccessFile(myDataFile, "rw");
        } catch (IOException ex) {
            String str = "Problem creating RandomAccessFile for " + myDataFile.getAbsolutePath();
            LOGGER.log(Level.SEVERE, str, ex);
        }
    }

    /**
     * The directory for the files
     *
     * @return
     */
    public File getDirectory() {
        return myDirectory;
    }

    /**
     * The base file name for the files
     *
     * @return
     */
    public String getFileName() {
        return myFileName;
    }

    /**
     * Makes a WelchDataFileAnalyzer based on the file in this collector
     *
     * @return
     */
    public WelchDataFileAnalyzer makeWelchDataFileAnalyzer() {
        return new WelchDataFileAnalyzer(getDataFile(), getMetaDataFile());
    }

    /**
     * The file made for the raw data
     *
     * @return
     */
    public File getDataFile() {
        return myDataFile;
    }

    /**
     * The file handle for the meta data file. The meta data file contains the
     * number of replications as the first line, and the number of observations
     * in each of the replications as the subsequent lines
     *
     * @return
     */
    public File getMetaDataFile() {
        return myMetaDataFile;
    }

    /**
     * The number of observations in each replication returned as an array. 0
     * element is the first replication count
     *
     * @return
     */
    public long[] getObservationCounts() {
        long[] c = new long[myObsCounts.size()];
        int i = 0;
        for (Long n : myObsCounts) {
            c[i] = n;
            i++;
        }
        return c;
    }

    /**
     * The average time between observations in each replication returned as an
     * array. 0 element is the first replication
     *
     * @return
     */
    public double[] getTimePerObservation() {
        double[] c = new double[myTimePerObs.size()];
        int i = 0;
        for (Double t : myTimePerObs) {
            c[i] = t;
            i++;
        }
        return c;
    }

    /**
     * The number of replications observed
     *
     * @return
     */
    public int getNumberOfReplications() {
        return myObsCounts.size();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("-------------------");
        sb.append(System.lineSeparator());
        sb.append("Welch Data File Collector");
        sb.append(System.lineSeparator());
        sb.append("Meta Data File : ");
        sb.append(myMetaDataFile.getName());
        sb.append(System.lineSeparator());
        sb.append("Data file: ");
        sb.append(myDataFile.getName());
        sb.append(System.lineSeparator());
        sb.append("Number of replications: ");
        sb.append(getNumberOfReplications());
        sb.append(System.lineSeparator());
        sb.append("Count per replication: ");
        sb.append(Arrays.toString(getObservationCounts()));
        sb.append(System.lineSeparator());
        sb.append("Time per Observation per replication: ");
        sb.append(Arrays.toString(getTimePerObservation()));
        sb.append(System.lineSeparator());
        sb.append("-------------------");
        sb.append(System.lineSeparator());
        return sb.toString();
    }
    
    @Override
    protected void beforeExperiment(ModelElement m, Object arg) {
        myResponse = (ResponseVariable) m;
        if (myResponse.getWarmUpOption()) {
            myResponse.setWarmUpOption(false);
        }
        myObsCounts.clear();
        myTimePerObs.clear();
        myAvgs.clear();
        myStats.reset();
    }
    
    @Override
    protected void beforeReplication(ModelElement m, Object arg) {
        myObsCount = 0;
        myStats.reset();
    }
    
    @Override
    protected void update(ModelElement m, Object arg) {
        double obs = myResponse.getValue();
        try {
            myData.writeDouble(obs);
        } catch (IOException ex) {
            Logger.getLogger(WelchDataFileCollector.class.getName()).log(Level.SEVERE, null, ex);
        }
        myStats.collect(obs);
        myObsCount++;
    }
    
    @Override
    protected void afterReplication(ModelElement m, Object arg) {
        myObsCounts.add(myObsCount);
        double t = m.getTime();
        if (myObsCount > 0) {
            myTimePerObs.add(t / myObsCount);
            myAvgs.add(myStats.getAverage());
        } else {
            myTimePerObs.add(Double.NaN);
            myAvgs.add(Double.NaN);
        }
    }
    
    @Override
    protected void afterExperiment(ModelElement m, Object arg) {
        if (myResponse.getWarmUpOption() == false) {
            myResponse.setWarmUpOption(true);
        }
        myMetaData.println(myObsCounts.size());
        for (int i = 0; i < myObsCounts.size(); i++) {
            myMetaData.print(myObsCounts.get(i));
            myMetaData.print(",");
            myMetaData.print(myTimePerObs.get(i));
            myMetaData.print(",");
            myMetaData.println(myAvgs.get(i));
        }
        myMetaData.close();
        try {
            myData.close();
        } catch (IOException ex) {
            Logger.getLogger(WelchDataFileCollector.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
