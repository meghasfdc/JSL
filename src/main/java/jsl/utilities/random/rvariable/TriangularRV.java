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
 *  Triangularmin, mode, max) random variable
 */
public final class TriangularRV extends AbstractRVariable {

    private final double myMin;

    private final double myMax;

    private final double myMode;

    public TriangularRV(double min, double mode, double max){
        this(min, mode, max, RNStreamFactory.getDefaultFactory().getStream());
    }

    public TriangularRV(double min, double mode, double max, RNStreamIfc rng){
        super(rng);
        if (min > mode) {
            throw new IllegalArgumentException("min must be <= mode");
        }

        if (min >= max) {
            throw new IllegalArgumentException("min must be < max");
        }

        if (mode > max) {
            throw new IllegalArgumentException("mode must be <= max");
        }

        myMode = mode;
        myMin = min;
        myMax = max;
    }

    /**
     *
     * @param rng the RngIfc to use
     * @return a new instance with same parameter value
     */
    public final TriangularRV newInstance(RNStreamIfc rng){
        return new TriangularRV(myMin, myMode, myMax, rng);
    }

    /**
     *
     * @return the minimum
     */
    public final double getMinimum() {
        return (myMin);
    }

    /**
     *
     * @return the mode or most likely value
     */
    public final double getMode() {
        return (myMode);
    }

    /**
     *
     * @return the maximum
     */
    public final double getMaximum() {
        return (myMax);
    }

    @Override
    protected final double generate() {
        double v = JSLRandom.rTriangular(myMin, myMode, myMax, myRNG);
        return v;
    }
}
