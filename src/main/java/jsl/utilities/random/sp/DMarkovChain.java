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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsl.utilities.random.sp;

import jsl.utilities.random.distributions.DEmpiricalPMF;
import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RNStreamIfc;
import jsl.utilities.statistic.IntegerFrequency;

/**
 *
 * @author rossetti
 */
public class DMarkovChain {

    private int myState;

    private int myInitialState;

    private DEmpiricalPMF[] myStateProb;
    
    private int myMaxState;

    /**
     * myRNG provides a reference to the underlying stream of random numbers
     */
    protected RNStreamIfc myRNG;

    /**
     *
     * @param initialState
     * @param prob
     */
    public DMarkovChain(int initialState, double[][] prob) {
        this(initialState, prob, RNStreamFactory.getDefaultFactory().getStream());
    }

    /**
     *
     * @param initialState
     * @param prob
     * @param rng
     */
    public DMarkovChain(int initialState, double[][] prob, RNStreamIfc rng) {
        setProbabilities(prob, rng);
        setInitialState(initialState);
        reset();
    }

    /**
     *
     * @param prob
     * @param rng
     */
    protected final void setProbabilities(double[][] prob, RNStreamIfc rng) {
        setRandomNumberGenerator(rng);
        myMaxState = prob.length;
        myStateProb = new DEmpiricalPMF[prob.length];
        for (int r = 0; r < prob.length; r++) {
            if (prob[r].length != prob.length) {
                throw new IllegalArgumentException("The #rows != #cols for probability array");
            }
            myStateProb[r] = new DEmpiricalPMF(rng);
            for (int c = 0; c < prob[r].length - 1; c++) {
                myStateProb[r].addProbabilityPoint(c, prob[r][c]);
            }
            myStateProb[r].addLastProbabilityPoint(prob[r].length - 1);
        }
    }

    /**
     * Sets the state back to the initial state
     *
     */
    public final void reset() {
        myState = myInitialState;
    }

    public final void setInitialState(int initialState) {
        if ((initialState < 1) || (initialState > myMaxState)) {
            throw new IllegalArgumentException("The initial state must be >= 1 and <= " + myMaxState);
        }
        myInitialState = initialState;
    }

    public final int getInitialState() {
        return myInitialState;
    }

    public final void setState(int state) {
        if ((state < 1) || (state > myMaxState)) {
            throw new IllegalArgumentException("The initial state must be >= 1 and <= " + myMaxState);
        }
        myState = state;
    }

    public final int getState() {
        return myState;
    }

    public final int next() {
        int x = (int) myStateProb[myState - 1].getValue();
        myState = x + 1;
        return myState;
    }

    public final RNStreamIfc getRandomNumberGenerator() {
        return (myRNG);
    }

    /**
     * Sets the underlying random number generator for the distribution Throws a
     * NullPointerException if rng is null
     *
     * @param rng the reference to the random number generator
     */
    public final void setRandomNumberGenerator(RNStreamIfc rng) {
        if (rng == null) {
            throw new NullPointerException("RngIfc rng must be non-null");
        }
        myRNG = rng;
    }

    public void advanceToNextSubstream() {
        myRNG.advanceToNextSubstream();
    }

    public void resetStartStream() {
        myRNG.resetStartStream();
    }

    public void resetStartSubstream() {
        myRNG.resetStartSubstream();
    }

    public void setAntitheticOption(boolean flag) {
        myRNG.setAntitheticOption(flag);
    }

    public boolean getAntitheticOption() {
        return myRNG.getAntitheticOption();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        double[][] p = {
            {0.3, 0.1, 0.6},
            {0.4, 0.4, 0.2},
            {0.1, 0.7, 0.2}};

        DMarkovChain mc = new DMarkovChain(1, p);
        IntegerFrequency f = new IntegerFrequency();

        for (int i = 1; i <= 100000; i++) {
            int k = mc.next();
            f.collect(k);
            //System.out.println("state = " + k);
        }
        System.out.println("True Steady State Distribution");
        System.out.println("P{X=1} = " + (238.0 / 854.0));
        System.out.println("P{X=2} = " + (350.0 / 854.0));
        System.out.println("P{X=3} = " + (266.0 / 854.0));
        System.out.println();
        System.out.println("Observed Steady State Distribution");
        System.out.println(f);
    }
}
