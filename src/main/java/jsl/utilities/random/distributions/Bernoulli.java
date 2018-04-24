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
import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RngIfc;
import jsl.utilities.random.rvariable.BernoulliRV;
import jsl.utilities.random.rvariable.GetRVariableIfc;
import jsl.utilities.random.rvariable.RVariableIfc;

/** An Bernouli provides an implementation of the Bernouli
 *  distribution with success probability (p)
 *  P(X=1) = p
 *  P(X=0) = 1-p
 */
public class Bernoulli extends Distribution implements DiscreteDistributionIfc, GetRVariableIfc {
    // private data members

    private double myProbSuccess;

    private double myProbFailure;

    /**
     * Constructs a probability distribution with the default
     * random number generator, the default is Bernouli(0.5)
     *
     */
    public Bernoulli() {
        this(0.5, RNStreamFactory.getDefault().getStream());
    }

    /** Constructs a probability distribution with the default
     * random number generator,
     *
     * @param parameters should be an array with parameter[0]=p
     */
    public Bernoulli(double[] parameters) {
        this(parameters[0], RNStreamFactory.getDefault().getStream());
    }

    /** Constructs a probability distribution
     *
     * @param parameters should be an array with parameter[0]=p
     * @param rng
     */
    public Bernoulli(double[] parameters, RngIfc rng) {
        this(parameters[0], rng);
    }

    /** Constructs a probability distribution with the default
     * random number generator,
     *
     * @param prob is the success probability
     */
    public Bernoulli(double prob) {
        this(prob, RNStreamFactory.getDefault().getStream());
    }

    /** Constructs a probability distribution with the default
     * random number generator,
     *
     * @param prob is the success probability
     * @param rng a class that implements the RngIfc
     */
    public Bernoulli(double prob, RngIfc rng) {
        super(rng);
        setProbabilityOfSuccess(prob);
    }

    /** Returns a new instance of the random source with the same parameters
     *  but an independent generator
     *
     * @return
     */
    @Override
    public final Bernoulli newInstance() {
        return (new Bernoulli(getParameters()));
    }

    /** Returns a new instance of the random source with the same parameters
     *  with the supplied RngIfc
     * @param rng
     * @return
     */
    @Override
    public final Bernoulli newInstance(RngIfc rng) {
        return (new Bernoulli(getParameters(), rng));
    }

    /** Returns a new instance that will supply values based
     *  on antithetic U(0,1) when compared to this distribution
     *
     * @return
     */
    @Override
    public final Bernoulli newAntitheticInstance() {
        RngIfc a = myRNG.newAntitheticInstance();
        return newInstance(a);
    }

    /** Sets the success probability
     * @param prob The success probability
     */
    public final void setProbabilityOfSuccess(double prob) {
        if ((prob < 0.0) || (prob > 1.0)) {
            throw new IllegalArgumentException("Probability must be [0,1]");
        }
        myProbSuccess = prob;
        myProbFailure = 1.0 - myProbSuccess;
    }

    /** Gets the success probability
     * @return The success probability
     */
    public final double getProbabilityOfSuccess() {
        return (myProbSuccess);
    }

    /** Returns the next random number 1 or 0
     * @return The random number
     */
    @Override
    public final double getValue() {
        if (myProbSuccess == 1.0) {
            return (1.0);
        } else if (myProbSuccess == 0.0) {
            return (0.0);
        } else {
            double prob = myRNG.randU01();
            return (invCDF(prob));
        }
    }

    /** Returns a randomly generated boolean according to the Bernoulli distribution
     *
     * @return
     */
    public final boolean nextBoolean() {
        if (getValue() == 0.0) {
            return (false);
        } else {
            return (true);
        }
    }
    
    /** Returns a boolean array filled via nextBoolean()
     * 
     * @param n the generate size, must be at least 1
     * @return the array
     */
    public final boolean[] getBooleanSample(int n){
        if (n <= 0){
            throw new IllegalArgumentException("The generate size must be > 0");
        }
        boolean[] b = new boolean[n];
        for(int i=0;i<n;i++){
            b[i] = nextBoolean();
        }
        return b;
    }

    /** Returns the P(X&lt;=x)
     * @param xx The value we want the cumulative probability up to
     * @return The cumulative probability
     */
    @Override
    public final double cdf(double xx) {
        int x = (int) xx;

        if (x < 0) {
            return 0.0;
        } else if (x >= 0 && x < 1) {
            return myProbFailure;
        } else //if (x >= 1)
        {
            return 1.0;
        }
    }

    /** Returns the value of x such that p = Pr{X &lt;= x} where F represents the
     * cumulative distribution function
     *
     * @param prob a double representing the probability
     * @return a double representing the bernoulli variate
     */
    @Override
    public final double invCDF(double prob) {
        if ((prob < 0.0) || (prob > 1.0)) {
            throw new IllegalArgumentException("Probability must be [0,1]");
        }

        if (prob <= myProbSuccess) {
            return (1.0);
        } else {
            return (0.0);
        }
    }

    /** Returns the mean of the distribution if defined
     * @return double  the mean or expected value for the distribution
     */
    @Override
    public final double getMean() {
        return myProbSuccess;
    }

    /** Returns the f(x) where f represents the probability
     * mass function for the distribution.
     * If JSLMath.equal(x,1.0) the probability of success is returned
     * If JSLMath.equal(x,0.0) the probability of failure is returned
     * otherwise 0.0 is returned.
     *
     * @param x a double representing the value to be evaluated
     * @return f(x)
     */
    @Override
    public final double pmf(double x) {
        if (JSLMath.equal(x, 0.0)) {
            return myProbFailure;
        } else if (JSLMath.equal(x, 1.0)) {
            return myProbSuccess;
        } else {
            return 0.0;
        }
    }

    /** Returns the f(x) where f represents the probability
     *  mass function for the distribution.
     *
     * @param x an int representing the value to be evaluated
     * @return f(x)
     */
    public final double pmf(int x) {
        if (x == 0) {
            return myProbFailure;
        } else if (x == 1) {
            return myProbSuccess;
        } else {
            return 0.0;
        }
    }

    /** Returns the variance of the random variate if defined
     * @return double  the variance of the random variable
     */
    @Override
    public final double getVariance() {
        return myProbSuccess * myProbFailure;
    }

    /** Sets the parameters for the distribution
     *
     * @param parameters an array of doubles representing the parameters for
     * the distribution
     */
    @Override
    public final void setParameters(double[] parameters) {
        setProbabilityOfSuccess(parameters[0]);
    }

    /** Gets the parameters for the distribution
     *
     * @return Returns an array of the parameters for the distribution
     */
    @Override
    public final double[] getParameters() {
        double[] param = new double[1];
        param[0] = myProbSuccess;
        return (param);
    }

    @Override
    public final RVariableIfc getRandomVariable(RngIfc rng) {
        return new BernoulliRV(myProbSuccess, rng);
    }
}
