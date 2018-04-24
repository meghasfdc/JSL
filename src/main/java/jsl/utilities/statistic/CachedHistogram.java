/*
 * Created on Jan 7, 2007
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

import jsl.utilities.random.distributions.Exponential;
import jsl.utilities.random.distributions.Normal;

/**
 * A CachedHistogram allow collection and forming of a histogram without
 * pre-specifying the number of bins. It works by using an initial cache of the
 * data to determine a reasonable number of bins and bin width based on the
 * observed minimum and maximum of the data within the cache. Once the cache is
 * observed, this class works essentially like a Histogram, which can be
 * returned via the getHistogram() method.
 *
 *
 */
public class CachedHistogram extends AbstractStatistic {

    public static final int DEFAULT_CACHE_SIZE = 100;

    /**
     * The number of bins for the histogram
     */
    protected int myNumBins;

    /**
     * Flag indicating the histogram is caching values to compute adequate
     * range.
     */
    protected boolean myCachingFlag = true;

    /**
     * Counts number of observations in the cache
     */
    protected int myCountCache = 0;

    /**
     * Cache for accumulated values.
     */
    protected double[] myDataCache;

    /**
     * Cache for accumulated weights
     */
    protected double[] myWeightCache;

    /**
     * Collects statistical information on the data during caching
     */
    protected Statistic myCacheStatistic;

    /**
     * Collects histogram statistics after caching is done
     */
    protected Histogram myHistogram;

    /**
     * Creates a CachedHistogram using the DEFAULT_CACHE_SIZE by determining a
     * reasonable number of bins
     */
    public CachedHistogram() {
        this(DEFAULT_CACHE_SIZE, 0, null);
    }

    /**
     * Creates a CachedHistogram using the DEFAULT_CACHE_SIZE by determining a
     * reasonable number of bins
     *
     * @param name
     */
    public CachedHistogram(String name) {
        this(DEFAULT_CACHE_SIZE, 0, name);
    }

    /**
     * Creates a CachedHistogram by determining a reasonable number of bins
     *
     * @param cacheSize The size of the cache for initializing the histogram
     * @param name
     */
    public CachedHistogram(int cacheSize, String name) {
        this(cacheSize, 0, name);
    }

    /**
     * Creates a CachedHistogram by determining a reasonable number of bins
     *
     * @param cacheSize The size of the cache for initializing the histogram
     */
    public CachedHistogram(int cacheSize) {
        this(cacheSize, 0, null);
    }

    /**
     * Creates a CachedHistogram
     *
     * @param cacheSize The size of the cache for initializing the histogram
     * @param numBins The number of desired bins, must be &gt;=0, if zero a
     * reasonable number of bins is automatically determined
     */
    public CachedHistogram(int cacheSize, int numBins) {
        this(cacheSize, numBins, null);
    }

    /**
     * Creates a CachedHistogram
     *
     * @param cacheSize The size of the cache for initializing the histogram
     * @param numBins The number of desired bins, must be &gt;=0, if zero a
     * reasonable number of bins is automatically determined
     * @param name
     */
    public CachedHistogram(int cacheSize, int numBins, String name) {
        super(name);
        if (cacheSize <= 0) {
            throw new IllegalArgumentException("The size of the cache must be > 0");
        }
        if (numBins < 0) {
            throw new IllegalArgumentException("The number of bins must be > 0");
        }
        myDataCache = new double[cacheSize];
        myWeightCache = new double[cacheSize];
        myNumBins = numBins;
        myCacheStatistic = new Statistic(getName() + " Cache Statistics");
    }

    /**
     * Returns a histogram based on the data or null if the cache limit has not
     * been reached.
     *
     * @return
     */
    public final Histogram getHistogram() {
        return myHistogram;
    }

