/*
 * Copyright (c) 2007, Manuel D. Rossetti (rossetti@uark.edu)
 *
 * Contact:
 *	Manuel D. Rossetti, Ph.D., P.E.
 *	Department of Industrial Engineering
 *	University of Arkansas
 *	4207 Bell Engineering Center
 *	Fayetteville, AR 72701
 *	Phone: (479) 575-6756
 *	Email: rossetti@uark.edu
 *	Web: www.uark.edu/~rossetti
 *
 * This file is part of the JSL (a Java Simulation Library). The JSL is a framework
 * of Java classes that permit the easy development and execution of discrete event
 * simulation programs.
 *
 * The JSL is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * The JSL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the JSL (see file COPYING in the distribution);
 * if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor,
 * Boston, MA  02110-1301  USA, or see www.fsf.org
 *
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
