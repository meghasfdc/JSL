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
package jsl.utilities.random;

import java.util.HashMap;

import jsl.utilities.controls.ControllableIfc;
import jsl.utilities.controls.Controls;

/**
 * @author rossetti
 *
 */
public abstract class AbstractRandom implements RandomIfc, ControllableIfc {

    /** A counter to count the number of created to assign "unique" ids
     */
    private static long myIdCounter_;

    /** The id of this object
     */
    protected long myId;
    
    /** Holds the name of the name of the object for the IdentityIfc
     */
    protected String myName;

    public AbstractRandom() {
        this(null);
    }

    /**
     * 
     * @param name 
     */
    public AbstractRandom(String name) {
        setId();
        setName(name);
    }

    @Override
    public final String getName() {
        return myName;
    }

    /** Sets the name
     * @param str The name as a string.
     */
    public final void setName(String str) {
        if (str == null) {
            myName = this.getClass().getSimpleName();
        } else {
            myName = str;
        }
    }

    @Override
    public final long getId() {
        return (myId);
    }

    @Override
    public Controls getControls() {
        return new RandomControls();
    }

    @Override
    public void setControls(Controls controls) {
        if (controls == null) {
            throw new IllegalArgumentException("The supplied controls were null!");
        }
        setParameters(controls.getDoubleArrayControl("parameters"));
    }

    public double[] sample(int sampleSize) {
        double[] x = new double[sampleSize];
        for (int i = 0; i < sampleSize; i++) {
            x[i] = getValue();
        }
        return (x);
    }

    public void sample(double[] values) {
        if (values == null) {
            throw new IllegalArgumentException("The supplied array was null");
        }
        for (int i = 0; i < values.length; i++) {
            values[i] = getValue();
        }
    }

    protected final void setId() {
        myIdCounter_ = myIdCounter_ + 1;
        myId = myIdCounter_;
    }

    protected class RandomControls extends Controls {

        protected void fillControls(){
            addDoubleArrayControl("parameters", getParameters());
        }
    }
}
