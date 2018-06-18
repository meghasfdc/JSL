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

import jsl.modeling.ModelElement;
import jsl.modeling.SchedulingElement;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.utilities.random.RandomIfc;

import java.util.Objects;

/**
 * A FailureProcess causes FailureNotices to be sent to ResourceUnits. By default the
 * failure process does not start automatically at time zero. The user can turn
 * on automatic starting of the failure process at time zero or use the
 * start() method. Once the failure process has been started it
 * cannot be started again. Once the failure process has been stopped, it cannot
 * be started again. A FailureProcess can be associated with 0 or more ResourceUnits.
 * If no ResourceUnits are added to the FailureProcess then the process will operate
 * but no resource units will receive FailureNotices.
 *
 * In the case of a FailureProcess sending failure notices to more than one ResourceUnit,
 * it is up to implementors to appropriately handle the notifications that
 * are received when the various failure notices transition through their states.
 *
 * @author rossetti
 */
abstract public class FailureProcess extends SchedulingElement {

    private final CreatedState myCreatedState = new CreatedState();
    private final RunningState myRunningState = new RunningState();
    private final SuspendedState mySuspendedState = new SuspendedState();
    private final StoppedState myStoppedState = new StoppedState();

    private final RandomVariable myFailureDurationRV;

    private boolean myAutoStartProcessOption;

    private boolean myDelayOption;

    private int myPriority;

    private FailureProcessState myProcessState;

//    protected final Set<ResourceUnit> myResourceUnits;

    /**
     * The delay option will be true
     *
     * @param parent   the parent ModelElement
     * @param duration governs the duration of the FailureNotices
     */
    public FailureProcess(ModelElement parent, RandomIfc duration) {
        this(parent, duration, true, null);
    }

    /**
     * @param parent      the parent ModelElement
     * @param duration    governs the duration of the FailureNotices
     * @param delayOption whether or not failure notices can be delayed if the
     *                    resource is busy
     */
    public FailureProcess(ModelElement parent, RandomIfc duration,
                          boolean delayOption) {
        this(parent, duration, delayOption, null);
    }

    /**
     * @param parent      the parent ModelElement
     * @param duration    governs the duration of the FailureNotices
     * @param delayOption whether or not failure notices can be delayed if the
     *                    resource is busy
     * @param name        the name of the FailureProcess
     */
    public FailureProcess(ModelElement parent, RandomIfc duration,
                          boolean delayOption, String name) {
        super(parent, name);
        Objects.requireNonNull(duration, "The failure duration must not be null");
        myFailureDurationRV = new RandomVariable(this, duration, getName() + ":Duration");
        myDelayOption = delayOption;
        myPriority = 1;
        turnOffAutoStartProcess();
        myProcessState = myCreatedState;
//        myResourceUnits = new LinkedHashSet<>();
    }

//    /**
//     *
//     * @param units the units to add, must not be null
//     */
//    public final void addResourceUnits(Collection<ResourceUnit> units){
//        Objects.requireNonNull(units, "The collection was null");
//        for(ResourceUnit ru: units){
//            addResourceUnit(ru);
//        }
//    }
//
//    /**
//     *
//     * @param unit the unit to add, must not be null
//     * @return true if added
//     */
//    public boolean addResourceUnit(ResourceUnit unit){
//        Objects.requireNonNull(unit, "Cannot add a null resource unit");
//        return myResourceUnits.add(unit);
//    }

    /**
     * @return the priority of the generated FailureNotices
     */
    public final int getPriority() {
        return myPriority;
    }

    /**
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

    /**
     * Sets the failure duration distribution
     *
     * @param d the distribution
     */
    public final void setFailureNoticeDurationTimeInitialRandomSource(RandomIfc d) {
        if (d == null) {
            throw new IllegalArgumentException("Failure duration was null!");
        }
        myFailureDurationRV.setInitialRandomSource(d);
    }

    /**
     * The default is false
     *
     * @return true if failure process will start automatically upon
     * initialization
     */
    public final boolean getAutoStartProcessOption() {
        return myAutoStartProcessOption;
    }

    /**
     * The failure process will not start automatically upon initialization
     */
    public final void turnOffAutoStartProcess() {
        myAutoStartProcessOption = false;
    }

    /**
     * The failure process is started automatically upon initialization.
     */
    public final void turnOnAutoStartProcess() {
        myAutoStartProcessOption = true;
    }

    @Override
    protected void initialize() {
        super.initialize();
        myProcessState = myCreatedState;
        if (getAutoStartProcessOption()){
            start();
        }
    }

    /** Should be used in implementations of sendFailureNotice()
     * @return creates a FailureNotice
     */
    protected final FailureNotice createFailureNotice() {
        double t = myFailureDurationRV.getValue();
        FailureNotice fn = new FailureNotice(this, t, myDelayOption);
        fn.setPriority(getPriority());
        return fn;
    }

    /**
     * Stops the failure process.  Once the failure process is stopped, the
     * the process cannot be restarted. The process can be stopped from
     *  the running or suspended states.
     */
    public final void stop(){
        myProcessState.stop();
    }

    /**
     * Causes the failure process to start. If the failure process has already
     * been started then an IllegalStateException is thrown. The process must be
     * started to be able to send failure notices. The failure process can
     * only be started once per replication. The process can only be started
     * from the created state.
     */
     public final void start(){
         myProcessState.start();
     }

