/*
 * Copyright (c) 2018. Manuel D. Rossetti, manuelrossetti@gmail.com
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
package jsl.utilities.statistic;

import jsl.utilities.Interval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Serves as an abstract base class for statistical collection.
 *
 *
 */
abstract public class AbstractStatistic extends AbstractCollector
        implements StatisticAccessorIfc, GetCSVStatisticIfc, Comparable<AbstractStatistic> {

    /**
     * the default confidence level
     */
    public final static double DEFAULT_CONFIDENCE_LEVEL = 0.95;

    /**
     * Holds the confidence coefficient for the statistic
     */
    protected double myConfidenceLevel;

    /**
     * Used to count the number of missing data points presented When a data
     * point having the value of (Double.NaN, Double.POSITIVE_INFINITY,
     * Double.NEGATIVE_INFINITY) are presented it is excluded from the summary
     * statistics and the number of missing points is noted. Implementers of
     * subclasses are responsible for properly collecting this value and
     * resetting this value.
     *
     */
    protected double myNumMissing = 0.0;

    /**
     *
     */
    public AbstractStatistic() {
        this(null);
    }

    /**
     *
     * @param name
     */
    public AbstractStatistic(String name) {
        super(name);
        myConfidenceLevel = DEFAULT_CONFIDENCE_LEVEL;
    }

    /**
     * Sets the confidence level for the statistic
     *
     * @param level must be in (0, 1)
     */
    public void setConfidenceLevel(double level) {
        if ((level <= 0.0) || (level >= 1.0)) {
            throw new IllegalArgumentException("Confidence Level must be (0,1)");
        }

        myConfidenceLevel = level;
    }

    @Override
    public double getConfidenceLevel() {
        return (myConfidenceLevel);
    }

    @Override
    abstract public boolean collect(double x, double weight);

    @Override
    public double getRelativeError() {
        return getStandardError() / getAverage();
    }

    @Override
    public double getRelativeWidth() {
        return 2.0 * getHalfWidth() / getAverage();
    }

    @Override
    public double getRelativeWidth(double level) {
        return 2.0 * getHalfWidth(level) / getAverage();
    }

    @Override
    public double getHalfWidth() {
        return getHalfWidth(getConfidenceLevel());
    }

    /**
     * Returns a two-sided confidence interval on the mean with 95% confidence
     * level based on StudentT distribution
     *
     * @return
     */
    @Override
    public Interval getConfidenceInterval() {
        return getConfidenceInterval(0.95);
    }

    /**
     * Returns a two-sided confidence interval on the mean with confidence level
     * alpha based on StudentT distribution
     *
     * @param level
     * @return the interval
     */
    @Override
    public Interval getConfidenceInterval(double level) {
        if (getCount() < 1.0) {
            return new Interval(Double.NaN, Double.NaN);
        }
        double hw = getHalfWidth(level);
        double avg = getAverage();
        double ll = avg - hw;
        double ul = avg + hw;
        Interval ci = new Interval(ll, ul);
        return ci;
    }

    @Override
    public double getNumberMissing() {
        return (myNumMissing);
    }

    @Override
    public void getStatistics(double[] statistics) {
        if (statistics.length != 23) {
            throw new IllegalArgumentException("The supplied array was not of size 23");
        }

        statistics[0] = getCount();
        statistics[1] = getAverage();
        statistics[2] = getStandardDeviation();
        statistics[3] = getStandardError();
        statistics[4] = getHalfWidth();
        statistics[5] = getConfidenceLevel();
        statistics[6] = getMin();
        statistics[7] = getMax();
        statistics[8] = getSum();
        statistics[9] = getVariance();
        statistics[10] = getWeightedAverage();
        statistics[11] = getWeightedSum();
        statistics[12] = getSumOfWeights();
        statistics[13] = getWeightedSumOfSquares();
        statistics[14] = getDeviationSumOfSquares();
        statistics[15] = getLastValue();
        statistics[16] = getLastWeight();
        statistics[17] = getKurtosis();
        statistics[18] = getSkewness();
        statistics[19] = getLag1Covariance();
        statistics[20] = getLag1Correlation();
        statistics[21] = getVonNeumannLag1TestStatistic();
        statistics[22] = getNumberMissing();

    }

    @Override
    public double[] getStatistics() {
        double[] x = new double[23];
        getStatistics(x);
        return (x);
    }

    /**
     * s[0] = "Count"; s[1] = "Average"; s[2] = "Standard Deviation"; s[3] =
     * "Standard Error"; s[4] = "Half-width"; s[5] = "Confidence Level"; s[6] =
     * "Minimum"; s[7] = "Maximum"; s[8] = "Sum"; s[9] = "Variance"; s[10] =
     * "Weighted Average"; s[11] = "Weighted Sum"; s[12] = "Sum of Weights";
     * s[13] = "Weighted Sum of Squares"; s[14] = "Deviation Sum of Squares";
     * s[15] = "Last value collected"; s[16] = "Last weighted collected"; s[17]
     * = "Kurtosis"; s[18] = "Skewness"; s[19] = "Lag 1 Covariance"; s[20] =
     * "Lag 1 Correlation"; s[21] = "Von Neumann Lag 1 Test Statistic"; s[22] =
     * "Number of missing observations";
     *
     * @return the header
     */
    public String[] getStatisticsHeader() {
        String[] s = new String[23];
        s[0] = "Count";
        s[1] = "Average";
        s[2] = "Standard Deviation";
        s[3] = "Standard Error";
        s[4] = "Half-width";
        s[5] = "Confidence Level";
        s[6] = "Minimum";
        s[7] = "Maximum";
        s[8] = "Sum";
        s[9] = "Variance";
        s[10] = "Weighted Average";
        s[11] = "Weighted Sum";
        s[12] = "Sum of Weights";
        s[13] = "Weighted Sum of Squares";
        s[14] = "Deviation Sum of Squares";
        s[15] = "Last value collected";
        s[16] = "Last weighted collected";
        s[17] = "Kurtosis";
        s[18] = "Skewness";
        s[19] = "Lag 1 Covariance";
        s[20] = "Lag 1 Correlation";
        s[21] = "Von Neumann Lag 1 Test Statistic";
        s[22] = "Number of missing observations";
        return s;
    }

    @Override
    public String getCSVStatistic() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName());
        sb.append(",");
        double[] stats = getStatistics();
        for (int i = 0; i < stats.length; i++) {
            if (Double.isNaN(stats[i]) || Double.isInfinite(stats[i])) {
                sb.append("");
            } else {
                sb.append(stats[i]);
            }
            if (i < stats.length - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    /**
     * Returns the values of all the statistics as a list of strings
     *
     *
     * @return the values of all the statistics as a list of strings
     */
    public List<String> getCSVValues() {
        List<String> sb = new ArrayList<String>();
        sb.add(getName());
        double[] stats = getStatistics();
        for (int i = 0; i < stats.length; i++) {
            if (Double.isNaN(stats[i]) || Double.isInfinite(stats[i])) {
                sb.add("");
            } else {
                sb.add(Double.toString(stats[i]));
            }
        }
        return sb;
    }

    @Override
    public String getCSVStatisticHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append("Statistic Name,");
        sb.append("Count,");
        sb.append("Average,");
        sb.append("Standard Deviation,");
        sb.append("Standard Error,");
        sb.append("Half-width,");
        sb.append("Confidence Level,");
        sb.append("Minimum,");
        sb.append("Maximum,");
        sb.append("Sum,");
        sb.append("Variance,");
        sb.append("Weighted Average,");
        sb.append("Weighted Sum,");
        sb.append("Sum of Weights,");
        sb.append("Weighted Sum of Squares,");
        sb.append("Deviation Sum of Squares,");
        sb.append("Last value collected,");
        sb.append("Last weighted collected,");
        sb.append("Kurtosis,");
        sb.append("Skewness,");
        sb.append("Lag 1 Covariance,");
        sb.append("Lag 1 Correlation,");
        sb.append("Von Neumann Lag 1 Test Statistic,");
        sb.append("Number of missing observations");
        return sb.toString();
    }

    /**
     * Gets the comma separated value header as a list of strings
     *
     * @return the comma separated value header as a list of strings
     */
    public List<String> getCSVHeader() {
        List<String> sb = new ArrayList<String>();
        sb.add("Statistic Name");
        sb.add("Count");
        sb.add("Average");
        sb.add("Standard Deviation");
        sb.add("Standard Error");
        sb.add("Half-width");
        sb.add("Confidence Level");
        sb.add("Minimum");
        sb.add("Maximum");
        sb.add("Sum");
        sb.add("Variance");
        sb.add("Weighted Average");
        sb.add("Weighted Sum");
        sb.add("Sum of Weights");
        sb.add("Weighted Sum of Squares");
        sb.add("Deviation Sum of Squares");
        sb.add("Last value collected");
        sb.add("Last weighted collected");
        sb.add("Kurtosis");
        sb.add("Skewness");
        sb.add("Lag 1 Covariance");
        sb.add("Lag 1 Correlation");
        sb.add("Von Neumann Lag 1 Test Statistic");
        sb.add("Number of missing observations");
        return sb;
    }

    /**
     * Fills the map with the statistics
     *
     * @param stats
     */
    public void fillStatistics(Map<String, Double> stats) {
        if (stats == null) {
            stats = new HashMap<String, Double>();
        }

        stats.put("Count", getCount());
        stats.put("Average", getAverage());
        stats.put("Standard Deviation", getStandardDeviation());
        stats.put("Standard Error", getStandardError());
        stats.put("Half-width", getHalfWidth());
        stats.put("Confidence Level", getConfidenceLevel());
        stats.put("Minimum", getMin());
        stats.put("Maximum", getMax());
        stats.put("Sum", getSum());
        stats.put("Variance", getVariance());
        stats.put("Weighted Average", getWeightedAverage());
        stats.put("Weighted Sum", getWeightedSum());
        stats.put("Sum of Weights", getSumOfWeights());
        stats.put("Weighted Sum of Squares", getWeightedSumOfSquares());
        stats.put("Deviation Sum of Squares", getDeviationSumOfSquares());
        stats.put("Last value collected", getLastValue());
        stats.put("Last weighted collected", getLastWeight());
        stats.put("Kurtosis", getKurtosis());
        stats.put("Skewness", getSkewness());
        stats.put("Lag 1 Covariance", getLag1Covariance());
        stats.put("Lag 1 Correlation", getLag1Correlation());
        stats.put("Von Neumann Lag 1 Test Statistic", getVonNeumannLag1TestStatistic());
        stats.put("Number of missing observations", getNumberMissing());

    }

    /**
     * Returns a negative integer, zero, or a positive integer if this object is
     * less than, equal to, or greater than the specified object.
     *
     * The natural ordering is based on getAverage() with equals.
     *
     * @param stat The statistic to compare this statistic to
     * @return Returns a negative integer, zero, or a positive integer if this
     * object is less than, equal to, or greater than the specified object.
     */
    @Override
    public final int compareTo(AbstractStatistic stat) {

        // compare based on the average
        if (getAverage() < stat.getAverage()) {
            return (-1);
        } else if (getAverage() > stat.getAverage()) {
            return (1);
        } else {
            return 0;
        }

    }

}
