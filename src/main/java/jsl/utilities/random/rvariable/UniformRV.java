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
 *  Continuous uniform(min, max) random variable
 */
public final class UniformRV extends AbstractRVariable {

    private final double min;
    private final double max;

    public UniformRV(double min, double max){
        this(min, max, RNStreamFactory.getDefaultFactory().getStream());
    }

    public UniformRV(double min, double max, RNStreamIfc rng){
        super(rng);
        if (min >= max) {
            throw new IllegalArgumentException("Lower limit must be < upper limit. lower limit = " + min + " upper limit = " + max);
        }
        this.min = min;
        this.max = max;
    }

    /**
     *
     * @param rng the RngIfc to use
     * @return a new instance with same parameter value
     */
    public final UniformRV newInstance(RNStreamIfc rng){
        return new UniformRV(this.min, this.max, rng);
    }

    @Override
    public String toString() {
        return "UniformRV{" +
                "min=" + min +
                ", max=" + max +
                '}';
    }

    /** Gets the lower limit
     * @return The lower limit
     */
    public final double getMinimum() {
        return (min);
    }

    /** Gets the upper limit
     * @return The upper limit
     */
    public final double getMaximum() {
        return (max);
    }

    @Override
    protected final double generate() {
        double v = JSLRandom.rUniform(min, max, myRNG);
        return v;
    }
}
