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
 * A FailureProcess causes FailureNotices to be sent to a ResourceUnit. By default the
 * failure process does not start automatically at time zero. The user can turn
 * on automatic starting of the failure process at time zero or use the
 * start() method. Once the failure process has been started it
 * cannot be started again. Once the failure process has been stopped, it cannot
 * be started again. A FailureProcess is associated with one ResourceUnit.
 *
 * Will throw an IllegalArgumentException if the failure delay option of the
 * FailureProcess is inconsistent with that permitted by the ResourceUnit
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

    protected final ResourceUnit myResourceUnit;

    /**
     * The delay option will be true
     *
     * @param resourceUnit   the resourceUnit ModelElement
     * @param duration governs the duration of the FailureNotices
     */
    public FailureProcess(ResourceUnit resourceUnit, RandomIfc duration) {
        this(resourceUnit, duration, true, null);
    }

    /**
     * @param resourceUnit      the resourceUnit
     * @param duration    governs the duration of the FailureNotices
     * @param delayOption whether or not failure notices can be delayed if the
     *                    resource is busy
     */
    public FailureProcess(ResourceUnit resourceUnit, RandomIfc duration,
                          boolean delayOption) {
        this(resourceUnit, duration, delayOption, null);
    }

    /**
     * @param resourceUnit      the resourceUnit
     * @param duration    governs the duration of the FailureNotices
     * @param delayOption whether or not failure notices can be delayed if the
     *                    resource is busy
     * @param name        the name of the FailureProcess
     */
    public FailureProcess(ResourceUnit resourceUnit, RandomIfc duration,
                          boolean delayOption, String name) {
        super(resourceUnit, name);
        if (delayOption != resourceUnit.getFailureDelayOption()) {
            throw new IllegalArgumentException("Attempted to add a FailureProcess "
                    + "that is inconsistent with ResourceUnit failure delay option");
        }
        Objects.requireNonNull(duration, "The failure duration must not be null");
        myFailureDurationRV = new RandomVariable(this, duration, getName() + ":Duration");
        myDelayOption = delayOption;
        myPriority = 1;
        turnOffAutoStartProcess();
        myProcessState = myCreatedState;
        myResourceUnit = resourceUnit;
        myResourceUnit.addFailureProcess(this);
    }

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
        if (getAutoStartProcessOption()) {
            start();
        }
    }

    /**
     * Should be used in implementations of sendFailureNotice()
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
     * Stops the failure process.  Once the failure process is stopped, the
     * the process cannot be restarted. The process can be stopped from
     * the running or suspended states.
     */
    public final void stop() {
        myProcessState.stop();
    }

    /**
     * Causes the failure process to start. If the failure process has already
     * been started then an IllegalStateException is thrown. The process must be
     * started to be able to send failure notices. The failure process can
     * only be started once per replication. The process can only be started
     * from the created state.
     */
    public final void start() {
        myProcessState.start();
    }

    /**
     * Tells the process to suspend the generation of FailureNotices.
     * Once suspended, resume() can be used to continue the generation
     * of FailureNotices. Suspending the generation of FailureNotices should
     * cause no new FailureNotices to be sent. The process can only
     * be suspended from the running state.
     */
    public final void suspend() {
        myProcessState.suspend();
    }

    /**
     * Tells the process to resume the generation of FailureNotices.
     * Once resumed, suspend() can be used to pause the generation of new
     * FailureNotices. Resuming the generation of FailureNotices should
     * allow the process to continue sending notices if resumption of the
     * process is permitted. The process can only be resumed from the suspended state.
     */
    public final void resume() {
        myProcessState.resume();
    }

    /**
     *
     * @return the resource unit attached to this failure process
     */
    protected final ResourceUnit getResourceUnit(){
        return myResourceUnit;
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
     */
    protected final void fail() {
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

        @Override
        protected void start() {
            myProcessState = myRunningState;
            startProcess();
        }

    }

    private final class RunningState extends FailureProcessState {

        private RunningState() {
            super("Running");
        }

        @Override
        protected void fail() {
            signalFailure();
        }

        @Override
        protected void suspend() {
            myProcessState = mySuspendedState;
            suspendProcess();
        }

        @Override
        protected void stop() {
            myProcessState = myStoppedState;
            stopProcess();
        }

    }

    private final class SuspendedState extends FailureProcessState {

        private SuspendedState() {
            super("Suspended");
        }

        @Override
        protected void resume() {
            myProcessState = myRunningState;
            resumeProcess();
        }

        @Override
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
