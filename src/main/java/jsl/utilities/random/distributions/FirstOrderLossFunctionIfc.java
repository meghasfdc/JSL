/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jsl.utilities.random.distributions;

/**
 *
 * @author rossetti
 */
public interface FirstOrderLossFunctionIfc {

     /** Computes the first order loss function for the
     * function for given value of x, G1(x) = E[max(X-x,0)]
     * @param x The value to be evaluated
     * @return The loss function value, E[max(X-x,0)]
     */
    double firstOrderLossFunction(double x);
}
