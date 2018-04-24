/*
 * Created on Mar 1, 2007
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
package jsl.utilities.random.robj;

import java.util.HashMap;

import jsl.utilities.controls.ControllableIfc;
import jsl.utilities.controls.Controls;
import jsl.utilities.random.RandomIfc;
import jsl.utilities.random.SampleIfc;
import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RngIfc;
import jsl.utilities.random.rvariable.JSLRandom;

/** A DPopulation is a population of doubles that can be sampled from and permuted.
 * @author rossetti
 *
 */
public class DPopulation implements RandomIfc, SampleIfc, ControllableIfc {

    /** A counter to count the number of created to assign "unique" ids
     */
    private static long myIdCounter_;

    /** The id of this object
     */
    protected long myId;

    /** Holds the name of the statistic for reporting purposes.
     */
    protected String myName;

    private RngIfc myRNG;

    private double[] myElements;

    /**
     *
     * @param elements
     */
    public DPopulation(double[] elements) {
        this(elements, RNStreamFactory.getDefault().getStream(), null);
    }

    /**
     * @param elements
     * @param rng
     */
    public DPopulation(double[] elements, RngIfc rng) {
        this(elements, rng, null);
    }

    /**
     * @param elements
     * @param rng
     * @param name
     */
    public DPopulation(double[] elements, RngIfc rng, String name) {
        setId();
        setName(name);
        setRandomNumberGenerator(rng);
        setParameters(elements);
    }

    /** Gets the name.
     * @return The name of object.
     */
    @Override
    public final String getName() {
        return myName;
    }

    /** Sets the name
     * @param str The name as a string.
     */
    public final void setName(String str) {
        if (str == null) {
            String s = this.getClass().getName();
            int k = s.lastIndexOf(".");
            if (k != -1) {
                s = s.substring(k + 1);
            }
            myName = s;
        } else {
            myName = str;
        }
    }

    /** Returns the id for this collector
     *
     * @return
     */
    @Override
    public final long getId() {
        return (myId);
    }

    /** Returns a new instance of the random source with the same parameters
     *  but an independent generator
     *
     * @return
     */
    @Override
    public final DPopulation newInstance() {
        return (new DPopulation(getParameters()));
    }

    /** Returns a new instance of the random source with the same parameters
     *  but an independent generator
     * @param rng
     * @return
     */
    @Override
    public final DPopulation newInstance(RngIfc rng) {
        return (new DPopulation(getParameters(), rng));
    }

    /** Creates a new array that contains a random generate without replacement
     *  from the existing population.
     *
     * @param sampleSize
     * @return
     */
    public final double[] getSampleWithoutReplacement(int sampleSize) {
        JSLRandom.sampleWithoutReplacement(myElements, sampleSize, myRNG);
        double[] x = new double[sampleSize];
        System.arraycopy(myElements, 0, x, 0, x.length);
        return (x);
    }

    /** Creates a new array that contains a random permutation of the population
     *
     * @return
     */
    public final double[] getPermutation() {
        return (getSampleWithoutReplacement(myElements.length));
    }

    /** Causes the population to form a new permutation,
     *  The ordering of the elements in the population will be changed.
     */
    public final void permute() {
        JSLRandom.permutation(myElements, myRNG);
    }

    /** Returns the value at the supplied index
     *
     * @param index must be &gt; 0 and less than size() - 1
     * @return
     */
    public final double get(int index) {
        return (myElements[index]);
    }

    /** Sets the element at the supplied index to the supplied value
     *
     * @param index
     * @param value 
     */
    public final void set(int index, double value) {
        myElements[index] = value;
    }

    /** Returns the number of elements in the population
     *
     * @return the size of the population
     */
    public final int size() {
        return (myElements.length);
    }

    /** Gets a copy of the population array, in its current state
     * @return
     */
    @Override
    public double[] getParameters() {
        double x[] = new double[myElements.length];
        System.arraycopy(myElements, 0, x, 0, myElements.length);
        return x;
    }

    /** Copies the values from the supplied array to the population array
     *
     * @param elements
     */
    @Override
    public final void setParameters(double[] elements) {
        if (elements == null) {
            throw new IllegalArgumentException("The element array was null");
        }
        if (elements.length == 0){
            throw new IllegalArgumentException("The element array had no elements.");
        }
        myElements = new double[elements.length];
        System.arraycopy(elements, 0, myElements, 0, elements.length);
    }

    protected class DPopControls extends Controls {

        protected void fillControls() {
            addDoubleArrayControl("parameters", getParameters());
        }
    }

    @Override
    public Controls getControls() {
        return new DPopControls();
    }

    @Override
    public void setControls(Controls controls) {
        if (controls == null) {
            throw new IllegalArgumentException("The supplied controls were null!");
        }
        setParameters(controls.getDoubleArrayControl("parameters"));
    }

    /** Returns a randomly selected element from the population.  All
     *  elements are equally likely.
     * @return
     */
    @Override
    public final double getValue() {
        return sample();
    }

    @Override
    public final double sample(){
        return myElements[getRandomIndex()];
    }

    /** Returns a random index into the population (assuming elements numbered starting at zero)
     *
     * @return
     */
    protected final int getRandomIndex() {
        return (myRNG.randInt(0, myElements.length - 1));
    }

    @Override
    public final void advanceToNextSubstream() {
        myRNG.advanceToNextSubstream();
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
    public final boolean getAntitheticOption() {
        return myRNG.getAntitheticOption();
    }

    @Override
    public final void setAntitheticOption(boolean flag) {
        myRNG.setAntitheticOption(flag);
    }

    /** Returns the underlying random number generator
     *
     * @return
     */
    public final RngIfc getRandomNumberGenerator() {
        return (myRNG);
    }

    /** Sets the underlying random number generator 
     * Throws a NullPointerException if rng is null
     * @param rng the reference to the random number generator
     */
    public final void setRandomNumberGenerator(RngIfc rng) {
        if (rng == null) {
            throw new NullPointerException("RngIfc rng must be non-null");
        }
        myRNG = rng;
    }

    @Override
    public String toString() {
        return (toString(myElements));
    }

    public static String toString(double[] x) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < x.length; i++) {
            sb.append("Element(");
            sb.append(i);
            sb.append(") = ");
            sb.append(x[i]);
            sb.append(System.lineSeparator());
        }
        return (sb.toString());
    }

    protected final void setId() {
        myIdCounter_ = myIdCounter_ + 1;
        myId = myIdCounter_;
    }
}
