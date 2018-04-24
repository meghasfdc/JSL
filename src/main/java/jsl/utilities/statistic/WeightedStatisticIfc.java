/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsl.utilities.statistic;

import jsl.utilities.IdentityIfc;

/**
 *
 * @author rossetti
 */
public interface WeightedStatisticIfc extends IdentityIfc, GetCSVStatisticIfc {

    /**
     * Gets the count of the number of the observations.
     *
     * @return A double representing the count
     */
    double getCount();

    /**
     * Gets the maximum of the observations.
     *
     * @return A double representing the maximum
     */
    double getMax();

    /**
     * Gets the minimum of the observations.
     *
     * @return A double representing the minimum
     */
    double getMin();

    /**
     * Gets the sum of the observed weights.
     *
     * @return A double representing the sum of the weights
     */
    double getSumOfWeights();

    /**
     * Gets the weighted average of the collected observations.
     *
     * @return A double representing the weighted average or Double.NaN if no
     * observations.
     */
    double getAverage();

    /**
     * Gets the weighted sum of observations observed.
     *
     * @return A double representing the weighted sum
     */
    double getWeightedSum();

    /**
     * Gets the weighted sum of squares for observations observed.
     *
     * @return A double representing the weighted sum of squares
     */
    double getWeightedSumOfSquares();

    /**
     * Clears all the statistical accumulators
     */
    void reset();

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
}
