/*
 * Copyright (c) 2018. Manuel D. Rossetti, rossetti@uark.edu
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

import jsl.utilities.Identity;
import jsl.utilities.IdentityIfc;
import jsl.utilities.random.rng.RNStreamIfc;

abstract public class AbstractMVRVariable implements MVRVariableIfc, IdentityIfc {

    private final Identity myIdentity;

    /**
     * myRNG provides a reference to the underlying stream of random numbers
     */
    protected RNStreamIfc myRNG;

    public AbstractMVRVariable(RNStreamIfc rng) {
        myIdentity = new Identity();
        setRandomNumberGenerator(rng);
    }

    @Override
    public final String getName() {
        return myIdentity.getName();
    }

    @Override
    public final long getId() {
        return myIdentity.getId();
    }

    /** Sets the name
     * @param str The name as a string.
     */
    public final void setName(String str) {
        myIdentity.setName(str);
    }

    /**
     *
     * @return the underlying random number source
     */
    public final RNStreamIfc getRandomNumberGenerator() {
        return (myRNG);
    }

    /**
     * Sets the underlying random number source
     *
     * @param rng the reference to the random number generator, must not be null
     */
    public final void setRandomNumberGenerator(RNStreamIfc rng) {
        if (rng == null) {
            throw new NullPointerException("RngIfc rng must be non-null");
        }
        myRNG = rng;
    }

    @Override
    public final void resetStartStream() {
        myRNG.resetStartStream();
    }

    @Override
    public final void resetStartSubstream() {
        myRNG.resetStartSubstream();
    }

    @Override
    public final void advanceToNextSubstream() {
        myRNG.advanceToNextSubstream();
    }

    @Override
    public final void setAntitheticOption(boolean flag) {
        myRNG.setAntitheticOption(flag);
    }

    @Override
    public final boolean getAntitheticOption() {
        return myRNG.getAntitheticOption();
    }
}
