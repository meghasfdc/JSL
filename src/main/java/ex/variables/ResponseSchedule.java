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
package ex.variables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import jsl.modeling.EventActionIfc;
import jsl.modeling.JSLEvent;
import jsl.modeling.ModelElement;
import jsl.modeling.SchedulingElement;
import jsl.modeling.elements.variable.Counter;
import jsl.modeling.elements.variable.ResponseVariable;

/**
 * This class allows the creation of a schedule that represents a list of
 * Intervals of time. The schedule has a cycle length. The user adds intervals
 * and responses for which statistics need to be collected during the interval.
 * The schedule can be repeated after the cycle length of the schedule is
 * reached. The intervals within the cycle may overlap in time. The start time
 * of an interval is specified relative to the beginning of the cycle. The end
 * time of an interval is specified relative to the beginning of the cycle. All
 * beginning and ending times for any interval must be contained in the cycle
 * length. The length of any interval must be finite.
 *
 * The entire schedule has a cycle length. The schedule can be repeated after
 * all intervals have been collected. The default length of a schedule's cycle
 * length is positive infinity. The default starting time of the first cycle is
 * time 0.0.
 *
 */
public class ResponseSchedule extends SchedulingElement {

    /**
     * Need to ensure that start event happens before interval responses
     */
    public final int START_EVENT_PRIORITY = 1;

    /**
     * The time that the schedule should start
     */
    protected double myStartTime;

    /**
     * The time that the schedule started for its current cycle
     *
     */
    protected double myCycleStartTime;

    /**
     * The schedule repeat flag controls whether or not the entire schedule will
     * repeat after its entire cycle has elapsed. The use of this flag only
     * makes sense if a finite schedule length is specified. The default is
     * false.
     *
     */
    protected boolean myScheduleRepeatFlag = false;

    /**
     * Represents the length of time of the schedule based on the intervals
     * added
     *
     */
    protected double myLength;

    /**
     * Holds the intervals to be invoked on schedule
     */
    protected List<ResponseScheduleItem> myScheduleItems;

    /**
     * Holds the set of intervals that have been scheduled
     */
    protected Set<ResponseInterval> myScheduledIntervals;

    /**
     * Represents the event scheduled to start the schedule
     */
    protected JSLEvent myStartEvent;

    protected StartScheduleAction myStartAction;

    /**
     * Indicates if the schedule has been scheduled
     */
    protected boolean myScheduledFlag;

    /**
     * @param parent the parent model element
     */
    public ResponseSchedule(ModelElement parent) {
        this(parent, Double.POSITIVE_INFINITY, false, null);
    }

    /**
     * @param parent the parent model element
     * @param name the name of the model element
     */
    public ResponseSchedule(ModelElement parent, String name) {
        this(parent, Double.POSITIVE_INFINITY, false, name);
    }

    /**
     *
     * @param parent the parent model element
     * @param cycleLength The total time available for the schedule
     */
    public ResponseSchedule(ModelElement parent, double cycleLength) {
        this(parent, cycleLength, false, null);
    }

    /**
     *
     * @param parent the parent model element
     * @param cycleLength The total time available for the schedule
     * @param name the name of the model element
     */
    public ResponseSchedule(ModelElement parent, double cycleLength, String name) {
        this(parent, cycleLength, false, name);
    }

    /**
     *
     * @param parent the parent model element
     * @param cycleLength The total time available for the schedule
     * @param repeatSchedule Whether or not the schedule will repeat
     * @param name
     */
    public ResponseSchedule(ModelElement parent, double cycleLength,
            boolean repeatSchedule, String name) {
        super(parent, name);
        myLength = 0.0;
        myScheduledFlag = false;
        setScheduleRepeatFlag(repeatSchedule);
        myStartAction = new StartScheduleAction();
        myScheduleItems = new ArrayList<>();
        myScheduledIntervals = new HashSet<>();
        myStartTime = Double.NEGATIVE_INFINITY;
    }

