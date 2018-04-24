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
package jsl.calendar;

import jsl.modeling.JSLEvent;

/**
 * The interface defines behavior for holding, adding and retrieving JSLEvents.
 * <p>
 */
public interface CalendarIfc {

    /**
     * The add method will place the provided JSLEvent into the
     * underlying data structure ensuring the ordering of the events
     * to be processed
     *
     * @param event The JSLEvent to be added to the calendar
     */
    public void add(JSLEvent event);

    /**
     * Returns the next JSLEvent to be executed. The event is removed from
     * the calendar if it exists
     *
     * @return The JSLEvent to be executed next
     */
    public JSLEvent nextEvent();

    /**
     * Peeks at the next event without removing it
     *
     * @return
     */
    public JSLEvent peekNext();

    /**
     * Checks to see if the calendar is empty
     *
     * @return true is empty, false is not empty
     */
    public boolean isEmpty();

    /**
     * Clears or cancels every event in the data structure. Removes all
     * JSLEvents
     * from the data structure.
     */
    public void clear();

    /**
     * Cancels the supplied JSLEvent in the calendar. Canceling does not remove
     * the event from the data structure. It simply indicates that the
     * scheduled event must not be executed.
     *
     * @param event The JSLEvent to be canceled
     */
    public void cancel(JSLEvent event);

    /**
     * Returns the number of events in the calendar
     *
     * @return An int representing the number of events.
     */
    public int size();
}
