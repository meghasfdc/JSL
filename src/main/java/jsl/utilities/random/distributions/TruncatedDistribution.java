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

import jsl.utilities.math.JSLMath;
import jsl.utilities.random.rng.RngIfc;

/**
 */
public class TruncatedDistribution extends Distribution {

    protected DistributionIfc myDistribution;

    protected double myLowerLimit;

    protected double myUpperLimit;

    protected double myCDFLL;

    protected double myCDFUL;

    protected double myFofLL;

    protected double myFofUL;

    protected double myDeltaFUFL;

    /** Constructs a truncated distribution based on the provided distribution
     *
     * @param distribution
     * @param cdfLL The lower limit of the range of support of the distribution
     * @param cdfUL  The upper limit of the range of support of the distribution
     * @param truncLL The truncated lower limit (if moved in from cdfLL), must be &gt;= cdfLL
     * @param truncUL The truncated upper limit (if moved in from cdfUL), must be &lt;= cdfUL
     */
    public TruncatedDistribution(DistributionIfc distribution, double cdfLL, double cdfUL,
            double truncLL, double truncUL) {
        this(distribution, cdfLL, cdfUL, truncLL, truncUL, distribution.getRandomNumberGenerator());
    }

    /** Constructs a truncated distribution based on the provided distribution
     *
     * @param distribution
     * @param cdfLL The lower limit of the range of support of the distribution
     * @param cdfUL  The upper limit of the range of support of the distribution
     * @param truncLL The truncated lower limit (if moved in from cdfLL), must be &gt;= cdfLL
     * @param truncUL The truncated upper limit (if moved in from cdfUL), must be &lt;= cdfUL
     * @param rng
     */
    public TruncatedDistribution(DistributionIfc distribution, double cdfLL, double cdfUL,
            double truncLL, double truncUL, RngIfc rng) {
        super(rng);
        setDistribution(distribution, cdfLL, cdfUL, truncLL, truncUL);
    }

    /** Returns a new instance of the random source with the same parameters
     *  but an independent generator
     *
     * @return
     */
    public final TruncatedDistribution newInstance() {
        DistributionIfc d = (DistributionIfc) myDistribution.newInstance();
        return (new TruncatedDistribution(d, myCDFLL, myCDFUL, myLowerLimit, myUpperLimit));
    }

    /** Returns a new instance of the random source with the same parameters
     *  but an independent generator
     *
     * @return
     */
    public final TruncatedDistribution newInstance(RngIfc rng) {
        DistributionIfc d = (DistributionIfc) myDistribution.newInstance();
        return (new TruncatedDistribution(d, myCDFLL, myCDFUL, myLowerLimit, myUpperLimit, rng));
    }

    /** Returns a new instance that will supply values based
     *  on antithetic U(0,1) when compared to this distribution
     *
     * @return
     */
    public final TruncatedDistribution newAntitheticInstance() {
        RngIfc a = myRNG.newAntitheticInstance();
        return newInstance(a);
    }

    public final void setDistribution(DistributionIfc distribution, double cdfLL, double cdfUL,
            double truncLL, double truncUL) {
        if (distribution == null) {
            throw new IllegalArgumentException("The distribution must not be null");
        }
        myDistribution = distribution;
        setLimits(cdfLL, cdfUL, truncLL, truncUL);
    }

    public final void setLimits(double cdfLL, double cdfUL, double truncLL, double truncUL) {
        if (truncLL >= truncUL) {
            throw new IllegalArgumentException("The lower limit must be < the upper limit");
        }

        if (truncLL < cdfLL) {
            throw new IllegalArgumentException("The lower limit must be >= " + cdfLL);
        }

        if (truncUL > cdfUL) {
            throw new IllegalArgumentException("The upper limit must be <= " + cdfUL);
        }

        if ((truncLL == cdfLL) && (truncUL == cdfUL)) {
            throw new IllegalArgumentException("There was no truncation over the interval of support");
        }

        myLowerLimit = truncLL;
        myUpperLimit = truncUL;
        myCDFLL = cdfLL;
        myCDFUL = cdfUL;
        if ((truncLL > cdfLL) && (truncUL < cdfUL)) {
            // truncation on both ends
            myFofUL = myDistribution.cdf(myUpperLimit);
            myFofLL = myDistribution.cdf(myLowerLimit);
        } else if (truncUL < cdfUL) { // truncation on upper tail
            // must be that upperLimit < UL, and lowerLimit == LL
            myFofUL = myDistribution.cdf(myUpperLimit);
            myFofLL = 0.0;
        } else { //truncation on the lower tail
            // must be that upperLimit == UL, and lowerLimit > LL
            myFofUL = 1.0;
            myFofLL = myDistribution.cdf(myLowerLimit);
        }

        myDeltaFUFL = myFofUL - myFofLL;

        if (JSLMath.equal(myDeltaFUFL, 0.0)) {
            throw new IllegalArgumentException("The supplied limits have no probability support (F(upper) - F(lower) = 0.0)");
        }
    }

