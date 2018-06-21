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

package jsl.modeling.resource;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import jsl.modeling.JSLEvent;
import jsl.modeling.ModelElement;
import jsl.modeling.SchedulingElement;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.utilities.GetValueIfc;
import jsl.utilities.random.RandomIfc;
import jsl.utilities.random.distributions.Constant;
import jsl.utilities.reporting.JSL;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Models a single failure event that can effect many resource units.  If no
 * resource units are attached then a warning message occurs.  If you really do not
 * want to attach resource units and do not want the warning then use
 * setNoResourceUnitWarningOption(false)
 */
public class MultipleResourceUnitSingleFailureEvent extends SchedulingElement {

    private RandomVariable myInitialStartTimeRV;
    private final RandomVariable myEventDurationRV;
    private boolean myAutoStartProcessOption;
    private final BiMap<ResourceUnit, SingleFailureEvent> myFailures;
    private boolean myStartedFlag;
    private boolean myStoppedFlag;
    private boolean mySuspendedFlag;
    private Constant myTimeToEvent;
    private Constant myEventDuration;
    private final Set<FailureEventListenerIfc> myFailureEventListeners;
    private JSLEvent myStartEvent;
    private JSLEvent myEndEvent;
    private boolean myTurnOffNoResourceUnitWarning;

    /**
     * @param parent             a parent model element
     * @param eventDuration      the duration of the event
     * @param initialStartTimeRV the initial starting time for the event
     */
    public MultipleResourceUnitSingleFailureEvent(ModelElement parent, RandomIfc eventDuration,
                                                  RandomIfc initialStartTimeRV) {
        this(parent, eventDuration, initialStartTimeRV, null);
    }

    /**
     * @param parent             a parent model element
     * @param eventDuration      the duration of the event
     * @param initialStartTimeRV the initial starting time for the event
     * @param name               the name of model element
     */
    public MultipleResourceUnitSingleFailureEvent(ModelElement parent, RandomIfc eventDuration,
                                                  RandomIfc initialStartTimeRV, String name) {
        super(parent, name);
        myEventDurationRV = new RandomVariable(this, eventDuration, getName() + ":EventDuration");
        myEventDuration = new Constant(myEventDurationRV.getValue());
        if (initialStartTimeRV != null) {
            myInitialStartTimeRV = new RandomVariable(this, initialStartTimeRV, getName() + ":InitialStartTime");
            myTimeToEvent = new Constant(myInitialStartTimeRV.getValue());
        }
        myFailureEventListeners = new LinkedHashSet<>();
        myFailures = HashBiMap.create();
        myStartedFlag = false;
        myStoppedFlag = false;
        mySuspendedFlag = false;
        myTurnOffNoResourceUnitWarning = true;
    }

    /**
     * The default is false
     *
     * @return true if failure process will start automatically upon
     * initialization
     */
    public final boolean getAutoStartProcessOption() {
        return myInitialStartTimeRV != null;
    }

    /**
     * Adds a listener to react to failureStarted event
     *
     * @param listener the listener to add
     */
    public final void addFailureEventListener(FailureEventListenerIfc listener) {
        if (listener == null) {
            return;
        }
        myFailureEventListeners.add(listener);
    }

    /**
     * Removes the listener from the event
     *
     * @param listener the listener to remove
     */
    public final void removeFailureEventListener(FailureEventListenerIfc listener) {
        if (listener == null) {
            return;
        }
        myFailureEventListeners.remove(listener);
    }

    /**
     * @return true means that a warning will occur if there are no resource
     * units attached when running the simulation
     */
    public final boolean getNoResourceUnitWarningOption() {
        return myTurnOffNoResourceUnitWarning;
    }

    /**
     * @param option true means that a warning will occur if there are no
     *               resource units attached when running the simulation
     */
    public final void setNoResourceUnitWarningOption(boolean option) {
        this.myTurnOffNoResourceUnitWarning = option;
    }

    @Override
    protected void beforeExperiment() {
        super.beforeExperiment();
        if (getNoResourceUnitWarningOption()) {
            if (myFailures.isEmpty()) {
                JSL.LOGGER.warn("In MultipleResourceUnitSingleFailureEvent: {} There were no resource units attached to the failure event.", getName());
            }
        }
    }

    @Override
    protected void initialize() {
        super.initialize();
        myStartedFlag = false;
        if (getAutoStartProcessOption()) {
            start(myTimeToEvent);
        }
    }

    /**
     * Start the process at the current time. In other words in getTime() + 0.0 into the future.
     */
    public final void start() {
        start(0.0);
    }

    /**
     * Causes the failure process to start at getTime() + value.getValue().
     *
     * @param value the GetValueIfc object that should be used get the value of the starting time
     */
    public final void start(GetValueIfc value) {
        Objects.requireNonNull(value, "The supplied GetValueIfc was null");
        start(value.getValue());
    }

    /**
     * Starts the failure event process.  If the process is already started, nothing happens
     */
    public final void start(double time) {
        if (!isStarted()) {
            myStartedFlag = true;
            scheduleFailure(time);
        }
    }