    @Override
    public final boolean collect(double x, double weight) {
        if (isTurnedOff()){
            return false;
        }
        
        if (Double.isNaN(x)) {
            myNumMissing++;
            return true;
        }

        if (getSaveDataOption()) {
            saveData(x, weight);
        }

        if (myCachingFlag == true) {
            myDataCache[myCountCache] = x;
            myWeightCache[myCountCache] = weight;
            myCountCache++;
            myCacheStatistic.collect(x, weight);
            if (myCountCache == myDataCache.length) {
                collectOnCache();
            }
        } else {
            myHistogram.collect(x, weight);
        }
        return true;
    }

    /**
     * When the cache is full this method is called to form the histogram
     *
     *
     */
    protected final void collectOnCache() {
        // turn off caching
        myCachingFlag = false;
        // need to set up histogram
        double LL = myCacheStatistic.getMin();
        double UL = myCacheStatistic.getMax();
        if (myNumBins == 0) { // user has not specified a number of bins
            // try to approximate a reasonable number of bins from the cache
            // first determine a reasonable bin width
            double s = myCacheStatistic.getStandardDeviation();
            double n = myCacheStatistic.getCount();
            // http://www.fmrib.ox.ac.uk/analysis/techrep/tr00mj2/tr00mj2/node24.html
            double width = 3.49 * s * Math.pow(n, -1.0 / 3.0);
            // now compute a number of bins for this with
            double nb = (UL - LL) / width;
            myNumBins = (int) Math.floor(nb + 0.5);
        }
        // form the histogram
        myHistogram = Histogram.makeHistogram(LL, UL, myNumBins);
        // collect on the cache
        for (int i = 0; i < myDataCache.length; i++) {
            myHistogram.collect(myDataCache[i], myWeightCache[i]);
        }
        // clear cache
        myCacheStatistic.reset();
        myCacheStatistic = null;
        myDataCache = null;
        myWeightCache = null;

    }

    @Override
    public final double getAverage() {
        if (myCachingFlag) {
            return myCacheStatistic.getAverage();
        } else {
            return myHistogram.getAverage();
        }
    }

    @Override
    public final double getConfidenceLevel() {
        if (myCachingFlag) {
            return myCacheStatistic.getConfidenceLevel();
        } else {
            return myHistogram.getConfidenceLevel();
        }
    }

    @Override
    public final double getCount() {
        if (myCachingFlag) {
            return myCacheStatistic.getCount();
        } else {
            return myHistogram.getCount();
        }
    }

    @Override
    public final double getDeviationSumOfSquares() {
        if (myCachingFlag) {
            return myCacheStatistic.getDeviationSumOfSquares();
        } else {
            return myHistogram.getDeviationSumOfSquares();
        }
    }

    @Override
    public final double getHalfWidth(double alpha) {
        if (myCachingFlag) {
            return myCacheStatistic.getHalfWidth(alpha);
        } else {
            return myHistogram.getHalfWidth(alpha);
        }
    }

    @Override
    public final double getKurtosis() {
        if (myCachingFlag) {
            return myCacheStatistic.getKurtosis();
        } else {
            return myHistogram.getKurtosis();
        }
    }

    @Override
    public final double getLag1Correlation() {
        if (myCachingFlag) {
            return myCacheStatistic.getLag1Correlation();
        } else {
            return myHistogram.getLag1Correlation();
        }
    }

    @Override
    public final double getLag1Covariance() {
        if (myCachingFlag) {
            return myCacheStatistic.getLag1Covariance();
        } else {
            return myHistogram.getLag1Covariance();
        }
    }

    @Override
    public final double getLastValue() {
        if (myCachingFlag) {
            return myCacheStatistic.getLastValue();
        } else {
            return myHistogram.getLastValue();
        }
    }

    @Override
    public final double getLastWeight() {
        if (myCachingFlag) {
            return myCacheStatistic.getLastWeight();
        } else {
            return myHistogram.getLastWeight();
        }
    }

    @Override
    public final double getMax() {
        if (myCachingFlag) {
            return myCacheStatistic.getMax();
        } else {
            return myHistogram.getMax();
        }
    }

    @Override
    public final double getMin() {
        if (myCachingFlag) {
            return myCacheStatistic.getMin();
        } else {
            return myHistogram.getMin();
        }
    }

