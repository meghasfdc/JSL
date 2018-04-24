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
package jsl.utilities.random.rng;

import jsl.utilities.random.ar.AR1Normal;
import jsl.utilities.random.distributions.Normal;

/**  Uses the auto-regressive to anything algorithm
 *   to generate correlated uniform variates.
 *   The user supplies the correlation of the underlying
 *   AR(1) process.  The resulting correlation in the u's
 *   may not necessarily meet this correlation, due to
 *   the correlation matching problem.
 *
 */
public class AR1CorrelatedRngStream implements RngIfc {

    private AR1Normal myAR1;

    private double myPrevU;

    /**
     *
     */
    public AR1CorrelatedRngStream() {
        this(0.0, RNStreamFactory.getDefault().getStream());
    }

    /**
     *
     * @param correlation 
     */
    public AR1CorrelatedRngStream(double correlation) {
        this(correlation, RNStreamFactory.getDefault().getStream());
    }

    /**
     *
     * @param correlation
     * @param rng
     */
    public AR1CorrelatedRngStream(double correlation, RngIfc rng) {
        myAR1 = new AR1Normal(0.0, 1.0, correlation, rng);
    }

    public double randU01() {
        // generate the correlated normal
        double z = myAR1.getValue();
        // invert to get the correlated uniforms
        double u = Normal.stdNormalCDF(z);
        myPrevU = u;
        return u;
    }

    /** Returns a (pseudo)random number from the discrete uniform distribution
     * over the integers {i, i + 1, . . . , j }, using this stream. Calls randU01 once.
     * @param i start of range
     * @param j end of range
     * @return The integer pseudo random number
     */
    public final int randInt(int i, int j) {
        return (i + (int) (randU01() * (j - i + 1)));
    }

    public final void advanceToNextSubstream() {
        myAR1.advanceToNextSubstream();
    }

    public final void resetStartStream() {
        myAR1.resetStartStream();
    }

    public final void resetStartSubstream() {
        myAR1.resetStartSubstream();
    }

    public final void setAntitheticOption(boolean flag) {
        myAR1.setAntitheticOption(flag);
    }

        public boolean getAntitheticOption() {
        return myAR1.getAntitheticOption();
    }

    public final void setLag1Correlation(double phi) {
        myAR1.setLag1Correlation(phi);
    }

    public final double getLag1Correlation() {
        return myAR1.getLag1Correlation();
    }

    /** The previous U(0,1) generated (returned) by randU01()
     *
     * @return
     */
    public final double getPrevU01() {
        return myPrevU;
    }

    /** Returns the antithetic of the previous U(0,1)
     *  i.e. 1.0 - getPrevU01()
     *
     * @return
     */
    public final double getAntitheticValue() {
        return 1.0 - myPrevU;
    }

    public RngIfc newInstance() {
        return newInstance(null);
    }

    public RngIfc newInstance(String name) {
       RngIfc rng = myAR1.getRandomNumberGenerator();
       RngIfc c = rng.newInstance(name);
       double r = getLag1Correlation();
       return new AR1CorrelatedRngStream(r, c);
    }

    public RngIfc newAntitheticInstance(String name) {
       RngIfc rng = myAR1.getRandomNumberGenerator();
       RngIfc c = rng.newAntitheticInstance(name);
       double r = getLag1Correlation();
       return new AR1CorrelatedRngStream(r, c);
    }

    public RngIfc newAntitheticInstance() {
        return newAntitheticInstance(null);
    }
}
