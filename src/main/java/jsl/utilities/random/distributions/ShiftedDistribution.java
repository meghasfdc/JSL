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

import jsl.utilities.random.rng.RngIfc;

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
    public ShiftedDistribution(DistributionIfc distribution, double shift, RngIfc rng) {
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
    public final ShiftedDistribution newInstance(RngIfc rng) {
        DistributionIfc d = (DistributionIfc) myDistribution.newInstance();
        return (new ShiftedDistribution(d, myShift, rng));
    }

    /** Returns a new instance that will supply values based
     *  on antithetic U(0,1) when compared to this distribution
     *
     * @return
     */
    public final ShiftedDistribution newAntitheticInstance() {
        RngIfc a = myRNG.newAntitheticInstance();
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
