/*
 * Copyright (c) 2018. Manuel D. Rossetti, rossetti@uark.edu
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

package jsl.modeling.elements.variable;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.Table;
import jsl.modeling.*;
import jsl.observers.ModelElementObserver;
import jsl.utilities.Interval;
import jsl.utilities.statistic.StateFrequency;

import java.util.Arrays;
import java.util.List;

import static jsl.utilities.reporting.JSL.D2FORMAT;

/**
 *  Collects statistics on whether or not a specific level associated with a variable is
 *  maintained.
 */
public class LevelResponse extends SchedulingElement {

    private final Variable myVariable;
    private final double myLevel;
    private final State myAbove;
    private final State myBelow;
    private final StateFrequency myStateFreq;
    private final ModelElementObserver myObserver = new TheObserver();
    private State myCurrentState;
    private final ResponseVariable myDistanceAbove;
    private final ResponseVariable myDistanceBelow;
    // all these are collected within replicationEnded()
    private ResponseVariable myAvgTimeAbove;
    private ResponseVariable myAvgTimeBelow;
    private ResponseVariable myMaxTimeAbove;
    private ResponseVariable myMaxTimeBelow;
    private ResponseVariable myPAA;
    private ResponseVariable myPAB;
    private ResponseVariable myPBB;
    private ResponseVariable myPBA;
    private ResponseVariable myNAA;
    private ResponseVariable myNAB;
    private ResponseVariable myNBB;
    private ResponseVariable myNBA;
    private final ResponseVariable myPctTimeAbove;
    private final ResponseVariable myPctTimeBelow;
    private final ResponseVariable myTotalTimeAbove;
    private final ResponseVariable myTotalTimeBelow;
    private final ResponseVariable myMaxDistanceAbove;
    private final ResponseVariable myMaxDistanceBelow;
    // end of collected in replicationEnded()
    private final boolean myStatsOption;
    protected double myInitTime;
    private double myObservationIntervalStartTime;
    private double myObservationIntervalDuration;
    private JSLEvent myObservationIntervalStartEvent;
    private JSLEvent myObservationIntervalEndEvent;
    private boolean myHasObservationIntervalFlag = false;
    private boolean myIntervalEndedFlag;
    private boolean myIntervalStartedFlag;

    /**
     *
     * @param variable the variable to observe
     * @param level the level to associate with the variable
     * @param name the name of the response
     */
    public LevelResponse(Variable variable, double level, String name) {
        this(variable, level, true, name);
    }

    /**
     *
     * @param variable the variable to observe
     * @param level the level to associate with the variable
     */
    public LevelResponse(Variable variable, double level) {
        this(variable, level, true, null);
    }

    /**
     *
     * @param variable the variable to observe
     * @param level the level to associate with the variable
     * @param stats whether or not detailed state change statistics are collected
     */
    public LevelResponse(Variable variable, boolean stats, double level) {
        this(variable, level, stats, null);
    }

