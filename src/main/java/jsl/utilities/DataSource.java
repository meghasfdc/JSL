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
package jsl.utilities;

import java.util.Observable;

import jsl.utilities.reporting.JSL;

/**
 * @author rossetti
 *
 */
public abstract class DataSource extends Observable implements GetValueIfc, PreviousValueIfc,
        IdentityIfc {

    //  ===========================================
    //      CLASS ATTRIBUTES
    //  ===========================================
    /** A counter to count the number of created to assign "unique" ids
     */
    protected static long myIdCounter_;

    /** An "enum" to indicate that a new value has just been made available to observers
     */
    public static final int NEW_VALUE = JSL.getNextEnumConstant();

    //  ===========================================
    //      OBJECT ATTRIBUTES
    //  ===========================================
    /** Keeps track of the current state for observers
     */
    private int myObserverState = 0;

    /** Keeps track of the previous type of state change for observers
     */
    private int myPreviousObserverState = 0;

    /** The id of this object
     */
    protected long myId;

    /** Holds the name of the statistic for reporting purposes.
     */
    protected String myName;

    /** The current value
     * 
     */
    private double myValue = Double.NaN;

    /** The previous value of the variable.
     */
    private double myPrevValue = Double.NaN;

    /**
     *
     */
    public DataSource() {
        this(null);
    }

    /**
     *
     */
    public DataSource(String name) {
        setId();
        setName(name);
    }

    /** Gets the name.
     * @return The name of object.
     */
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

    /** Returns the id for this data source
     *
     * @return
     */
    public final long getId() {
        return (myId);
    }

    /** Every data source must implement the getValue method.  This method simply
     * returns the current value.  Returns Double.NaN if no current value
     *  is available
     *  
     * @return The value.
     */
    public double getValue() {
        return (myValue);
    }

    /** Returns the previous value for this data source if there was one
     *  returns Double.NaN if no previous value is available.
     * 
     * @return
     */
    public double getPreviousValue() {
        return (myPrevValue);
    }

    /** Checks to see if the technique is in the given observer state.
     *  This method can be used by observers that are interested in reacting to the
     *  action associated with this state for the technique.
     *
     *  NEW_VALUE
     *
     * @return True means that this DataSource is in the given state.
     */
    public final boolean checkObserverState(int observerState) {
        return (myObserverState == observerState);
    }

    /**  Returns an integer representing the state of the technique
     *   This can be used by Observers to find out what occurred for the technique
     *
     * @return The current state
     */
    public final int getObserverState() {
        return (myObserverState);
    }

    /**  Returns an integer representing the previous state of the DataSource
     *   This can be used by Observers to find out which action occurred prior to the current state change
     *
     * @return The previous state
     */
    public final int getPreviousObserverState() {
        return (myPreviousObserverState);
    }

    protected void setId() {
        myIdCounter_ = myIdCounter_ + 1;
        myId = myIdCounter_;
    }

    /** Properly assigns the value and remembers previous value
     *  notifies any observers of the change
     * 
     * @param value
     */
    protected void setValue(double value) {
        // remember the old values
        myPrevValue = myValue;
        // record the new value and time
        myValue = value;
        notifyObservers(NEW_VALUE);
    }

    /** Used to notify observers that this data source has entered the given state.
     *  Valid values for observerState include:
     *
     *  NEW_VALUE
     *
     * @param observerState
     */
    protected final void notifyObservers(int observerState) {
        setObserverState(observerState);
        setChanged();
        notifyObservers();
    }

    /**
     * @param observerState
     */
    protected final void setObserverState(int observerState) {
        myPreviousObserverState = myObserverState;
        myObserverState = observerState;
    }
}
