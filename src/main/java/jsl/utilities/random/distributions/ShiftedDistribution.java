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

import jsl.utilities.random.rng.RNStreamIfc;

/** Represents a Distribution that has been Shifted (translated to the right)
 *  The shift must be &gt;= 0.0
 */
public class ShiftedDistribution extends Distribution {

    protected DistributionIfc myDistribution;

    protected LossFunctionDistributionIfc myLossFunctionDistribution;

    protected double myShift;

    /** Constructs a shifted distribution based on the provided distribution
     *
     * @param distribution
     * @param shift The linear shift
     */
    public ShiftedDistribution(DistributionIfc distribution, double shift) {
        this(distribution, shift, distribution.getRandomNumberGenerator());
    }

    /** Constructs a shifted distribution based on t he provided distribution
     *
     * @param distribution
     * @param shift The linear shift
     * @param rng
     */
    public ShiftedDistribution(DistributionIfc distribution, double shift, RNStreamIfc rng) {
        super(rng);
        setDistribution(distribution, shift);
    }

    /** Returns a new instance of the random source with the same parameters
     *  but an independent generator
     *
     * @return
     */
    public final ShiftedDistribution newInstance() {
        DistributionIfc d = (DistributionIfc) myDistribution.newInstance();
        return (new ShiftedDistribution(d, myShift));
    }

    /** Returns a new instance of the random source with the same parameters
     *  with the supplied RngIfc
     * @param rng
     * @return
     */
    public final ShiftedDistribution newInstance(RNStreamIfc rng) {
        DistributionIfc d = (DistributionIfc) myDistribution.newInstance();
        return (new ShiftedDistribution(d, myShift, rng));
    }

    /** Returns a new instance that will supply values based
     *  on antithetic U(0,1) when compared to this distribution
     *
     * @return
     */
    public final ShiftedDistribution newAntitheticInstance() {
        RNStreamIfc a = myRNG.newAntitheticInstance();
        return newInstance(a);
    }

    /** Changes the underlying distribution and the shift
     *
     * @param distribution must not be null
     * @param shift must be &gt;=0.0
     */
    public final void setDistribution(DistributionIfc distribution, double shift) {
        if (distribution == null) {
            throw new IllegalArgumentException("The distribution must not be null");
        }
        myDistribution = distribution;
        setShift(shift);
    }

    /** Changes the shift
     *
     * @param shift must be &gt;=0.0
     */
    public final void setShift(double shift) {
        if (shift < 0.0) {
            throw new IllegalArgumentException("The shift should not be < 0.0");
        }
        myShift = shift;
    }

    /** Sets the parameters of the shifted distribution
     * shift = parameter[0]
     * If supplied, the other elements of the array are used in setting the
     * parameters of the underlying distribution.  If only the shift is supplied
     * as a parameter, then the underlying distribution's parameters are not changed
     * (and do not need to be supplied)
     */
    public void setParameters(double[] parameters) {
        if (parameters == null) {
            throw new IllegalArgumentException("The parameters array was null");
        }
        setShift(parameters[0]);
        if (parameters.length == 1) {
            return;
        }

        double[] y = new double[parameters.length - 1];

        for (int i = 0; i < y.length; i++) {
            y[i] = parameters[i + 1];
        }
        myDistribution.setParameters(y);
    }

    /* (non-Javadoc)
     * @see jsl.utilities.random.DistributionIfc#cdf(double)
     */
    public double cdf(double x) {
        if (x < myShift) {
            return 0.0;
        } else {
            return myDistribution.cdf(x - myShift);
        }
    }

    /* (non-Javadoc)
     * @see jsl.utilities.random.DistributionIfc#getMean()
     */
    public double getMean() {
        return myShift + myDistribution.getMean();
    }

    /** Gets the parameters for the shifted distribution
     * shift = parameter[0]
     * The other elements of the returned array are
     * the parameters of the underlying distribution
     */
    public double[] getParameters() {
        double[] x = myDistribution.getParameters();
        double[] y = new double[x.length + 1];

        y[0] = myShift;
        for (int i = 0; i < x.length; i++) {
            y[i + 1] = x[i];
        }
        return y;
    }

    /* (non-Javadoc)
     * @see jsl.utilities.random.DistributionIfc#getVariance()
     */
    public double getVariance() {
        return myDistribution.getVariance();
    }

    /* (non-Javadoc)
     * @see jsl.utilities.random.DistributionIfc#invCDF(double)
     */
    public double invCDF(double p) {
        return (myDistribution.invCDF(p) + myShift);
    }
}
