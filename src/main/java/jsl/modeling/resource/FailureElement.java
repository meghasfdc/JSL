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
package jsl.modeling.resource;

import jsl.modeling.SchedulingElement;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.utilities.random.RandomIfc;
import jsl.utilities.reporting.JSL;

/**
 *
 * @author rossetti
 */
abstract public class FailureElement extends SchedulingElement {

    protected RandomIfc myRepairTime;

    protected RandomVariable myRepairTimeRV;

    protected final ResourceUnit myResourceUnit;

    private boolean myState;

    protected boolean myAutoStartFailuresFlag;

    protected boolean myDelayOption;

    protected int myPriority;

    protected FailureNotice myLastNotice;

    /**
     * The delay option will be true
     *
     * @param parent the associated ResourceUnit
     * @param repairTime governs the time that the element indicates down
     */
    public FailureElement(ResourceUnit parent, RandomIfc repairTime) {
        this(parent, repairTime, true, null);
    }

    /**
     *
     * @param parent the associated ResourceUnit
     * @param repairTime governs the time that the element indicates down
     * @param delayOption whether or not failure notices can be delayed if the
     * resource is busy
     */
    public FailureElement(ResourceUnit parent, RandomIfc repairTime,
            boolean delayOption) {
        this(parent, repairTime, delayOption, null);
    }

    /**
     *
     * @param parent the associated ResourceUnit
     * @param repairTime governs the time that the element indicates down
     * @param delayOption whether or not failure notices can be delayed if the
     * resource is busy
     * @param name the name of the ResourceUnit
     */
    public FailureElement(ResourceUnit parent, RandomIfc repairTime,
            boolean delayOption, String name) {
        super(parent, name);
        myResourceUnit = parent;
        myDelayOption = delayOption;
        myPriority = 1;
        turnOffAutoFailures();
        setStateUp();
        setRepairTimeInitialRandomSource(repairTime);
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
     * can be delayed if the ResourceUnit is busy
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
     * @return true if the element is down
     */
    public final boolean isDown() {
        return myState;
    }

    /**
     *
     * @return true if the element is up
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
    public final void setRepairTimeInitialRandomSource(RandomIfc d) {
        if (d == null) {
            throw new IllegalArgumentException("Down time was null!");
        }

        myRepairTime = d;

        if (myRepairTimeRV == null) { // not made yet
            myRepairTimeRV = new RandomVariable(this, myRepairTime);
        } else { // already had been made, and added to model
            // just change the distribution
            myRepairTimeRV.setInitialRandomSource(myRepairTime);
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
     * Causes the element to start being down, i.e. failed. When the element
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
     * @return the last FailureNotice sent by this element
     */
    protected final FailureNotice getLastFailureNotice() {
        return myLastNotice;
    }

    /**
     *
     * @return creates a FailureNotice
     */
    protected final FailureNotice createFailureNotice() {
        double t = myRepairTimeRV.getValue();
        myLastNotice = new FailureNotice(this, t, myDelayOption);
        myLastNotice.setPriority(getPriority());
        return myLastNotice;
    }

    /**
     * Specifies what to do when the element is repaired
     * <p>
     */
    abstract protected void repairAction(); //TODO is this needed

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
     * this method is called. This can be used to react to the notice
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
        JSL.out.println(getTime() + " > In FailureElement.failureNoticeCompleted() with " + fn);
        setStateUp();
        resume();
    }