    /**
     *
     * @param variable the variable to observe
     * @param level the level to associate with the variable
     * @param stats whether or not detailed state change statistics are collected
     * @param name the name of the response
     */
    public LevelResponse(Variable variable, double level, boolean stats, String name) {
        super(variable, name);
        myVariable = variable;
        if ((level < myVariable.getLowerLimit()) || (myVariable.getUpperLimit() < level)) {
            Interval i = new Interval(myVariable.getLowerLimit(), myVariable.getUpperLimit());
            throw new IllegalArgumentException("The supplied level " + level + " was outside the range of the variable " + i);
        }
        myLevel = level;
        // collected during the replication
        myStateFreq = new StateFrequency(2);
        List<State> list = myStateFreq.getStates();
        myAbove = list.get(0);
        myBelow = list.get(1);
        myAbove.setName(getName() + ":+");
        myBelow.setName(getName() + ":-");
        myAbove.turnOnSojournTimeCollection();
        myBelow.turnOnSojournTimeCollection();
        myVariable.addObserver(myObserver);
        myDistanceAbove = new ResponseVariable(this, getName() + ":DistAboveLimit:" + D2FORMAT.format(level));
        myDistanceBelow = new ResponseVariable(this, getName() + ":DistBelowLimit:" + D2FORMAT.format(level));
        // collected after the replication ends
        myMaxDistanceAbove = new ResponseVariable(this, getName() + ":MaxDistAboveLimit:" + D2FORMAT.format(level));
        myMaxDistanceBelow = new ResponseVariable(this, getName() + ":MaxDistBelowLimit:" + D2FORMAT.format(level));
        myPctTimeAbove = new ResponseVariable(this, getName() + ":PctTimeAbove:" + D2FORMAT.format(level));
        myPctTimeBelow = new ResponseVariable(this, getName() + ":PctTimeBelow:" + D2FORMAT.format(level));
        myTotalTimeAbove = new ResponseVariable(this, getName() + ":TotalTimeAbove:" + D2FORMAT.format(level));
        myTotalTimeBelow = new ResponseVariable(this, getName() + ":TotalTimeBelow:" + D2FORMAT.format(level));
        myStatsOption = stats;
        if (stats) {
            myAvgTimeAbove = new ResponseVariable(this, getName() + ":AvgTimeAboveLimit:" + D2FORMAT.format(level));
            myAvgTimeBelow = new ResponseVariable(this, getName() + ":AvgTimeBelowLimit:" + D2FORMAT.format(level));
            myMaxTimeAbove = new ResponseVariable(this, getName() + ":MaxTimeAboveLimit:" + D2FORMAT.format(level));
            myMaxTimeBelow = new ResponseVariable(this, getName() + ":MaxTimeBelowLimit:" + D2FORMAT.format(level));
            myPAA = new ResponseVariable(this, getName() + ":P(AboveToAbove)");
            myPAB = new ResponseVariable(this, getName() + ":P(AboveToBelow)");
            myPBB = new ResponseVariable(this, getName() + ":P(BelowToBelow)");
            myPBA = new ResponseVariable(this, getName() + ":P(BelowToAbove)");
            myNAA = new ResponseVariable(this, getName() + ":#(AboveToAbove)");
            myNAB = new ResponseVariable(this, getName() + ":#(AboveToBelow)");
            myNBB = new ResponseVariable(this, getName() + ":#(BelowToBelow)");
            myNBA = new ResponseVariable(this, getName() + ":#(BelowToAbove)");
        }
    }

    /**
     * Causes an observation interval to be specified. An observation interval is
     * an interval of time over which the response statistics will be collected.  This
     * method will cause events to be scheduled (at the start of the simulation) that
     * represent the interval.
     *
     * @param startTime the time to start the interval, must be greater than or equal to 0.0
     * @param duration  the duration of the observation interval, must be greater than 0.0
     */
    public void scheduleObservationInterval(double startTime, double duration) {
        if (startTime < 0.0) {
            throw new IllegalArgumentException("Start time must be non-negative");
        }
        if (duration <= 0.0) {
            throw new IllegalArgumentException("Duration must be greater than zero");
        }
        myObservationIntervalStartTime = startTime;
        myObservationIntervalDuration = duration;
        myHasObservationIntervalFlag = true;
    }

    /**
     * @return true if scheduleObservationInterval() has been previously called
     */
    public boolean hasObservationInterval() {
        return myHasObservationIntervalFlag;
    }

    /**
     *  Causes the cancellation of the observation interval events
     */
    public void cancelObservationInterval() {
        if (myObservationIntervalStartEvent != null) {
            myObservationIntervalStartEvent.setCanceledFlag(true);
        }
        if (myObservationIntervalEndEvent != null) {
            myObservationIntervalEndEvent.setCanceledFlag(true);
        }
    }

    private class StartObservationIntervalAction implements EventActionIfc {

        /**
         * This must be implemented by any objects that want to supply event
         * logic.  This is essentially the "event routine".
         *
         * @param evt The event that triggered this action.
         */
        @Override
        public void action(JSLEvent evt) {
            // clear any previous statistics prior to start of the interval
            warmUp();
            myIntervalStartedFlag = true;
        }
    }

    private class EndObservationIntervalAction implements EventActionIfc {

        /**
         * This must be implemented by any objects that want to supply event
         * logic.  This is essentially the "event routine".
         *
         * @param evt The event that triggered this action.
         */
        @Override
        public void action(JSLEvent evt) {
            myIntervalEndedFlag = true;
        }
    }

    /**
     * @return true if detailed state change statistics are collected
     */
    public final boolean getStatisticsOption() {
        return myStatsOption;
    }

