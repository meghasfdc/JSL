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
package jsl.utilities.random.ar;

import java.util.HashMap;

import jsl.utilities.controls.ControllableIfc;
import jsl.utilities.controls.Controls;
import jsl.utilities.random.RandomIfc;
import jsl.utilities.random.SampleIfc;
import jsl.utilities.random.distributions.Normal;
import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RngIfc;

/**
 *
 */
public class AR1Normal implements RandomIfc, SampleIfc, ControllableIfc {

    /** A counter to count the number of created to assign "unique" ids
     */
    private static long myIdCounter_;

    /** The id of this object
     */
    protected long myId;

    /** Holds the name of the statistic for reporting purposes.
     */
    protected String myName;

    private Normal myErrors;

    private double myPhi;

    private double myMean;

    private double myVar;

    private double myX;

    /** Creates an autoregressive order 1 normal, N(0,1) zero correlation process
     *
     */
    public AR1Normal() {
        this(0.0, 1.0, 0.0, RNStreamFactory.getDefault().getStream());
    }

    public AR1Normal(RngIfc rng) {
        this(0.0, 1.0, 0.0, rng);
    }

    /** Creates an autoregressive order 1 normal, N(0,1) process
     *
     * @param correlation
     */
    public AR1Normal(double correlation) {
        this(0.0, 1.0, correlation, RNStreamFactory.getDefault().getStream());
    }

    /** Creates an autoregressive order 1 normal process
     *
     * @param mean
     * @param variance
     * @param correlation
     */
    public AR1Normal(double mean, double variance, double correlation) {
        this(mean, variance, correlation, RNStreamFactory.getDefault().getStream());
    }

    /** Creates an autoregressive order 1 normal process
     *
     * @param mean
     * @param variance
     * @param correlation
     * @param rng
     */
    public AR1Normal(double mean, double variance, double correlation, RngIfc rng) {
        this(mean, variance, correlation, rng, null);
    }

    /** Creates an autoregressive order 1 normal process
     *
     * @param mean
     * @param variance
     * @param correlation
     * @param rng
     * @param name
     */
    public AR1Normal(double mean, double variance, double correlation, RngIfc rng, String name) {
        setId();
        setName(name);
        // create the error distribution
        myErrors = new Normal(0.0, 1.0, rng);
        // set the mean of the process
        setMean(mean);
        // set the variance of the process
        // this first call has phi = 0.0, thus errors are now N(0, variance)
        setVariance(variance);
        // generate the first value for the process N(mean, variance)
        myX = myMean + myErrors.getValue();
        // now set the correlation and the error distribution N(0, myVar*(1-myPhi^2)
        setLag1Correlation(correlation);
    }

    @Override
    public final String getName() {
        return myName;
    }

    /** Sets the name
     * @param str The name as a string.
     */
    public final void setName(String str) {
        if (str == null) {
            String s = this.getClass().getName();
            int k = s.lastIndexOf(".");
            if (k != -1) {
                s = s.substring(k + 1);
            }
            myName = s;
        } else {
            myName = str;
        }
    }

    @Override
    public final long getId() {
        return (myId);
    }

    @Override
    public final AR1Normal newInstance() {
        AR1Normal n = new AR1Normal();
        n.setParameters(this.getParameters());
        return n;
    }

    @Override
    public final AR1Normal newInstance(RngIfc rng) {
        AR1Normal n = new AR1Normal(rng);
        n.setParameters(getParameters());
        return n;
    }

    /** Returns the distributions underlying random number generator
     *
     * @return
     */
    public final RngIfc getRandomNumberGenerator() {
        return (myErrors.getRandomNumberGenerator());
    }

    /** Sets the underlying random number generator for the distribution
     * Throws a NullPointerException if rng is null
     * @param rng the reference to the random number generator
     */
    public final void setRandomNumberGenerator(RngIfc rng) {
        myErrors.setRandomNumberGenerator(rng);
    }

    /** Sets the mean of the AR(1) process
     *
     * @param mean of the process
     */
    public final void setMean(double mean) {
        myMean = mean;
    }

    public final double getMean() {
        return myMean;
    }

    /** Sets the variance of the AR(1) process, this also changes
     *  the variance of the underlying error process
     *
     * @param variance of the process, must be &gt; 0
     */
    public final void setVariance(double variance) {
        if (variance <= 0) {
            throw new IllegalArgumentException("Variance must be positive");
        }
        myVar = variance;
        double v = myVar * (1.0 - myPhi * myPhi);
        myErrors.setVariance(v);
    }

    /** The variance of the process
     *
     * @return
     */
    public final double getVariance() {
        return myVar;
    }

    /** The variance of the underlying errors
     *
     * @return
     */
    public final double getErrorVariance() {
        return (myErrors.getVariance());
    }

    /** Returns the standard deviation for the process
     * as the square root of the variance
     *
     * @return sqrt(getVariance())
     */
    public final double getStandardDeviation() {
        return Math.sqrt(getVariance());
    }

    /** Sets the lag 1 autocorrelation, this also changes the variance
     *  of the underlying error process
     *
     * @param phi
     */
    public final void setLag1Correlation(double phi) {
        if ((phi <= -1) || (phi >= 1)) {
            throw new IllegalArgumentException("Phi must be (-1,1)");
        }
        myPhi = phi;
        double v = myVar * (1.0 - myPhi * myPhi);
        myErrors.setVariance(v);
    }

    /** Gets the lag 1 autocorrelation
     *
     * @return
     */
    public final double getLag1Correlation() {
        return (myPhi);
    }

    /** Sets the parameters for the distribution
     * mean = parameters[0], variance = parameters[1]
     * phi = parameters[2]
     * @param parameters an array of doubles representing the parameters for
     * the process
     */
    @Override
    public final void setParameters(double[] parameters) {
        setMean(parameters[0]);
        setVariance(parameters[1]);
        setLag1Correlation(parameters[2]);
    }

    @Override
    public final double[] getParameters() {
        double[] param = new double[3];
        param[0] = myMean;
        param[1] = myVar;
        param[2] = myPhi;
        return (param);
    }

    protected class AR1Controls extends Controls {

        protected void fillControls() {
            addDoubleArrayControl("parameters", getParameters());
        }
    }

    @Override
    public Controls getControls() {
        return new AR1Controls();
    }

    @Override
    public void setControls(Controls controls) {
        if (controls == null) {
            throw new IllegalArgumentException("The supplied controls were null!");
        }

        setParameters(controls.getDoubleArrayControl("parameters"));
    }

    @Override
    public final double getValue() {
        return sample();
    }

    public double sample(){
        myX = myMean + myPhi * (myX - myMean) + myErrors.getValue();
        return myX;
    }

    @Override
    public final void advanceToNextSubstream() {
        myErrors.advanceToNextSubstream();
    }

    @Override
    public final void resetStartStream() {
        myErrors.resetStartStream();
    }

    @Override
    public final void resetStartSubstream() {
        myErrors.resetStartSubstream();
    }

    @Override
    public final void setAntitheticOption(boolean flag) {
        myErrors.setAntitheticOption(flag);
    }

    @Override
    public final boolean getAntitheticOption() {
        return myErrors.getAntitheticOption();
    }
    
    protected final void setId() {
        myIdCounter_ = myIdCounter_ + 1;
        myId = myIdCounter_;
    }
}
