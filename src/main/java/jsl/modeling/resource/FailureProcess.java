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

import jsl.modeling.SchedulingElement;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.utilities.random.RandomIfc;
import jsl.utilities.reporting.JSL;

/**  A FailureProcess causes FailureNotices to be sent to ResourceUnits. By default the
 * failure process does not start automatically at time zero. The user can turn
 * on automatic starting of the failure process at time zero or use the
 * startFailureProcess() method. Once the failure process has been started it
 * cannot be started again. Once the failure process has been stopped, it cannot
 * be started again.
 *
 * @author rossetti
 */
abstract public class FailureProcess extends SchedulingElement {

    protected RandomIfc myFailureDuration;

    protected RandomVariable myFailureDurationRV;

    protected final ResourceUnit myResourceUnit;

    private boolean myState;

    protected boolean myAutoStartFailuresFlag;

    protected boolean myDelayOption;

    protected int myPriority;

    /**
     * The delay option will be true
     *
     * @param parent the associated ResourceUnit
     * @param duration governs the duration of the FailureNotices
     */
    public FailureProcess(ResourceUnit parent, RandomIfc duration) {
        this(parent, duration, true, null);
    }

    /**
     *
     * @param parent the associated ResourceUnit
     * @param duration governs the duration of the FailureNotices
     * @param delayOption whether or not failure notices can be delayed if the
     * resource is busy
     */
    public FailureProcess(ResourceUnit parent, RandomIfc duration,
                          boolean delayOption) {
        this(parent, duration, delayOption, null);
    }

    /**
     *
     * @param parent the associated ResourceUnit
     * @param duration governs the duration of the FailureNotices
     * @param delayOption whether or not failure notices can be delayed if the
     * resource is busy
     * @param name the name of the ResourceUnit
     */
    public FailureProcess(ResourceUnit parent, RandomIfc duration,
                          boolean delayOption, String name) {
        super(parent, name);
        myResourceUnit = parent;
        myDelayOption = delayOption;
        myPriority = 1;
        turnOffAutoFailures();
        setStateUp();
        setFailureNoticeDurationTimeInitialRandomSource(duration);
    }

    /**
     *
     * @return the priority of the generated FailureNotices
     */
    public final int getPriority() {
        return myPriority;
    }

    /**
     *
     * @param priority the priority of the generated FailureNotices
     */
    public final void setPriority(int priority) {
        myPriority = priority;
    }

    /**
     * The default option is true
     *
     * @return returns true if the FailureNotices made by this FailureElement
     * can be delayed if the ResourceUnit it is sent to is busy
     */
    public final boolean getFailureDelayOption() {
        return myDelayOption;
    }

    @Override
    protected void initialize() {
        super.initialize();
        setStateUp();
    }

    /**
     *
     * @return true if the process has an active failure
     */
    public final boolean isDown() {
        return myState;
    }

    /**
     *
     * @return true if the process does not have an active failure
     */
    public final boolean isUp() {
        return !myState;
    }

    /**
     * Makes the element down
     */
    protected final void setStateDown() {
        myState = true;
    }

    /**
     * Makes the element up
     */
    protected final void setStateUp() {
        myState = false;
    }

    /**
     * Sets the down time distribution
     *
     * @param d the distribution
     */
    public final void setFailureNoticeDurationTimeInitialRandomSource(RandomIfc d) {
        if (d == null) {
            throw new IllegalArgumentException("Down time was null!");
        }

        myFailureDuration = d;

        if (myFailureDurationRV == null) { // not made yet
            myFailureDurationRV = new RandomVariable(this, myFailureDuration);
        } else { // already had been made, and added to model
            // just change the distribution
            myFailureDurationRV.setInitialRandomSource(myFailureDuration);
        }

    }

    /**
     * The default is false
     *
     * @return true if failure process will start automatically upon
     * initialization
     */
    public final boolean getAutoFailuresFlag() {
        return myAutoStartFailuresFlag;
    }

    /**
     * The failure process will not start automatically upon initialization
     */
    public final void turnOffAutoFailures() {
        myAutoStartFailuresFlag = false;
    }

    /**
     * The failure process is started automatically upon initialization.
     */
    public final void turnOnAutoFailures() {
        myAutoStartFailuresFlag = true;
    }

    /**
     * Causes the process to start being down, i.e. failed. When the process
     * becomes down,
     * the associated ResourceUnit is notified that a failure has occurred. by
     * sending a FailureNotice to the ResourceUnit for processing based on the
     * down time distribution.
     * <p>
     * Sends a FailureNotice to the ResourceUnit for processing
     */
    protected void sendFailureNoticeToResourceUnit() {
        myResourceUnit.receiveFailureNotice(createFailureNotice());
    }

