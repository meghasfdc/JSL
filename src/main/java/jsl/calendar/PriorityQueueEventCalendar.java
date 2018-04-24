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

import java.util.PriorityQueue;

import jsl.modeling.JSLEvent;

/**
 * This class provides an event calendar by using a priority queue to hold the
 * underlying events.
 */
public class PriorityQueueEventCalendar implements CalendarIfc {

    private final PriorityQueue<JSLEvent> myEventSet;

    public PriorityQueueEventCalendar() {
        super();
        myEventSet = new PriorityQueue<>();
    }

    @Override
    public final void add(JSLEvent event) {
        myEventSet.add(event);
    }

    @Override
    public final JSLEvent nextEvent() {
        return ((JSLEvent) myEventSet.poll());
    }

    @Override
    public final JSLEvent peekNext() {
        return (myEventSet.peek());
    }

    @Override
    public final boolean isEmpty() {
        return (myEventSet.isEmpty());
    }

    @Override
    public final void clear() {
        myEventSet.clear();
    }

    @Override
    public final void cancel(JSLEvent event) {
        event.setCanceledFlag(true);
    }

    @Override
    public int size() {
        return (myEventSet.size());
    }

}
