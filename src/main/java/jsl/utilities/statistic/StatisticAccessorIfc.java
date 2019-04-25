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

import jsl.utilities.Interval;

/**
 * The StatisticAccessIfc class presents a read-only view of a Statistic
 */
public interface StatisticAccessorIfc extends GetCSVStatisticIfc {

    /**
     * Gets the name of the Statistic
     *
     * @return The name as a String
     */
    public String getName();

    /**
     * Gets the count of the number of the observations.
     *
     * @return A double representing the count
     */
    public double getCount();

    /**
     * Gets the unweighted sum of the observations.
     *
     * @return A double representing the unweighted sum
     */
    public double getSum();

    /**
     * Gets the weighted sum of observations observed.
     *
     * @return A double representing the weighted sum
     */
    public double getWeightedSum();

    /**
     * Gets the weighted sum of squares of the observations.
     *
     * @return A double representing the weighted sum of squares
     */
    public double getWeightedSumOfSquares();

    /**
     * Gets the unweighted average of the observations.
     *
     * @return A double representing the average or Double.NaN if no
     * observations.
     */
    public double getAverage();

    /**
     * Gets the sum of the observed weights.
     *
     * @return A double representing the sum of the weights
     */
    public double getSumOfWeights();

    /**
     * Gets the weighted average of the observations.
     *
     * @return A double representing the weighted average or Double.NaN if no
     * observations.
     */
    public double getWeightedAverage();

    /**
     * Gets the sum of squares of the deviations from the average This is the
     * numerator in the classic generate variance formula
     *
     * @return A double representing the sum of squares of the deviations from
     * the average
     */
    public double getDeviationSumOfSquares();

    /**
     * Gets the unweighted generate variance of the observations.
     *
     * @return A double representing the generate variance or Double.NaN if 1 or
     * less observations.
     */
    public double getVariance();

    /**
     * Gets the unweighted generate standard deviation of the observations. Simply
     * the square root of getVariance()
     *
     * @return A double representing the generate standard deviation or Double.NaN
     * if 1 or less observations.
     */
    public double getStandardDeviation();

    /**
     * Gets the minimum of the observations.
     *
     * @return A double representing the minimum
     */
    public double getMin();

    /**
     * Gets the maximum of the observations.
     *
     * @return A double representing the maximum
     */
    public double getMax();

    /**
     * Gets the last observed data point
     *
     * @return A double representing the last observations
     */
    public double getLastValue();

    /**
     * Gets the last observed weight
     *
     * @return A double representing the last weight
     */
    public double getLastWeight();

    /**
     * Gets the kurtosis of the data
     *
     * @return A double representing the kurtosis
     */
    public double getKurtosis();

    /**
     * Gets the skewness of the data
     *
     * @return A double representing the skewness
     */
    public double getSkewness();

    /**
     * Gets the standard error of the observations. Simply the generate standard
     * deviation divided by the square root of the number of observations
     *
     * @return A double representing the standard error or Double.NaN if &lt; 1
     * observation
     */
    public double getStandardError();

    /**
     * Gets the confidence interval half-width. Simply the generate standard error
     * times the confidence coefficient
     *
     * @return A double representing the half-width or Double.NaN if &lt; 1
     * observation
     */
    public double getHalfWidth();

    /**
     * Gets the confidence interval half-width. Simply the generate standard error
     * times the confidence coefficient as determined by an appropriate sampling
     * distribution
     *
     * @param level the confidence level
     * @return A double representing the half-width or Double.NaN if &lt; 1
     * observation
     */
    public double getHalfWidth(double level);

    /**
     * Gets the confidence level. The default is given by
     * Statistic.DEFAULT_CONFIDENCE_LEVEL = 0.95, which is a 95% confidence
     * level
     *
     * @return A double representing the confidence level
     */
    public double getConfidenceLevel();

    /**
     * A confidence interval for the mean based on the confidence level
     *
     * @return the interval
     */
    public Interval getConfidenceInterval();

    /**
     * Returns the relative error: getStandardError() / getAverage()
     *
     * @return the relative error
     */
    public double getRelativeError();

    /**
     * Returns the relative width of the default confidence interval: 2.0 *
     * getHalfWidth() / getAverage()
     *
     * @return the relative width
     */
    public double getRelativeWidth();

    /**
     * Returns the relative width of the level of the confidence interval: 2.0 *
     * getHalfWidth(level) / getAverage()
     *
     *  @param level the confidence level
     * @return the relative width for the level
     */
    public double getRelativeWidth(double level);

