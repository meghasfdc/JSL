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

import jsl.utilities.random.ParametersIfc;
import jsl.utilities.random.distributions.Normal;
import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RandomStreamIfc;
import jsl.utilities.random.rng.RngIfc;

/** Allows for the generation of bivariate normal
 *  random variables
 *
 * @author rossetti
 */
public class BivariateNormalRV extends AbstractMVRVariable{

    private final double myMu1;

    private final double myVar1;

    private final double myMu2;

    private final double myVar2;

    private final double myRho;

    /** Constructs a standard bivariate normal with no correlation
     *
     */
    public BivariateNormalRV() {
        this(0.0, 1.0, 0.0, 1.0, 0.0, RNStreamFactory.getDefault().getStream());
    }

    /** Constructs a standard bivariate normal with no correlation
     *
     */
    public BivariateNormalRV(RngIfc rng) {
        this(0.0, 1.0, 0.0, 1.0, 0.0, rng);
    }

    /**
     *
     * @param mean1
     * @param var1
     * @param mean2
     * @param var2
     * @param rho
     */
    public BivariateNormalRV(double mean1, double var1, double mean2, double var2, double rho) {
        this(mean1, var1, mean2, var2, rho, RNStreamFactory.getDefault().getStream());
    }

    /** Constructs a bivariate normal with the provided parameters
     *
     * @param mean1
     * @param var1
     * @param mean2
     * @param var2
     * @param rho
     * @param rng
     */
    public BivariateNormalRV(double mean1, double var1, double mean2, double var2, double rho, RngIfc rng) {
        super(rng);
        if (var1 <= 0) {
            throw new IllegalArgumentException("The first variance was <=0");
        }
        if (var2 <= 0) {
            throw new IllegalArgumentException("The second variance was <=0");
        }
        if ((rho < -1.0) || (rho > 1.0)) {
            throw new IllegalArgumentException("The correlation must be [-1,1]");
        }
        myMu1 = mean1;
        myMu2 = mean2;
        myVar1 = var1;
        myVar2 = var2;
        myRho = rho;
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
    public final double[] sample() {
        double[] x = new double[2];
        double z0 = Normal.stdNormalInvCDF(myRNG.randU01());
        double z1 = Normal.stdNormalInvCDF(myRNG.randU01());
        double s1 = Math.sqrt(myVar1);
        double s2 = Math.sqrt(myVar2);
        x[0] = myMu1 + s1 * z0;
        x[1] = myMu2 + s2 * (myRho * z0 + Math.sqrt(1.0 - myRho * myRho) * z1);
        return x;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("BivariateNormalRV{");
        sb.append("mu1=").append(myMu1);
        sb.append(", var1=").append(myVar1);
        sb.append(", mu2=").append(myMu2);
        sb.append(", var2=").append(myVar2);
        sb.append(", rho=").append(myRho);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public final MVRVariableIfc newInstance(RngIfc rng) {
        return new BivariateNormalRV(myMu1, myVar1, myMu2, myVar2, myRho, rng);
    }
}
