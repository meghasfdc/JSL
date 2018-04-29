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
package jsl.utilities.random.distributions;

import jsl.utilities.random.AbstractRandom;
import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RNStreamIfc;

/**
 *
 * @author rossetti
 */
public class JohnsonB extends AbstractRandom {

    private double myAlpha1;

    private double myAlpha2;

    private double myMin;

    private double myMax;

    private RNStreamIfc myRNG;

    public JohnsonB() {
        this(0.0, 1.0, 0.0, 1.0, RNStreamFactory.getDefaultFactory().getStream());
    }

    public JohnsonB(double[] parameters) {
        this(parameters[0], parameters[1], parameters[2], parameters[3],
                RNStreamFactory.getDefaultFactory().getStream());
    }

    public JohnsonB(double[] parameters, RNStreamIfc rng) {
        this(parameters[0], parameters[1], parameters[2], parameters[3], rng);
    }

    public JohnsonB(double alpha1, double alpha2, double min, double max) {
        this(alpha1, alpha2, min, max, RNStreamFactory.getDefaultFactory().getStream());
    }

    public JohnsonB(double alpha1, double alpha2, double min, double max, RNStreamIfc rng) {
        setParameters(alpha1, alpha2, min, max);
        setRandomNumberGenerator(rng);
    }

    @Override
    public final JohnsonB newInstance() {
        return (new JohnsonB(getParameters()));
    }

    /** Returns a new instance of the random source with the same parameters
     *  with the supplied RngIfc
     * @param rng
     * @return
     */
    @Override
    public final JohnsonB newInstance(RNStreamIfc rng) {
        return (new JohnsonB(getParameters(), rng));
    }

    /** Returns a new instance that will supply values based
     *  on antithetic U(0,1) when compared to this distribution
     *
     * @return
     */
    public final JohnsonB newAntitheticInstance() {
        RNStreamIfc a = myRNG.newAntitheticInstance();
        return newInstance(a);
    }

    public final void setParameters(double alpha1, double alpha2, double min, double max) {
        setAlpha1(alpha1);
        setAlpha2(alpha2);
        setRange(min, max);
    }

    @Override
    public void setParameters(double[] parameters) {
        setAlpha1(parameters[0]);
        setAlpha2(parameters[1]);
        setRange(parameters[2], parameters[3]);
    }

    @Override
    public double[] getParameters() {
        double[] p = new double[4];
        p[0] = getAlpha1();
        p[1] = getAlpha2();
        p[2] = getMin();
        p[3] = getMax();
        return p;
    }

    @Override
    public double getValue() {
        double u = myRNG.randU01();
        double z = Normal.stdNormalInvCDF(u);
        double y = Math.exp((z - myAlpha1) / myAlpha2);
        double x = (myMin + myMax * y) / (y + 1.0);
        return x;
    }

    @Override
    public void resetStartStream() {
        myRNG.resetStartStream();
    }

    @Override
    public void resetStartSubstream() {
        myRNG.resetStartSubstream();
    }

    @Override
    public void advanceToNextSubstream() {
        myRNG.advanceToNextSubstream();
    }

    @Override
    public void setAntitheticOption(boolean flag) {
        myRNG.setAntitheticOption(flag);
    }

    @Override
    public boolean getAntitheticOption() {
        return myRNG.getAntitheticOption();
    }

    public double getMin() {
        return myMin;
    }

    public double getAlpha1() {
        return myAlpha1;
    }

    public void setAlpha1(double alpha1) {
        myAlpha1 = alpha1;
    }

    public double getAlpha2() {
        return myAlpha2;
    }

    public void setAlpha2(double alpha2) {
        if (alpha2 <= 0) {
            throw new IllegalArgumentException("alpha2 must be > 0");
        }
        myAlpha2 = alpha2;
    }

    public double getMax() {
        return myMax;
    }

    public void setRange(double min, double max) {
        if (max <= min) {
            throw new IllegalArgumentException("the min must be < than the max");
        }
        myMin = min;
        myMax = max;
    }

    public RNStreamIfc getRandomNumberGenerator() {
        return (myRNG);
    }

    public final void setRandomNumberGenerator(RNStreamIfc rng) {
        if (rng == null) {
            throw new NullPointerException("RngIfc rng must be non-null");
        }
        myRNG = rng;
    }
}
