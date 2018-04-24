/*
 * Created on Aug 4, 2007
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import jsl.modeling.JSLEvent;
import jsl.modeling.ModelElement;
import jsl.modeling.SchedulingElement;

/** This class allows the creation of a schedule that represents a list of 
 *  actions with the time between each action specified. The user adds a ScheduleAction
 *  or a duration/action pair and can specify when the schedule should start invoking 
 *  the actions with a start time.  The actions can be repeated after all duration/action 
 *  pairs have been invoked only if their total duration does not exceed the time
 *  remaining until the end of the action schedule's cycle length.
 *  
 *		Model m = Model.createModel();
 *
 *		ActionSchedule s = new ActionSchedule(m);
 *		
 *		ScheduledAction a1 = new ScheduledAction("action 1");
 *		ScheduledAction a2 = new ScheduledAction("action 2");
 *	
 *		s.addAction(20.0, a1);
 *		s.addAction(15.0, a2);
 *  
 *  After 20 time units, a1 will be invoked, after 15 time units a2 will be invoked. This
 *  pattern will be repeated. Thus the duration represents the time until the action is
 *  invoked after the previous action (or start of the schedule).
 *  
 *  The entire schedule has a cycle length. The total of all the durations added must be less than
 *  the action schedule's cycle length.  The schedule can be repeated after all actions have been completed.
 *  The default length of a schedule's cycle length is positive infinity
 *
 */
public class ActionSchedule extends SchedulingElement {

    public static final int START_EVENT = 1;

    public static final int ACTION_EVENT = 2;

    public static final int END_EVENT = 3;

    /** Indicates whether or not the schedule should be started
     *  automatically upon initialization, default is true
     */
    protected boolean myAutomaticStartFlag = true;

    /** Indicates whether or not the actions on the schedule should be repeated
     *  after completing all the scheduled actions.  The
     *  default is to repeat the actions
     */
    protected boolean myActionRepeatFlag = true;

    /** The time from the beginning of the replication
     *  to the time that the schedule is to start
     */
    protected double myInitialStartTime = 0.0;

    /** The time that the schedule started for its current cycle
     *
     */
    protected double myCycleStartTime;

    /** The schedule repeat flag controls whether or not
     *  the entire schedule will repeat after its entire duration
     *  has elapsed. The default is to repeat the schedule.  The
     *  use of this flag only makes sense if a finite schedule length is specified
     *
     */
    protected boolean myScheduleRepeatFlag = true;

    /** Represents the total length of time of the schedule.
     *  The total of the durations added to the schedule cannot exceed this amount
     *  After this time has elapsed the entire schedule can repeat if the
     *  schedule repeat flag is true.  The default is infinite.
     *
     */
    protected double myScheduleLength = Double.POSITIVE_INFINITY;

    /** Keeps track of the total of the durations that have
     *  been added to the schedule.
     *
     */
    protected double myDurationTotal = 0.0;

    /** Holds the actions to be invoked by time
     */
    protected List<ScheduledAction> myActions;

    /** Represents the event scheduled to start the schedule
     */
    protected JSLEvent myStartEvent;

    /** Represents the event for the actions on the schedule
     */
    protected JSLEvent myActionEvent;

    /** Represents the event for the end of the schedule
     */
    protected JSLEvent myEndEvent;

    /** An iterator over the actions
     */
    protected Iterator<ScheduledAction> myActionIterator;

    /** The action that is currently scheduled, next to occur
     */
    protected ScheduledAction myNextScheduledAction;

    /**
     * @param parent
     */
    public ActionSchedule(ModelElement parent) {
        this(parent, 0.0, Double.POSITIVE_INFINITY, true, true, true, null);
    }

    /**
     * @param parent
     * @param name
     */
    public ActionSchedule(ModelElement parent, String name) {
        this(parent, 0.0, Double.POSITIVE_INFINITY, true, true, true, name);
    }

    /**
     *
     * @param parent
     * @param scheduleLength The total time available for the schedule
     */
    public ActionSchedule(ModelElement parent, double scheduleLength) {
        this(parent, 0.0, scheduleLength, true, true, true, null);
    }

    /**
     *
     * @param parent
     * @param startTime The time that the schedule should start
     * @param scheduleLength The total time available for the schedule
     */
    public ActionSchedule(ModelElement parent, double startTime, double scheduleLength) {
        this(parent, startTime, scheduleLength, true, true, true, null);
    }

