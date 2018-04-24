/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsl.utilities.welch;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import jsl.observers.ObservableComponent;
import jsl.observers.ObservableIfc;
import jsl.observers.ObserverIfc;
import jsl.utilities.math.JSLMath;
import jsl.utilities.reporting.JSL;
import jsl.utilities.statistic.Statistic;
import static jsl.utilities.reporting.JSL.LOGGER;
import jsl.utilities.statistic.BatchStatistic;

/**
 * This class knows how to process data collected by the WelchDataFileCollector
 * class and produce "Welch Data". That is for every observation, this file will
 * average across the replications and compute the average across the
 * replications and compute the cumulative sum over the averages.
 *
 * It can make "wpdf" files which are binary DataOutputStream files holding the
 * welch average and the cumulative average for each of the observations.
 *
 * It can make a csv file that holds the welch average and cumulative average
 *
 * It can open and display a Welch plot chart via JavaFX.
 * 
 * An Observer can be attached.  It will be notified when a call to get() 
 * occurs. getLastDataPoint(), getLastObservationIndex(), and getLastReplicationIndex()
 * can be used by the observer to determine the value, observation number,
 * and replication of the observation after notification.
 *
 * @author rossetti
 */
public class WelchDataFileAnalyzer implements ObservableIfc {

    public static final int NUMBYTES = 8;

    public static final int MIN_BATCH_SIZE = 10;

    protected File myDataFile;

    protected File myMetaDataFile;

    protected long[] myObsCounts;

    protected double[] myTimePerObs;

    protected double[] myRepAvgs;

    protected long myMinObsCount;

    protected RandomAccessFile myData;

    protected Statistic myAcrossRepStat;

    protected double[] myRowData;

    protected DataInputStream myWelchPlotData;

    protected File myDir;

    protected String myBaseName;

    protected ObservableComponent myObsComponent;

    private double myLastDataPoint = Double.NaN;

    private long myLastObsIndex = Long.MIN_VALUE;

    private long myLastRepIndex = Long.MIN_VALUE;

    public WelchDataFileAnalyzer(File dataFile, File metaDataFile) {
        if (dataFile == null) {
            throw new IllegalArgumentException("The supplied data file was null");
        }

        if (metaDataFile == null) {
            throw new IllegalArgumentException("The supplied meta data file was null");
        }

        myObsComponent = new ObservableComponent();

        // need to check if extensions are correct
        // check if both files have the same name w/o extension
        String name = dataFile.getName();
        String[] s = name.split(Pattern.quote("."));

        if (!s[1].equals("wdf")) {
            throw new IllegalArgumentException("The data file was not type wdf");
        }

        String mname = metaDataFile.getName();
        String[] m = mname.split(Pattern.quote("."));

        if (!m[1].equals("wdfmd")) {
            throw new IllegalArgumentException("The data file was not type wdfmd");
        }

        if (!s[0].equals(m[0])) {
            throw new IllegalArgumentException("The files do not have the same base name");
        }
        myBaseName = s[0];
        myDataFile = dataFile;
        myMetaDataFile = metaDataFile;
        myDir = myDataFile.getParentFile();
        // open the meta data for reading
        readMetaData();
        try {
            myData = new RandomAccessFile(myDataFile, "r");
        } catch (IOException ex) {
            String str = "Problem creating RandomAccessFile for " + myDataFile.getAbsolutePath();
            LOGGER.log(Level.SEVERE, str, ex);
        }
        myMinObsCount = JSLMath.getMin(myObsCounts);
        myAcrossRepStat = new Statistic();
        myRowData = new double[myObsCounts.length];

    }

    @Override
    public void addObserver(ObserverIfc observer) {
        myObsComponent.addObserver(observer);
    }

    @Override
    public void deleteObserver(ObserverIfc observer) {
        myObsComponent.deleteObserver(observer);
    }

    @Override
    public void deleteObservers() {
        myObsComponent.deleteObservers();
    }

