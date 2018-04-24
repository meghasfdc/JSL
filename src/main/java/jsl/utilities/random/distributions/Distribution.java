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
import jsl.utilities.math.FunctionIfc;
import jsl.utilities.random.AbstractRandom;
import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RngIfc;
import jsl.utilities.rootfinding.BisectionRootFinder;

/**
 * An Distribution provides a skeletal implementation for classes that must
 * implement the DistributionIfc. This class is an abstract class. Subclasses
 * must provide concrete implementations. A Distribution generates random
 * variates via its getValue() method.
 *
 * Remarks:
 *
 * 1) Each Distribution that is created, instantiates its own reference to an
 * object that implements the RngIfc. By default this is an instance of
 * RngStream. The user can provide their own RngIfc using the
 * setRandomNumberGenerator() method. Subclasses can use this reference to
 * assist with random variate generation
 *
 */
public abstract class Distribution extends AbstractRandom implements DistributionIfc {

    private static BisectionRootFinder myRootFinder;

    private static Interval myInterval;

    /**
     * myRNG provides a reference to the underlying stream of random numbers
     */
    protected RngIfc myRNG;

    public Distribution() {
        this(RNStreamFactory.getDefault().getStream(), null);
    }

    /**
     * Constructs a probability distribution using the supplied RngIfc
     *
     * @param rng class that implements the RngIfc @returns a valid Distribution
     */
    public Distribution(RngIfc rng) {
        this(rng, null);
    }

    /**
     * Constructs a probability distribution using the supplied RngIfc
     *
     * @param rng a class that implements the RngIfc
     * @param name a String name @returns a valid Distribution
     */
    public Distribution(RngIfc rng, String name) {
        super(name);
        setRandomNumberGenerator(rng);
    }

    @Override
    public double cdf(double x1, double x2) {
        if (x1 > x2) {
            String msg = "x1 = " + x1 + " > x2 = " + x2 + " in cdf(x1,x2)";
            throw new IllegalArgumentException(msg);
        }

        return (cdf(x2) - cdf(x1));
    }

    @Override
    public double complementaryCDF(double x) {
        return (1.0 - cdf(x));
    }

    /**
     * Returns the antithetic of the last getValue() If getValue() has never
     * been called then returns Double.NaN
     *
     * @return
     */
    public double getAntitheticValue() {
        return invCDF(myRNG.getAntitheticValue());
    }

    @Override
    public double getValue() {
        return invCDF(myRNG.randU01());
    }

    @Override
    public final double getStandardDeviation() {
        return Math.sqrt(getVariance());
    }

    @Override
    public void advanceToNextSubstream() {
        myRNG.advanceToNextSubstream();
    }

    @Override
    public void resetStartStream() {
        myRNG.resetStartStream();
    }

    @Override
    public void resetStartSubstream() {
        myRNG.resetStartSubstream();
    }

    @Override
    public void setAntitheticOption(boolean flag) {
        myRNG.setAntitheticOption(flag);
    }

    @Override
    public boolean getAntitheticOption() {
        return myRNG.getAntitheticOption();
    }

    /**
     * Returns a new instance of the random source with the same parameters but
     * an independent generator
     *
     * @return
     */
    @Override
    abstract public Distribution newInstance();

    /**
     * Returns a new instance of the random source with the same parameters with
     * the supplied RngIfc
     *
     * @param rng
     * @return
     */
    @Override
    abstract public Distribution newInstance(RngIfc rng);

    /**
     * Returns a new instance that will supply values based on antithetic U(0,1)
     * when compared to this distribution
     *
     * @return
     */
    abstract public Distribution newAntitheticInstance();

    @Override
    public RngIfc getRandomNumberGenerator() {
        return (myRNG);
    }

