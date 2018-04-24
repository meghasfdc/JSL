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
package jsl.modeling;

import jsl.utilities.IdentityIfc;
import jsl.utilities.statistic.Statistic;

/**
 *
 */
public interface StateAccessorIfc extends IdentityIfc {

    /**
     * Gets whether or not the state has been entered
     *
     * @return True means that the state has been entered
     */
    public abstract boolean isEntered();

    /**
     * Gets the time that the state was last entered
     *
     * @return A double representing the time that the state was last entered
     */
    public abstract double getTimeStateEntered();

    /**
     * Gets the time that the state was last exited
     *
     * @return A double representing the time that the state was last exited
     */
    public abstract double getTimeStateExited();

    /**
     * Gets the number of times the state was entered
     *
     * @return A double representing the number of times entered
     */
    public abstract double getNumberOfTimesEntered();

    /**
     * Gets the number of times the state was exited
     *
     * @return A double representing the number of times exited
     */
    public abstract double getNumberOfTimesExited();

    /**
     * Gets a statistic that collected sojourn times
     *
     * @return A statistic for sojourn times or null if use statistic was false
     */
    public abstract Statistic getSojournTimeStatistic();

    /**
     * Gets the total time spent in the state
     *
     * @return a double representing the total sojourn time
     */
    public abstract double getTotalTimeInState();

    /**
     * 
     * @return returns getTimeStateExited() - getTimeStateEntered()
     */
    default double getTimeInState() {
        return getTimeStateExited() - getTimeStateEntered();
    }

}
