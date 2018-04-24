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

import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RngIfc;

/**
 * Constructs a degenerate distribution with all probability at the provided
 * point. Once made the value of the constant cannot be changed.
 *
 * A default RngIfc is supplied, but it does not perform any random generation.
 * For efficiency purposes the default RngIfc is shared (common) across all
 * instances of Constant.
 *
 */
public class Constant extends Distribution implements DiscreteDistributionIfc {

    /**
     * A constant to represent zero for sharing
     */
    public final static Constant ZERO = new Constant(0.0);
    /**
     * A constant to represent one for sharing
     */
    public final static Constant ONE = new Constant(1.0);

    /**
     * A constant to represent two for sharing
     */
    public final static Constant TWO = new Constant(2.0);

    /**
     * A constant to represent positive infinity for sharing
     */
    public final static Constant POSITIVE_INFINITY = new Constant(Double.POSITIVE_INFINITY);

    protected double myValue;

    /**
     * Construct a constant using the supplied value
     *
     * @param value the value for the constant
     */
    public Constant(double value) {
        this(value, RNStreamFactory.getDefaultStream());
    }

    /**
     *
     * @param parameters
     */
    public Constant(double[] parameters) {
        this(parameters[0]);
    }

    /**
     * Construct a constant using the supplied value
     *
     * @param value the value for the constant
     * @param rng a RngIfc (pointless in this case since it is never used)
     */
    public Constant(double value, RngIfc rng) {
        super(rng);
        myValue = value;
    }

    /**
     * Returns a new instance of the random source with the same parameters but
     * an independent generator
     *
     * @return
     */
    @Override
    public Constant newInstance() {
        return (new Constant(getValue()));
    }

    /**
     * Returns a new instance of the random source with the same parameters with
     * the supplied RngIfc. Since the rng is not used for Constant this method
     * is defined for sub-class compatibility with Distribution
     *
     * @param rng
     * @return
     */
    @Override
    public Constant newInstance(RngIfc rng) {
        return (newInstance());
    }

    /**
     * Returns a new instance that will supply values based on antithetic U(0,1)
     * when compared to this distribution Since the rng is not used for Constant
     * this method is defined for sub-class compatibility with Distribution
     *
     * @return
     */
    @Override
    public Constant newAntitheticInstance() {
        return newInstance();
    }

    @Override
    public void setParameters(double[] parameters) {
    }

    @Override
    public final double[] getParameters() {
        double[] parameters = new double[1];
        parameters[0] = myValue;
        return parameters;
    }

    @Override
    public final double pmf(double x) {
        if (x == myValue) {
            return (1.0);
        } else {
            return (0.0);
        }
    }

    @Override
    public final double cdf(double x) {
        if (x < myValue) {
            return (0.0);
        } else {
            return (1.0);
        }
    }

    @Override
    public final double getMean() {
        return myValue;
    }

    @Override
    public final double getVariance() {
        return 0;
    }

    @Override
    public final double getValue() {
        return myValue;
    }

    @Override
    public final double invCDF(double p) {
        return myValue;
    }
}
