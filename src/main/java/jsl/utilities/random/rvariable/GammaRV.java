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

import jsl.utilities.random.distributions.Gamma;
import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RngIfc;

/**
 *  Gamma(shape, scale) random variable
 */
public final class GammaRV extends AbstractRVariable {

    private final Gamma myGamma;

    public GammaRV(double shape, double scale){
        this(shape, scale, RNStreamFactory.getDefault().getStream());
    }

    public GammaRV(double shape, double scale, RngIfc rng){
        super(rng);
        myGamma = new Gamma(shape, scale);
    }

    /**
     *
     * @param rng the RngIfc to use
     * @return a new instance with same parameter value
     */
    public final GammaRV newInstance(RngIfc rng){
        return new GammaRV(this.getShape(), this.getScale(), rng);
    }

    @Override
    public String toString() {
        return "GammaRV{" +
                "shape=" + myGamma.getShape() +
                ", scale=" + myGamma.getScale() +
                '}';
    }

    /** Gets the shape
     * @return The shape parameter as a double
     */
    public double getShape() {
        return myGamma.getShape();
    }

    /** Gets the scale parameter
     * @return The scale parameter as a double
     */
    public double getScale() {
        return myGamma.getScale();
    }

    @Override
    protected final double generate() {
        double v = myGamma.invCDF(myRNG.randU01());
        return v;
    }
}
