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
 *  Poisson(mean) random variable
 */
public final class PoissonRV extends AbstractRVariable {

    private final double mean;

    public PoissonRV(double mean){
        this(mean, RNStreamFactory.getDefault().getStream());
    }

    public PoissonRV(double mean, RngIfc rng){
        super(rng);
        if (mean <= 0.0) {
            throw new IllegalArgumentException("Poisson mean must be > 0.0");
        }
        this.mean = mean;
    }

    /**
     *
     * @param rng the RngIfc to use
     * @return a new instance with same parameter value
     */
    public final PoissonRV newInstance(RngIfc rng){
        return new PoissonRV(this.mean, rng);
    }

    @Override
    public String toString() {
        return "PoissonRV{" +
                "mean=" + mean +
                '}';
    }

    /**
     *
     * @return the mean value
     */
    public final double getMean() {
        return mean;
    }

    @Override
    protected final double generate() {
        double v = JSLRandom.rPoisson(mean, myRNG);
        return v;
    }
}