/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsl.utilities.random.distributions;

import jsl.utilities.random.rng.RngIfc;

/** A distribution on a single value.  The value may
 *  be changed via the setParameters() method
 *
 * @author rossetti
 */
public class VConstant extends Constant {

    public VConstant(double value, RngIfc rng) {
        super(value, rng);
    }

    public VConstant(double value) {
        super(value);
    }

    public VConstant(double[] parameters) {
        super(parameters[0]);
    }

    @Override
    public VConstant newInstance() {
        return (new VConstant(getValue()));
    }

    @Override
    public void setParameters(double[] parameters) {
        myValue = parameters[0];
    }

    /** Returns a new instance of the random source with the same parameters
     *  with the supplied RngIfc
     * @param rng
     * @return
     */
    @Override
    public VConstant newInstance(RngIfc rng) {
        return (newInstance());
    }

    /** Returns a new instance that will supply values based
     *  on antithetic U(0,1) when compared to this distribution
     * Since the rng is not used for Constant
     *  this method is defined for sub-class compatibility with Distribution
     *
     * @return
     */
    @Override
    public VConstant newAntitheticInstance() {
        return newInstance();
    }
}
