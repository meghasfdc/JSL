/*
 * Copyright (c) 2018. Manuel D. Rossetti, manuelrossetti@gmail.com
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
package jsl.utilities.random.rng;

import jsl.utilities.random.distributions.Normal;
import jsl.utilities.random.rvariable.AR1NormalRV;

/**
 * Uses the auto-regressive to anything algorithm
 * to generate correlated uniform variates.
 * The user supplies the correlation of the underlying
 * AR(1) process.  The resulting correlation in the u's
 * may not necessarily meet this correlation, due to
 * the correlation matching problem.
 */
public class AR1CorrelatedRngStream implements RngIfc {

    private AR1NormalRV myAR1;

    private double myPrevU;

    private RngIfc myRNG;

    /**
     *
     */
    public AR1CorrelatedRngStream() {
        this(0.0, RNStreamFactory.getDefault().getStream());
    }

    /**
     * @param correlation
     */
    public AR1CorrelatedRngStream(double correlation) {
        this(correlation, RNStreamFactory.getDefault().getStream());
    }

    /**
     * @param correlation
     * @param rng
     */
    public AR1CorrelatedRngStream(double correlation, RngIfc rng) {
        myAR1 = new AR1NormalRV(0.0, 1.0, correlation, rng);
        myRNG = rng;
    }

    public double randU01() {
        // generate the correlated normal
        double z = myAR1.getValue();
        // invert to get the correlated uniforms
        double u = Normal.stdNormalCDF(z);
        myPrevU = u;
        return u;
    }

    /**
     * Returns a (pseudo)random number from the discrete uniform distribution
     * over the integers {i, i + 1, . . . , j }, using this stream. Calls randU01 once.
     *
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

    public final double getLag1Correlation() {
        return myAR1.getLag1Correlation();
    }

    /**
     * The previous U(0,1) generated (returned) by randU01()
     *
     * @return
     */
    public final double getPrevU01() {
        return myPrevU;
    }

    /**
     * Returns the antithetic of the previous U(0,1)
     * i.e. 1.0 - getPrevU01()
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
        RngIfc c = myRNG.newInstance(name);
        double r = getLag1Correlation();
        return new AR1CorrelatedRngStream(r, c);
    }

    public RngIfc newAntitheticInstance(String name) {
        RngIfc c = myRNG.newAntitheticInstance(name);
        double r = getLag1Correlation();
        return new AR1CorrelatedRngStream(r, c);
    }

    public RngIfc newAntitheticInstance() {
        return newAntitheticInstance(null);
    }
}
