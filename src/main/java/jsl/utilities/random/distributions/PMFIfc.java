/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsl.utilities.random.distributions;

/**
 * Represents the probability mass function for 1-d discrete distributions
 *
 * @author rossetti
 */
public interface PMFIfc {

    /**
     * Returns the f(x) where f represents the probability mass function for the
     * distribution.
     *
     * @param x a double representing the value to be evaluated
     * @return f(x) the P(X=x)
     */
    double pmf(double x);
}