    /**
     *
     * @param parent
     * @param scheduleLength The total time available for the schedule
     * @param name
     */
    public ActionSchedule(ModelElement parent, double scheduleLength, String name) {
        this(parent, 0.0, scheduleLength, true, true, true, name);
    }

    /**
     *
     * @param parent
     * @param startTime The time that the schedule should start
     * @param scheduleLength The total time available for the schedule
     * @param name
     */
    public ActionSchedule(ModelElement parent, double startTime, double scheduleLength, String name) {
        this(parent, startTime, scheduleLength, true, true, true, name);
    }

    /**
     *
     * @param parent
     * @param startTime The time that the schedule should start
     * @param scheduleLength The total time available for the schedule
     * @param repeatSchedule Whether or not the schedule will repeat
     * @param repeatActions  Whether or not the actions may repeat
     * @param startAutomatically Whether or not the start of the schedule is scheduled automatically
     * @param name
     */
    public ActionSchedule(ModelElement parent, double startTime, double scheduleLength, 
            boolean repeatSchedule, boolean repeatActions, boolean startAutomatically, String name) {
        super(parent, name);
        setInitialStartTime(startTime);
        setScheduleLength(scheduleLength);
        setScheduleRepeatFlag(repeatSchedule);
        setActionRepeatFlag(repeatActions);
        setAutomaticStartFlag(startAutomatically);
        myActions = new ArrayList<ScheduledAction>();
    }

    /** Sets the flag that indicates whether or not the first action will automatically
     *  schedule when initialize() is called.
     *
     * @param flag
     */
    public final void setAutomaticStartFlag(boolean flag) {
        myAutomaticStartFlag = flag;
    }

    /** This flag indicates whether or not the action will automatically
     *  scheduled when initialize() is called.  By default this option is
     *  true.
     *
     * @return
     */
    public final boolean getAutomaticStartFlag() {
        return myAutomaticStartFlag;
    }

    /** True means the scheduled actions will repeat after all actions have been invoked. By default the
     *  scheduled actions will repeat.
     * @return Returns the repeatActionFlag.
     */
    public final boolean getActionRepeatFlag() {
        return myActionRepeatFlag;
    }

    /** True means the scheduled actions will repeat after all actions have been invoked. By default the
     *  scheduled actions will repeat.
     * @param repeatActionFlag The repeatActionFlag to set.
     */
    public final void setActionRepeatFlag(boolean repeatActionFlag) {
        myActionRepeatFlag = repeatActionFlag;
    }

    /** Gets the time after the beginning of the replication that represents
     *  the starting time for the schedule
     *
     * @return
     */
    public final double getInitialStartTime() {
        return (myInitialStartTime);
    }

    /** Sets the starting time after the beginning of the replication for the
     *  schedule to start.
     *
     * @param startTime Must be &gt;= zero
     */
    protected final void setInitialStartTime(double startTime) {
        if (startTime < 0) {
            throw new IllegalArgumentException("The start time must be >= 0");
        }
        myInitialStartTime = startTime;
    }

    /** Returns the time that the schedule started its current cycle
     *
     * @return
     */
    public final double getCycleStartTime() {
        return myCycleStartTime;
    }

    /** The time that has elapsed into the current cycle
     *
     * @return
     */
    public final double getElapsedCycleTime() {
        return getTime() - myCycleStartTime;
    }

    /** The time remaining within the current cycle
     *
     * @return
     */
    public final double getRemainingCycleTime() {
        return myCycleStartTime + myScheduleLength - getTime();
    }

    /** Sets whether or not the schedule will repeat after it reaches
     *  it length
     *
     * @param flag
     */
    public final void setScheduleRepeatFlag(boolean flag) {
        myScheduleRepeatFlag = flag;
    }

    /** Returns whether or not the schedule will repeat after it reaches
     *  it length
     *
     * @return
     */
    public final boolean getScheduleRepeatFlag() {
        return myScheduleRepeatFlag;
    }

    /** Sets the total length of the schedule
     *
     * @param scheduleLength Must be &gt; 0
     */
    protected final void setScheduleLength(double scheduleLength) {
        if (scheduleLength <= 0) {
            throw new IllegalArgumentException("The schedule length must be > 0");
        }
        myScheduleLength = scheduleLength;
    }