    /**
     * Specifies when the schedule is to start If negative, then the schedule
     * will not be started
     *
     * @param startTime must not be infinite
     */
    public final void setStartTime(double startTime) {
        if (startTime == Double.POSITIVE_INFINITY) {
            throw new IllegalArgumentException("The start time cannot be infinity");
        }
        myStartTime = startTime;
    }

    /**
     *
     * @return the time to start the schedule
     */
    public final double getStartTime() {
        return myStartTime;
    }

    /**
     *
     * @return true if the interval has been scheduled
     */
    public final boolean isScheduled() {
        return myScheduledFlag;
    }

    /**
     * Returns the time that the schedule started its current cycle
     *
     * @return the cycle start time
     */
    public final double getCycleStartTime() {
        return myCycleStartTime;
    }

    /**
     * The time that has elapsed into the current cycle
     *
     * @return the time within the cycle
     */
    public final double getElapsedCycleTime() {
        return getTime() - myCycleStartTime;
    }

    /**
     * The time remaining within the current cycle
     *
     * @return time remaining within the current cycle
     */
    public final double getRemainingCycleTime() {
        return myCycleStartTime + myLength - getTime();
    }

    /**
     * Sets whether or not the schedule will repeat after it reaches it length
     *
     * @param flag true means repeats
     */
    public final void setScheduleRepeatFlag(boolean flag) {
        myScheduleRepeatFlag = flag;
    }

    /**
     * Returns whether or not the schedule will repeat after it reaches it
     * length
     *
     * @return true means it repeats
     */
    public final boolean getScheduleRepeatFlag() {
        return myScheduleRepeatFlag;
    }

    /**
     * Gets the total length of the schedule.
     *
     * @return the length
     */
    public final double getLength() {
        return myLength;
    }

    /**
     * The number of intervals in the schedule
     *
     * @return number of interval in the schedule
     */
    public final int getNumberOfIntervals() {
        return myScheduleItems.size();
    }

    /**
     * Adds an interval response to the schedule. It should start at the time
     * specified relative to the start of the schedule
     *
     * @param startTime the start time relative to the start of the schedule
     * @param iResponse the interval response to add
     */
    public final void addIntervalResponse(double startTime, ResponseInterval iResponse) {

        ResponseScheduleItem item = new ResponseScheduleItem(startTime, iResponse);

        if (startTime + iResponse.getDuration() > getLength()) {
            myLength = startTime + iResponse.getDuration();
        }

        iResponse.setResponseSchedule(this);
        myScheduleItems.add(item);
    }

    /**
     * An unmodifiable list of the ResponseScheduleItem
     *
     * @return An unmodifiable list of the ResponseScheduleItem
     */
    public final List<ResponseScheduleItem> getResponseScheduleItems() {
        return Collections.unmodifiableList(myScheduleItems);
    }

    /**
     * Causes interval statistics to be collected for the response for every
     * interval in the schedule
     *
     * @param response the response to add
     */
    public final void addResponseToAllIntervals(ResponseVariable response) {
        for (ResponseScheduleItem item : myScheduleItems) {
            item.getResponseInterval().addResponseToInterval(response);
        }
    }

    /**
     * Causes interval statistics to be collected for the counter for every
     * interval in the schedule
     *
     * @param counter the counter to add
     */
    public final void addCounterToAllIntervals(Counter counter) {
        for (ResponseScheduleItem item : myScheduleItems) {
            item.getResponseInterval().addCounterToInterval(counter);
        }
    }

    /**
     * Add an ResponseInterval
     *
     * @param startTime
     * @param label the label associated with the interval, must not be null
     * @param duration duration of the interval
     * @return the ResponseInterval
     */
    public final ResponseInterval addIntervalResponse(double startTime,
            double duration, String label) {
        ResponseInterval ir = new ResponseInterval(this, duration, label);
        addIntervalResponse(startTime, ir);
        return ir;
    }

    /**
     * Add non-overlapping, sequential intervals to the schedule, each having
     * the provided duration
     *
     * @param numIntervals the number of intervals
     * @param duration the duration of each interval
     */
    public final void addConsecutiveIntervals(int numIntervals, double duration) {
        addConsecutiveIntervals(numIntervals, duration, null);
    }

