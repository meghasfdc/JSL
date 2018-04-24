/*
 * Copyright (c) 2007, Manuel D. Rossetti (rossetti@uark.edu)
 *
 * Contact:
 *	Manuel D. Rossetti, Ph.D., P.E.
 *	Department of Industrial Engineering
 *	University of Arkansas
 *	4207 Bell Engineering Center
 *	Fayetteville, AR 72701
 *	Phone: (479) 575-6756
 *	Email: rossetti@uark.edu
 *	Web: www.uark.edu/~rossetti
 *
 * This file is part of the JSL (a Java Simulation Library). The JSL is a framework
 * of Java classes that permit the easy development and execution of discrete event
 * simulation programs.
 *
 * The JSL is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * The JSL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the JSL (see file COPYING in the distribution);
 * if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor,
 * Boston, MA  02110-1301  USA, or see www.fsf.org
 *
 */
package jsl.utilities.random.distributions;

import jsl.utilities.Interval;
import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RngIfc;
import jsl.utilities.random.rvariable.AbstractRVariable;
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
        this(1.0, RNStreamFactory.getDefault().getStream());
    }

    /** Constructs a exponential random variable where parameter[0] is the
     * mean of the distribution
     * @param parameters A array containing the mean of the distribution, must be &gt; 0.0
     */
    public Exponential(double[] parameters) {
        this(parameters[0], RNStreamFactory.getDefault().getStream());
    }

    /** Constructs a exponential random variable where parameter[0] is the
     * mean of the distribution
     * @param parameters A array containing the mean of the distribution, must be &gt; 0.0
     * @param rng
     */
    public Exponential(double[] parameters, RngIfc rng) {
        this(parameters[0], rng);
    }

    /** Constructs a exponential random variable where mean is the
     * mean of the distribution
     * @param mean The mean of the distribution, , must be &gt; 0.0
     */
    public Exponential(double mean) {
        this(mean, RNStreamFactory.getDefault().getStream());
    }

    /** Constructs a exponential random variable where mean is the
     * mean of the distribution
     * @param mean The mean of the distribution, , must be &gt; 0.0
     * @param rng 
     */
    public Exponential(double mean, RngIfc rng) {
        super(rng);
        setMean(mean);
    }

    @Override
    public final Exponential newInstance() {
        return (new Exponential(getParameters()));
    }

    @Override
    public final Exponential newInstance(RngIfc rng) {
        return (new Exponential(getParameters(), rng));
    }

    @Override
    public final Exponential newAntitheticInstance() {
        RngIfc a = myRNG.newAntitheticInstance();
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
    public final RVariableIfc getRandomVariable(RngIfc rng){
        return new ExponentialRV(getMean(), rng);
    }

}