    /**
     * Tells the process to suspend the generation of FailureNotices.
     * Once suspended, resume() can be used to continue the generation
     * of FailureNotices. Suspending the generation of FailureNotices should
     * cause no new FailureNotices to be sent. The process can only
     * be suspended from the running state.
     */
    public final void suspend(){
        myProcessState.suspend();
    }

    /**
     * Tells the process to resume the generation of FailureNotices.
     * Once resumed, suspend() can be used to pause the generation of new
     * FailureNotices. Resuming the generation of FailureNotices should
     * allow the process to continue sending notices if resumption of the
     * process is permitted. The process can only be resumed from the suspended state.
     */
    public final void resume(){
        myProcessState.resume();
    }

    /**
     * When a FailureNotice created by this FailureElement is made active,
     * this method is called. Called from FailureNotice.CreatedState or
     * FailureNotice.DelayedState when activate() is called.  A FailureNotice
     * is activated from ResourceUnit when scheduling the end of failure for the generated
     * FailureNotice using ResourceUnit's scheduleEndOfFailure(FailureNotice failureNotice).
     * If the FailureNotice is not delayed then it is activated immediately upon
     * the ResourceUnit receiving the failure notice.
     * If the FailureNotice is delayed, then after the delay it is activated
     * This can be used to react to the notices
     * becoming active.
     *
     * @param fn the failure notice
     */
    abstract protected void failureNoticeActivated(FailureNotice fn);

    /**
     * When a FailureNotice created by this FailureElement is delayed,
     * this method is called. This can be used to react to the notice
     * becoming delayed.
     *
     * @param fn the failure notice
     */
    abstract protected void failureNoticeDelayed(FailureNotice fn);

    /**
     * When a FailureNotice created by this FailureElement is ignored,
     * this method is called. This can be used to react to the notice
     * becoming ignored. The default behavior is to do nothing and log
     * a warning message
     *
     * @param fn the failure notice
     */
    abstract protected void failureNoticeIgnored(FailureNotice fn);

    /**
     * Use this method to cause FailureNotices to be sent. This
     * method properly checks the state of the process before sending.
     *
     */
     protected final void fail(){
         myProcessState.fail();
     }

    /**
     * When a FailureNotice created by this FailureElement is completed,
     * this method is called. This can be used to react to the notice
     * becoming completed.
     *
     * @param fn the failure notice
     */
    abstract protected void failureNoticeCompleted(FailureNotice fn);

    /**
     * Performs the work to start the failure process
     */
    abstract protected void startProcess();

    /**
     * Performs work associated with suspending the process
     */
    abstract protected void suspendProcess();

    /**
     * Performs work associated with stopping the process
     */
    abstract protected void stopProcess();

    /**
     * Performs work to resume the process.
     */
    abstract protected void resumeProcess();

    /**
     * Implement this method signal ResourceUnits via FailureNotices
     * This method is called by fail() which properly
     * checks the state of the process before signalling
     */
    abstract protected void signalFailure();

    /**
     * @return true if the failure process is in the running state
     */
    public final boolean isRunning() {
        return myProcessState == myRunningState;
    }

    /**
     * @return true if the failure process is in the created state
     */
    public final boolean isCreated() {
        return myProcessState == myCreatedState;
    }

    /**
     * @return true if the failure process is in the suspended state
     */
    public final boolean isSuspended() {
        return myProcessState == mySuspendedState;
    }

    /**
     * @return true if the failure process is in the running state
     */
    public final boolean isStopped() {
        return myProcessState == myStoppedState;
    }

    private class FailureProcessState {
        protected final String myName;

        private FailureProcessState(String name) {
            myName = name;
        }

        protected void fail() {
            throw new IllegalStateException("Tried to fail from an illegal state: " + myName);
        }

        protected void start() {
            throw new IllegalStateException("Tried to start from an illegal state: " + myName);
        }

        protected void suspend() {
            throw new IllegalStateException("Tried to suspend from an illegal state: " + myName);
        }

        protected void resume() {
            throw new IllegalStateException("Tried to resume from an illegal state: " + myName);
        }

        protected void stop() {
            throw new IllegalStateException("Tried to stop from an illegal state: " + myName);
        }

    }

    private final class CreatedState extends FailureProcessState {

        private CreatedState() {
            super("Created");
        }

        protected void start() {
            myProcessState = myRunningState;
            startProcess();
        }

    }

    private final class RunningState extends FailureProcessState {

        private RunningState() {
            super("Running");
        }

        protected void fail() {
            signalFailure();
        }

        protected void suspend() {
            myProcessState = mySuspendedState;
            suspendProcess();
        }

        protected void stop() {
            myProcessState = myStoppedState;
            stopProcess();
        }

    }

    private final class SuspendedState extends FailureProcessState {

        private SuspendedState() {
            super("Suspended");
        }

        protected void resume() {
            myProcessState = myRunningState;
            resumeProcess();
        }

        protected void stop() {
            myProcessState = myStoppedState;
            stopProcess();
        }
    }

    private final class StoppedState extends FailureProcessState {

        private StoppedState() {
            super("Stopped");
        }
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