    /**
     * Sets the underlying random number generator for the distribution Throws a
     * NullPointerException if rng is null
     *
     * @param rng the reference to the random number generator
     */
    public final void setRandomNumberGenerator(RngIfc rng) {
        if (rng == null) {
            throw new NullPointerException("RngIfc rng must be non-null");
        }
        myRNG = rng;
    }

    /**
     * Returns a string describing the distribution
     *
     *
     * @return a String representation of the distribution
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name ");
        sb.append(getName());
        sb.append("\n");
        sb.append("Mean ");
        sb.append(getMean());
        sb.append("\n");
        sb.append("Variance ");
        sb.append(getVariance());
        sb.append("\n");
        sb.append(myRNG);
        return (sb.toString());
    }

    /**
     * Computes the inverse CDF by using the bisection method [ll,ul] must
     * contain the desired value. Initial search point is (ll+ul)/2.0
     * 
     * [ll, ul] are defined on the domain of the CDF, i.e. the X values
     * 
     * @param cdf
     * @param p must be in [0,1]
     * @param ll lower limit of search range, must be &lt; ul
     * @param ul upper limit of search range, must be &gt; ll
     * @return the inverse of the CDF evaluated at p
     */
    public static double inverseContinuousCDFViaBisection(final ContinuousDistributionIfc cdf, final double p,
            double ll, double ul) {
        return inverseContinuousCDFViaBisection(cdf, p, ll, ul, (ll + ul) / 2.0);
    }

    /**
     * Computes the inverse CDF by using the bisection method [ll,ul] must
     * contain the desired value
     * 
     * [ll, ul] are defined on the domain of the CDF, i.e. the x values
     * 
     * @param cdf
     * @param p must be in [0,1]
     * @param ll lower limit of search range, must be &lt; ul
     * @param ul upper limit of search range, must be &gt; ll
     * @param initialX an initial starting point that must be in [ll,ul]
     * @return the inverse of the CDF evaluated at p
     */
    public static double inverseContinuousCDFViaBisection(final ContinuousDistributionIfc cdf, final double p,
            double ll, double ul, double initialX) {

        if (ll >= ul) {
            String msg = "Supplied lower limit " + ll + " must be less than upper limit " + ul;
            throw new IllegalArgumentException(msg);
        }

        if ((p < ll) || (p > ul)) {
            String msg = "Supplied probability was " + p + " Probability must be [0,1)";
            throw new IllegalArgumentException(msg);
        }

        if (myInterval == null) { // lazy initialization
            myInterval = new Interval(ll, ul);
        } else {
            myInterval.setInterval(ll, ul);
        }

        if (myRootFinder == null) {// lazy initialization
            myRootFinder = new BisectionRootFinder();
        }

        FunctionIfc f = new FunctionIfc() {

            @Override
            public double fx(double x) {
                return cdf.cdf(x) - p;
            }
        };

        if (!myRootFinder.hasRoot(ll, ul)) {
            String msg = "[" + ll + " ,  " + ul + " ] does not contain a root";
            throw new IllegalArgumentException(msg);
        }
        myRootFinder.setInterval(f, myInterval);
        myRootFinder.setInitialPoint(initialX);
        myRootFinder.evaluate();

        return myRootFinder.getResult();
    }

    /** Searches starting at the value start until the CDF &gt; p
     *  "start" must be the smallest possible value for the range of the CDF
     *  as an integer.  This requirement is NOT checked
     *  
     *  Each value is incremented by 1. Thus, the range of possible
     *  values for the CDF is assumed to be {start, start + 1, start + 2, etc.}
     * 
     * @param df
     * @param p
     * @param start
     * @return
     */
    public static double inverseDiscreteCDFViaSearchUp(DiscreteDistributionIfc df, double p, int start) {
        if ((p < 0.0) || (p > 1.0)) {
            throw new IllegalArgumentException("Supplied probability was " + p + " Probability must be [0,1)");
        }

        int i = start;
        while (p > df.cdf(i)) {
            i++;
        }
        return i;
    }
}
