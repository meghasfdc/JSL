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

import jsl.modeling.JSLEvent;
import jsl.modeling.ModelElement;
import jsl.modeling.SchedulingElement;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.utilities.random.RandomIfc;
import jsl.utilities.random.distributions.Constant;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 *  A FailureEvent models a one time failure process.  There is a time until the failure
 *  occurs and a duration for the failure.
 */
public class FailureEvent extends FailureProcess {

    private final double myTimeToEvent;
    private JSLEvent myStartEvent;

    public FailureEvent(ResourceUnit resourceUnit, double timeToEvent, double eventDuration) {
        this(resourceUnit, timeToEvent, eventDuration, null);
    }

    public FailureEvent(ResourceUnit resourceUnit, double timeToEvent, double eventDuration, String name) {
        super(resourceUnit, new Constant(eventDuration), name);
        if (eventDuration <= 0.0){
            throw new IllegalArgumentException("The duration of the event must be > 0.0");
        }
        if (timeToEvent < 0.0){
            throw new IllegalArgumentException("The time to the event must be >= 0.0");
        }
        setPriority(JSLEvent.DEFAULT_PRIORITY - 5);
        myTimeToEvent = timeToEvent;
    }

    @Override
    protected void failureNoticeActivated(FailureNotice fn) {
        // nothing to do because it is a one time event
       // System.out.printf("%f > The FailureEvent %d was activated. %n", getTime(), fn.getId());
    }

    @Override
    protected void failureNoticeDelayed(FailureNotice fn) {
        //System.out.printf("%f > The FailureEvent %d was delayed. %n", getTime(), fn.getId());
        // nothing to do because it is a one time event
    }

    @Override
    protected void failureNoticeIgnored(FailureNotice fn) {
        // nothing to do because it is a one time event
    }

    @Override
    protected void failureNoticeCompleted(FailureNotice fn) {
        // nothing to do because it is a one time event
       // System.out.printf("%f > The FailureEvent %d was completed. %n", getTime(), fn.getId());
    }

    @Override
    protected void startProcess() {
        myStartEvent = schedule(this::startEvent).havingPriority(getPriority()).in(myTimeToEvent).units();
    }

    @Override
    protected void suspendProcess() {
        // if the first event is not executed yet, then cancel it.
        myStartEvent.setCanceledFlag(true);
    }

    @Override
    protected void stopProcess() {
        // if the first event is not executed yet, then cancel it.
        myStartEvent.setCanceledFlag(true);
    }

    @Override
    protected void resumeProcess() {
        // can only be resumed if suspended, so schedule a new event
        myStartEvent = schedule(this::startEvent).havingPriority(getPriority()).in(myTimeToEvent).units();
    }

    private void startEvent(JSLEvent event){
        fail();
    }

}
