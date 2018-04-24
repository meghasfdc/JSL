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
package jsl.modeling.elements;

import jsl.modeling.JSLEvent;

/**
 *
 */
public abstract class TimedActionListener implements Comparable<TimedActionListener> {

    /** Represents the default priority for the action 
     * DEFAULT_PRIORITY = 10.  Lower priority goes first.  All integer
     * priority numbers can be used to set the priority of a listener.
     */
    public static final int DEFAULT_PRIORITY = 10;

    private String myName;

    private int myPriority;

    private int myId;

    private boolean myCallActionFlag;

    private TimedAction myTimedAction;

    /** Create and attach the TimedActionListener to the supplied TimedAction
     *
     * @param timedAction
     */
    public TimedActionListener(TimedAction timedAction) {
        this(timedAction, DEFAULT_PRIORITY, null);
    }

    /** Create and attach the TimedActionListener to the supplied TimedAction
     *
     * @param timedAction
     * @param name
     */
    public TimedActionListener(TimedAction timedAction, String name) {
        this(timedAction, DEFAULT_PRIORITY, name);
    }

    /** Create and attach the TimedActionListener to the supplied TimedAction
     *
     * @param timedAction
     * @param priority
     */
    public TimedActionListener(TimedAction timedAction, int priority) {
        this(timedAction, priority, null);
    }

    /** Create and attach the TimedActionListener to the supplied TimedAction
     *
     * @param timedAction
     * @param priority
     * @param name
     */
    public TimedActionListener(TimedAction timedAction, int priority, String name) {
        myPriority = priority;
        myCallActionFlag = true;
        setName(name);
        setTimedAction(timedAction);
    }

    /** Gets the name of the event
     * @return The name of the event
     */
    public final String getName() {
        return (myName);
    }

    /** Sets the name
     *
     * @param name
     */
    public final void setName(String name) {
        myName = name;
    }

    /** Gets the priority of the event
     * @return An int representing the priority.  Lower is better.
     */
    public final int getPriority() {
        return (myPriority);
    }

    /** Gets the the id assigned to the event by the scheduler.  No two
     * events have the same id.
     * @return A long representing the id
     */
    public final int getId() {
        return (myId);
    }

    /** Indicates whether or not the listener will
     *  be called when its TimedAction occurs
     * 
     * @return
     */
    public final boolean getCallActionFlag() {
        return this.myCallActionFlag;
    }

    /** If the flag is false, the listener will not
     *  be called by its TimedAction, but it will not
     *  be removed(detached) from the TimedAction
     *
     * @param flag
     */
    public final void setCallActionFlag(boolean flag) {
        myCallActionFlag = flag;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID = ").append(myId).append("\t");
        sb.append("Priority = ").append(myPriority).append("\t");
        return sb.toString();
    }

    /** Sets the id of the event, package scope because only the Scheduler should be setting the id
     * @param id Provided by the Scheduler class
     */
    protected final void setId(int id) {
        myId = id;
    }

    /**
     *
     * @param timedAction
     */
    protected final void setTimedAction(TimedAction timedAction) {
        if (timedAction == null) {
            throw new IllegalArgumentException("The supplied TimedAction was null");
        }
        myTimedAction = timedAction;
        myTimedAction.attachTimedActionListener(this);
    }

    /**
     *
     * @return
     */
    protected final TimedAction getTimedAction(){
        return myTimedAction;
    }
    
    /** Must be overridden to provide logic associated with the action
     *
     * @param event
     */
    abstract protected void action(JSLEvent event);

    /** Returns a negative integer, zero, or a positive integer
     * if this object is less than, equal to, or greater than the
     * specified object.
     *
     * Natural ordering:  priority, then order of creation
     *
     * Lower priority, lower order of creation goes first
     *
     * Throws ClassCastException if the specified object's type
     * prevents it from begin compared to this object.
     *
     * Throws RuntimeException if the id's of the objects are the same,
     * but the references are not when compared with equals.
     *
     * Note:  This class may have a natural ordering that is inconsistent
     * with equals.
     * @param listener The listener to compare this listener to
     * @return Returns a negative integer, zero, or a positive integer
     * if this object is less than, equal to, or greater than the
     * specified object.
     */
    @Override
    public int compareTo(TimedActionListener listener) {

        // check priorities

        if (myPriority < listener.getPriority()) {
            return (-1);
        }

        if (myPriority > listener.getPriority()) {
            return (1);
        }

        // priorities are equal, compare ids

        if (myId < listener.getId()) // lower id, implies created earlier
        {
            return (-1);
        }

        if (myId > listener.getId()) {
            return (1);
        }

        // if the id's are equal then the object references must be equal
        // if this is not the case there is a problem

        if (this.equals(listener)) {
            return (0);
        } else {
            throw new RuntimeException("Id's were equal, but references were not, in JSLEvent compareTo");
        }

    }
}
