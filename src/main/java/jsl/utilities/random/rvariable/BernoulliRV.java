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
import jsl.utilities.random.rng.RngIfc;

/**
 *  Bernoulli(probability of success) random variable
 */
public final class BernoulliRV extends AbstractRVariable {

    private final double myProbSuccess;

    public BernoulliRV(double prob){
        this(prob, RNStreamFactory.getDefault().getStream());
    }

    public BernoulliRV(double prob, RngIfc rng){
        super(rng);
        if ((prob < 0.0) || (prob > 1.0)) {
            throw new IllegalArgumentException("Probability must be [0,1]");
        }
        myProbSuccess = prob;
    }

    /**
     *
     * @param rng the RngIfc to use
     * @return a new instance with same parameter value
     */
    public final BernoulliRV newInstance(RngIfc rng){
        return new BernoulliRV(this.myProbSuccess, rng);
    }

    @Override
    public String toString() {
        return "BernoulliRV{" +
                "pSuccess=" + myProbSuccess +
                '}';
    }

    /** Gets the success probability
     * @return The success probability
     */
    public final double getProbabilityOfSuccess() {
        return (myProbSuccess);
    }

    @Override
    protected final double generate(){
        double v = JSLRandom.rBernoulli(myProbSuccess, myRNG);
        return v;
    }

    /** Returns a randomly generated boolean according to the Bernoulli distribution
     *
     * @return
     */
    public final boolean getBoolean() {
        if (getValue() == 0.0) {
            return (false);
        } else {
            return (true);
        }
    }

}