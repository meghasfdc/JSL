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
package jsl.utilities.statistic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import jsl.utilities.random.distributions.StudentT;

/** Implements the Standardized Time Series method as described on 
 *  page 534 of Law and Kelton, 3rd edition
 *  
 *
 */
public class StandardizedTimeSeriesStatistic extends AbstractStatistic {

    /** the default minimum number of observations
     *  per batch, the batch size
     */
    public static final int BATCH_SIZE = 1024;

    /** the size of the current batch
     */
    private int myBatchSize;

    private double myBSConstant;

    /** Number of batches
     */
    private double myNumBatches;

    /** collects the statistics
     */
    private Statistic myStatistic;

    /** collects the statistics
     */
    private Statistic myCurrentBatchStatistic;

    /** collects the across batch statistics
     */
    private Statistic myBMStatistic;

    /** the last observed value
     */
    private double myValue;

    /** the weight associated with the value
     */
    private double myWeight;

    /** A running total of STS Area Squared
     */
    private double mySTSAreaSquared;

    /** Holds the batch means
     */
    private List<Statistic> myBatchMeans;

    /** Creates an STS Statistic with the default batch size = 1024
     *  The batch means will not be saved
     *
     */
    public StandardizedTimeSeriesStatistic() {
        this(BATCH_SIZE, false, null, null);
    }

    /** Creates an STS Statistic with the default batch size = 1024
     *  The batch means will not be saved
     *
     * @param name 
     */
    public StandardizedTimeSeriesStatistic(String name) {
        this(BATCH_SIZE, false, name, null);
    }

    /** Creates an STS Statistic with the default batch size = 1024
     *  The batch means will not be saved
     * @param saveBMFlag Indicates whether or not the batch means will be saved, true
     *  means saved, false means not saved.  False is the default
     */
    public StandardizedTimeSeriesStatistic(boolean saveBMFlag) {
        this(BATCH_SIZE, saveBMFlag, null, null);
    }

    /** Creates an STS Statistic with the default batch size = 1024
     *  The batch means will not be saved
     * @param saveBMFlag Indicates whether or not the batch means will be saved, true
     *  means saved, false means not saved.  False is the default
     * @param values an array of values to collect statistics on
     */
    public StandardizedTimeSeriesStatistic(boolean saveBMFlag, double[] values) {
        this(BATCH_SIZE, saveBMFlag, null, values);
    }

    /** Creates an STS Statistic with the default batch size = 1024
     *  The batch means will not be saved
     * @param values an array of values to collect statistics on
     */
    public StandardizedTimeSeriesStatistic(double[] values) {
        this(BATCH_SIZE, false, null, values);
    }

    /** Creates an STS Statistic with the default batch size = 1024
     *  The batch means will not be saved
     * @param name The name
     * @param values an array of values to collect statistics on
     */
    public StandardizedTimeSeriesStatistic(String name, double[] values) {
        this(BATCH_SIZE, false, name, values);
    }

    /** Creates an STS Statistic with the given batchsize. The batch
     *  means will not be saved
     *
     * @param batchSize Must be &gt;= 2
     */
    public StandardizedTimeSeriesStatistic(int batchSize) {
        this(batchSize, false, null, null);
    }

    /** Creates an STS Statistic with the given batchsize. The batch
     *  means will not be saved
     *
     * @param batchSize Must be &gt;= 2
     * @param values an array of values to collect statistics on
     */
    public StandardizedTimeSeriesStatistic(int batchSize, double[] values) {
        this(batchSize, false, null, values);
    }

    /** Creates an STS Statistic with the given batch size and name.
     *  The batch means will not be save.
     *
     * @param batchSize Must be &gt;= 2
     * @param name The name
     */
    public StandardizedTimeSeriesStatistic(int batchSize, String name) {
        this(batchSize, false, name, null);
    }

    /** Creates an STS Statistic with the given batch size and name.
     *  The batch means will not be save.
     *
     * @param batchSize Must be &gt;= 2
     * @param name The name
     * @param values an array of values to collect statistics on
     */
    public StandardizedTimeSeriesStatistic(int batchSize, String name, double[] values) {
        this(batchSize, false, name, values);
    }