    /**
     * This method is called by ResourceUnit when a state change has
     * occurred. It calls the resourceUnitXtoY() methods to allow
     * sub-classes to specialize the behavior associated with a
     * state change on a ResourceUnit.
     */
    protected void resourceUnitStateChange() {
        if (myResourceUnit.isPreviousStateIdle()) {
            if (myResourceUnit.isBusy()) {
                //idle to busy
                resourceUnitIdleToBusy();
            } else if (myResourceUnit.isFailed()) {
                // idle to failed
                resourceUnitIdleToFailed();
            } else if (myResourceUnit.isInactive()) {
                // idle to inactive
                resourceUnitIdleToInactive();
            } else if (myResourceUnit.isIdle()) {
                // idle to idle, not possible
                resourceUnitIdleToIdle();
            }
        } else if (myResourceUnit.isPreviousStateInactive()) {
            if (myResourceUnit.isBusy()) {
                //inactive to busy
                resourceUnitInactiveToBusy();
            } else if (myResourceUnit.isFailed()) {
                // inactive to failed
                resourceUnitInactiveToFailed();
            } else if (myResourceUnit.isInactive()) {
                // inactive to inactive
                resourceUnitInactiveToInactive();
            } else if (myResourceUnit.isIdle()) {
                // inactive to idle
                resourceUnitInactiveToIdle();
            }
        } else if (myResourceUnit.isPreviousStateBusy()) {
            if (myResourceUnit.isBusy()) {
                //busy to busy
                resourceUnitBusyToBusy();
            } else if (myResourceUnit.isFailed()) {
                // busy to failed
                resourceUnitBusyToFailed();
            } else if (myResourceUnit.isInactive()) {
                // busy to inactive
                resourceUnitBusyToInactive();
            } else if (myResourceUnit.isIdle()) {
                // busy to idle
                resourceUnitBusyToIdle();
            }
        } else if (myResourceUnit.isPreviousStateFailed()) {
            if (myResourceUnit.isBusy()) {
                //failed to busy
                resourceUnitFailedToBusy();
            } else if (myResourceUnit.isFailed()) {
                // failed to failed
                resourceUnitFailedToFailed();
            } else if (myResourceUnit.isInactive()) {
                // failed to inactive
                resourceUnitFailedToInactive();
            } else if (myResourceUnit.isIdle()) {
                // failed to idle
                resourceUnitFailedToIdle();
            }
        }
    }

    protected void resourceUnitInactiveToIdle() {
//     JSL.out.println(getTime() + " > transition from Inactive to Idle");
    }

    protected void resourceUnitInactiveToInactive() {
//     JSL.out.println(getTime() + " > transition from Inactive to Inactive");
    }

    protected void resourceUnitInactiveToFailed() {
//    JSL.out.println(getTime() + " > transition from Inactive to Failed");
    }

    protected void resourceUnitInactiveToBusy() {
//        JSL.out.println(getTime() + " > transition from Inactive to Busy");
    }

    protected void resourceUnitBusyToBusy() {
//        System.out.println(getTime() + " > transition from Busy to Busy");
    }

    protected void resourceUnitBusyToFailed() {
//      JSL.out.println(getTime() + " > transition from Busy to Failed");
    }

    protected void resourceUnitBusyToInactive() {
//        JSL.out.println(getTime() + " > transition from Busy to Inactive");
    }

    protected void resourceUnitBusyToIdle() {
//        JSL.out.println(getTime() + " > transition from Busy to Idle");
    }

    protected void resourceUnitFailedToBusy() {
//        JSL.out.println(getTime() + " > transition from Failed to Busy");
    }

    protected void resourceUnitFailedToFailed() {
//    JSL.out.println(getTime() + " > transition from Failed to Failed");
    }

    protected void resourceUnitFailedToInactive() {
//      JSL.out.println(getTime() + " > transition from Failed to Inactive");
    }

    protected void resourceUnitFailedToIdle() {
//        JSL.out.println(getTime() + " > transition from Failed to Idle");
    }

    protected void resourceUnitIdleToBusy() {
//        JSL.out.println(getTime() + " > transition from Idle to Busy");
    }

    protected void resourceUnitIdleToFailed() {
//       JSL.out.println(getTime() + " > transition from Idle to Failed");
    }

    protected void resourceUnitIdleToInactive() {
//        JSL.out.println(getTime() + " > transition from Idle to Inactive");
    }

    protected void resourceUnitIdleToIdle() {
//       JSL.out.println(getTime() + " > transition from Idle to Idle");
    }
}