    private class TheObserver extends ModelElementObserver {
        @Override
        protected void update(ModelElement m, Object arg) {
            if (hasObservationInterval()) {
                // has observation interval, only capture during the interval
                if ((myIntervalStartedFlag == true) && (myIntervalEndedFlag == false)){
                    // interval has started but not yet ended
                    stateUpdate();
                }
            } else {
                // no interval, always capture
                stateUpdate();
            }
        }
    }

    @Override
    protected void initialize() {
        myIntervalEndedFlag = false;
        myIntervalStartedFlag = false;
        myInitTime = getTime();
        myAbove.initialize();
        myBelow.initialize();
        myStateFreq.reset();
        if (myVariable.getInitialValue() >= myLevel) {
            myCurrentState = myAbove;
        } else {
            myCurrentState = myBelow;
        }
        myCurrentState.enter(getTime());
        if (hasObservationInterval()) {
            myObservationIntervalStartEvent = scheduleEvent(new StartObservationIntervalAction(), myObservationIntervalStartTime);
            myObservationIntervalEndEvent = scheduleEvent(new EndObservationIntervalAction(), myObservationIntervalStartTime + myObservationIntervalDuration);
        }
    }

    /**
     * This method should be overridden by subclasses that need actions
     * performed at the warm up event during each replication. It is called once
     * during each replication if the model element reacts to warm up actions.
     */
    @Override
    protected void warmUp() {
        myInitTime = getTime();
        myAbove.initialize();
        myBelow.initialize();
        myStateFreq.reset();
        if (myVariable.getPreviousValue() >= myLevel) {
            myCurrentState = myAbove;
        } else {
            myCurrentState = myBelow;
        }
        myCurrentState.enter(getTime());
    }

    /**
     * This method should be overridden by subclasses that need actions
     * performed when the replication ends and prior to the calling of
     * afterReplication() . It is called when each replication ends and can be
     * used to collect data from the the model element, etc.
     */
    @Override
    protected void replicationEnded() {
        myMaxDistanceAbove.setValue(myDistanceAbove.getWithinReplicationStatistic().getMax());
        myMaxDistanceBelow.setValue(myDistanceBelow.getWithinReplicationStatistic().getMax());
        if (myAbove.getSojournTimeStatistic().isPresent()) {
            double totalTimeInState = myAbove.getTotalTimeInState();
            myPctTimeAbove.setValue(totalTimeInState / (getTime() - myInitTime));
            myTotalTimeAbove.setValue(totalTimeInState);
        }
        if (myBelow.getSojournTimeStatistic().isPresent()) {
            double totalTimeInState = myBelow.getTotalTimeInState();
            myPctTimeBelow.setValue(totalTimeInState / (getTime() - myInitTime));
            myTotalTimeBelow.setValue(totalTimeInState);
        }
        // collect state statistics
        if (getStatisticsOption()) {
            if (myAbove.getSojournTimeStatistic().isPresent()) {
                myAvgTimeAbove.setValue(myAbove.getSojournTimeStatistic().get().getAverage());
                myMaxTimeAbove.setValue(myAbove.getSojournTimeStatistic().get().getMax());
            }
            if (myBelow.getSojournTimeStatistic().isPresent()) {
                myAvgTimeBelow.setValue(myBelow.getSojournTimeStatistic().get().getAverage());
                myMaxTimeBelow.setValue(myBelow.getSojournTimeStatistic().get().getMax());
            }
            double[][] p = myStateFreq.getTransitionProportions();
            if (p != null) {
                myPAA.setValue(p[0][0]);
                myPAB.setValue(p[0][1]);
                myPBB.setValue(p[1][1]);
                myPBA.setValue(p[1][0]);
            }
            int[][] n = myStateFreq.getTransitionCounts();
            if (n != null) {
                myNAA.setValue(n[0][0]);
                myNAB.setValue(n[0][1]);
                myNBB.setValue(n[1][1]);
                myNBA.setValue(n[1][0]);
            }
        }
    }

    protected void stateUpdate() {
        State nextState;
        if (myVariable.getPreviousValue() >= myLevel) {
            myDistanceAbove.setValue(myVariable.getPreviousValue() - myLevel);
            nextState = myAbove;
        } else {
            nextState = myBelow;
            myDistanceBelow.setValue(myLevel - myVariable.getPreviousValue());
        }
        myStateFreq.collect(nextState);
        if (myCurrentState != nextState) {
            myCurrentState.exit(getTime());
            nextState.enter(getTime());
            myCurrentState = nextState;
        }
    }

}
