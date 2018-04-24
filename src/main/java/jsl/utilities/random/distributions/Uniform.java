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
import jsl.utilities.random.rvariable.ExponentialRV;
import jsl.utilities.random.rvariable.GetRVariableIfc;
import jsl.utilities.random.rvariable.RVariableIfc;
import jsl.utilities.random.rvariable.UniformRV;

/** Defines a uniform distribution over the given range.
 *
 */
public class Uniform extends Distribution implements ContinuousDistributionIfc,
        InverseCDFIfc, GetRVariableIfc {

    private double myMin;

    private double myMax;

    private double myRange;

    /** Constructs a uniform distribution over the range (0,1)
     */
    public Uniform() {
        this(0.0, 1.0, RNStreamFactory.getDefault().getStream());
    }

    /** Constructs a uniform distribution with
     * lower limit = parameters[0], upper limit = parameters[1]
     * @param parameters The array of parameters
     */
    public Uniform(double[] parameters) {
        this(parameters[0], parameters[1], RNStreamFactory.getDefault().getStream());
    }

    /** Constructs a uniform distribution with
     * lower limit = parameters[0], upper limit = parameters[1]
     * @param parameters The array of parameters
     * @param rng
     */
    public Uniform(double[] parameters, RngIfc rng) {
        this(parameters[0], parameters[1], rng);
    }

    /** Constructs a uniform distribution over the provided range
     *
     * @param lowerLimit limit of the distribution
     * @param upperLimit limit of the distribution
     */
    public Uniform(double lowerLimit, double upperLimit) {
        this(lowerLimit, upperLimit, RNStreamFactory.getDefault().getStream());
    }

    /** Constructs a uniform distribution over the provided range
     *
     * @param lowerLimit limit of the distribution
     * @param upperLimit limit of the distribution
     * @param rng A RngIfc
     */
    public Uniform(double lowerLimit, double upperLimit, RngIfc rng) {
        super(rng);
        setRange(lowerLimit, upperLimit);
    }

    /** Returns a new instance of the random source with the same parameters
     *  but an independent generator
     *
     * @return
     */
    @Override
    public final Uniform newInstance() {
        return (new Uniform(getParameters()));
    }

    /** Returns a new instance of the random source with the same parameters
     *  with the supplied RngIfc
     * @param rng
     * @return
     */
    @Override
    public final Uniform newInstance(RngIfc rng) {
        return (new Uniform(getParameters(), rng));
    }

    /** Returns a new instance that will supply values based
     *  on antithetic U(0,1) when compared to this distribution
     *
     * @return
     */
    @Override
    public final Uniform newAntitheticInstance() {
        RngIfc a = myRNG.newAntitheticInstance();
        return newInstance(a);
    }

    @Override
    public final Interval getDomain(){
        return new Interval(getMinimum(), getMaximum());
    }

    /** Gets the lower limit for the distribution
     * @return The lower limit
     */
    public final double getMinimum() {
        return (myMin);
    }

    /** Gets the upper limit of the distribution
     * @return The upper limit
     */
    public final double getMaximum() {
        return (myMax);
    }

    /** Sets the minimum and maximum value of the distribution
     *  throws IllegalArgumentException when if min &gt;= max
     *
     * @param min The minimum value of the distribution
     * @param max The maximum value of the distribution
     */
    public final void setParameters(double min, double max) {
        setRange(min, max);
    }

    /** Sets the range
     * @param min The lower limit for the distribution
     * @param max The upper limit for the distribution
     */
    public final void setRange(double min, double max) {
        if (min >= max) {
            throw new IllegalArgumentException("Lower limit must be < upper limit. lower limit = " + min + " upper limit = " + max);
        }
        myMin = min;
        myMax = max;
        myRange = myMax - myMin;
    }

    public final double getRange() {
        return (myRange);
    }

    public final double cdf(double x) {
        if (x < myMin) {
            return 0.0;
        } else if ((x >= myMin) && (x <= myMax)) {
            return ((x - myMin) / myRange);
        } else //if (x > myMax)
        {
            return 1.0;
        }
    }

    /** Returns the inverse cumulative distribution function of the distribution
     * throws IllegalArgumentException if the value of the argument passed is beyond the range [0,1]
     *
     * @param prob the cumulative probability that requires the corresponding point
     * @return double the value in the triangular distribution at which the cumulative distribution funtion equals the value of p
     *
     */
    @Override
    public final double invCDF(double prob) {
        if ((prob < 0.0) || (prob > 1.0)) {
            throw new IllegalArgumentException("Probability must be [0,1]");
        }

        return (myMin + myRange * prob);
    }

    public final double pdf(double x) {
        if ((x < myMin) || (x > myMax)) {
            return (0.0);
        }

        return (1.0 / myRange);
    }

    @Override
    public final double getMean() {
        return ((myMin + myMax) / 2.0);
    }

    public final double getMoment3() {
        return (1.0 / 4.0) * ((myMin + myMax) * ((myMin * myMin) + (myMax * myMax)));
    }

    public final double getMoment4() {
        double min2 = myMin * myMin;
        double max2 = myMax * myMax;
        return (1.0 / 5.0) * ((min2 * min2) + (min2 * myMin * myMax) + (min2 * max2) + (myMin * myMax * max2) + (max2 * max2));
    }

    @Override
    public final double getVariance() {
        return ((myRange * myRange) / 12.0);
    }

    /** Gets the kurtosis of the distribution
     * www.mathworld.wolfram.com/UniformDistribution.html
     * @return the kurtosis
     */
    public final double getKurtosis() {
        return (-6.0 / 5.0);
    }

    /** Gets the skewness of the distribution
     *  www.mathworld.wolfram.com/UniformDistribution.html
     * @return the skewness
     */
    public final double getSkewness() {
        return (0.0);
    }

    /** Sets the parameters for the distribution where parameters[0] is the
     *  lowerlimit and parameters[1] is the upper limit of the range.
     *  the lower limit must be &lt; upper limit
     * 
     * @param parameters an array of doubles representing the parameters for
     * the distribution
     */
    @Override
    public final void setParameters(double[] parameters) {
        setRange(parameters[0], parameters[1]);
    }

    /** Gets the parameters for the distribution
     *
     * @return Returns an array of the parameters for the distribution
     */
    @Override
    public final double[] getParameters() {
        double[] param = new double[2];
        param[0] = myMin;
        param[1] = myMax;
        return (param);
    }

    @Override
    public final RVariableIfc getRandomVariable(RngIfc rng){
        return new UniformRV(getMinimum(), getMaximum(), rng);
    }
}
