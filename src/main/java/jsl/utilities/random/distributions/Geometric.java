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
package jsl.utilities.random.distributions;

import jsl.utilities.math.JSLMath;
import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RNStreamIfc;
import jsl.utilities.random.rvariable.GeometricRV;
import jsl.utilities.random.rvariable.GetRVariableIfc;
import jsl.utilities.random.rvariable.RVariableIfc;

/** The geometric distribution is the probability distribution of
 * the number Y = X − 1 of failures before the first success,
 * supported on the set { 0, 1, 2, 3, ... }, where X is the number of
 * Bernoulli trials needed to get one success.
 * 
 */
public class Geometric extends Distribution implements DiscreteDistributionIfc,
        LossFunctionDistributionIfc, GetRVariableIfc {

    /**
     *  The probability of success on a trial
     */
    private double myProbSuccess;

    /**
     * The probability of failure on a trial
     */
    private double myProbFailure;

    /**
     *   Constructs a Geometric with success probability = 0.5
     *
     */
    public Geometric() {
        this(0.5, RNStreamFactory.getDefaultFactory().getStream());
    }

    /**
     *   Constructs a Geometric using the supplied parameters array
     *   parameters[0] is probability of success
     * @param parameters
     */
    public Geometric(double[] parameters) {
        this(parameters[0], RNStreamFactory.getDefaultFactory().getStream());
    }

    /**
     *   Constructs a Geometric using the supplied parameters array
     *   parameters[0] is probability of success
     * @param parameters
     * @param rng
     */
    public Geometric(double[] parameters, RNStreamIfc rng) {
        this(parameters[0], rng);
    }

    /**  Constructs a Geometric using the supplied success probability
     * @param prob, the probability of success
     */
    public Geometric(double prob) {
        this(prob, RNStreamFactory.getDefaultFactory().getStream());
    }

    /** Constructs a Geometric using the supplied success probability
     *  and lower range
     *
     * @param prob
     * @param rng
     */
    public Geometric(double prob, RNStreamIfc rng) {
        super(rng);
        setProbabilityOfSuccess(prob);
    }

    /** Returns a new instance of the random source with the same parameters
     *  but an independent generator
     *
     * @return
     */
    @Override
    public final Geometric newInstance() {
        return (new Geometric(getParameters()));
    }

    /** Returns a new instance of the random source with the same parameters
     *  with the supplied RngIfc
     * @param rng
     * @return
     */
    @Override
    public final Geometric newInstance(RNStreamIfc rng) {
        return (new Geometric(getParameters(), rng));
    }

    /** Returns a new instance that will supply values based
     *  on antithetic U(0,1) when compared to this distribution
     *
     * @return
     */
    @Override
    public final Geometric newAntitheticInstance() {
        RNStreamIfc a = myRNG.newAntitheticInstance();
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
        return (((myProbFailure) / myProbSuccess));
    }

    @Override
    public final double getVariance() {
        return (myProbFailure) / (myProbSuccess * myProbSuccess);
    }

    /**  Sets the parameters using the supplied array
     * parameters[0] is probability of success
     *  parameters[1] is lower range
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
     *   f(x) = p(1-p)^(x) for x&gt;=0, 0 otherwise
     *
     * @param x
     * @return
     */
    public final double pmf(int x) {
        if (x < 0) {
            return 0.0;
        }
        return (myProbSuccess * Math.pow(myProbFailure, x));
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
     * @param x, must be &gt;= lower limit
     * @return
     */
    @Override
    public final double cdf(double x) {
        if (x < 0.0) {
            return 0.0;
        }
        double xx = Math.floor(x) + 1.0;
        return (1 - Math.pow(myProbFailure, xx));
    }

    /** Gets the inverse cdf for the distribution
     *
     *  @param prob Must be in range [0,1)
     */
    @Override
    public final double invCDF(double prob) {
        if ((prob < 0.0) || (prob > 1.0)) {
            throw new IllegalArgumentException("Supplied probability was " + prob + " Probability must be (0,1)");
        }

        if (JSLMath.equal(prob, 1.0, JSLMath.getMachinePrecision())) {
            throw new IllegalArgumentException("Supplied probability was within machine precision of 1.0 Probability must be (0,1)");
        }

        if (JSLMath.equal(prob, 0.0, JSLMath.getMachinePrecision())) {
            throw new IllegalArgumentException("Supplied probability was within machine precision of 0.0 Probability must be (0,1)");
        }

        return (Math.ceil((Math.log(1.0 - prob) / (Math.log(1.0 - myProbSuccess))) - 1.0));
    }

    /* (non-Javadoc)
     * @see jsl.utilities.random.LossFunctionDistributionIfc#firstOrderLossFunction(double)
     */
    @Override
    public double firstOrderLossFunction(double x) {
        double mu = getMean();
        if (x < 0.0) {
            return (Math.floor(Math.abs(x)) + mu);
        } else if (x > 0.0) {
            double b, q, g1, p;
            p = myProbSuccess;
            q = myProbFailure;
            b = (1.0 - p) / p;
            g1 = b * Math.pow(q, x);
            return g1;
        } else // x== 0.0
        {
            return mu;
        }
    }

    /* (non-Javadoc)
     * @see jsl.utilities.random.LossFunctionDistributionIfc#secondOrderLossFunction(double)
     */
    @Override
    public double secondOrderLossFunction(double x) {
        double mu = getMean();
        double sbm = 0.5 * (getVariance() + mu * mu - mu);// 1/2 the 2nd binomial moment
        if (x < 0.0) {
            double s = 0.0;
            for (int y = 0; y > x; y--) {
                s = s + firstOrderLossFunction(y);
            }
            return (s + sbm);
        } else if (x > 0.0) {
            double b, q, g2, p;
            p = myProbSuccess;
            q = myProbFailure;
            b = (1.0 - p) / p;
            g2 = b * b * Math.pow(q, x);
            return g2;
        } else // x == 0.0
        {
            return sbm;
        }
    }

    @Override
    public final RVariableIfc getRandomVariable(RNStreamIfc rng) {
        return new GeometricRV(myProbSuccess, rng);
    }
}
