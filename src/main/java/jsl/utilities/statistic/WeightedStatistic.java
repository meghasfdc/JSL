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
package jsl.utilities.statistic;

/**
 * Collects basic weighted statistical summary
 *
 * @author rossetti
 *
 */
public class WeightedStatistic extends AbstractCollector implements WeightedStatisticIfc, GetCSVStatisticIfc {

    /**
     * Used to count the number of missing data points presented When a data
     * point having the value of (Double.NaN, Double.POSITIVE_INFINITY,
     * Double.NEGATIVE_INFINITY) are presented it is excluded from the summary
     * statistics and the number of missing points is noted. Implementers of
     * subclasses are responsible for properly collecting this value and
     * resetting this value.
     * <p>
     */
    protected double myNumMissing = 0.0;

    /**
     * Holds the minimum of the observed data.
     */
    protected double min = Double.POSITIVE_INFINITY;

    /**
     * Holds the maximum of the observed data
     */
    protected double max = Double.NEGATIVE_INFINITY;

    /**
     * Holds the number of observations observed
     */
    protected double num = 0.0;

    /**
     * Holds the weighted sum of the data.
     */
    protected double wsum = 0.0;

    /**
     * Holds the weighted sum of squares of the data.
     */
    protected double wsumsq = 0.0;

    /**
     * Holds the sum of the weights observed.
     */
    protected double sumw = 0.0;

    /**
     * Holds the last value observed
     */
    protected double myValue;

    /**
     * Holds the last weight observed
     */
    protected double myWeight;

    /**
     *
     */
    public WeightedStatistic() {
        this(null);
    }

    /**
     * @param name
     */
    public WeightedStatistic(String name) {
        super(name);
        reset();
    }

    @Override
    public final boolean collect(double x, double weight) {
        if (isTurnedOff()) {
            return false;
        }
        if (Double.isNaN(x) || Double.isInfinite(x)) {
            myNumMissing++;
            return true;
        }

        if (getSaveDataOption()) {
            saveData(x, weight);
        }
        // update moments
        num = num + 1.0;
        sumw = sumw + weight;
        wsum = wsum + x * weight;
        wsumsq = wsumsq + x * x * weight;

        // update min, max, current value, current weight
        if (x > max) {
            max = x;
        }
        if (x < min) {
            min = x;
        }
        myValue = x;
        myWeight = weight;
        return true;
    }

    /**
     * Returns a statistic that summarizes the passed in array of values
     *
     * @param x the values to compute statistics for
     * @return
     */
    public static WeightedStatistic collectStatistics(double[] x) {
        WeightedStatistic s = new WeightedStatistic();
        s.collect(x);
        return (s);
    }

    /**
     * Returns a statistic that summarizes the passed in arrays The lengths of
     * the arrays must be the same.
     *
     * @param x the values
     * @param w the weights
     * @return
     */
    public static WeightedStatistic collectStatistics(double[] x, double[] w) {
        if (x.length != w.length) {
            throw new IllegalArgumentException("The supplied arrays are not of equal length");
        }

        WeightedStatistic s = new WeightedStatistic();
        s.collect(x, w);

        return (s);
    }

    /**
     * Creates a instance of Statistic that is a copy of the supplied Statistic
     * All internal state is the same except for the id of the returned
     * Statistic
     *
     * @param stat
     * @return
     */
    public static WeightedStatistic newInstance(WeightedStatistic stat) {
        WeightedStatistic s = new WeightedStatistic();
        s.max = stat.max;
        s.min = stat.min;
        s.myName = stat.myName;
        s.num = stat.num;
        s.sumw = stat.sumw;
        s.wsum = stat.wsum;
        s.wsumsq = stat.wsumsq;
        s.myValue = stat.myValue;
        s.myWeight = stat.myWeight;
        return (s);
    }

    /**
     * Creates a instance of Statistic that is a copy of this Statistic All
     * internal state is the same except for the id of the returned Statistic
     *
     * @return
     */
    public final WeightedStatistic newInstance() {
        WeightedStatistic s = new WeightedStatistic();
        s.max = max;
        s.min = min;
        s.myName = myName;
        s.num = num;
        s.sumw = sumw;
        s.wsum = wsum;
        s.wsumsq = wsumsq;
        s.myValue = myValue;
        s.myWeight = myWeight;
        return (s);
    }

    /* (non-Javadoc)
     * @see jsl.utilities.statistic.AbstractCollector#reset()
     */
    @Override
    public final void reset() {
        myValue = Double.NaN;
        myWeight = Double.NaN;
        num = 0.0;
        wsum = 0.0;
        sumw = 0.0;
        wsumsq = 0.0;
        min = Double.POSITIVE_INFINITY;
        max = Double.NEGATIVE_INFINITY;
        myNumMissing = 0.0;
        clearSavedData();
    }

    @Override
    public final double getLastValue() {
        return (myValue);
    }

    @Override
    public final double getLastWeight() {
        return (myWeight);
    }

    /**
     * Gets the weighted average of the collected observations.
     *
     * @return A double representing the weighted average or Double.NaN if no
     * observations.
     */
    @Override
    public final double getAverage() {
        if (sumw <= 0.0) {
            return Double.NaN;
        }
        return (wsum / sumw);
    }

    /**
     * Gets the count of the number of the observations.
     *
     * @return A double representing the count
     */
    @Override
    public final double getCount() {
        return (num);
    }

    /**
     * Gets the weighted sum of observations observed.
     *
     * @return A double representing the weighted sum
     */
    @Override
    public final double getWeightedSum() {
        return (wsum);
    }

