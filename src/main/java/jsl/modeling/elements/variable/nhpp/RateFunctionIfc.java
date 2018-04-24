/*
 * Created on Aug 30, 2007
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
package jsl.modeling.elements.variable.nhpp;

/** Models a rate function for the non-stationary Poisson Process
 * @author rossetti
 *
 */
public interface RateFunctionIfc {

    /** Returns the rate the the supplied time
     * 
     * @param time the time to evaluate
     * @return Returns the rate the the supplied time
     */
    double getRate(double time);

    /** Gets the maximum value of the rate function over its time horizon
     *  
     * @return Gets the maximum value of the rate function over its time horizon
     */
    double getMaximum();

    /** Gets the minimum value of the rate function over its time horizon
     * 
     * @return Gets the minimum value of the rate function over its time horizon
     */
    double getMinimum();

    /** The function's lower limit on the time range
     * 
     * @return The function's lower limit on the time range
     */
    double getTimeRangeLowerLimit();

    /** The function's upper limit on the time range
     * 
     * @return The function's upper limit on the time range
     */
    double getTimeRangeUpperLimit();

    /** Returns true if the supplied time is within the time range
     *  of the rate function
     * 
     * @param time the time to evaluate
     * @return true if the supplied time is within the time range
     */
    boolean contains(double time);
}