    /**
     * Add non-overlapping, sequential intervals to the schedule, each having
     * the provided duration
     *
     * @param numIntervals the number of intervals
     * @param duration the duration of each interval
     * @param label a base label for each interval, if null a label is created
     */
    public final void addConsecutiveIntervals(int numIntervals, double duration,
            String label) {
        if (numIntervals < 1) {
            throw new IllegalArgumentException("The number of intervals must be >=1");
        }
        double t = 0.0;
        String s;
        for (int i = 1; i <= numIntervals; i++) {
            if (label == null) {
                s = String.format("[%.1f,%.1f]", t, t + duration);
            } else {
                s = label + ":" + i;
            }
            addIntervalResponse(t, duration, s);
            t = t + duration;
        }
    }

    @Override
    public String asString(){
        StringBuilder sb = new StringBuilder();
        sb.append(getName());
        sb.append(System.lineSeparator());
        sb.append("Repeats: ");
        sb.append(getScheduleRepeatFlag());
        sb.append(System.lineSeparator());
        sb.append("Start time: ");
        sb.append(getStartTime());
        sb.append(System.lineSeparator());
        sb.append("Length: ");
        sb.append(getLength());
        sb.append(System.lineSeparator());
        sb.append("#Intervals: ");
        sb.append(getNumberOfIntervals());
        sb.append(System.lineSeparator());
        sb.append("------");
        sb.append(System.lineSeparator());
        int i = 1;
        for (ResponseScheduleItem item : myScheduleItems) {
            sb.append("Item: ");
            sb.append(i);
            sb.append(System.lineSeparator());
            sb.append(item);
            sb.append(System.lineSeparator());
            i++;
        }
        sb.append("------");
        return sb.toString();
    }

    /**
     * Schedules the start of the schedule for t + time the schedule if it has
     * not already be started
     *
     * @param timeToStart
     */
    protected final void scheduleStart(double timeToStart) {
        if (isScheduled()) {
            throw new IllegalStateException("The schedule as already been scheduled to start");
        }
        myScheduledFlag = true;
//        System.out.println("In ResponseSchedule: scheduleStart()");
//        System.out.println("> scheduling the start of the schedule");
        myStartEvent = scheduleEvent(myStartAction, timeToStart, START_EVENT_PRIORITY);

    }

    @Override
    protected void initialize() {
//        System.out.println("In ResponseSchedule: initialize()");
//        System.out.println("getStartTime() = " + getStartTime());
        super.initialize();
        if (getStartTime() >= 0.0) {
            scheduleStart(getStartTime());
        }
    }

    @Override
    protected void afterReplication() {
//        System.out.println("In ResponseSchedule: afterReplication()");
        super.afterReplication();
        myScheduledFlag = false;
        myStartEvent = null;
        myScheduledIntervals.clear();
    }

    /**
     * Used to communicate that the response interval ended
     *
     * @param responseInterval the interval that ended
     */
    protected void responseIntervalEnded(ResponseInterval responseInterval) {
        // cancel the interval so that it can be used again
        responseInterval.cancelInterval();
        myScheduledIntervals.remove(responseInterval);
        if (getScheduleRepeatFlag() == true) {
            if (myScheduledIntervals.isEmpty()) {
                // all response intervals have completed, safe to start again
                myScheduledFlag = false;
//                System.out.println(getTime() + "> *** Scheduling the schedule to repeat:");
                scheduleStart(0.0);
            }
        }
    }

    protected class StartScheduleAction implements EventActionIfc {

        @Override
        public void action(JSLEvent evt) {
//            System.out.println("In StartScheduleAction: action()");
//            System.out.println(getTime() + " > scheduling the intervals");
            for (ResponseScheduleItem item : myScheduleItems) {
                double startTime = item.getStartTime();
                ResponseInterval ri = item.getResponseInterval();
                ri.scheduleInterval(startTime);
                myScheduledIntervals.add(ri);
            }
        }

    }

    //TODO need to handle removal of intervals from the model
}
