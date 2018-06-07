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
package jsl.utilities.random.distributions;

import jsl.utilities.Interval;
import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RNStreamIfc;
import jsl.utilities.random.rvariable.ExponentialRV;
import jsl.utilities.random.rvariable.GetRVariableIfc;
import jsl.utilities.random.rvariable.RVariableIfc;

/** Models exponentially distributed random variables
 *  This distribution is commonly use to model the time between events
 *
 */
public class Exponential extends Distribution implements 
        ContinuousDistributionIfc, InverseCDFIfc, GetRVariableIfc {

    private double myMean;

    /** Constructs a exponential random variable with mean 1.0
     */
    public Exponential() {
        this(1.0, RNStreamFactory.getDefaultFactory().getStream());
    }

    /** Constructs a exponential random variable where parameter[0] is the
     * mean of the distribution
     * @param parameters A array containing the mean of the distribution, must be &gt; 0.0
     */
    public Exponential(double[] parameters) {
        this(parameters[0], RNStreamFactory.getDefaultFactory().getStream());
    }

    /** Constructs a exponential random variable where parameter[0] is the
     * mean of the distribution
     * @param parameters A array containing the mean of the distribution, must be &gt; 0.0
     * @param rng
     */
    public Exponential(double[] parameters, RNStreamIfc rng) {
        this(parameters[0], rng);
    }

    /** Constructs a exponential random variable where mean is the
     * mean of the distribution
     * @param mean The mean of the distribution, , must be &gt; 0.0
     */
    public Exponential(double mean) {
        this(mean, RNStreamFactory.getDefaultFactory().getStream());
    }

    /** Constructs a exponential random variable where mean is the
     * mean of the distribution
     * @param mean The mean of the distribution, , must be &gt; 0.0
     * @param rng 
     */
    public Exponential(double mean, RNStreamIfc rng) {
        super(rng);
        setMean(mean);
    }

    @Override
    public final Exponential newInstance() {
        return (new Exponential(getParameters()));
    }

    @Override
    public final Exponential newInstance(RNStreamIfc rng) {
        return (new Exponential(getParameters(), rng));
    }

    @Override
    public final Exponential newAntitheticInstance() {
        RNStreamIfc a = myRNG.newAntitheticInstance();
        return newInstance(a);
    }

    @Override
    public final Interval getDomain(){
        return new Interval(0, Double.POSITIVE_INFINITY);
    }

    /** Sets the mean parameter for the distribution
     * @param val The mean of the distribution, must be &gt; 0.0
     */
    public final void setMean(double val) {
        if (val <= 0.0) {
            throw new IllegalArgumentException("Exponential mean must be > 0.0");
        }
        myMean = val;
    }

    @Override
    public final double getMean() {
        return myMean;
    }

    public final double getMoment3() {
        return Math.pow(myMean, 3) * Math.exp(Gamma.logGammaFunction(4));
    }

    public final double getMoment4() {
        return Math.pow(myMean, 4) * Math.exp(Gamma.logGammaFunction(5));
    }

    @Override
    public final double cdf(double x) {
        if (x >= 0) {
            return (1 - Math.exp(-x / myMean));
        } else {
            return 0.0;
        }
    }

    @Override
    public final double invCDF(double prob) {
        if ((prob < 0.0) || (prob > 1.0)) {
            throw new IllegalArgumentException("Supplied probability was " + prob + " Probability must be [0,1]");
        }

        if (prob <= 0.0) {
            return 0.0;
        }

        if (prob >= 1.0) {
            return Double.POSITIVE_INFINITY;
        }

        return (-myMean * Math.log(1.0 - prob));
    }

    @Override
    public final double pdf(double x) {
        if (x >= 0) {
            return (Math.exp(-x / myMean) / myMean);
        } else {
            return 0.0;
        }
    }

    @Override
    public final double getVariance() {
        return (myMean * myMean);
    }

    /** Gets the kurtosis of the distribution
     * @return the kurtosis
     */
    public final double getKurtosis() {
        return (6.0);
    }

    /** Gets the skewness of the distribution
     * @return the skewness
     */
    public final double getSkewness() {
        return (2.0);
    }

    @Override
    public final void setParameters(double[] parameters) {
        setMean(parameters[0]);
    }

    @Override
    public final double[] getParameters() {
        double[] param = new double[1];
        param[0] = myMean;
        return (param);
    }

    @Override
    public final RVariableIfc getRandomVariable(RNStreamIfc rng){
        return new ExponentialRV(getMean(), rng);
    }

}
