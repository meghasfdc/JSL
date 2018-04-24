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
import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RngIfc;
import jsl.utilities.random.rvariable.GetRVariableIfc;
import jsl.utilities.random.rvariable.RVariableIfc;
import jsl.utilities.random.rvariable.WeibullRV;

/** This class defines a Weibull distribution
 *
 */
public class Weibull extends Distribution implements ContinuousDistributionIfc,
        InverseCDFIfc, GetRVariableIfc {

    private double myShape; // alpha

    private double myScale;  // beta

    /** Creates new weibull with shape 1.0, scale 1.0
     */
    public Weibull() {
        this(1.0, 1.0, RNStreamFactory.getDefault().getStream());
    }

    /** Constructs a weibull distribution with
     * shape = parameters[0] and scale = parameters[1]
     * @param parameters An array with the shape and scale
     */
    public Weibull(double[] parameters) {
        this(parameters[0], parameters[1], RNStreamFactory.getDefault().getStream());
    }

    /** Constructs a weibull distribution with
     * shape = parameters[0] and scale = parameters[1]
     * @param parameters An array with the shape and scale
     * @param rng
     */
    public Weibull(double[] parameters, RngIfc rng) {
        this(parameters[0], parameters[1], rng);
    }

    /** Constructs a weibull distribution with supplied shape and scale
     *
     * @param shape The shape parameter of the distribution
     * @param scale The scale parameter of the distribution
     */
    public Weibull(double shape, double scale) {
        this(shape, scale, RNStreamFactory.getDefault().getStream());
    }

    /** Constructs a weibull distribution with supplied shape and scale
     *
     * @param shape The shape parameter of the distribution
     * @param scale The scale parameter of the distribution
     * @param rng A RngIfc
     */
    public Weibull(double shape, double scale, RngIfc rng) {
        super(rng);
        setParameters(shape, scale);
    }

    /** Returns a new instance of the random source with the same parameters
     *  but an independent generator
     *
     * @return
     */
    @Override
    public final Weibull newInstance() {
        return (new Weibull(getParameters()));
    }

    /** Returns a new instance of the random source with the same parameters
     *  with the supplied RngIfc
     * @param rng
     * @return
     */
    @Override
    public final Weibull newInstance(RngIfc rng) {
        return (new Weibull(getParameters(), rng));
    }

        /** Returns a new instance that will supply values based
     *  on antithetic U(0,1) when compared to this distribution
     *
     * @return
     */
    @Override
    public final Weibull newAntitheticInstance() {
        RngIfc a = myRNG.newAntitheticInstance();
        return newInstance(a);
    }

    @Override
    public final Interval getDomain(){
        return new Interval(0, Double.POSITIVE_INFINITY);
    }

    /** Sets the parameters
     * @param shape The shape parameter must &gt; 0.0
     * @param scale The scale parameter must be &gt; 0.0
     */
    public final void setParameters(double shape, double scale) {
        setShape(shape);
        setScale(scale);
    }

    /** Sets the parameters for the distribution with
     * shape = parameters[0] and scale = parameters[1]
     *
     * @param parameters an array of doubles representing the parameters for
     * the distribution
     */
    @Override
    public final void setParameters(double[] parameters) {
        setShape(parameters[0]);
        setScale(parameters[1]);
    }

    /** Gets the parameters for the distribution
     *
     * @return Returns an array of the parameters for the distribution
     */
    @Override
    public final double[] getParameters() {
        double[] param = new double[2];
        param[0] = myShape;
        param[1] = myScale;
        return (param);
    }

    /** Sets the shape parameter
     * @param shape The shape parameter must &gt; 0.0
     */
    public final void setShape(double shape) {
        if (shape <= 0) {
            throw new IllegalArgumentException("Shape parameter must be positive");
        }
        myShape = shape;
    }

    /** Sets the scale parameter
     * @param scale The scale parameter must be &gt; 0.0
     */
    public final void setScale(double scale) {
        if (scale <= 0) {
            throw new IllegalArgumentException("Scale parameter must be positive");
        }
        myScale = scale;
    }

    /** Gets the shape
     * @return The shape parameter as a double
     */
    public final double getShape() {
        return myShape;
    }

    /** Gets the scale parameter
     * @return The scale parameter as a double
     */
    public final double getScale() {
        return myScale;
    }

    @Override
    public final double getMean() { // shape = alpha, scale = beta
        double ia = 1.0 / myShape;
        double gia = Gamma.gammaFunction(ia);
        double m = myScale * ia * gia;
        return (m);
    }

    @Override
    public final double getVariance() {
        double ia = 1.0 / myShape;
        double gia = Gamma.gammaFunction(ia);
        double g2ia = Gamma.gammaFunction(2.0 * ia);
        double v = myScale * myScale * ia * (2.0 * g2ia - ia * gia * gia);
        return (v);
    }

    @Override
    public final double cdf(double x) {
        if (x > 0.0) {
            return 1 - Math.exp(-Math.pow(x / myScale, myShape));
        } else {
            return (0.0);
        }
    }

    @Override
    public final double pdf(double x) {
        if (x <= 0) {
            return (0.0);
        }
        double e1 = -Math.pow(x / myScale, myShape);
        double f = myScale * Math.pow(myScale, -myShape);
        f = f * Math.pow(x, myShape - 1.0);
        f = f * Math.exp(e1);
        return (f);
    }

    /** Returns the inverse cumulative distribution function of the triangular distribution
     * throws IllegalArgumentException if the value of the argument passed is beyond the range [0,1]
     * p = 0.0 returns 0.0
     * p = 1.0 returns Double.POSITIVE_INFINTITY

     * @param p the cumulative probability that requires the corresponding point
     * @return double the value in the distribution at which the cumulative distribution funtion equals the value of p
     *
     */
    @Override
    public final double invCDF(double p) {
        if ((p < 0.0) || (p > 1.0)) {
            throw new IllegalArgumentException("Supplied probability was " + p
                    + " Probability must be [0,1]");
        }

        if (p <= 0.0) {
            return 0.0;
        }

        if (p >= 1.0) {
            return Double.POSITIVE_INFINITY;
        }

        return myScale * Math.pow(-Math.log(1.0 - p), 1.0 / myShape);
    }

    public final double getMoment3() {
        return Math.pow(myShape, 3) * Math.exp(Gamma.logGammaFunction(1 + (3 * (1 / myScale))));
    }

    public final double getMoment4() {
        return Math.pow(myShape, 4) * Math.exp(Gamma.logGammaFunction(1 + (4 * (1 / myScale))));
    }

    /** Gets the kurtosis of the distribution
     * www.mathworld.wolfram.com/WeibullDistribution.html
     * @return the kurtosis
     */
    public final double kurtosis() {
        double c1 = (myShape + 1.0) / myShape;
        double c2 = (myShape + 2.0) / myShape;
        double c3 = (myShape + 3.0) / myShape;
        double c4 = (myShape + 4.0) / myShape;
        double gc1 = Gamma.gammaFunction(c1);
        double gc2 = Gamma.gammaFunction(c2);
        double gc3 = Gamma.gammaFunction(c3);
        double gc4 = Gamma.gammaFunction(c4);
        double n = -3.0 * gc1 * gc1 * gc1 * gc1 + 6.0 * gc1 * gc1 * gc2 - 4.0 * gc1 * gc3 + gc4;
        double d = (gc1 * gc1 - gc2) * (gc1 * gc1 - gc2);
        return ((n / d) - 3.0);
    }

    /** Gets the skewness of the distribution
     *  www.mathworld.wolfram.com/WeibullDistribution.html
     * @return the skewness
     */
    public final double skewness() {
        double c1 = (myShape + 1.0) / myShape;
        double c2 = (myShape + 2.0) / myShape;
        double c3 = (myShape + 3.0) / myShape;
        double gc1 = Gamma.gammaFunction(c1);
        double gc2 = Gamma.gammaFunction(c2);
        double gc3 = Gamma.gammaFunction(c3);
        double n = 2.0 * gc1 * gc1 * gc1 - 3.0 * gc1 * gc2 + gc3;
        double d = Math.sqrt((gc2 - gc1 * gc1) * (gc2 - gc1 * gc1) * (gc2 - gc1 * gc1));
        return (n / d);
    }

    @Override
    public final RVariableIfc getRandomVariable(RngIfc rng) {
        return new WeibullRV(myShape, myScale, rng);
    }
}
