/*
 *  Copyright (C) 2017 rossetti
 * 
 *  Contact:
 * 	Manuel D. Rossetti, Ph.D., P.E.
 * 	Department of Industrial Engineering
 * 	University of Arkansas
 * 	4207 Bell Engineering Center
 * 	Fayetteville, AR 72701
 * 	Phone: (479) 575-6756
 * 	Email: rossetti@uark.edu
 * 	Web: www.uark.edu/~rossetti
 * 
 *  This file is part of the JSL (a Java Simulation Library). The JSL is a framework
 *  of Java classes that permit the easy development and execution of discrete event
 *  simulation programs.
 * 
 *  The JSL is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 * 
 *  The JSL is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jsl.modeling.resource;

import jsl.modeling.queue.QObject;

/**
 * A FailureNotice represents a notification that the ResourceUnit should go
 * down due to a failure. The failure notice may be required to be immediate or
 * not. If not immediate, then the failure may be delayed until the resource
 * unit finishes its current activity.
 *
 *
 * @author rossetti
 */
public class FailureNotice extends QObject {

    private final CreatedState myCreatedState = new CreatedState();
    private final ActiveState myActiveState = new ActiveState();
    private final DelayedState myDelayedState = new DelayedState();
    private final IgnoredState myIgnoredState = new IgnoredState();
    private final CompletedState myCompletedState = new CompletedState();

    private final FailureElement myFailureElement;
    private final double myDownTime;
    private final boolean myDelayableFlag;
    private FailureNoticeState myState;

    /**
     *
     * @param fe the associated FailureElement
     * @param downTime the time that the failure should last
     * @param delayOption true means it does not need to be immediate
     */
    FailureNotice(FailureElement fe, double downTime, boolean delayOption) {
        super(fe.getTime(), "FailureNotice");
        myFailureElement = fe;
        myDownTime = downTime;
        myDelayableFlag = delayOption;
        myState = myCreatedState;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(", time = ").append(getDownTime());
        sb.append(", delayable = ").append(isDelayable());
        sb.append(", state = ").append(myState.myName);
        return sb.toString();
    }

    /**
     *
     * @return the time that the failure should last
     */
    public final double getDownTime() {
        return myDownTime;
    }

    /**
     *
     * @return the associated FailureElement
     */
    public final FailureElement getFailureElement() {
        return myFailureElement;
    }

    /**
     * If the notice can be delayed while resource finishes busy state
     *
     * @return true if the notice can be delayed
     */
    public final boolean isDelayable() {
        return myDelayableFlag;
    }

    /**
     *
     * @return true if in created state
     */
    public final boolean isInCreatedState() {
        return myState == myCreatedState;
    }

    /**
     *
     * @return true if in delayed state
     */
    public final boolean isInDelayedState() {
        return myState == myDelayedState;
    }

    /**
     *
     * @return true if in ignored state
     */
    public final boolean isInIgnoredState() {
        return myState == myIgnoredState;
    }

    /**
     *
     * @return true if in completed state
     */
    public final boolean isInCompletedState() {
        return myState == myCompletedState;
    }

    /**
     *
     * @return true if in active state
     */
    public final boolean isInActiveState() {
        return myState == myActiveState;
    }

    final void activate() {
        myState.activate();
    }

    final void delay() {
        if (!isDelayable()) {
            throw new IllegalStateException("Tried to delay a FailureNotice that is not delayable.");
        }
        myState.delay();
    }

    final void ignore() {
        myState.ignore();
    }

    final void complete() {
        myState.complete();
    }

    protected class FailureNoticeState {

        protected final String myName;

        protected FailureNoticeState(String name) {
            myName = name;
        }

        protected void activate() {
            throw new IllegalStateException("Tried to activate from an illegal state: " + myName);
        }

        protected void delay() {
            throw new IllegalStateException("Tried to delay from an illegal state: " + myName);
        }

        protected void ignore() {
            throw new IllegalStateException("Tried to ignore from an illegal state: " + myName);
        }

        protected void complete() {
            throw new IllegalStateException("Tried to complete from an illegal state: " + myName);
        }
    }

    protected class CreatedState extends FailureNoticeState {

        public CreatedState() {
            super("Created");
        }

        @Override
        protected void activate() {
            myState = myActiveState;
            myFailureElement.failureNoticeActivated(FailureNotice.this);
        }

        @Override
        protected void delay() {
            myState = myDelayedState;
            myFailureElement.failureNoticeDelayed(FailureNotice.this);
        }

        @Override
        protected void ignore() {
            myState = myIgnoredState;
            myFailureElement.failureNoticeIgnored(FailureNotice.this);
        }
    }

    protected class ActiveState extends FailureNoticeState {

        public ActiveState() {
            super("Active");
        }

        @Override
        protected void complete() {
            myState = myCompletedState;
            myFailureElement.failureNoticeCompleted(FailureNotice.this);
        }

    }

    protected class DelayedState extends FailureNoticeState {

        public DelayedState() {
            super("Delayed");
        }

        @Override
        protected void activate() {
            myState = myActiveState;
            myFailureElement.failureNoticeActivated(FailureNotice.this);
        }

        @Override
        protected void ignore() {
            myState = myIgnoredState;
            myFailureElement.failureNoticeIgnored(FailureNotice.this);
        }
    }

    protected class CompletedState extends FailureNoticeState {

        public CompletedState() {
            super("Completed");
        }

    }

    protected class IgnoredState extends FailureNoticeState {

        public IgnoredState() {
            super("Completed");
        }

    }
}