    /** Creates an STS Statistic with the given batch size.
     *
     * @param batchSize Must be &gt;=2
     * @param saveBMFlag Indicates whether or not the batch means will be saved, true
     *  means saved, false means not saved.  False is the default
     */
    public StandardizedTimeSeriesStatistic(int batchSize, boolean saveBMFlag) {
        this(batchSize, saveBMFlag, null, null);
    }

    /** Creates an STS Statistic with the given batch size.
     *
     * @param batchSize Must be &gt;=2
     * @param saveBMFlag Indicates whether or not the batch means will be saved, true
     *  means saved, false means not saved.  False is the default
     * @param values an array of values to collect statistics on
     */
    public StandardizedTimeSeriesStatistic(int batchSize, boolean saveBMFlag, double[] values) {
        this(batchSize, saveBMFlag, null, values);
    }

    /** Creates an STS Statistic with the given batch size.
     *
     * @param batchSize Must be &gt;=2
     * @param saveBMFlag Indicates whether or not the batch means will be saved, true
     *  means saved, false means not saved.  False is the default
     *  @param name The name of the statistic
     * @param values an array of values to collect statistics on
     */
    public StandardizedTimeSeriesStatistic(int batchSize, boolean saveBMFlag, String name, double[] values) {
        super(name);
        setBatchSize(batchSize);
        if (saveBMFlag == true) {
            myBatchMeans = new ArrayList<Statistic>();
        }
        mySTSAreaSquared = 0.0;
        myNumBatches = 0.0;
        myStatistic = new Statistic();
        myBMStatistic = new Statistic();
        myCurrentBatchStatistic = new Statistic();
        if (values != null) {
            for (double x : values) {
                collect(x);
            }
        }
    }

    /** Gets the number of batches
     * @return Returns the number of batches
     */
    public final double getNumBatches() {
        return myNumBatches;
    }

    @Override
    public final boolean collect(double value, double weight) {
        if (isTurnedOff()){
            return false;
        }
        
        if (Double.isNaN(value)) {
            myNumMissing++;
            return true;
        }

        if (getSaveDataOption()) {
            saveData(value, weight);
        }

        myValue = value;
        myWeight = weight;
        myStatistic.collect(myValue, myWeight);
        myCurrentBatchStatistic.collect(myValue, myWeight);

        if (myCurrentBatchStatistic.getCount() == myBatchSize) {
            // count the batch
            myNumBatches = myNumBatches + 1.0;

            // collect the batch statistics
            myBMStatistic.collect(myCurrentBatchStatistic.getWeightedAverage());

            // compute the STS area
            double a = myCurrentBatchStatistic.getObsWeightedSum() - myBSConstant * myCurrentBatchStatistic.getSum();
            mySTSAreaSquared = mySTSAreaSquared + a * a;

            // record the current batch mean
            if (myBatchMeans != null) {
                // save the current batch statistic
                myBatchMeans.add(myCurrentBatchStatistic);
                // create a new statistic for next batch
                myCurrentBatchStatistic = new Statistic();
            } else // clear the current batch statistic for use collecting the next batch
            {
                myCurrentBatchStatistic.reset();
            }
        }
        return true;
    }

    @Override
    public final void reset() {
        myNumMissing = 0.0;
        mySTSAreaSquared = 0.0;
        myNumBatches = 0.0;
        myStatistic.reset();
        myBMStatistic.reset();
        myCurrentBatchStatistic.reset();
        if (myBatchMeans != null) {
            myBatchMeans.clear();
        }
        clearSavedData();
    }

    @Override
    public final double getAverage() {
        return (myBMStatistic.getAverage());
    }

    @Override
    public final double getCount() {
        return (myBMStatistic.getCount());
    }

    @Override
    public final double getSum() {
        return (myBMStatistic.getSum());
    }

    @Override
    public final double getWeightedSum() {
        return (myBMStatistic.getWeightedSum());
    }

    @Override
    public final double getWeightedSumOfSquares() {
        return (myBMStatistic.getWeightedSumOfSquares());
    }

