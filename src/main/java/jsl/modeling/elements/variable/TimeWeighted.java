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
package jsl.modeling.elements.variable;

import jsl.modeling.ModelElement;

/**
 *
 */
public class TimeWeighted extends ResponseVariable {

    /**
     * Creates a TimeWeighted with the given parent with initial value 0.0 over
     * the range [Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY]
     *
     * @param parent the variable's parent model element.
     */
    public TimeWeighted(ModelElement parent) {
        this(parent, 0.0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, null);
    }

    /**
     * Creates a TimeWeighted with the given name and initial value over the
     * range [Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY]
     *
     * @param parent the variable's parent model element.
     * @param initialValue The initial value of the variable.
     */
    public TimeWeighted(ModelElement parent, double initialValue) {
        this(parent, initialValue, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, null);
    }

    /**
     * Creates a TimeWeighted with the given name and initial value, 0.0, over
     * the range [Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY]
     *
     * @param parent the variable's parent model element.
     * @param name The name of the variable.
     */
    public TimeWeighted(ModelElement parent, String name) {
        this(parent, 0.0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, name);
    }

    /**
     * Creates a TimeWeighted with the given name and initial value over the
     * supplied range The default range is [Double.NEGATIVE_INFINITY,
     * Double.POSITIVE_INFINITY]
     *
     * @param parent the variable's parent model element
     * @param initialValue The initial value of the variable.
     * @param name The name of the variable.
     */
    public TimeWeighted(ModelElement parent, double initialValue, String name) {
        this(parent, initialValue, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, name);
    }

    /**
     * Creates a TimeWeighted with the given name and initial value over the
     * supplied range [lowerLimit, Double.POSITIVE_INFINITY]
     *
     * @param parent the variable's parent model element
     * @param initialValue The initial value of the variable.
     * @param lowerLimit the lower limit on the range for the variable, must be
     * &lt; upperLimit
     * @param name The name of the variable.
     */
    public TimeWeighted(ModelElement parent, double initialValue, double lowerLimit, String name) {
        this(parent, initialValue, lowerLimit, Double.POSITIVE_INFINITY, name);
    }

    /**
     * Creates a TimeWeighted with the initial value over the supplied range
     * [lowerLimit, Double.POSITIVE_INFINITY]
     *
     * @param parent the variable's parent model element
     * @param initialValue The initial value of the variable.
     * @param lowerLimit the lower limit on the range for the variable, must be
     * &lt; upperLimit
     */
    public TimeWeighted(ModelElement parent, double initialValue, double lowerLimit) {
        this(parent, initialValue, lowerLimit, Double.POSITIVE_INFINITY, null);
    }

    /**
     * Creates a TimeWeighted with the initial value over the supplied range
     * [lowerLimit, upperLimit]
     *
     * @param parent the variable's parent model element
     * @param initialValue The initial value of the variable.
     * @param lowerLimit the lower limit on the range for the variable, must be
     * &lt; upperLimit
     * @param upperLimit the upper limit on the range for the variable
     */
    public TimeWeighted(ModelElement parent, double initialValue, double lowerLimit, double upperLimit) {
        this(parent, initialValue, lowerLimit, upperLimit, null);
    }

    /**
     * Creates a TimeWeighted with the given name and initial value over the
     * supplied range [lowerLimit, upperLimit]
     *
     * @param parent the variable's parent model element
     * @param initialValue The initial value of the variable. Must be within the
     * range.
     * @param lowerLimit the lower limit on the range for the variable, must be
     * &lt; upperLimit
     * @param upperLimit the upper limit on the range for the variable
     * @param name The name of the variable.
     */
    public TimeWeighted(ModelElement parent, double initialValue, double lowerLimit, double upperLimit, String name) {
        super(parent, initialValue, lowerLimit, upperLimit, name);
    }

    /**
     * Increments the value of the variable by 1 at the current time.
     */
    public final void increment() {
        increment(1.0);
    }

    /**
     * Increments the value of the variable by the amount supplied. Throws an
     * IllegalArgumentException if the value is negative.
     *
     * @param value The amount to increment by. Must be non-negative.
     */
    public final void increment(double value) {
        if (value < 0) {
            throw new IllegalArgumentException("Invalid argument. Attempted an negative increment.");
        }

        setValue(myValue + value);
    }

    /**
     * Decrements the value of the variable by 1 at the current time.
     */
    public final void decrement() {
        decrement(1.0);
    }

    /**
     * Decrements the value of the variable by the amount supplied. Throws an
     * IllegalArgumentException if the value is negative.
     *
     * @param value The amount to decrement by. Must be non-negative.
     */
    public final void decrement(double value) {
        if (value < 0) {
            throw new IllegalArgumentException("Invalid argument. Attempted an negative decrement.");
        }

        setValue(myValue - value);
    }

    /**
     * Sets the weight, the current time - the time of the last change
     */
    @Override
    protected final void setWeight() {
        myWeight = getTime() - myTimeOfChange;
        if (myWeight < 0) {
            myWeight = 0.0;
        }
    }

    @Override
    protected void collectStatistics() {
        myWithinRepStats.collect(getPreviousValue(), getWeight());
        if (myWithinIntervalStats != null) {
            myWithinIntervalStats.collect(getPreviousValue(), getWeight());
        }
    }

    /**
     * Initialize the value to the current value at this time
     */
    @Override
    protected void initialize() {
//		System.out.println("In TimeWeighted initialize() " + getName());
        super.initialize();
        // this is so at least two changes are recorded on the variable
        // to properly account for variables that have zero area throughout the replication
        //setValue(getInitialValue());
        setValue(getValue());
    }

    @Override
    protected void timedUpdate() {
        // this is to capture the area under the curve up to and including
        // the current time
        setValue(getValue());
        if (myWithinIntervalStats != null) {
            if (!((myLastUpdateTime < myTimeOfWarmUp) && (myTimeOfWarmUp < getTime()))) {
                // if the warm up did not occur during the interval, then collect the average
                myAcrossIntervalResponse.setValue(myWithinIntervalStats.getAverage());
            }
            myWithinIntervalStats.reset();
        }
        myLastUpdateTime = getTime();
    }

    /**
     * Schedules the batch events for after the warm up period Sets the value of
     * the variable to the current value at the current time Resets the time of
     * last change to the current time
     */
    @Override
    protected void warmUp() {
        super.warmUp();
        // make it think that it changed at the warm up time to the same value
        myTimeOfChange = getTime();
        // this is so at least two changes are recorded on the variable
        // to properly account for variables that have zero area throughout the replication
        setValue(getValue());
    }

    /**
     * Sets the value of the variable to the current value at the current time
     * to collect state to end of replication
     */
    @Override
    protected void replicationEnded() {
        super.replicationEnded();
//        System.out.println("in replication ended for " + getName());
//        System.out.println("time = " + getTime());
        // this allows time weighted to be collected all the way to end of simulation
        setValue(getValue());
    }
}
