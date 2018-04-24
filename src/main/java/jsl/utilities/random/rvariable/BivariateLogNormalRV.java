/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 *
 * Copyright (c) Manuel D. Rossetti (rossetti@uark.edu)
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
package jsl.utilities.random.rvariable;

import jsl.utilities.random.distributions.Normal;
import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RngIfc;

/** Allows for the generation of bivariate normal
 *  random variables
 *
 * @author rossetti
 */
public class BivariateLogNormalRV extends AbstractMVRVariable{

    private final  BivariateNormalRV myBVN;

    private final double myMu1;

    private final double myVar1;

    private final double myMu2;

    private final double myVar2;

    private final double myRho;

    /** Constructs a standard bivariate normal with no correlation
     *
     */
    public BivariateLogNormalRV() {
        this(1.0, 1.0, 1.0, 1.0, 0.0, RNStreamFactory.getDefault().getStream());
    }

    /** Constructs a standard bivariate normal with no correlation
     *
     */
    public BivariateLogNormalRV(RngIfc rng) {
        this(1.0, 1.0, 1.0, 1.0, 0.0, rng);
    }

    /**
     *
     * @param mean1
     * @param var1
     * @param mean2
     * @param var2
     * @param rho
     */
    public BivariateLogNormalRV(double mean1, double var1, double mean2, double var2, double rho) {
        this(mean1, var1, mean2, var2, rho, RNStreamFactory.getDefault().getStream());
    }

    /** Constructs a bivariate normal with the provided parameters
     *
     * @param m1
     * @param v1
     * @param m2
     * @param v2
     * @param r
     * @param rng
     */
    public BivariateLogNormalRV(double m1, double v1, double m2, double v2, double r, RngIfc rng) {
        super(rng);

        if (m1 <= 0) {
            throw new IllegalArgumentException("Mean 1 must be positive");
        }
        if (m2 <= 0) {
            throw new IllegalArgumentException("Mean 1 must be positive");
        }
        if (v1 <= 0) {
            throw new IllegalArgumentException("Variance 1 must be positive");
        }
        if (v2 <= 0) {
            throw new IllegalArgumentException("Variance 2 must be positive");
        }
        if ((r < -1.0) || (r > 1.0)) {
            throw new IllegalArgumentException("The correlation must be within [-1,1]");
        }
        // set the parameters
        myMu1 = m1;
        myVar1 = v1;
        myMu2 = m2;
        myVar2 = v2;
        myRho = r;

        // calculate parameters of underlying bivariate normal
        // get the means
        double mean1 = Math.log((m1 * m1) / Math.sqrt(m1 * m1 + v1));
        double mean2 = Math.log((m2 * m2) / Math.sqrt(m2 * m2 + v2));
        // get the variances
        double var1 = Math.log(1.0 + (v1 / Math.abs(m1 * m1)));
        double var2 = Math.log(1.0 + (v2 / Math.abs(m2 * m2)));
        // calculate the correlation

        double cov = Math.log(1.0 + ((r * Math.sqrt(v1 * v2)) / Math.abs(m1 * m2)));
        double rho = cov / Math.sqrt(var1 * var2);
        myBVN = new BivariateNormalRV(mean1, var1, mean2, var2, rho, rng);
    }


    /** Gets the first mean
     *
     * @return
     */
    public final double getMean1() {
        return myMu1;
    }

    /** Gets the first variance
     *
     * @return
     */
    public final double getVariance1() {
        return myVar1;
    }

    /** Gets the second mean
     *
     * @return
     */
    public final double getMean2() {
        return myMu2;
    }

    /** Gets the 2nd variance
     *
     * @return
     */
    public final double getVariance2() {
        return myVar2;
    }

    /** Gets the correlation
     *
     * @return
     */
    public final double getCorrelation() {
        return myRho;
    }

    /**
     *
     * @return
     */
    public double[] sample() {
        double[] x = myBVN.sample();
        // transform them to bivariate lognormal
        x[0] = Math.exp(x[0]);
        x[1] = Math.exp(x[1]);
        return x;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BivariateLogNormalRV{");
        sb.append("mu1=").append(myMu1);
        sb.append(", var1=").append(myVar1);
        sb.append(", mu2=").append(myMu2);
        sb.append(", var2=").append(myVar2);
        sb.append(", rho=").append(myRho);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public MVRVariableIfc newInstance(RngIfc rng) {
        return new BivariateLogNormalRV(myMu1, myVar1, myMu2, myVar2, myRho, rng);
    }
}
