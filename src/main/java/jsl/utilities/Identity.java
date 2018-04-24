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
package jsl.utilities;

/**
 *
 * @author rossetti
 */
public class Identity implements IdentityIfc {

    /** A counter to count the number of objects created to assign "unique" ids
     */
    private static long myIdCounter_;

    /** The name of this object
     */
    private String myName;

    /** The id of this object
     */
    private long myId;

    public Identity(){
        this(null);
    }

    public Identity(String name) {
        myIdCounter_ = myIdCounter_ + 1;
        myId = myIdCounter_;
        setName(name);
    }

    /** Gets the name.
     * @return The name of object.
     */
    @Override
    public final String getName() {
        return myName;
    }

    /** Returns the id for this object
     *
     * @return
     */
    @Override
    public final long getId() {
        return (myId);
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
}
