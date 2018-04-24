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
import jsl.utilities.random.rvariable.GetRVariableIfc;
import jsl.utilities.random.rvariable.LognormalRV;
import jsl.utilities.random.rvariable.RVariableIfc;

/** Models lognormally distributed random variables
 *  This distribution is commonly use to model the time of a task
 *
 */
public class Lognormal extends Distribution implements ContinuousDistributionIfc,
        LossFunctionDistributionIfc, InverseCDFIfc, GetRVariableIfc {

    private double myMean;

    private double myVar;

    private double myNormalMu;

    private double myNormalSigma;

    /** Constructs a lognormal distribution with mean 1.0 and variance 1.0
     */
    public Lognormal() {
        this(1.0, 1.0, RNStreamFactory.getDefault().getStream());
    }

    /** Constructs a lognormal distribution with
     * mean = parameters[0] and variance = parameters[1]
     * @param parameters An array with the mean and variance
     */
    public Lognormal(double[] parameters) {
        this(parameters[0], parameters[1], RNStreamFactory.getDefault().getStream());
    }

    /** Constructs a lognormal distribution with
     * mean = parameters[0] and variance = parameters[1]
     * @param parameters An array with the mean and variance
     * @param rng
     */
    public Lognormal(double[] parameters, RngIfc rng) {
        this(parameters[0], parameters[1], rng);
    }

    /** Constructs a lognormal distribution with
     * mean and variance.  Note: these parameters are the
     * actual mean and variance of the lognormal, not the underlying
     * normal as in many other implementations.
     *
     * @param mean must be &gt; 0
     * @param variance must be &gt; 0
     */
    public Lognormal(double mean, double variance) {
        this(mean, variance, RNStreamFactory.getDefault().getStream());
    }

    /** Constructs a lognormal distribution with
     * mean and variance.  Note: these parameters are the
     * actual mean and variance of the lognormal, not the underlying
     * normal as in many other implementations.
     *
     * @param mean must be &gt; 0
     * @param variance must be &gt; 0
     * @param rng A RngIfc
     */
    public Lognormal(double mean, double variance, RngIfc rng) {
        super(rng);
        setParameters(mean, variance);
    }

    /** Returns a new instance of the random source with the same parameters
     *  but an independent generator
     *
     * @return
     */
    @Override
    public final Lognormal newInstance() {
        return (new Lognormal(getParameters()));
    }

    /** Returns a new instance of the random source with the same parameters
     *  with the supplied RngIfc
     * @param rng
     * @return
     */
    @Override
    public final Lognormal newInstance(RngIfc rng) {
        return (new Lognormal(getParameters(), rng));
    }

    /** Returns a new instance that will supply values based
     *  on antithetic U(0,1) when compared to this distribution
     *
     * @return
     */
    @Override
    public final Lognormal newAntitheticInstance() {
        RngIfc a = myRNG.newAntitheticInstance();
        return newInstance(a);
    }

    @Override
    public final Interval getDomain(){
        return new Interval(0, Double.POSITIVE_INFINITY);
    }

    /** Sets the parameters of a lognormal distribution to
     * mean and variance.  Note: these parameters are the
     * actual mean and variance of the lognormal, not the underlying
     * normal as in many other implementations.
     *
     * @param mean must be &gt; 0
     * @param variance must be &gt; 0
     */
    public final void setParameters(double mean, double variance) {
        if (mean <= 0) {
            throw new IllegalArgumentException("Mean must be positive");
        }
        myMean = mean;

        if (variance <= 0) {
            throw new IllegalArgumentException("Variance must be positive");
        }
        myVar = variance;

        double d = myVar + myMean * myMean;
        double t = myMean * myMean;

        myNormalMu = Math.log((t) / Math.sqrt(d));
        myNormalSigma = Math.sqrt(Math.log(d / t));
    }

    @Override
    public final double getMean() {
        return myMean;
    }

    public final double getMoment3() {
        double calculatingM = (-(1 / 2) * Math.log((myVar / (myMean * myMean * myMean * myMean)) + 1));
        double calculatingS = Math.log((myVar / (myMean * myMean)) + (myMean * myMean));

        return Math.exp((3 * calculatingM) + (9 * calculatingS / 2));
    }

    public final double getMoment4() {
        double calculatingM = (-(1 / 2) * Math.log((myVar / (myMean * myMean * myMean * myMean)) + 1));
        double calculatingS = Math.log((myVar / (myMean * myMean)) + (myMean * myMean));

        return Math.exp((4 * calculatingM) + (8 * calculatingS));
    }

    @Override
    public final double getVariance() {
        return myVar;
    }

    /** Provides a normal distribution with correct parameters
     *  as related to this lognormal distribution
     * @return The Normal distribution
     */
    public final Normal getNormal() {
        return (new Normal(myNormalMu, myNormalSigma*myNormalSigma));
    }

    /** The mean of the underlying normal
     *
     * @return
     */
    public final double getNormalMean() {
        return myNormalMu;
    }

    /** The variance of the underlying normal
     *
     * @return
     */
    public final double getNormalVariance() {
        return myNormalSigma * myNormalSigma;
    }

    /** The standard deviation of the underlying normal
     *
     * @return
     */
    public final double getNormalStdDev() {
        return myNormalSigma;
    }

    @Override
    public final double cdf(double x) {
        if (x <= 0) {
            return (0.0);
        }
        double z = (Math.log(x) - myNormalMu) / myNormalSigma;
        return (Normal.stdNormalCDF(z));
    }

    /** Provides the inverse cumulative distribution function for the distribution
     * @param p The probability to be evaluated for the inverse, p must be [0,1] or
     * an IllegalArgumentException is thrown
     * p = 0.0 returns 0.0
     * p = 1.0 returns Double.POSITIVE_INFINITY
     * 
     * @return The inverse cdf evaluated at prob
     */
    @Override
    public final double invCDF(double p) {
        if ((p < 0.0) || (p > 1.0)) {
            throw new IllegalArgumentException("Supplied probability was " + p + " Probability must be [0,1)");
        }

        if (p <= 0.0) {
            return 0.0;
        }

        if (p >= 1.0) {
            return Double.POSITIVE_INFINITY;
        }

        double z = Normal.stdNormalInvCDF(p);
        double x = z * myNormalSigma + myNormalMu;
        return (Math.exp(x));
    }

    @Override
    public final double pdf(double x) {
        if (x <= 0) {
            return (0.0);
        }
        double z = (Math.log(x) - myNormalMu) / myNormalSigma;
        return (Normal.stdNormalPDF(z) / x);
    }

    /** Gets the skewness of the distribution
     * @return the skewness
     */
    public final double getSkewness() {
        double t = Math.exp(myNormalSigma * myNormalSigma);
        return (Math.sqrt(t - 1.0) * (t + 2.0));
    }

    /** Gets the kurtosis of the distribution
     * @return the kurtosis
     */
    public final double getKurtosis() {
        double t1 = Math.exp(4.0 * myNormalSigma * myNormalSigma);
        double t2 = Math.exp(3.0 * myNormalSigma * myNormalSigma);
        double t3 = Math.exp(2.0 * myNormalSigma * myNormalSigma);
        return (t1 + 2.0 * t2 + 3.0 * t3 - 6.0);
    }

    /** Sets the parameters for the distribution
     * mean = parameters[0] and variance = parameters[1]
     * @param parameters an array of doubles representing the parameters for
     * the distribution
     */
    @Override
    public final void setParameters(double[] parameters) {
        setParameters(parameters[0], parameters[1]);
    }

    /** Gets the parameters for the distribution
     *
     * @return Returns an array of the parameters for the distribution
     */
    @Override
    public final double[] getParameters() {
        double[] param = new double[2];
        param[0] = myMean;
        param[1] = myVar;
        return (param);
    }

    @Override
    public double firstOrderLossFunction(double x) {
        if (x <= 0.0) {
            return getMean() - x;
        }

        double z = (Math.log(x) - myNormalMu) / myNormalSigma;
        double t1 = Normal.stdNormalCDF(myNormalSigma - z);
        double t2 = Normal.stdNormalCDF(-z);
        double f1 = getMean() * t1 - x * t2;
        return f1;
    }

    @Override
    public double secondOrderLossFunction(double x) {
        double m = getMean();
        double m2 = getVariance() + m * m;
        if (x <= 0.0) {
            double f2 = 0.5 * (m2 - 2.0 * x * m + x * x);
            return f2;
        } else {
            double z = (Math.log(x) - myNormalMu) / myNormalSigma;
            double t1 = Normal.stdNormalCDF(2.0 * myNormalSigma - z);
            double t2 = Normal.stdNormalCDF(myNormalSigma - z);
            double t3 = Normal.stdNormalCDF(-z);
            double f2 = 0.5 * (m2 * t1 - 2.0 * x * m * t2 + x * x * t3);
            return f2;
        }
    }

    @Override
    public final RVariableIfc getRandomVariable(RngIfc rng) {
        return new LognormalRV(getMean(), getVariance(), rng);
    }
}