    /**
     *
     * @return creates a FailureNotice
     */
    protected final FailureNotice createFailureNotice() {
        double t = myFailureDurationRV.getValue();
        FailureNotice fn = new FailureNotice(this, t, myDelayOption);
        fn.setPriority(getPriority());
        return fn;
    }

    /**
     * Stops the failure process. If the element is down, then no future
     * failures should occur. If the element is up, then any failure mechanism
     * should be canceled or stopped. Once the failure process is stopped, the
     * element remains in the up state forever.
     */
    abstract protected void stopFailureProcess();

    /**
     * Causes the failure process to start. If the failure process has already
     * been started then an IllegalStateException is thrown. The process must be
     * in the up state to be able to schedule a failure. The failure process can
     * only be started once per replication.
     */
    abstract public void startFailureProcess();

    /**
     *
     * @return true if the failure process has been started
     */
    abstract public boolean isStarted();

    /**
     * Tells the FailureElement to suspend the generation of FailureNotices.
     * Once suspended, resume() can be used to continue the generation
     * of FailureNotices. Suspending the generation of FailureNotices should
     * cause no new FailureNotices to be sent to the ResourceUnit
     */
    abstract protected void suspend();

    /**
     * Tells the FailureElement to resume the generation of FailureNotices.
     * Once resumed, suspend() can be used to pause the generation of new
     * FailureNotices. Resuming the generation of FailureNotices should
     * allow the FailureElement to continue sending notices to the ResourceUnit.
     */
    abstract protected void resume();

    /**
     * When a FailureNotice created by this FailureElement is made active,
     * this method is called. Called from FailureNotice.CreatedState or
     * FailureNotice.DelayedState when activate() is called.  A FailureNotice
     * is activated from ResourceUnit when scheduling the end of failure for the generated
     * FailureNotice using ResourceUnit's scheduleEndOfFailure(FailureNotice failureNotice).
     * If the FailureNotice is not delayed then it is activated immediately upon failure.
     * If the FailureNotice is delayed, then after the delay it is activated
     *
     * This can be used to react to the notice
     * becoming active. The default behavior is to suspend() the creation
     * of future notices. In other words, while a ResourceUnit is handling
     * a failure notice from this FailureElement additional notices will
     * not be sent.
     *
     * @param fn the failure notice
     */
    protected void failureNoticeActivated(FailureNotice fn) {
        setStateDown();
        suspend();
    }

    /**
     * When a FailureNotice created by this FailureElement is delayed,
     * this method is called. This can be used to react to the notice
     * becoming delayed. The default behavior is to suspend() the production
     * of future notices. In other words, while a ResourceUnit is handling
     * a failure notice from this FailureElement additional notices will
     * not be sent.
     *
     * @param fn the failure notice
     */
    protected void failureNoticeDelayed(FailureNotice fn) {
        setStateDown();
        suspend();
    }

    /**
     * When a FailureNotice created by this FailureElement is ignored,
     * this method is called. This can be used to react to the notice
     * becoming ignored. The default behavior is to do nothing and log
     * a warning message
     *
     * @param fn the failure notice
     */
    protected void failureNoticeIgnored(FailureNotice fn) {
        StringBuilder sb = new StringBuilder();
        sb.append(getTime());
        sb.append(" > ");
        sb.append(getName());
        sb.append(" ignored FailureNotice ");
        sb.append(fn.toString());
        sb.append(System.lineSeparator());
        JSL.LOGGER.warning(sb.toString());
    }

    /**
     * When a FailureNotice created by this FailureElement is completed,
     * this method is called. This can be used to react to the notice
     * becoming completed. The default behavior is to resume the generation
     * of failure notices
     *
     * @param fn the failure notice
     */
    protected void failureNoticeCompleted(FailureNotice fn) {
//        JSL.out.println(getTime() + " > In FailureElement.failureNoticeCompleted() with " + fn);
        setStateUp();
        resume();
    }

