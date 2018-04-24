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

/** Represents a Mixed translated Poisson random variable
 * 
 * 
 *
 */
public class MTP extends Distribution implements LossFunctionDistributionIfc {

    protected double myMixProb1;

    protected double myMixProb2;

    protected double myMean;

    protected ShiftedLossFunctionDistribution SD1;

    protected ShiftedLossFunctionDistribution SD2;

    double[] parameter;

    public MTP() {
        this(0.5, 0.0, 1.0, RNStreamFactory.getDefault().getStream());
    }

    /** Constructs an MTP with mixing probabilities 1-mixProb and mixProb with shifts of
     * shift and shift+1 with rates equal to rate
     * @param mixProb
     * @param shift
     * @param rate
     */
    public MTP(double mixProb, double shift, double rate) {
        this(mixProb, shift, rate, RNStreamFactory.getDefault().getStream());
    }

    /** Constructs an MTP using the supplied parameters
     *
     * @param mixProb
     * @param shift
     * @param rate
     * @param rng
     */
    public MTP(double mixProb, double shift, double rate, RngIfc rng) {
        super(rng);
        myMixProb1 = mixProb;
        myMixProb2 = 1 - myMixProb1;
        myMean = rate;
        //System.out.println("Mean1: "+myMean1);
        //System.out.println("Mean2: "+myMean2);
        SD1 = new ShiftedLossFunctionDistribution(new Poisson(rate, myRNG), shift);
        SD2 = new ShiftedLossFunctionDistribution(new Poisson(rate, myRNG), shift + 1);
    }

    /** Constructs an MTP with array of parameters
     *
     *
     * parameters[0] - mixing probability;
     * parameters[1] - shift;
     * parameters[2] - rate;
     * @param parameters
     */
    public MTP(double[] parameters) {
        this(parameters[0], parameters[1], parameters[2],
                RNStreamFactory.getDefault().getStream());
    }

    /** Constructs an MTP with array of parameters
     *
     *
     * parameters[0] - mixing probability;
     * parameters[1] - shift;
     * parameters[2] - rate;
     * @param parameters
     * @param rng
     */
    public MTP(double[] parameters, RngIfc rng) {
        this(parameters[0], parameters[1], parameters[2], rng);
    }

    /** Returns a new instance of the random source with the same parameters
     *  but an independent generator
     *
     * @return
     */
    public final MTP newInstance() {
        return (new MTP(getParameters()));
    }

    /** Returns a new instance of the random source with the same parameters
     *  with the supplied RngIfc
     * @param rng
     * @return
     */
    public final MTP newInstance(RngIfc rng) {
        return (new MTP(getParameters(), rng));
    }

    /** Returns a new instance that will supply values based
     *  on antithetic U(0,1) when compared to this distribution
     *
     * @return
     */
    public final MTP newAntitheticInstance() {
        RngIfc a = myRNG.newAntitheticInstance();
        return newInstance(a);
    }

    @Override
    public double cdf(double x) {
        return myMixProb1 * SD1.cdf(x) + (myMixProb2 * SD2.cdf(x));
    }

    @Override
    public double getMean() {

        return myMixProb1 * SD1.getMean() + myMixProb2 * SD2.getMean();
    }

    @Override
    public double[] getParameters() {
        double[] x = SD1.myDistribution.getParameters();
        double[] y = SD1.getParameters();
        double[] z = new double[x.length + 2];

        z[0] = myMixProb1;
        z[1] = y[0];
        z[2] = x[0];

        return z;
    }

    public double getVariance() {
        return myMixProb1 * ((SD1.getMean() * SD1.getMean()) + myMean) + myMixProb2 * ((SD2.getMean() * SD2.getMean()) + myMean) - (getMean() * getMean());
    }

    public double invCDF(double p) {
        if ((p < 0.0) || (p > 1.0)) {
            throw new IllegalArgumentException("Supplied probability was " + p + " Probability must be [0,1)");
        }
        //if(JSLMath.equal(p, 1.0))
        //throw new IllegalArgumentException("Supplied probability was 1.0 Probability must be [0,1)");

        int i = 0;
        while (p > cdf(i)) {
            i++;
        }
        return i;
    }

    public void setParameters(double[] parameters) {
        if (parameters == null) {
            throw new IllegalArgumentException("The parameters array was null");
        }
        setParameters(parameters[2], parameters[1], parameters[0]);
    }

    public double complementaryCDF(double x) {
        return myMixProb1 * SD1.complementaryCDF(x) + myMixProb2 * SD2.complementaryCDF(x);
    }

    public double firstOrderLossFunction(double x) {

        return myMixProb1 * SD1.firstOrderLossFunction(x) + myMixProb2 * SD2.firstOrderLossFunction(x);
    }

    public double secondOrderLossFunction(double x) {

        return myMixProb1 * SD1.secondOrderLossFunction(x) + myMixProb2 * SD2.secondOrderLossFunction(x);
    }

    public void setParameters(double rate, double shift, double mixProbability) {
        if (rate <= 0.0) {
            throw new IllegalArgumentException("Rate should be > 0.0");
        }
        if (shift < 0.0) {
            throw new IllegalArgumentException("shift should be >= 0.0");
        }
        if (mixProbability > 1.0) {
            throw new IllegalArgumentException("Mixing probability should be between 0 and 1");
        }
        SD1.setShift(shift);
        SD2.setShift(shift + 1);
        myMixProb1 = mixProbability;
        myMixProb2 = 1 - myMixProb1;
        parameter = new double[1];
        parameter[0] = rate;
        SD1.myDistribution.setParameters(parameter);
        SD2.myDistribution.setParameters(parameter);
        myMean = rate;
    }

    public static void main(String[] args) {

        MTP mtp = new MTP(0.5, 1, 0.1);

        System.out.println("Second order loss function: " + mtp.secondOrderLossFunction(2));
    }
}
