/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsl.utilities.random.distributions;

/** Represents the probability density function for
 *  1-d continous distributions
 *
 * @author rossetti
 */
public interface PDFIfc {

    /** Returns the f(x) where f represents the probability
     * density function for the distribution.  Note this is not
     * a probability.
     *
     * @param x a double representing the value to be evaluated
     * @return f(x)
     */
    double pdf(double x);
}
