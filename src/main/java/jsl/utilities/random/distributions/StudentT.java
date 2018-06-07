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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsl.utilities.random.distributions;

import jsl.utilities.Interval;
import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RNStreamIfc;

/** The Student T distribution
 *  
 *  See http://www.mth.kcl.ac.uk/~shaww/web_page/papers/Tdistribution06.pdf
 *  See http://en.wikipedia.org/wiki/Student's_t-distribution
 *  
 *  This implementation limits the degrees of freedom to be greater
 *  than or equal to 1.0
 * 
 *  Bailey's acceptance rejection is used for sampling by default but
 *  inverse transform can be selected
 * 
 * @author rossetti
 */
public class StudentT extends Distribution implements ContinuousDistributionIfc, InverseCDFIfc {

    /** A default instance for easily computing Student-T values
     * 
     */
    public static final StudentT defaultT = new StudentT();

    private double myDoF;

    private double myIntervalFactor = 6.0;

    private boolean myInvCDFSamplingOption = false;

    /** Constructs a StudentT distribution with 1.0 degree of freedom
     */
    public StudentT() {
        this(1.0, RNStreamFactory.getDefaultFactory().getStream());
    }

    /** Constructs a StudentT distribution with
     * parameters[0] = degrees of freedom 
     * @param parameters An array with the degrees of freedom
     */
    public StudentT(double[] parameters) {
        this(parameters[0], RNStreamFactory.getDefaultFactory().getStream());
    }

    /** Constructs a StudentT distribution with
     * parameters[0] = degrees of freedom
     * @param parameters An array with the v
     * @param rng
     */
    public StudentT(double[] parameters, RNStreamIfc rng) {
        this(parameters[0], rng);
    }

    /** Constructs a StudentT distribution dof degrees of freedom
     *
     * @param dof  degrees of freedom
     */
    public StudentT(double dof) {
        this(dof, RNStreamFactory.getDefaultFactory().getStream());
    }

    /** Constructs a StudentT distribution dof degrees of freedom
     *
     * @param dof  degrees of freedom
     * @param rng A RngIfc
     */
    public StudentT(double dof, RNStreamIfc rng) {
        super(rng);
        setDegreesOfFreedom(dof);
    }

    @Override
    public final StudentT newInstance() {
        return (new StudentT(getParameters()));
    }

    @Override
    public final StudentT newInstance(RNStreamIfc rng) {
        return (new StudentT(getParameters(), rng));
    }

    @Override
    public final StudentT newAntitheticInstance() {
        RNStreamIfc ac = myRNG.newAntitheticInstance();
        return newInstance(ac);
    }