    @Override
    public final double getSkewness() {
        if (myCachingFlag) {
            return myCacheStatistic.getSkewness();
        } else {
            return myHistogram.getSkewness();
        }
    }

    @Override
    public final double getStandardDeviation() {
        if (myCachingFlag) {
            return myCacheStatistic.getStandardDeviation();
        } else {
            return myHistogram.getStandardDeviation();
        }
    }

    @Override
    public final double getStandardError() {
        if (myCachingFlag) {
            return myCacheStatistic.getStandardError();
        } else {
            return myHistogram.getStandardError();
        }
    }

    @Override
    public final double getSum() {
        if (myCachingFlag) {
            return myCacheStatistic.getSum();
        } else {
            return myHistogram.getSum();
        }
    }

    @Override
    public final double getSumOfWeights() {
        if (myCachingFlag) {
            return myCacheStatistic.getSumOfWeights();
        } else {
            return myHistogram.getSumOfWeights();
        }
    }

    @Override
    public final double getVariance() {
        if (myCachingFlag) {
            return myCacheStatistic.getVariance();
        } else {
            return myHistogram.getVariance();
        }
    }

    @Override
    public final double getVonNeumannLag1TestStatistic() {
        if (myCachingFlag) {
            return myCacheStatistic.getVonNeumannLag1TestStatistic();
        } else {
            return myHistogram.getVonNeumannLag1TestStatistic();
        }
    }

    @Override
    public final double getVonNeumannLag1TestStatisticPValue(){
        return Normal.stdNormalComplementaryCDF(getVonNeumannLag1TestStatistic());
    }
    
    @Override
    public final double getWeightedAverage() {
        if (myCachingFlag) {
            return myCacheStatistic.getWeightedAverage();
        } else {
            return myHistogram.getWeightedAverage();
        }
    }

    @Override
    public final double getWeightedSum() {
        if (myCachingFlag) {
            return myCacheStatistic.getWeightedSum();
        } else {
            return myHistogram.getWeightedSum();
        }
    }

    @Override
    public final double getWeightedSumOfSquares() {
        if (myCachingFlag) {
            return myCacheStatistic.getWeightedSumOfSquares();
        } else {
            return myHistogram.getWeightedSumOfSquares();
        }
    }

    @Override
    public final int getLeadingDigitRule(double a) {
        if (myCachingFlag) {
            return myCacheStatistic.getLeadingDigitRule(a);
        } else {
            return myHistogram.getLeadingDigitRule(a);
        }
    }

    @Override
    public final void reset() {
        myNumMissing = 0.0;
        if (myCachingFlag) {
            myCacheStatistic.reset();
            for (int i = 0; i < myCountCache; i++) {
                myDataCache[i] = 0.0;
                myWeightCache[i] = 0.0;
            }
            myCountCache = 0;
        } else {
            myHistogram.reset();
        }
        clearSavedData();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (myCachingFlag) {
            sb.append("Histogram: ");
            sb.append(getName());
            sb.append("\n");
            sb.append("-------------------------------------\n");
            sb.append("Caching is still on. Only within cache statistics available:\n");
            sb.append("Cache size = ");
            sb.append(myDataCache.length);
            sb.append("\n");
            sb.append("Number of observations cached: ");
            sb.append(myCountCache);
            sb.append("\n");
            sb.append(myCacheStatistic);
        } else {
            sb.append(myHistogram);
        }

        return (sb.toString());
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        Exponential d = new Exponential(2);
        CachedHistogram h = new CachedHistogram(50);
        for (int i = 1; i <= 100; ++i) {
            h.collect(d.getValue());
        }
        System.out.println(h);
        d.resetStartStream();
        // should not get out of caching
        h = new CachedHistogram(150);
        for (int i = 1; i <= 100; ++i) {
            h.collect(d.getValue());
        }
        System.out.println(h);
        // specify 10 bins
        d.resetStartStream();
        h = new CachedHistogram(50, 10);
        for (int i = 1; i <= 100; ++i) {
            h.collect(d.getValue());
        }
        System.out.println(h);
    }
}
