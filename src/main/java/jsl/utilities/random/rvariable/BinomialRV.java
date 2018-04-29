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

package jsl.utilities.random.rvariable;

import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RNStreamIfc;

/**
 *  Binomial(probability of success, number of trials)
 */
public final class BinomialRV extends AbstractRVariable {

    private double myProbSuccess;

    private int myNumTrials;

    public BinomialRV(double prob, int numTrials){
        this(prob, numTrials, RNStreamFactory.getDefaultFactory().getStream());
    }

    public BinomialRV(double prob, int numTrials, RNStreamIfc rng){
        super(rng);
        if ((prob < 0.0) || (prob > 1.0)) {
            throw new IllegalArgumentException("Success Probability must be [0,1]");
        }
        if (numTrials <= 0) {
            throw new IllegalArgumentException("Number of trials must be >= 1");
        }
        myProbSuccess = prob;
        myNumTrials = numTrials;
    }

    /**
     *
     * @param rng the RngIfc to use
     * @return a new instance with same parameter value
     */
    public final BinomialRV newInstance(RNStreamIfc rng){
        return new BinomialRV(this.myProbSuccess, this.myNumTrials, rng);
    }

    @Override
    public String toString() {
        return "BinomialRV{" +
                "probSuccess=" + myProbSuccess +
                ", numTrials=" + myNumTrials +
                '}';
    }

    /** Gets the success probability
     * @return The success probability
     */
    public final double getProb() {
        return (myProbSuccess);
    }

    /** Gets the number of trials
     * @return the number of trials
     */
    public final int getTrials() {
        return (myNumTrials);
    }

    @Override
    protected final double generate() {
        double v = JSLRandom.rBinomial(myProbSuccess, myNumTrials, myRNG);
        return v;
    }
}