    @Override
    public final double getSumOfWeights() {
        return (myBMStatistic.getSumOfWeights());
    }

    @Override
    public final double getWeightedAverage() {
        return (myBMStatistic.getWeightedAverage());
    }

    @Override
    public final double getDeviationSumOfSquares() {
        return (myBMStatistic.getDeviationSumOfSquares());
    }

    @Override
    public final double getVariance() {
        return (myBMStatistic.getVariance());
    }

    @Override
    public final double getStandardDeviation() {
        return (myBMStatistic.getStandardDeviation());
    }

    @Override
    public final double getHalfWidth(double alpha) {
        return (myBMStatistic.getHalfWidth(alpha));
    }

    @Override
    public final double getMin() {
        return (myBMStatistic.getMin());
    }

    @Override
    public final double getMax() {
        return (myBMStatistic.getMax());
    }

    @Override
    public final double getLastValue() {
        return (myBMStatistic.getLastValue());
    }

    @Override
    public final double getLastWeight() {
        return (myBMStatistic.getLastWeight());
    }

    @Override
    public final double getKurtosis() {
        return (myBMStatistic.getKurtosis());
    }

    @Override
    public final double getSkewness() {
        return (myBMStatistic.getSkewness());
    }

    @Override
    public final double getStandardError() {
        return (myBMStatistic.getStandardError());
    }

    @Override
    public final double getLag1Correlation() {
        return (myBMStatistic.getLag1Correlation());
    }

    @Override
    public final double getLag1Covariance() {
        return (myBMStatistic.getLag1Covariance());
    }

    @Override
    public final double getVonNeumannLag1TestStatistic() {
        return (myBMStatistic.getVonNeumannLag1TestStatistic());
    }

    @Override
    public final double getVonNeumannLag1TestStatisticPValue() {
        return myBMStatistic.getVonNeumannLag1TestStatisticPValue();
    }

    @Override
    public final int getLeadingDigitRule(double a) {
        return myBMStatistic.getLeadingDigitRule(a);
    }

    /** Returns an iterator across all the batch means collected so far
     *
     * @return
     */
    public final Iterator<Statistic> getBatchMeansIterator() {
        if (myBatchMeans != null) {
            return (myBatchMeans.iterator());
        } else {
            return (new ArrayList<Statistic>().iterator());
        }
    }

    /** Gets the standard error for the STS
     *  confidence interval
     *
     * @return
     */
    public final double getSTSStandardError() {
        double b = myBatchSize;
        double k = getNumBatches();
        double n = b * k;
        double a = getSTSAreaConstant();
        return (Math.sqrt(a / (n * k)));
    }

    /** Computes the half-width of a STS based
     *  confidence interval based on a 95% confidence level
     *
     * @return
     */
    public final double getSTSHalfWidth() {
        return getSTSHalfWidth(getConfidenceLevel());
    }

    /** Computes the half-width of a STS based
     *  confidence interval based on the supplied
     *  confidence coefficient.
     *
     * @param level 
     * @return
     */
    public final double getSTSHalfWidth(double level) {
        if (getNumBatches() <= 1.0) {
            return (Double.NaN);
        }
        double dof = getNumBatches();
        double alpha = 1.0 - level;
        double p = 1.0 - alpha/2.0;
        double t = StudentT.getInvCDF(dof, p);
        double hw = t * getSTSStandardError();
        return (hw);
    }

    /** Computes an estimate of the STS Area Constant
     *
     * @return
     */
    public final double getSTSAreaConstant() {

        double d = (myBatchSize * myBatchSize * myBatchSize - myBatchSize);
        if (d == 0.0) {
            return (Double.POSITIVE_INFINITY);
        } else {
            return ((12.0 * mySTSAreaSquared) / d);
        }
    }

    /** Gets a StatisticAccessIfc that represents the across
     *  batch statistics
     *
     * @return Returns the statistic.
     */
    public final StatisticAccessorIfc getAcrossBatchStatistic() {
        return myBMStatistic;
    }

