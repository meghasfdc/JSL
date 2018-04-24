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
package jsl.modeling.elements.resource;

import jsl.utilities.IdentityIfc;

/**
 *
 */
public class AttributeType implements IdentityIfc {

    /** incremented to give a running total of the
     *  number of attribute types created
     */
    private static int myCounter_;

    /** The id of the attribute type, currently if
     *  the attribute type is the ith attribute type created
     *  then the id is equal to i
     */
    private int myId;

    /** The name of the attribute type
     */
    private String myName;

    protected AttributeType() {
        this(null);
    }

    /**
     * @param name
     */
    protected AttributeType(String name) {
        myCounter_ = myCounter_ + 1;
        myId = myCounter_;
        setName(name);
    }

    /** Sets the name of this attribute type
     * @param str The name as a string.
     */
    protected void setName(String str) {

        if (str == null) { // no name is being passed, construct a default name
            String s = this.getClass().getName();
            int k = s.lastIndexOf(".");
            if (k != -1) {
                s = s.substring(k + 1);
            }
            str = s + "-" + getId();
        }
        myName = str;
    }

    /** Gets this attribute type's name.
     * @return The name of the attribute type.
     */
    public final String getName() {
        return myName;
    }

    /** Gets a uniquely assigned integer identifier for this attribute type.
     * This identifier is assigned when the attribute type is
     * created.  It may vary if the order of creation changes.
     * @return The identifier for the attribute type.
     */
    public final long getId() {
        return (myId);
    }
}
