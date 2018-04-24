/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 *
 * Copyright (c) Manuel D. Rossetti (rossetti@uark.edu)
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
package jsl.modeling.elements.resource;

/**
 *
 * @author rossetti
 */
abstract public class SeizeRequirement implements Comparable<SeizeRequirement> {

    protected static int myCounter_ = 0;

    protected int myId;

    protected int myAmtNeeded;

    protected boolean myPartialFillFlag;

    protected int myPriority;

    public SeizeRequirement(int amt, int priority, boolean partialFillFlag) {
        if (amt <= 0) {
            throw new IllegalArgumentException("The amount required must be > 0");
        }
        myCounter_ = myCounter_ + 1;
        myId = myCounter_;
        myAmtNeeded = amt;
        myPriority = priority;
        myPartialFillFlag = partialFillFlag;
    }

    abstract public Request createRequest(Entity entity, AllocationListenerIfc listener);

    abstract public SeizeIfc getResource();

    public final int getAmountRequired() {
        return myAmtNeeded;
    }

    public final int getPriority() {
        return myPriority;
    }

    public final boolean isPartiallyFillable() {
        return myPartialFillFlag;
    }

    public final int getId() {
        return myId;
    }

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
     * @param req The requirement to compare this listener to
     * @return Returns a negative integer, zero, or a positive integer
     * if this object is less than, equal to, or greater than the
     * specified object.
     */
    @Override
    public int compareTo(SeizeRequirement req) {

        // check priorities

        if (myPriority < req.getPriority()) {
            return (-1);
        }

        if (myPriority > req.getPriority()) {
            return (1);
        }

        // priorities are equal, compare ids

        if (myId < req.getId()) // lower id, implies created earlier
        {
            return (-1);
        }

        if (myId > req.getId()) {
            return (1);
        }

        // if the id's are equal then the object references must be equal
        // if this is not the case there is a problem

        if (this.equals(req)) {
            return (0);
        } else {
            throw new RuntimeException("Id's were equal, but references were not, in JSLEvent compareTo");
        }

    }
}
