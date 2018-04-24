/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsl.utilities.random.distributions;

/** Defines an interface for getting the variance of a
 *  distribution
 *
 * @author rossetti
 */
public interface VarianceIfc {

    /** Returns the variance of the distribution if defined
     * @return double  the variance of the random variable
     */
    public abstract double getVariance();

    /**
     * Returns the standard deviation for the probability distribution
     * as the square root of the variance if it exists
     *
     * @return sqrt(getVariance())
     */
    public abstract double getStandardDeviation();
}
