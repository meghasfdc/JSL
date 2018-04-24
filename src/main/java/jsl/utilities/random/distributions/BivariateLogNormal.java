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
package jsl.utilities.random.distributions;

import jsl.utilities.random.ParametersIfc;
import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RandomStreamIfc;
import jsl.utilities.random.rng.RngIfc;

/**  Allows for the generation of bivariate lognormal random variables.
 *
 *   Note that this class takes in the actual parameters of the bivariate
 *   lognormal and computes the necessary parameters for the underlying bivariate normal
 *
 * @author rossetti
 */
public class BivariateLogNormal implements RandomStreamIfc, ParametersIfc {

    protected BivariateNormal myBVN;

    protected double myMu1;

    protected double myVar1;

    protected double myMu2;

    protected double myVar2;

    protected double myRho;

    /** Constructs a bivariate lognormal with mean's = 1.0, variance = 1.0. correlation = 0.0
     * 
     */
    public BivariateLogNormal() {
        this(1.0, 1.0, 1.0, 1.0, 0.0, RNStreamFactory.getDefault().getStream());
    }

    /** Constructs a bivariate lognormal with mean's = 1.0, variance = 1.0. correlation = 0.0
     *
     * @param rng
     */
    public BivariateLogNormal(RngIfc rng) {
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
    public BivariateLogNormal(double mean1, double var1, double mean2, double var2, double rho) {
        this(mean1, var1, mean2, var2, rho, RNStreamFactory.getDefault().getStream());
    }

    /** Interprets the array of parameters as the parameters
     *  param[0] = mean 1;
     *  param[1] = variance 1;
     *  param[2] = mean 2;
     *  param[3] = variance 2;
     *  param[4] = correlation;
     *
     * @param param
     */
    public BivariateLogNormal(double[] param) {
        this(param[0], param[1], param[2], param[3], param[4],
                RNStreamFactory.getDefault().getStream());
    }

    /** Interprets the array of parameters as the parameters
     *  param[0] = mean 1;
     *  param[1] = variance 1;
     *  param[2] = mean 2;
     *  param[3] = variance 2;
     *  param[4] = correlation;
     *
     * @param param
     * @param rng
     */
    public BivariateLogNormal(double[] param, RngIfc rng) {
        this(param[0], param[1], param[2], param[3], param[4], rng);
    }

    /** These parameters are the parameters of the lognormal (not the bivariate normal)
     *
     * @param mean1 lognormal mean
     * @param var1 lognormal variance
     * @param mean2 lognormal 2nd mean
     * @param var2 lognormal 2nd variance
     * @param rho correlation of lognormals
     * @param rng
     */
    public BivariateLogNormal(double mean1, double var1, double mean2, double var2, double rho, RngIfc rng) {
        myBVN = new BivariateNormal(rng);
        setParameters(mean1, var1, mean2, var2, rho);
    }

    /** Takes in the parameters of the bivariate lognormal and sets
     *  the parameters of the underlying bivariate normal
     * 
     * @param m1
     * @param v1
     * @param m2
     * @param v2
     * @param r
     */
    public void setParameters(double m1, double v1, double m2, double v2, double r) {
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
        // calcuate parameters of underlying bivariate normal
        // get the means
        double mean1 = Math.log((m1 * m1) / Math.sqrt(m1 * m1 + v1));
        double mean2 = Math.log((m2 * m2) / Math.sqrt(m2 * m2 + v2));
        // get the variances
        double var1 = Math.log(1.0 + (v1 / Math.abs(m1 * m1)));
        double var2 = Math.log(1.0 + (v2 / Math.abs(m2 * m2)));
        // calculate the correlation

        double cov = Math.log(1.0 + ((r * Math.sqrt(v1 * v2)) / Math.abs(m1 * m2)));
        double rho = cov / Math.sqrt(var1 * var2);
        // set the parameters of the underlying bivariate normal
        myBVN.setParameters(mean1, var1, mean2, var2, rho);
    }

    /** Interprets the array of parameters as the parameters
     *  param[0] = mean 1;
     *  param[1] = variance 1;
     *  param[2] = mean 2;
     *  param[3] = variance 2;
     *  param[4] = correlation;
     *
     * @param param
     */
    public final void setParameters(double[] param) {
        setParameters(param[0], param[1], param[2], param[3], param[4]);
    }

    /** Returns the parameters as an array
     *
     *   param[0] = mean 1;
     *  param[1] = variance 1;
     *  param[2] = mean 2;
     *  param[3] = variance 2;
     *  param[4] = correlation;
     *
     * @return
     */
    public final double[] getParameters() {
        double[] param = new double[5];
        param[0] = myMu1;
        param[1] = myVar1;
        param[2] = myMu2;
        param[3] = myVar2;
        param[4] = myRho;
        return param;
    }

    /** Returns the parameters of the underlying bivariate normal
     *   param[0] = mean 1;
     *  param[1] = variance 1;
     *  param[2] = mean 2;
     *  param[3] = variance 2;
     *  param[4] = correlation;
     * 
     * @return
     */
    public final double[] getBiVariateNormalParameters() {
        return myBVN.getParameters();
    }

    /** Takes in the parameters of a bivariate normal and returns
     *  the corresponding parameters for the bivariate lognormal
     *
     *   param[0] = mean 1 of bivariate lognormal
     *  param[1] = variance 1 of bivariate lognormal
     *  param[2] = mean 2 of bivariate lognormal
     *  param[3] = variance 2 of bivariate lognormal
     *  param[4] = correlation of bivariate lognormal
     *
     * @param m1 mean 1 of bivariate normal
     * @param v1 variance 1 of bivariate normal
     * @param m2 mean 1 of bivariate normal
     * @param v2 variance 2 of bivariate normal
     * @param r correlation of bivariate normal
     * @return
     */
    public static final double[] getBVLNParametersFromBVNParameters(double m1, double v1, double m2, double v2, double r) {
        if (v1 <= 0) {
            throw new IllegalArgumentException("Variance 1 must be positive");
        }
        if (v2 <= 0) {
            throw new IllegalArgumentException("Variance 2 must be positive");
        }
        if ((r < -1.0) || (r > 1.0)) {
            throw new IllegalArgumentException("The correlation must be within [-1,1]");
        }

        double[] x = new double[5];
        x[0] = Math.exp(m1 + (v1 / 2.0));// mu 1 of LN
        x[1] = Math.exp(2.0 * m1 + v1) * (Math.exp(v1) - 1.0);// var 1 of LN
        x[2] = Math.exp(m2 + (v2 / 2.0)); // mu 2 of LN
        x[3] = Math.exp(2.0 * m2 + v2) * (Math.exp(v2) - 1.0);// var 2 of LN
        // compute covariance of normal
        double cov = r * Math.sqrt(v1) * Math.sqrt(v2);
        double n = Math.exp(cov) - 1.0;
        double d = Math.sqrt((Math.exp(v1) - 1.0) * (Math.exp(v2) - 1.0));
        x[4] = n / d;
        return x;
    }

    /** Takes in the parameters of a bivariate normal and returns
     *  the corresponding parameters for the bivariate lognormal
     *
     *   x[0] = mean 1 of bivariate lognormal
     *  x[1] = variance 1 of bivariate lognormal
     *  x[2] = mean 2 of bivariate lognormal
     *  x[3] = variance 2 of bivariate lognormal
     *  x[4] = correlation of bivariate lognormal
     *
     * @param param array of parameters representing the bivariate normal
     * @return
     */
    public static final double[] getBVLNParametersFromBVNParameters(double[] param) {
        return getBVLNParametersFromBVNParameters(param[0], param[1], param[2], param[3], param[4]);
    }

    /** Sets the first mean
     *
     * @param mean of the distribution
     */
    public final void setMean1(double mean) {
        setParameters(mean, myVar1, myMu2, myVar2, myRho);
    }

    /** Gets the first mean
     *
     * @return
     */
    public final double getMean1() {
        return myMu1;
    }

    /** Sets the first variance
     *
     * @param variance of the distribution, must be &gt; 0
     */
    public final void setVariance1(double variance) {
        setParameters(myMu1, variance, myMu2, myVar2, myRho);
    }

    /** Gets the first variance
     *
     * @return
     */
    public final double getVariance1() {
        return myVar1;
    }

    /** Sets the second mean
     *
     * @param mean
     */
    public final void setMean2(double mean) {
        setParameters(myMu1, myVar1, mean, myVar2, myRho);
    }

    /** Gets the second mean
     *
     * @return
     */
    public final double getMean2() {
        return myMu2;
    }

    /** Sets the 2nd variance
     *
     * @param variance of the distribution, must be &gt; 0
     */
    public final void setVariance2(double variance) {
        setParameters(myMu1, myVar1, myMu2, variance, myRho);
    }

    /** Gets the 2nd variance
     *
     * @return
     */
    public final double getVariance2() {
        return myVar2;
    }

    /** Sets the correlation
     *
     * @param rho
     */
    public final void setCorrelation(double rho) {
        setParameters(myMu1, myVar1, myMu2, myVar2, rho);
    }

    /** Gets the correlation
     *
     * @return
     */
    public final double getCorrelation() {
        return myRho;
    }

    public void setAntitheticOption(boolean flag) {
        myBVN.setAntitheticOption(flag);
    }

    public void resetStartSubstream() {
        myBVN.resetStartSubstream();
    }

    public void resetStartStream() {
        myBVN.resetStartStream();
    }

    public void advanceToNextSubstream() {
        myBVN.advanceToNextSubstream();
    }

    public boolean getAntitheticOption() {
        return myBVN.getAntitheticOption();
    }

    /** Fills the supplied array with 2 values
     *  As a convenience also returns the array
     *
     * @param x Must be of size 2 or larger
     * @return
     */
    public double[] getValues(double[] x) {
        // generate the bivariate normals
        x = myBVN.getValues(x);
        // transform them to bivariate lognormal
        x[0] = Math.exp(x[0]);
        x[1] = Math.exp(x[1]);
        return x;
    }

    /** Returns an array containing the bivariate pair
     *  x[0] = 1st marginal
     *  x[1] = 2nd marginal
     *
     * @return
     */
    public double[] getValues() {
        return (getValues(new double[2]));
    }

    public RngIfc getRandomNumberGenerator() {
        return myBVN.getRandomNumberGenerator();
    }

    public void setRandomNumberGenerator(RngIfc rng) {
        myBVN.setRandomNumberGenerator(rng);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Bivariate LogNormal\n");
        sb.append("mean 1 = " + myMu1 + "\n");
        sb.append("variance 1 = " + myVar1 + "\n");
        sb.append("mean 2 = " + myMu2 + "\n");
        sb.append("variance 2 = " + myVar2 + "\n");
        sb.append("correlation = " + myRho + "\n");
        sb.append("Underlying bivariate normal\n");
        sb.append(myBVN);
        return sb.toString();
    }

    public static void main(String[] args) {
        // set the parameters of bivariate normal
        double m1 = -0.288879;
        double v1 = Math.pow(0.399334, 2.0);
        double m2 = 0.2965255;
        double v2 = Math.pow(0.7731198, 2.0);
        double r = 0.8512;

        BivariateNormal g = new BivariateNormal(m1, v1, m2, v2, r);
        System.out.println(g);

        System.out.println("");
        System.out.println("Computing bivariate lognormal parameters from bivariate normal parameters");
        double[] params = BivariateLogNormal.getBVLNParametersFromBVNParameters(m1, v1, m2, v2, r);
        System.out.println("Bivariate Lognormal Parameters: ");
        System.out.println("myMu1\tmyVar1\tmyMu2\tmyVar2\tmyRho");
        System.out.println(+params[0] + " " + params[1] + " " + params[2] + " " + params[3] + " " + params[4]);

        BivariateLogNormal bvln = new BivariateLogNormal(params);
        System.out.println(bvln);

        System.out.println("i\tX1\tX2");
        for (int i = 1; i <= 10; i++) {

            double[] pair = bvln.getValues();
            System.out.println(+i + " " + pair[0] + " " + pair[1]);

        }
        System.out.println("");



        System.out.println("done!");
    }
}