    protected final void scheduleFailure(double time) {
        myStartEvent = schedule(this::startEvent).havingPriority(JSLEvent.DEFAULT_PRIORITY - 7).in(time).units();
        double endTime = time + myEventDuration.getValue();
        myEndEvent = schedule(this::endEvent).havingPriority(JSLEvent.DEFAULT_PRIORITY + 1).in(endTime).units();
    }

    protected final void cancelFailure() {
        if (myStartEvent != null) {
            myStartEvent.setCanceledFlag(true);
        }
        if (myEndEvent != null) {
            myEndEvent.setCanceledFlag(true);
        }
    }

    /**
     * If the failure process is started, then it is stopped (i.e. it will not occur). If it has not
     * been started nothing happens. If it has already been stopped, nothing happens.
     */
    public final void stop() {
        if (!isStopped() && isStarted()) {
            myStoppedFlag = true;
            cancelFailure();
            Set<SingleFailureEvent> singleFailureEvents = myFailures.values();
            for (SingleFailureEvent fe : singleFailureEvents) {
                fe.stop();
            }
        }
    }

    /**
     * If the failure process is suspended (and started and not stopped), then it is resumed.
     * This reschedules the failure event to the original failure event time.
     */
    public final void resume() {
        if (isSuspended() && isStarted() && !isStopped()) {
            mySuspendedFlag = false;
            scheduleFailure(myTimeToEvent.getValue());
            Set<SingleFailureEvent> singleFailureEvents = myFailures.values();
            for (SingleFailureEvent fe : singleFailureEvents) {
                fe.resume();
            }
        }
    }

    /**
     * If the failure process has been started (but not suspended or stopped) then will be suspended.
     * Otherwise nothing happens
     */
    public final void suspend() {
        if (isStarted() && !isSuspended() && !isStopped()) {
            mySuspendedFlag = true;
            cancelFailure();
            Set<SingleFailureEvent> singleFailureEvents = myFailures.values();
            for (SingleFailureEvent fe : singleFailureEvents) {
                fe.suspend();
            }
        }
    }

    /**
     * @return true if the failure process is started (i.e. scheduled to occur)
     */
    public final boolean isStarted() {
        return myStartedFlag;
    }

    /**
     * Once stopped, it cannot be restarted.
     *
     * @return true if the failure process is stopped.
     */
    public final boolean isStopped() {
        return myStoppedFlag;
    }

    /**
     * @return true if the failure process is suspended.
     */
    public final boolean isSuspended() {
        return mySuspendedFlag;
    }

    /**
     * Adds a resource unit to the failure event
     *
     * @param resourceUnit must not be null. Repeats are silently ignored.
     */
    public final void addResourceUnit(ResourceUnit resourceUnit) {
        Objects.requireNonNull(resourceUnit, "The supplied resource unit was null");
        if (!myFailures.containsKey(resourceUnit)) {
            String name = getName() + ":" + resourceUnit.getName() + ":FailureEvent";
            // no initial start time because controlled from within this class
            SingleFailureEvent fe = new SingleFailureEvent(resourceUnit, myEventDurationRV, name);
            myFailures.put(resourceUnit, fe);
        }
    }

    /**
     * @return true if resource units have been added to the failure event
     */
    public final boolean hasResourceUnits() {
        return !myFailures.isEmpty();
    }

    /**
     * @param units the resource units to add. Must not be null
     */
    public final void addResourceUnits(Collection<ResourceUnit> units) {
        Objects.requireNonNull(units, "The collection was null");
        for (ResourceUnit ru : units) {
            addResourceUnit(ru);
        }
    }

    /**
     * @param pool the ResourcePool to add resource units from, must not be null
     */
    public final void addResourceUnits(ResourcePool pool) {
        Objects.requireNonNull(pool, "The resource pool was null");
        addResourceUnits(pool.getUnits());
    }

    /**
     * Used internally to notify failure event listeners that the failure started
     */
    protected final void notifyFailureEventListenersFailureStarted() {
        for (FailureEventListenerIfc fpl : myFailureEventListeners) {
            fpl.failureStarted();
        }
    }

    /**
     * Used internally to notify failure event listeners that the failure completed
     */
    protected final void notifyFailureEventListenersFailureCompleted() {
        for (FailureEventListenerIfc fpl : myFailureEventListeners) {
            fpl.failureCompleted();
        }
    }

    private void startEvent(JSLEvent event) {
//        System.out.printf("%f > starting the failure. %n", getTime());
        // the start of the failure, tell all to start immediately
        Set<SingleFailureEvent> singleFailureEvents = myFailures.values();
        for (SingleFailureEvent fe : singleFailureEvents) {
            fe.start();
        }
        notifyFailureEventListenersFailureStarted();
    }

    private void endEvent(JSLEvent event) {
        // the end of the failure
//        System.out.printf("%f > ending the failure. %n", getTime());
        notifyFailureEventListenersFailureCompleted();
    }
}
