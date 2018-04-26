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
 *  Normal(mean, variance)
 */
public final class NormalRV extends AbstractRVariable {

    private final double myMean;

    private final double myVar;

    /**
     *  N(0,1)
     */
    public NormalRV(){
        this(0,1.0);
    }

    public NormalRV(double mean, double variance){
        this(mean, variance, RNStreamFactory.getDefault().getStream());
    }

    public NormalRV(double mean, double variance, RngIfc rng){
        super(rng);
        myMean = mean;
        if (variance <= 0) {
            throw new IllegalArgumentException("Variance must be positive");
        }
        myVar = variance;
    }

    /**
     *
     * @param rng the RngIfc to use
     * @return a new instance with same parameter value
     */
    public final NormalRV newInstance(RngIfc rng){
        return new NormalRV(this.myMean, this.myVar, rng);
    }

    @Override
    public String toString() {
        return "NormalRV{" +
                "mean=" + myMean +
                ", variance=" + myVar +
                '}';
    }

    /**
     *
     * @return mean of the random variable
     */
    public final double getMean() {
        return myMean;
    }

    /**
     *
     * @return variance of the random variable
     */
    public final double getVariance() {
        return myVar;
    }

    @Override
    protected final double generate() {
        double v = JSLRandom.rNormal(myMean, myVar, myRNG);
        return v;
    }
}