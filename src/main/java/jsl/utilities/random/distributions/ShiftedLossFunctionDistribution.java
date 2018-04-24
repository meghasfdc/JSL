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
 * @author rossetti
 *
 */
public class ShiftedLossFunctionDistribution extends ShiftedDistribution
        implements LossFunctionDistributionIfc {

    /**
     * @param distribution
     * @param shift
     */
    public ShiftedLossFunctionDistribution(LossFunctionDistributionIfc distribution, double shift) {
        this(distribution, shift, RNStreamFactory.getDefault().getStream());
    }

    /**
     * @param distribution
     * @param shift
     * @param rng
     */
    public ShiftedLossFunctionDistribution(LossFunctionDistributionIfc distribution, double shift, RngIfc rng) {
        super((DistributionIfc) distribution, shift, rng);
    }

    /* (non-Javadoc)
     * @see jsl.utilities.random.LossFunctionDistributionIfc#firstOrderLossFunction(double)
     */
    public double firstOrderLossFunction(double x) {
        LossFunctionDistributionIfc cdf = (LossFunctionDistributionIfc) myDistribution;
        return cdf.firstOrderLossFunction(x - myShift);
    }

    /* (non-Javadoc)
     * @see jsl.utilities.random.LossFunctionDistributionIfc#secondOrderLossFunction(double)
     */
    public double secondOrderLossFunction(double x) {
        LossFunctionDistributionIfc cdf = (LossFunctionDistributionIfc) myDistribution;
        return cdf.secondOrderLossFunction(x - myShift);
    }

    public static void main(String[] args) {
        ShiftedLossFunctionDistribution SD = new ShiftedLossFunctionDistribution(new Poisson(1.0), 0.5);
        Poisson p = new Poisson(1.0);

        System.out.println("PMF_P(1) =" + p.pmf(1));
        System.out.println("CDF(1.5) =" + SD.cdf(1.5));
        System.out.println("CDF_P(1) =" + p.cdf(1));
        System.out.println("CCDF(1.5) =" + SD.complementaryCDF(1.5));
        System.out.println("FOLF(1.5) =" + SD.firstOrderLossFunction(1.5));
        System.out.println("SOLF(1.5) =" + SD.secondOrderLossFunction(1.5));
        System.out.println("FOLF_P(1) =" + p.firstOrderLossFunction(1.0));
        System.out.println("SOLF_P(1) =" + p.secondOrderLossFunction(1.0));
    }
}