    @Override
    public final Interval getDomain(){
        return new Interval(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    /** Sets the degrees of freedom for the distribution
     * 
     * @param dof must be &gt;= 1.0
     */
    public final void setDegreesOfFreedom(double dof) {
        if (dof < 1) {
            throw new IllegalArgumentException("The degrees of freedom must be >= 1.0");
        }
        myDoF = dof;
    }

    /** Used in the binary search to set the search interval for the inverse
     *  CDF. The default addFactor is 6.0
     * 
     *  The interval will be:
     *  start = Normal.stdNormalInvCDF(p)
     *  ll = start - getIntervalFactor()*getStandardDeviation();
     *  ul = start + getIntervalFactor()*getStandardDeviation();
     *
     * @return
     */
    public final double getIntervalFactor() {
        return myIntervalFactor;
    }

    /** Used in the binary search to set the search interval for the inverse
     *  CDF. The default addFactor is 6.0
     * 
     *  The interval will be:
     *  start = Normal.stdNormalInvCDF(p)
     *  ll = start - getIntervalFactor()*getStandardDeviation();
     *  ul = start + getIntervalFactor()*getStandardDeviation();
     *
     * @param factor
     */
    public final void setIntervalFactor(double factor) {
        if (factor < 1.0) {
            throw new IllegalArgumentException("The interval addFactor must >= 1");

        }
        myIntervalFactor = factor;
    }

    /** False means use Bailey's Box-Mueller acceptance rejection
     *  algorithm
     * 
     * @return true means use inverse CDF method for sampling
     */
    public boolean getInvCDFSamplingOption() {
        return myInvCDFSamplingOption;
    }

    /** False means use Bailey's Box-Mueller acceptance rejection
     *  algorithm
     * 
     * @param  option true means use inverse CDF method for sampling
     */
    public void setInvCDFSamplingOption(boolean option) {
        myInvCDFSamplingOption = option;
    }

    @Override
    public void setParameters(double[] parameters) {
        setDegreesOfFreedom(parameters[0]);
    }

    @Override
    public double[] getParameters() {
        double[] param = new double[1];
        param[0] = myDoF;
        return (param);
    }

    @Override
    public double getMean() {
        return 0.0;
    }

    @Override
    public double getVariance() {
        if (myDoF > 2.0) {
            return (myDoF / (myDoF - 2.0));
        } else {
            return Double.NaN;
        }
    }

    @Override
    public double pdf(double x) {
        if (myDoF == 1.0) {
            double d = Math.PI * (1.0 + x * x);
            return 1.0 / d;
        }
        if (myDoF == 2.0) {
            double d = Math.pow((2.0 + x * x), -1.5);
            return d;
        }
        double b1 = 1.0 / Math.sqrt(myDoF * Math.PI);
        double p = (myDoF + 1.0) / 2.0;
        double lnn1 = Gamma.gammaFunction(p);
        double lnd1 = Gamma.gammaFunction(myDoF / 2.0);
        double tmp = lnn1 - lnd1;
        double b2 = Math.exp(tmp);
        double b3 = 1.0 / Math.pow((1.0 + (x * x / myDoF)), p);
        return b1 * b2 * b3;
    }

    /** A convenience method that uses defaultT to 
     *  return the value of the CDF at the supplied x
     *  This method has the side effect of changing
     *  the degrees of freedom defaultT
     * 
     * @param dof
     * @param x
     * @return 
     */
    public static double getCDF(double dof, double x) {
        defaultT.setDegreesOfFreedom(dof);
        return defaultT.cdf(x);
    }

    /** A convenience method that uses defaultT to 
     *  return the value of the inverse CDF at the supplied p
     *  This method has the side effect of changing
     *  the degrees of freedom defaultT
     * 
     * @param dof
     * @param p
     * @return 
     */
    public static double getInvCDF(double dof, double p) {
        defaultT.setDegreesOfFreedom(dof);
        return defaultT.invCDF(p);
    }

    @Override
    public double cdf(double x) {
        if (myDoF == 1.0) {
            return 0.5 + (1.0 / Math.PI) * Math.atan(x);
        }
        if (myDoF == 2.0) {
            double d = x / (Math.sqrt(2.0 + x * x));
            d = 1.0 + d;
            return d / 2.0;
        }
        double y = myDoF / (x * x + myDoF);
        double a = myDoF / 2.0;
        double b = 1.0 / 2.0;
        double rBeta = Beta.regularizedIncompleteBetaFunction(y, a, b);
        return 0.5 * (1.0 + Math.signum(x) * (1.0 - rBeta));
    }

    @Override
    public double invCDF(double p) {
        if ((p < 0.0) || (p > 1.0)) {
            throw new IllegalArgumentException("Supplied probability was " + p + " Probability must be (0,1)");
        }

        if (p <= 0.0) {
            return Double.NEGATIVE_INFINITY;
        }

        if (p >= 1.0) {
            return Double.POSITIVE_INFINITY;
        }

        if (myDoF == 1.0) {
            return Math.tan(Math.PI * (p - 0.5));
        }
        if (myDoF == 2.0) {
            double n = 2.0 * p - 1.0;
            double d = Math.sqrt(2.0 * p * (1.0 - p));
            return n / d;
        }

        //use normal distribution to initialize bisection search
        double start = Normal.stdNormalInvCDF(p);
        double ll = start - getIntervalFactor() * getStandardDeviation();
        double ul = start + getIntervalFactor() * getStandardDeviation();
        return Distribution.inverseContinuousCDFViaBisection(this, p, ll, ul, start);
    }

    @Override
    public double getValue() {
        if (getInvCDFSamplingOption()) {
            return invCDF(myRNG.randU01());
        } else {
            return baileysAcceptanceRejection();
        }
    }

    /** Directly generate a random variate using Bailey's
     *  acceptance-rejection algorithm
     * 
     * @return
     */
    public final double baileysAcceptanceRejection() {
        double W;
        double U;
        do {
            double u = myRNG.randU01();
            double v = myRNG.randU01();
            U = 2.0 * u - 1.0;
            double V = 2.0 * v - 1.0;
            W = U * U + V * V;
        } while (W > 1.0);

        double tmp = myDoF * (Math.pow(W, (-2.0 / myDoF)) - 1.0) / W;
        double T = U * Math.sqrt(tmp);
        return T;
    }
}