    /**
     * A confidence interval for the mean based on the confidence level
     *
     * @param level the confidence level
     * @return the interval
     */
    public Interval getConfidenceInterval(double level);

    /**
     * Gets the lag-1 generate covariance of the unweighted observations. Note:
     * See Box, Jenkins, Reinsel, Time Series Analysis, 3rd edition,
     * Prentice-Hall, pg 31
     *
     * @return A double representing the generate covariance or Double.NaN if
     * &lt;=2 observations
     */
    public double getLag1Covariance();

    /**
     * Gets the lag-1 generate correlation of the unweighted observations. Note:
     * See Box, Jenkins, Reinsel, Time Series Analysis, 3rd edition,
     * Prentice-Hall, pg 31
     *
     * @return A double representing the generate correlation or Double.NaN if
     * &lt;=2 observations
     */
    public double getLag1Correlation();

    /**
     * Gets the Von Neumann Lag 1 test statistic for checking the hypothesis
     * that the data are uncorrelated Note: See Handbook of Simulation, Jerry
     * Banks editor, McGraw-Hill, pg 253.
     *
     * @return A double representing the Von Neumann test statistic
     */
    public double getVonNeumannLag1TestStatistic();

    /**
     * Returns the asymptotic p-value for the Von Nueumann Lag-1 Test Statistic:
     *
     * Normal.stdNormalComplementaryCDF(getVonNeumannLag1TestStatistic());
     *
     * @return the p-value
     */
    public double getVonNeumannLag1TestStatisticPValue();

    /**
     * When a data point having the value of (Double.NaN,
     * Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY) are presented it is
     * excluded from the summary statistics and the number of missing points is
     * noted. This method reports the number of missing points that occurred
     * during the collection
     *
     * @return the number missing
     */
    public double getNumberMissing();

    /**
     * Computes the right most meaningful digit according to
     * (int)Math.floor(Math.log10(a*getStandardError())) See doi
     * 10.1287.opre.1080.0529 by Song and Schmeiser
     *
     * @param a the std error multiplier
     * @return the meaningful digit
     */
    public int getLeadingDigitRule(double a);

    /**
     * Returns a String representation of the Statistic
     *
     * @return A String with basic summary statistics
     */
    @Override
    public String toString();

    /**
     * Fills up the supplied array with the statistics defined by this interface
     * index = statistic
     *
     * statistics[0] = getCount(); statistics[1] = getAverage(); statistics[2] =
     * getStandardDeviation(); statistics[3] = getStandardError(); statistics[4]
     * = getHalfWidth(); statistics[5] = getConfidenceLevel(); statistics[6] =
     * getMin(); statistics[7] = getMax(); statistics[8] = getSum();
     * statistics[9] = getVariance(); statistics[10] = getWeightedAverage();
     * statistics[11] = getWeightedSum(); statistics[12] = getSumOfWeights();
     * statistics[13] = getWeightedSumOfSquares(); statistics[14] =
     * getDeviationSumOfSquares(); statistics[15] = getLastValue();
     * statistics[16] = getLastWeight(); statistics[17] = getKurtosis();
     * statistics[18] = getSkewness(); statistics[19] = getLag1Covariance();
     * statistics[20] = getLag1Correlation(); statistics[21] =
     * getVonNeumannLag1TestStatistic(); statistics[22] = getNumberMissing();
     *
     * The array must be of size 23 or an exception will be thrown
     *
     * @param statistics an array of statistic values
     */
    public void getStatistics(double[] statistics);

    /**
     * Fills up an array with the statistics defined by this interface
     *
     * statistics[0] = getCount(); statistics[1] = getAverage(); statistics[2] =
     * getStandardDeviation(); statistics[3] = getStandardError(); statistics[4]
     * = getHalfWidth(); statistics[5] = getConfidenceLevel(); statistics[6] =
     * getMin(); statistics[7] = getMax(); statistics[8] = getSum();
     * statistics[9] = getVariance(); statistics[10] = getWeightedAverage();
     * statistics[11] = getWeightedSum(); statistics[12] = getSumOfWeights();
     * statistics[13] = getWeightedSumOfSquares(); statistics[14] =
     * getDeviationSumOfSquares(); statistics[15] = getLastValue();
     * statistics[16] = getLastWeight(); statistics[17] = getKurtosis();
     * statistics[18] = getSkewness(); statistics[19] = getLag1Covariance();
     * statistics[20] = getLag1Correlation(); statistics[21] =
     * getVonNeumannLag1TestStatistic(); statistics[22] = getNumberMissing();
     *
     * @return an array of values
     */
    public double[] getStatistics();
}
