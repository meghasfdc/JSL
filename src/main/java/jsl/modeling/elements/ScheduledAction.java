/*
 * Copyright (c) 2018. Manuel D. Rossetti, manuelrossetti@gmail.com
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
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
