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

import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RNStreamIfc;

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
    public Constant(double value, RNStreamIfc rng) {
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
    public Constant newInstance(RNStreamIfc rng) {
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