    /**
     * Gets the sum of the observed weights.
     *
     * @return A double representing the sum of the weights
     */
    @Override
    public final double getSumOfWeights() {
        return (sumw);
    }

    /**
     * Gets the weighted sum of squares (sum of x*x*w)
     *
     * @return
     */
    @Override
    public final double getWeightedSumOfSquares() {
        return wsumsq;
    }

    /**
     * Gets the minimum of the observations.
     *
     * @return A double representing the minimum
     */
    @Override
    public final double getMin() {
        return (min);
    }

    /**
     * Gets the maximum of the observations.
     *
     * @return A double representing the maximum
     */
    @Override
    public final double getMax() {
        return (max);
    }

    /**
     * When a data point having the value of (Double.NaN,
     * Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY) are presented it is
     * excluded from the summary statistics and the number of missing points is
     * noted. This method reports the number of missing points that occurred
     * during the collection
     *
     * @return
     */
    public double getNumberMissing() {
        return (myNumMissing);
    }

    /**
     * Returns a String representation of the Statistic
     *
     * @return A String with basic summary statistics
     */
    @Override
    public final String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID ");
        sb.append(getId());
        sb.append(System.lineSeparator());

        sb.append("Name ");
        sb.append(getName());
        sb.append(System.lineSeparator());

        sb.append("Number ");
        sb.append(getCount());
        sb.append(System.lineSeparator());

        sb.append("Minimum ");
        sb.append(getMin());
        sb.append(System.lineSeparator());

        sb.append("Maximum ");
        sb.append(getMax());
        sb.append(System.lineSeparator());

        sb.append("Weighted Average ");
        sb.append(getAverage());
        sb.append(System.lineSeparator());

        sb.append("Weighted Sum ");
        sb.append(getWeightedSum());
        sb.append(System.lineSeparator());

        sb.append("Weighted Sum of Squares ");
        sb.append(getWeightedSumOfSquares());
        sb.append(System.lineSeparator());

        sb.append("Sum of Weights ");
        sb.append(getSumOfWeights());
        sb.append(System.lineSeparator());

        sb.append("Number Missing ");
        sb.append(getNumberMissing());
        sb.append(System.lineSeparator());

        sb.append("Last Value ");
        sb.append(getLastValue());
        sb.append(System.lineSeparator());

        sb.append("Last Weight ");
        sb.append(getLastWeight());
        sb.append(System.lineSeparator());

        return (sb.toString());
    }

    /**
     * Fills up the supplied array with the statistics defined by index =
     * statistic
     * <p>
     * statistics[0] = getCount();
     * <p>
     * statistics[1] = getAverage();
     * <p>
     * statistics[2] = getMin();
     * <p>
     * statistics[3] = getMax();
     * <p>
     * statistics[4] = getSum();
     * <p>
     * statistics[5] = getSumOfWeights();
     * <p>
     * statistics[6] = getWeightedSumOfSquares();
     * <p>
     * statistics[7] = getLastValue();
     * <p>
     * statistics[8] = getLastWeight();
     * <p>
     * <p>
     * The array must be of size 9 or an exception will be thrown
     *
     * @param statistics the array to fill
     */
    public final void getStatistics(double[] statistics) {
        if (statistics.length != 9) {
            throw new IllegalArgumentException("The supplied array was not of size 7");
        }

        statistics[0] = getCount();
        statistics[1] = getAverage();
        statistics[2] = getMin();
        statistics[3] = getMax();
        statistics[4] = getWeightedSum();
        statistics[5] = getSumOfWeights();
        statistics[6] = getWeightedSumOfSquares();
        statistics[7] = getLastValue();
        statistics[8] = getLastWeight();
    }

    /**
     * Returns an array with the statistics defined by index = statistic
     * <p>
     * statistics[0] = getCount();
     * <p>
     * statistics[1] = getAverage();
     * <p>
     * statistics[2] = getMin();
     * <p>
     * statistics[3] = getMax();
     * <p>
     * statistics[4] = getSum();
     * <p>
     * statistics[5] = getSumOfWeights();
     * <p>
     * statistics[6] = getWeightedSumOfSquares();
     * <p>
     * statistics[7] = getLastValue();
     * <p>
     * statistics[8] = getLastWeight();
     * <p>
     *
     * @return the array of statistics
     */
    public final double[] getStatistics() {
        double[] x = new double[9];
        getStatistics(x);
        return (x);
    }

    /**
     * s[0] = "Count"; s[1] = "Average"; s[2] = "Minimum"; s[3] = "Maximum";
     * s[4] = "Weighted Sum"; s[5] = "Sum of Weights"; s[6] = "Weighted sum of
     * squares"; s[7] = "Last Value"; s[8] = "Last Weight";
     *
     * @return the headers
     */
    public String[] getStatisticsHeader() {
        String[] s = new String[9];
        s[0] = "Count";
        s[1] = "Average";
        s[2] = "Minimum";
        s[3] = "Maximum";
        s[4] = "Weighted Sum";
        s[5] = "Sum of Weights";
        s[6] = "Weighted sum of squares";
        s[7] = "Last Value";
        s[8] = "Last Weight";
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

    @Override
    public String getCSVStatisticHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append("Statistic Name,");
        sb.append("Count,");
        sb.append("Average,");
        sb.append("Minimum,");
        sb.append("Maximum,");
        sb.append("Weighted Sum,");
        sb.append("Sum of Weights,");
        sb.append("Weighted sum of squares,");
        sb.append("Last Value,");
        sb.append("Last Weight");
        return sb.toString();
    }
}
