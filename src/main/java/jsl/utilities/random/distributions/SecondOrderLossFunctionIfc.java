/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jsl.utilities.random.distributions;

/** Represents the 2nd order loss function
 *
 * @author rossetti
 */
public interface SecondOrderLossFunctionIfc {

     /** Computes the 2nd order loss function for the
     * distribution function for given value of x, G2(x) = (1/2)E[max(X-x,0)*max(X-x-1,0)]
     * @param x The value to be evaluated
     * @return The loss function value, (1/2)E[max(X-x,0)*max(X-x-1,0)]
     */
    double secondOrderLossFunction(double x);
}
