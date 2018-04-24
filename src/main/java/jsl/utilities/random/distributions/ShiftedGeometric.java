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
import jsl.utilities.random.rvariable.GetRVariableIfc;
import jsl.utilities.random.rvariable.RVariableIfc;
import jsl.utilities.random.rvariable.ShiftedGeometricRV;

/** The ShiftedeGeometric distribution is the probability distribution of
 * the number of Bernoulli trials needed to get one success.
 * supported on the set {1, 2, 3, ... }
 *
 */
public class ShiftedGeometric extends Distribution implements DiscreteDistributionIfc,
        GetRVariableIfc {

    /**
     *  The probability of success on a trial
     */
    private double myProbSuccess;

    /**
     * The probability of failure on a trial
     */
    private double myProbFailure;

    /**
     *   Constructs a ShiftedGeometric with success probability = 0.5
     *   and a starting range of 0
     *
     */
    public ShiftedGeometric() {
        this(0.5, RNStreamFactory.getDefault().getStream());
    }

    /**
     *   Constructs a ShiftedGeometric using the supplied parameters array
     *   parameters[0] is probability of success

     * @param parameters
     */
    public ShiftedGeometric(double[] parameters) {
        this(parameters[0], RNStreamFactory.getDefault().getStream());
    }

    /**
     *   Constructs a ShiftedGeometric using the supplied parameters array
     *   parameters[0] is probability of success

     * @param parameters
     * @param rng
     */
    public ShiftedGeometric(double[] parameters, RngIfc rng) {
        this(parameters[0], rng);
    }

    /**  Constructs a ShiftedGeometric using the supplied success probability
     *
     * @param prob the probability of success
     */
    public ShiftedGeometric(double prob) {
        this(prob, RNStreamFactory.getDefault().getStream());
    }

    /** Constructs a ShiftedGeometric using the supplied success probability
     *
     *
     * @param prob
     * @param rng
     */
    public ShiftedGeometric(double prob, RngIfc rng) {
        super(rng);
        setProbabilityOfSuccess(prob);
    }

    /** Returns a new instance of the random source with the same parameters
     *  but an independent generator
     *
     * @return
     */
    @Override
    public final ShiftedGeometric newInstance() {
        return (new ShiftedGeometric(getParameters()));
    }

    /** Returns a new instance of the random source with the same parameters
     *  with the supplied RngIfc
     * @param rng
     * @return
     */
    @Override
    public final ShiftedGeometric newInstance(RngIfc rng) {
        return (new ShiftedGeometric(getParameters(), rng));
    }

    /** Returns a new instance that will supply values based
     *  on antithetic U(0,1) when compared to this distribution
     *
     * @return
     */
    @Override
    public final ShiftedGeometric newAntitheticInstance() {
        RngIfc a = myRNG.newAntitheticInstance();
        return newInstance(a);
    }

    /** Sets the probability of success
     *
     * @param prob
     */
    public final void setProbabilityOfSuccess(double prob) {
        if ((prob < 0.0) || (prob > 1.0)) {
            throw new IllegalArgumentException("Probability must be [0,1]");
        }
        myProbSuccess = prob;
        myProbFailure = 1.0 - myProbSuccess;
    }

    /** Gets the probability of success
     *
     * @return
     */
    public final double getProbabilityOfSuccess() {
        return myProbSuccess;
    }

    @Override
    public final double getMean() {
        return (1.0 + ((myProbFailure) / myProbSuccess));
    }

    @Override
    public final double getVariance() {
        return (myProbFailure) / (myProbSuccess * myProbSuccess);
    }

    /**  Sets the parameters using the supplied array
     * parameters[0] is probability of success
     * @param parameters
     */
    @Override
    public final void setParameters(double[] parameters) {
        setProbabilityOfSuccess(parameters[0]);
    }

    /** Gets the parameters as an array
     * parameters[0] is probability of success
     *
     */
    @Override
    public final double[] getParameters() {
        double[] param = new double[1];
        param[0] = myProbSuccess;
        return (param);
    }

    /** computes the pmf of the distribution
     *   f(x) = p(1-p)^(x-1.0)
     *
     * @param x
     * @return
     */
    public final double pmf(int x) {
        if (x < 1) {
            return 0.0;
        }
        return (myProbSuccess * Math.pow(myProbFailure, x - 1.0));
    }

    /** If x is not and integer value, then the probability must be zero
     *  otherwise pmf(int x) is used to determine the probability
     *
     * @param x
     * @return
     */
    @Override
    public final double pmf(double x) {
        if (Math.floor(x) == x) {
            return pmf((int) x);
        } else {
            return 0.0;
        }
    }

    /** computes the cdf of the distribution
     *   F(X&lt;=x)
     *
     * @param x
     * @return
     */
    @Override
    public final double cdf(double x) {
        if (x < 1) {
            return 0.0;
        }
        double xx = Math.floor(x - 1.0) + 1.0;
        return (1 - Math.pow(myProbFailure, xx));
    }

    /** Gets the inverse cdf for the distribution
     *
     *  @param prob Must be in range [0,1)
     */
    @Override
    public final double invCDF(double prob) {
        if ((prob < 0.0) || (prob > 1.0)) {
            throw new IllegalArgumentException("Supplied probability was " + prob + " Probability must be [0,1)");
        }

        if (JSLMath.equal(prob, 1.0, JSLMath.getMachinePrecision())) {
            throw new IllegalArgumentException("Supplied probability was within machine precision of 1.0 Probability must be (0,1)");
        }

        if (JSLMath.equal(prob, 0.0, JSLMath.getMachinePrecision())) {
            throw new IllegalArgumentException("Supplied probability was within machine precision of 0.0 Probability must be (0,1)");
        }

        return (1.0 + Math.ceil((Math.log(1.0 - prob) / (Math.log(1.0 - myProbSuccess))) - 1.0));
    }

    @Override
    public final RVariableIfc getRandomVariable(RngIfc rng) {
        return new ShiftedGeometricRV(myProbSuccess, rng);
    }
}