    /** Gets the total length of the schedule.  The total length of the
     *  schedule must be &gt;= the total of the durations on the schedule
     *
     * @return
     */
    public final double getScheduleLength() {
        return myScheduleLength;
    }

    /** Returns the amount of time left within the schedule cycle length for possible duration/actions
     *
     * @return
     */
    public final double getDurationRemainingOnSchedule() {
        return myScheduleLength - myDurationTotal;
    }

    /** Returns how much of the schedule's length has been covered by actions
     *
     * @return
     */
    public final double getDurationTotal() {
        return myDurationTotal;
    }

    /** The number of actions that have been scheduled
     *
     * @return
     */
    public final int getNumberOfActions() {
        return myActions.size();
    }

    /** Adds a scheduled action to the schedule.  The action must not have
     *  a duration that causes the total action durations to exceed the cycle length
     *  of the schedule
     *
     * @param action
     */
    protected final void addScheduledAction(ScheduledAction action) {
        if (action == null) {
            throw new IllegalArgumentException("The supplied action cannot be null");
        }

        if (myActions.contains(action)) {
            throw new IllegalArgumentException("The supplied action is already part of this schedule.");
        }

        double d = action.getDuration();
        if (d + myDurationTotal > myScheduleLength) {
            throw new IllegalArgumentException("The supplied scheduled action has a duration that overflows the time available on the schedule");
        }

        myActions.add(action);
        myDurationTotal = myDurationTotal + d;

    }

    /** Schedules the start of the schedule for the start time of the schedule
     *  if it has not already be started
     */
    public final void scheduleStart() {
        if (myStartEvent == null) {
            myStartEvent = scheduleEvent(getInitialStartTime(), START_EVENT);
        }
    }

    @Override
    protected void initialize() {
        if (myAutomaticStartFlag) {
            scheduleStart();
        }
    }

    @Override
    protected void afterReplication() {
        super.afterReplication();
        myStartEvent = null;
        myNextScheduledAction = null;
        myActionIterator = null;
        myActionEvent = null;
        myEndEvent = null;
    }

    protected void scheduleNextAction() {
        myNextScheduledAction = myActionIterator.next();
        rescheduleEvent(myActionEvent, myNextScheduledAction.getDuration());
    }

    @Override
    protected void handleEvent(JSLEvent event) {
        if (event.getType() == START_EVENT) {
            myCycleStartTime = getTime();
            // get iterator to actions
            myActionIterator = myActions.iterator();
            if (myActionIterator.hasNext()) {
                //System.out.println(getTime() + "> " + "scheduling the first action");
                // schedule first action
                myNextScheduledAction = myActionIterator.next();
                myActionEvent = scheduleEvent(myNextScheduledAction.getDuration(), ACTION_EVENT);
            }

            if (myScheduleLength < Double.POSITIVE_INFINITY) {
                myEndEvent = scheduleEvent(myScheduleLength, END_EVENT);
            }
            return;
        }

        if (event.getType() == ACTION_EVENT) {
            myNextScheduledAction.action(event);
            if (myActionIterator.hasNext()) {
                scheduleNextAction();
            } else {
                //System.out.println(getTime() + "> " + "all actions have completed within the schedule");
                // all actions have completed within the schedule
                // check if actions should repeat
                if (myActionRepeatFlag == true) {
                    // actions are allowed to repeat only if
                    // all the actions can complete before the end of the current cycle
                    if (myDurationTotal < getRemainingCycleTime()) {
                        //System.out.println(getTime() + "> " + "actions are repeating");
                        myActionIterator = myActions.iterator();
                        if (myActionIterator.hasNext()) {
                            scheduleNextAction();
                        }
                    }
                }
            }
            return;
        }

        if (event.getType() == END_EVENT) {
            if (myScheduleRepeatFlag == true) {
                //System.out.println(getTime() + "> " + "Schedule is repeating...");
                myCycleStartTime = getTime();
                // get iterator to actions
                myActionIterator = myActions.iterator();
                if (myActionIterator.hasNext()) {
                    // schedule first action
                    myNextScheduledAction = myActionIterator.next();
                    myActionEvent = scheduleEvent(myNextScheduledAction.getDuration(), ACTION_EVENT);
                }

                if (myScheduleLength < Double.POSITIVE_INFINITY) {
                    myEndEvent = scheduleEvent(myScheduleLength, END_EVENT);
                }
                return;
            }
        }
    }

}
