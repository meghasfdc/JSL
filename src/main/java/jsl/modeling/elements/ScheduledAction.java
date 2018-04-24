/*
 * Created on Aug 5, 2007
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

import jsl.modeling.EventActionIfc;
import jsl.modeling.JSLEvent;

/** A ScheduledAction is used on a ActionSchedule.
 *  A ScheduledAction represents a duration of time and action that
 *  will occur after the duration
 * 
 *
 */
public abstract class ScheduledAction {

    private ActionSchedule myActionSchedule;

    private double myDuration;

    private String myName;
    
    private EventActionIfc myEventAction;

    /** Creates a ScheduleAction and places it on the supplied ActionSchedule
     *
     * @param schedule
     * @param duration
     */
    public ScheduledAction(ActionSchedule schedule, double duration){
        this(schedule, duration, null);
    }
    
    /** Creates a ScheduleAction and places it on the supplied ActionSchedule
     *
     * @param schedule
     * @param duration
     * @param name
     */
    public ScheduledAction(ActionSchedule schedule, double duration, String name) {
        setDuration(duration);
        setActionSchedule(schedule);
        setName(name);
    }
    
    public final void setEventAction(EventActionIfc eventAction){
        myEventAction = eventAction;
    }

    public double getDuration() {
        return myDuration;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name = ").append(myName).append("\t");
        sb.append("Duration = ").append(myDuration).append("\t");
        return sb.toString();
    }

    /**
     *
     * @param event
     */
    protected void action(JSLEvent event){
        if (myEventAction != null){
            myEventAction.action(event);
        }
    }

    /** Sets the duration until the action is to occur
     *
     * @param duration
     */
    protected final void setDuration(double duration) {
        if (duration <= 0.0) {
            throw new IllegalArgumentException("The time duration must be > 0");
        }
        myDuration = duration;
    }

    /** Sets the ActionSchedule associated with this ScheduledAction
     *
     * @param schedule
     */
    protected final void setActionSchedule(ActionSchedule schedule) {
        if (schedule == null) {
            throw new IllegalArgumentException("The supplied TimedAction was null");
        }
        myActionSchedule = schedule;
        myActionSchedule.addScheduledAction(this);
    }
}