    /** Sets the parameters of the truncated distribution
     *  cdfLL = parameter[0]
     *  cdfUL = parameters[1]
     *  truncLL = parameters[2]
     *  truncUL = parameters[3]
     *
     *  any other values in the array should be interpreted as the parameters
     *  for the underlying distribution
     *
     */
    public final void setParameters(double[] parameters) {
        if (parameters == null) {
            throw new IllegalArgumentException("The parameters array was null");
        }

        setLimits(parameters[0], parameters[1], parameters[2], parameters[3]);
        double[] y = new double[parameters.length - 4];
        for (int i = 0; i < y.length; i++) {
            y[i] = parameters[i + 4];
        }
        myDistribution.setParameters(y);
    }

    /** Get the parameters for the truncated distribution
     *
     *  cdfLL = parameter[0]
     *  cdfUL = parameters[1]
     *  truncLL = parameters[2]
     *  truncUL = parameters[3]
     *
     *  any other values in the array should be interpreted as the parameters
     *  for the underlying distribution
     *
     */
    public final double[] getParameters() {
        double[] x = myDistribution.getParameters();
        double[] y = new double[x.length + 4];

        y[0] = myCDFLL;
        y[1] = myCDFUL;
        y[2] = myLowerLimit;
        y[3] = myUpperLimit;
        for (int i = 0; i < x.length; i++) {
            y[i + 4] = x[i];
        }
        return y;
    }

    /** The CDF's original lower limit
     *
     * @return
     */
    public final double getCDFLowerLimit() {
        return (myCDFLL);
    }

    /** The CDF's original upper limit
     *
     * @return
     */
    public final double getCDFUpperLimit() {
        return (myCDFUL);
    }

    /** The lower limit for the truncated distribution
     *
     * @return
     */
    public final double getTruncatedLowerLimit() {
        return (myLowerLimit);
    }

    /** The upper limit for the trunctated distribution
     *
     * @return
     */
    public final double getTruncatedUpperLimit() {
        return (myUpperLimit);
    }

    /* (non-Javadoc)
     * @see jsl.utilities.random.DistributionIfc#cdf(double)
     */
    public final double cdf(double x) {
        if (x < myLowerLimit) {
            return 0.0;
        } else if ((x >= myLowerLimit) && (x <= myUpperLimit)) {
            double F = myDistribution.cdf(x);
            return ((F - myFofLL) / myDeltaFUFL);
        } else //if (x > myUpperLimit)
        {
            return 1.0;
        }
    }

    /* (non-Javadoc)
     * @see jsl.utilities.random.DistributionIfc#getMean()
     */
    public final double getMean() {
        double mu = myDistribution.getMean();
        return (mu / myDeltaFUFL);
    }

    /* (non-Javadoc)
     * @see jsl.utilities.random.DistributionIfc#getVariance()
     */
    public final double getVariance() {
        // Var[X] = E[X^2] - E[X]*E[X]
        // first get 2nd moment of truncated distribution
        // E[X^2] = 2nd moment of original cdf/(F(b)-F(a)
        double mu = myDistribution.getMean();
        double s2 = myDistribution.getVariance();
        // 2nd moment of original cdf
        double m2 = s2 + mu * mu;
        // 2nd moment of truncated
        m2 = m2 / myDeltaFUFL;
        // mean of truncated
        mu = getMean();
        return (m2 - mu * mu);
    }

    /* (non-Javadoc)
     * @see jsl.utilities.random.DistributionIfc#invCDF(double)
     */
    public double invCDF(double p) {
        double v = myFofLL + myDeltaFUFL * p;
        return myDistribution.invCDF(v);
    }
}