    /**
     * This method is called by ResourceUnit when a state change has
     * occurred. It calls the resourceUnitXtoY() methods to allow
     * sub-classes to specialize the behavior associated with a
     * state change on a ResourceUnit.
     */
    protected void resourceUnitStateChange(ResourceUnit resourceUnit) {
        if (resourceUnit.isPreviousStateIdle()) {
            if (resourceUnit.isBusy()) {
                //idle to busy
                resourceUnitIdleToBusy(resourceUnit);
            } else if (resourceUnit.isFailed()) {
                // idle to failed
                resourceUnitIdleToFailed(resourceUnit);
            } else if (resourceUnit.isInactive()) {
                // idle to inactive
                resourceUnitIdleToInactive(resourceUnit);
            } else if (resourceUnit.isIdle()) {
                // idle to idle, not possible
                resourceUnitIdleToIdle(resourceUnit);
            }
        } else if (resourceUnit.isPreviousStateInactive()) {
            if (resourceUnit.isBusy()) {
                //inactive to busy
                resourceUnitInactiveToBusy(resourceUnit);
            } else if (resourceUnit.isFailed()) {
                // inactive to failed
                resourceUnitInactiveToFailed(resourceUnit);
            } else if (resourceUnit.isInactive()) {
                // inactive to inactive
                resourceUnitInactiveToInactive(resourceUnit);
            } else if (resourceUnit.isIdle()) {
                // inactive to idle
                resourceUnitInactiveToIdle(resourceUnit);
            }
        } else if (resourceUnit.isPreviousStateBusy()) {
            if (resourceUnit.isBusy()) {
                //busy to busy
                resourceUnitBusyToBusy(resourceUnit);
            } else if (resourceUnit.isFailed()) {
                // busy to failed
                resourceUnitBusyToFailed(resourceUnit);
            } else if (resourceUnit.isInactive()) {
                // busy to inactive
                resourceUnitBusyToInactive(resourceUnit);
            } else if (resourceUnit.isIdle()) {
                // busy to idle
                resourceUnitBusyToIdle(resourceUnit);
            }
        } else if (resourceUnit.isPreviousStateFailed()) {
            if (resourceUnit.isBusy()) {
                //failed to busy
                resourceUnitFailedToBusy(resourceUnit);
            } else if (resourceUnit.isFailed()) {
                // failed to failed
                resourceUnitFailedToFailed(resourceUnit);
            } else if (resourceUnit.isInactive()) {
                // failed to inactive
                resourceUnitFailedToInactive(resourceUnit);
            } else if (resourceUnit.isIdle()) {
                // failed to idle
                resourceUnitFailedToIdle(resourceUnit);
            }
        }
    }

    protected void resourceUnitInactiveToIdle(ResourceUnit resourceUnit) {
//     JSL.out.println(getTime() + " > transition from Inactive to Idle");
    }

    protected void resourceUnitInactiveToInactive(ResourceUnit resourceUnit) {
//     JSL.out.println(getTime() + " > transition from Inactive to Inactive");
    }

    protected void resourceUnitInactiveToFailed(ResourceUnit resourceUnit) {
//    JSL.out.println(getTime() + " > transition from Inactive to Failed");
    }

    protected void resourceUnitInactiveToBusy(ResourceUnit resourceUnit) {
//        JSL.out.println(getTime() + " > transition from Inactive to Busy");
    }

    protected void resourceUnitBusyToBusy(ResourceUnit resourceUnit) {
//        System.out.println(getTime() + " > transition from Busy to Busy");
    }

    protected void resourceUnitBusyToFailed(ResourceUnit resourceUnit) {
//      JSL.out.println(getTime() + " > transition from Busy to Failed");
    }

    protected void resourceUnitBusyToInactive(ResourceUnit resourceUnit) {
//        JSL.out.println(getTime() + " > transition from Busy to Inactive");
    }

    protected void resourceUnitBusyToIdle(ResourceUnit resourceUnit) {
//        JSL.out.println(getTime() + " > transition from Busy to Idle");
    }

    protected void resourceUnitFailedToBusy(ResourceUnit resourceUnit) {
//        JSL.out.println(getTime() + " > transition from Failed to Busy");
    }

    protected void resourceUnitFailedToFailed(ResourceUnit resourceUnit) {
//    JSL.out.println(getTime() + " > transition from Failed to Failed");
    }

    protected void resourceUnitFailedToInactive(ResourceUnit resourceUnit) {
//      JSL.out.println(getTime() + " > transition from Failed to Inactive");
    }

    protected void resourceUnitFailedToIdle(ResourceUnit resourceUnit) {
//        JSL.out.println(getTime() + " > transition from Failed to Idle");
    }

    protected void resourceUnitIdleToBusy(ResourceUnit resourceUnit) {
//        JSL.out.println(getTime() + " > transition from Idle to Busy");
    }

    protected void resourceUnitIdleToFailed(ResourceUnit resourceUnit) {
//       JSL.out.println(getTime() + " > transition from Idle to Failed");
    }

    protected void resourceUnitIdleToInactive(ResourceUnit resourceUnit) {
//        JSL.out.println(getTime() + " > transition from Idle to Inactive");
    }

    protected void resourceUnitIdleToIdle(ResourceUnit resourceUnit) {
//       JSL.out.println(getTime() + " > transition from Idle to Idle");
    }
}