    @Override
    public boolean contains(ObserverIfc observer) {
        return myObsComponent.contains(observer);
    }

    @Override
    public int countObservers() {
        return myObsComponent.countObservers();
    }

    /**
     * Returns the last data point read or Double.NaN if none read. Can be used
     * by Observers when data is read.
     *
     * @return
     */
    public double getLastDataPoint() {
        return myLastDataPoint;
    }

    /**
     * Makes a file and writes out the welch data to the DataOutputStream This
     * produces a file with the "wpdf" extension. All observations are written
     *
     * @return
     */
    public File makeWelchPlotDataFile() {
        return makeWelchPlotDataFile(myMinObsCount);
    }

    /**
     * Makes a file and writes out the welch data to the DataOutputStream This
     * produces a file with the "wpdf" extension.
     *
     * @param numObs number of observations to write out
     * @return
     */
    public File makeWelchPlotDataFile(long numObs) {
        File wpdf = JSL.makeFile(myDir, myBaseName, "wpdf");
        try {
            FileOutputStream fout = new FileOutputStream(wpdf);
            DataOutputStream out = new DataOutputStream(fout);
            writeWelchPlotData(out, numObs);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WelchDataFileAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WelchDataFileAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return wpdf;
    }

    /**
     * Writes out the welch plot data, xbar, cumxbar to the supplied
     * DataOutputStream. The file is flushed and closed.
     *
     * @param out
     * @param numObs number of observations to write out
     * @throws IOException
     */
    public void writeWelchPlotData(DataOutputStream out, long numObs) throws IOException {
        if (out == null) {
            throw new IllegalArgumentException("The DataOutputStream was null");
        }
        long n = Math.min(numObs, myMinObsCount);
        Statistic s = new Statistic();
        for (long i = 1; i <= n; i++) {
            double x = getAcrossReplicationAverage(i);
            s.collect(x);
            out.writeDouble(x);
            out.writeDouble(s.getAverage());
        }
        out.flush();
        out.close();
    }

    /**
     * Makes and writes out the welch plot data
     *
     * @return
     */
    public File makeCSVWelchPlotDataFile() {
        return makeCSVWelchPlotDataFile(myMinObsCount);
    }

    /**
     * Makes and writes out the welch plot data
     *
     * @param numObs number of observations to write
     * @return
     */
    public File makeCSVWelchPlotDataFile(long numObs) {
        File wpdf = JSL.makeFile(myDir, myBaseName, "csv");
        PrintWriter pw = JSL.makePrintWriter(wpdf);
        try {
            writeCSVWelchPlotData(pw, numObs);
        } catch (IOException ex) {
            Logger.getLogger(WelchDataFileAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return wpdf;
    }

    /**
     * Writes out all of the observations to the supplied PrintWriter This
     * results in a comma separated value file that has x_bar and cum_x_bar
     * where x_bar is the average across the replications
     *
     * @param out
     * @throws IOException
     */
    public void writeCSVWelchPlotData(PrintWriter out) throws IOException {
        writeCSVWelchPlotData(out, myMinObsCount);
    }

    /**
     * Writes out the number of observations to the supplied PrintWriter This
     * results in a comma separated value file that has x_bar and cum_x_bar
     * where x_bar is the average across the replications. The file is flushed
     * and closed.
     *
     * @param out
     * @param numObs
     * @throws IOException
     */
    public void writeCSVWelchPlotData(PrintWriter out, long numObs) throws IOException {
        if (out == null) {
            throw new IllegalArgumentException("The PrintWriter was null");
        }
        long n = Math.min(numObs, myMinObsCount);
        Statistic s = new Statistic();
        for (long i = 1; i <= n; i++) {
            double x = getAcrossReplicationAverage(i);
            s.collect(x);
            out.print(x);
            out.print(",");
            out.println(s.getAverage());
        }
        out.flush();
        out.close();
    }

    /**
     * Returns an array of the Welch averages. Since the number of observations
     * in the file may be very large, this may have memory implications.
     *
     * @param numObs
     * @return
     * @throws IOException
     */
    public double[] getWelchAverages(int numObs) throws IOException {
        int n;
        if (numObs <= myMinObsCount) {
            n = numObs;
        } else {
            n = Math.toIntExact(myMinObsCount);
        }
        double[] x = new double[n];
        for (int i = 1; i <= n; i++) {
            x[i - 1] = getAcrossReplicationAverage(i);
        }
        return x;
    }

    /**
     * Creates a BatchStatistic that batches the Welch averages according to the
     * batching parameters. Uses the number of observations via
     * getMinNumObservationsInReplications() to determine the number of batches
     * based on MIN_BATCH_SIZE. No data is deleted.
     *
     * @return
     * @throws IOException
     */
    public BatchStatistic batchWelchAverages() throws IOException {
        return batchWelchAverages(0, MIN_BATCH_SIZE);
    }

    /**
     * Creates a BatchStatistic that batches the Welch averages according to the
     * batching parameters. Uses the number of observations via
     * getMinNumObservationsInReplications() to determine the number of batches
     * based on MIN_BATCH_SIZE.
     *
     * @param deletePt the number of observations to delete at beginning of
     * series
     * @return
     * @throws IOException
     */
    public BatchStatistic batchWelchAverages(int deletePt) throws IOException {
        return batchWelchAverages(deletePt, MIN_BATCH_SIZE);
    }

    /**
     * Creates a BatchStatistic that batches the Welch averages according to the
     * batching parameters. Uses the number of observations via
     * getMinNumObservationsInReplications() to determine the number of batches
     * based on the supplied batch size.
     *
     * @param deletePt the number of observations to delete at beginning of
     * series
     * @param minBatchSize the size of the batches, must be GT 1
     * @return
     * @throws IOException
     */
    public BatchStatistic batchWelchAverages(int deletePt, int minBatchSize) throws IOException {
        if (minBatchSize <= 1) {
            throw new IllegalArgumentException("Batch size must be >= 2");
        }
        long n = getMinNumObservationsInReplications();
        long k = n / minBatchSize;
        int minNumBatches = Math.toIntExact(k);
        return batchWelchAverages(deletePt, minNumBatches, minBatchSize, 2);
    }

    /**
     * Creates a BatchStatistic that batches the Welch averages according to the
     * batching parameters. If the minNumBatches x minBatchSize = number of
     * observations then the maxNBMultiple does not matter. Uses a batch
     * multiple of 2.
     *
     * @param deletePt the number of observations to delete at beginning of
     * series
     * @param minNumBatches
     * @param minBatchSize
     * @return
     * @throws IOException
     */
    public BatchStatistic batchWelchAverages(int deletePt, int minNumBatches, int minBatchSize) throws IOException {
        return batchWelchAverages(deletePt, minNumBatches, minBatchSize, 2);
    }

    /**
     * Creates a BatchStatistic that batches the Welch averages according to the
     * batching parameters. If the minNumBatches x minBatchSize = number of
     * observations then the maxNBMultiple does not matter.
     *
     * @param deletePt the number of observations to delete at beginning of
     * series
     * @param minNumBatches
     * @param minBatchSize
     * @param maxNBMultiple
     * @return
     * @throws IOException
     */
    public BatchStatistic batchWelchAverages(int deletePt, int minNumBatches,
            int minBatchSize, int maxNBMultiple) throws IOException {
        if (deletePt < 0) {
            deletePt = 0;
        }
        int k = deletePt + 1;
        BatchStatistic b = new BatchStatistic(minNumBatches, minBatchSize, maxNBMultiple);
        for (long i = k; i <= myMinObsCount; i++) {
            b.collect(getAcrossReplicationAverage(i));
        }
        return b;
    }

    /**
     * Gets an array of the partial sum process for the provided data Based on
     * page 2575 Chapter 102 Nelson Handbook of Industrial Engineering,
     * Quantitative Methods in Simulation for producing a partial sum plot The
     * batch means array is used as the data
     *
     * @param bm The BatchStatistic
     * @return
     */
    public static double[] getPartialSums(BatchStatistic bm) {
        if (bm == null) {
            throw new IllegalArgumentException("The BatchStatistic was null");
        }
        double avg = bm.getAverage();
        double[] data = bm.getBatchMeanArrayCopy();
        return getPartialSums(avg, data);
    }

    /**
     * Gets an array of the partial sum process for the provided data Based on
     * page 2575 Chapter 102 Nelson Handbook of Industrial Engineering,
     * Quantitative Methods in Simulation for producing a partial sum plot
     *
     * @param avg the average of the supplied data array
     * @param data
     * @return
     */
    public static double[] getPartialSums(double avg, double[] data) {
        if (data == null) {
            throw new IllegalArgumentException("The data array was null");
        }
        int n = data.length;
        double[] s = new double[n + 1];
        if (n == 1) {
            s[0] = 0.0;
            s[1] = 0.0;
            return s;
        }
        // first pass computes cum sums
        s[0] = 0.0;
        for (int j = 1; j <= n; j++) {
            s[j] = s[j - 1] + data[j - 1];
        }
        // second pass computes partial sums
        for (int j = 1; j <= n; j++) {
            s[j] = j * avg - s[j];
        }
        return s;
    }

    /**
     * Uses the batch means array from the BatchStatistic to compute the
     * positive bias test statistic
     *
     * @param bm
     * @return
     */
    public static double getPositiveBiasTestStatistic(BatchStatistic bm) {
        if (bm == null) {
            throw new IllegalArgumentException("The BatchStatistic was null");
        }
        double[] data = bm.getBatchMeanArrayCopy();
        return getPositiveBiasTestStatistic(data);
    }

    /**
     * Computes initialization bias (positive) test statistic based on algorithm
     * on page 2580 Chapter 102 Nelson Handbook of Industrial Engineering,
     * Quantitative Methods in Simulation
     *
     * @param data
     * @return test statistic to be compared with F distribution
     */
    public static double getPositiveBiasTestStatistic(double[] data) {
        if (data == null) {
            throw new IllegalArgumentException("The data array was null!");
        }
        int n = data.length / 2;
        double[] x1 = Arrays.copyOfRange(data, 0, n);
        double[] x2 = Arrays.copyOfRange(data, n + 1, 2 * n);
        Statistic s = new Statistic();
        s.collect(x1);
        double a1 = s.getAverage();
        s.reset();
        s.collect(x2);
        double a2 = s.getAverage();
        int mi1 = Statistic.getIndexOfMax(x1);
        double max1 = Statistic.getMax(x1);
        int mi2 = Statistic.getIndexOfMax(x2);
        double max2 = Statistic.getMax(x2);
        double num = mi2 * (n - mi2) * max1 * max1;
        double denom = mi1 * (n - mi1) * max2 * max2;
        if (max2 == 0.0) {
            return Double.NaN;
        }
        if (denom == 0.0) {
            return Double.NaN;
        }
        double f = num / denom;
        return f;
    }

    /**
     * Uses the batch means array from the BatchStatistic to compute the
     * positive bias test statistic
     *
     * @param bm
     * @return
     */
    public static double getNegativeBiasTestStatistic(BatchStatistic bm) {
        if (bm == null) {
            throw new IllegalArgumentException("The BatchStatistic was null");
        }
        double[] data = bm.getBatchMeanArrayCopy();
        return getNegativeBiasTestStatistic(data);
    }

    /**
     * Computes initialization bias (negative) test statistic based on algorithm
     * on page 2580 Chapter 102 Nelson Handbook of Industrial Engineering,
     * Quantitative Methods in Simulation
     *
     * @param data
     * @return test statistic to be compared with F distribution
     */
    public static double getNegativeBiasTestStatistic(double[] data) {
        if (data == null) {
            throw new IllegalArgumentException("The data array was null!");
        }
        int n = data.length / 2;
        double[] x1 = Arrays.copyOfRange(data, 0, n);
        double[] x2 = Arrays.copyOfRange(data, n + 1, 2 * n);
        Statistic s = new Statistic();
        s.collect(x1);
        double a1 = s.getAverage();
        s.reset();
        s.collect(x2);
        double a2 = s.getAverage();
        int mi1 = Statistic.getIndexOfMin(x1);
        double min1 = Statistic.getMin(x1);
        int mi2 = Statistic.getIndexOfMin(x2);
        double min2 = Statistic.getMin(x2);
        double num = mi2 * (n - mi2) * min1 * min1;
        double denom = mi1 * (n - mi1) * min2 * min2;
        if (min2 == 0.0) {
            return Double.NaN;
        }
        if (denom == 0.0) {
            return Double.NaN;
        }
        double f = num / denom;
        return f;
    }

    /**
     * The number of observations in each replication
     *
     * @return
     */
    public long[] getObservationCounts() {
        return Arrays.copyOf(myObsCounts, myObsCounts.length);
    }

    /**
     * Returns the average amount of time taken per observation in each of the
     * replications
     *
     * @return
     */
    public double[] getTimePerObservation() {
        return Arrays.copyOf(myTimePerObs, myTimePerObs.length);
    }

    /**
     * Returns the average within each replication. That is, the average of the
     * observations within each replication. zero is the first replication
     *
     * @return
     */
    public double[] getReplicationAverages() {
        return Arrays.copyOf(myRepAvgs, myRepAvgs.length);
    }

    /**
     * The average time between observations in the simulation across all the
     * replications. This can be used to determine a warmup period in terms of
     * time.
     *
     * @return
     */
    public double getAverageTimePerObservation() {
        return Statistic.collectStatistics(myTimePerObs).getAverage();
    }

    /**
     * The number of observations across the replications
     *
     * @return
     */
    public long getMinNumObservationsInReplications() {
        return myMinObsCount;
    }

    /**
     * Computes and returns the across replication average for ith row of
     * observations
     *
     * @param i
     * @return
     * @throws IOException
     */
    public double getAcrossReplicationAverage(long i) throws IOException {
        myAcrossRepStat.reset();
        myAcrossRepStat.collect(getAcrossReplicationData(i, myRowData));
        return myAcrossRepStat.getAverage();
    }

    /**
     * Fills the supplied array with a row of observations across the
     * replications
     *
     * @param i
     * @param x
     * @return
     * @throws IOException
     */
    public double[] getAcrossReplicationData(long i, double[] x) throws IOException {
        if (x == null) {// make it if it is not supplied
            x = new double[myObsCounts.length];
        }
        if (x.length != myObsCounts.length) {
            throw new IllegalArgumentException("The supplied array's length was not " + myObsCounts.length);
        }
        if (i > getMinNumObservationsInReplications()) {
            throw new IllegalArgumentException("The desired row is larger than " + getMinNumObservationsInReplications());
        }
        for (int j = 1; j <= x.length; j++) {
            x[j - 1] = get(i, j);
        }
        return x;
    }

    /**
     * The number of replications
     *
     * @return
     */
    public int getNumberOfReplications() {
        return myObsCounts.length;
    }

    /**
     * Returns the ith observation in the jth replication
     *
     * @param i
     * @param j
     * @return
     * @throws IOException
     */
    public double get(long i, int j) throws IOException {
        setPosition(i, j);
        return get();
    }

    /**
     * Returns the value at the current position
     *
     * @return
     * @throws IOException
     */
    public double get() throws IOException {
        myLastDataPoint = myData.readDouble();
        myObsComponent.notifyObservers(this, null);
        return myLastDataPoint;
    }

    /**
     * Moves the file pointer to the position associated with the ith
     * observation at in the jth replication
     *
     * @param i
     * @param j
     * @throws IOException
     */
    public void setPosition(long i, int j) throws IOException {
        myData.seek(getPosition(i, j));
    }

    /**
     * Gets the position in the file relative to the beginning of the file of
     * the ith observation in the jth replication. This assumes that the data is
     * a double 8 bytes stored in column major form
     *
     * @param i the index to the ith observation
     * @param j the index to the jth replication
     * @return
     */
    public long getPosition(long i, int j) {
        if ((i < 1) || (j < 1) || (j > myObsCounts.length) || (i > myObsCounts[j - 1])) {
            throw new IllegalArgumentException("Invalid observation# or replication#");
        }
        myLastObsIndex = i;
        myLastRepIndex = j;
        long pos = 0;
        for (int n = 0; n < j - 1; n++) {
            pos = pos + myObsCounts[n];
        }
        pos = pos + (i - 1);
        return pos * NUMBYTES;
    }

    /** Returns the last observation index asked for.  Can be used by observers
     *  Returns Integer.MIN_VALUE if no observations have been read
     * @return 
     */
    public final long getLastObservationIndex() {
        return myLastObsIndex;
    }

    /** Returns the last replication index asked for.  Can be used by observers
     *  Returns Integer.MIN_VALUE if no observations have been read
     * @return 
     */
    public final long getLastReplicationIndex() {
        return myLastRepIndex;
    }

    /**
     * Creates a wpdf file and opens up a JavaFX window with a chart displaying
     * the Welch plot. Shows all data in the file.
     *
     */
    public void displayWelchChart() {
        displayWelchChart(myMinObsCount);
    }

    /**
     * Creates a wpdf file and opens up a JavaFX window with a chart displaying
     * the Welch plot
     *
     * @param numObs the number of observations to be on the chart
     */
    public void displayWelchChart(long numObs) {
        File wpdf = makeWelchPlotDataFile(numObs);
        displayWelchChart(wpdf, numObs);
    }

    /**
     * Opens up a JavaFX window with a chart displaying the Welch plot
     *
     * @param file must be a file of type wpdf made by a WelchDataCollector
     * @param numObs the number of observations to be on the chart
     */
    public static void displayWelchChart(File file, long numObs) {
        String[] args = new String[2];
        String canonicalPath = null;
        try {
            canonicalPath = file.getCanonicalPath();
            args[0] = canonicalPath;
            args[1] = Long.toString(numObs);
            // (new Thread(new WelchChartRunnable(args))).start();
            WelchChart.launchWelchChart(args);
        } catch (IOException ex) {
            Logger.getLogger(WelchDataFileAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
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
        sb.append("Average time per observation: ");
        sb.append(getAverageTimePerObservation());
        sb.append(System.lineSeparator());
        return sb.toString();
    }

    private void readMetaData() {
        try {
            String theLine;
            try (BufferedReader br = new BufferedReader(new FileReader(myMetaDataFile))) {
                int i = 0;
                boolean first = true;
                while ((theLine = br.readLine()) != null) {
                    if (first) {
                        int n = Integer.parseInt(theLine);
                        myObsCounts = new long[n];
                        myTimePerObs = new double[n];
                        myRepAvgs = new double[n];
                        first = false;
                    } else {
                        String[] s = theLine.split(",");
                        long v = Long.parseLong(s[0]);
                        myObsCounts[i] = v;
                        double t = Double.parseDouble(s[1]);
                        myTimePerObs[i] = t;
                        double x = Double.parseDouble(s[2]);
                        myRepAvgs[i] = x;
                        i++;
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WelchDataFileAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WelchDataFileAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void main(String args[]) {
        double[] y = {1.0, 2.0, 3.0, 4.0, 5.0};
        Statistic stat = new Statistic(y);
        double avg = stat.getAverage();
        double[] partialSums = getPartialSums(avg, y);
        System.out.println("avg = " + avg);
        System.out.println(Arrays.toString(partialSums));
    }
}