    /** Gets a StatisticAccessIfc for the current batch that is
     *  being collected. This is a statistic on the data within
     *  the batch
     *
     * @return Returns the statistic.
     */
    public final StatisticAccessorIfc getCurrentBatchStatistic() {
        return myCurrentBatchStatistic;
    }

    /** Gets a StatisticAccessorIfc that represents statistics
     *  that have been collected on each unbatched data point
     *  This is the same as using a Statistic on the data
     *
     * @return Returns the statistic.
     */
    public final StatisticAccessorIfc getUnBatchedStatistic() {
        return myStatistic;
    }

    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("---------------\n");
        sb.append("STS Batch Means Statistics: \n");
        sb.append("---------------\n");
        sb.append(myBMStatistic.toString());
        sb.append("---------------\n");
        sb.append("STS Statistics:\n");
        sb.append("---------------\n");
        sb.append("Batch size: ").append(myBatchSize).append("\n");
        sb.append("Number of batches: ").append(getNumBatches()).append("\n");
        sb.append("STS Total Data: ").append(myBatchSize * getNumBatches()).append("\n");
        sb.append("STS Area Constant: ").append(getSTSAreaConstant()).append("\n");
        sb.append("STS Standard Error: ").append(getSTSStandardError()).append("\n");
        sb.append("STS Half-Width: ").append(getSTSHalfWidth()).append("\n");
        sb.append("---------------\n");
        sb.append("Statistics on last not included batch:\n");
        sb.append("---------------\n");
        sb.append(myCurrentBatchStatistic.toString());
        sb.append("---------------\n");
        sb.append("Standard Statistics on unbatched data\n");
        sb.append("---------------\n");
        sb.append(myStatistic.toString()).append("\n");

        return (sb.toString());
    }

    /** Sets the number of batches, must be &gt;=2
     * @param batchSize The number of batches to set
     */
    protected final void setBatchSize(int batchSize) {
        if (batchSize <= 1) {
            throw new IllegalArgumentException("Number of batches must be >= 2");
        }

        myBatchSize = batchSize;
        myBSConstant = (myBatchSize + 1.0) / 2.0;
    }

    /*
    
    double[] xb = new double[7];
    
    double[] x = {0.0, 1.699767841, 0.722316832, 2.244257308, 3.27647354, 3.198350219,
    3.733133104, -0.18358764, 1.765818757, 3.095022526, 0.913299351,
    1.30979584, 0.309567673, 0.153089109, 1.022370503, 1.226492946,
    -0.117931217, 1.432075128, 1.595952431, 2.134853053, 1.634507049,
    1.67300937, 1.629759486, 3.342641553, 1.914715545, 1.813842351,
    1.486792603, 3.972211976, 2.865672973, 4.375654731, 1.345093329};
    
     */
    public static void main(String args[]) {

        StandardizedTimeSeriesStatistic stat = new StandardizedTimeSeriesStatistic(2, true);

        double[] xb = new double[4];

        double[] x = {0.0, 5.0, 3.0, 10.0, 2.0, 4.0, 2.0};

        for (int i = 1; i <= 6; i++) {
            stat.collect(x[i]);
        }

        System.out.println(stat);

        Iterator<Statistic> it = stat.getBatchMeansIterator();
        int jj = 1;
        while (it.hasNext()) {
            Statistic c = (Statistic) it.next();
            xb[jj] = c.getAverage();
            jj++;
            System.out.println(c);
        }

        double sum = 0.0;
        int k = 3;
        int b = 2;
        for (int j = 1; j <= k; j++) {
            System.out.println("xb[" + j + "]= " + xb[j]);
            double s2 = 0.0;
            for (int s = 1; s <= b; s++) {
                for (int i = 1; i <= s; i++) {
                    s2 = s2 + (xb[j] - x[i + (j - 1) * b]);
                }
            }
            sum = sum + s2 * s2;
        }

        System.out.println("sum = " + sum);
        double a = (12.0 * sum) / (b * b * b - b);
        System.out.println("A = " + a);

        double se = Math.sqrt(a / (k * b * k));
        System.out.println("se = " + se);
    }
}
