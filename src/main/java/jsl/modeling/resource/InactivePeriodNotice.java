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


/**
 * A InactivePeriodNotice represents a notification that the ResourceUnit should 
 * become inactive due to a schedule change. The inactive period notice may be 
 * required to be immediate or not. If not immediate, then the inactive period
 * may be delayed until the resource unit finishes its current activity.
 *
 * @author rossetti
 */
public class InactivePeriodNotice { //extends QObject {
    private static int idCounter;
    private final CreatedState myCreatedState = new CreatedState();
    private final ActiveState myActiveState = new ActiveState();
    private final DelayedState myDelayedState = new DelayedState();
    private final CanceledState myCanceledState = new CanceledState();
    private final CompletedState myCompletedState = new CompletedState();
    private final double myInactiveTime;
    private final boolean myDelayableFlag;
    private InactivePeriodNoticeState myState;
    private final double myCreateTime;
    private final int myId;

    /**
     *
     * @param createTime the time that the notice was created
     * @param inactiveTime the time that the failure should last
     * @param delayOption true means it does not need to be immediate
     */
    InactivePeriodNotice(double createTime, double inactiveTime, boolean delayOption) {
        idCounter = idCounter + 1;
        myId = idCounter;
        myCreateTime = createTime;
        myInactiveTime = inactiveTime;
        myDelayableFlag = delayOption;
        myState = myCreatedState;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Inactive Period Notice : ");
        sb.append(getId());
        sb.append(" : time = ").append(getInactiveTime());
        sb.append(" : delayable = ").append(isDelayable());
        sb.append(" : state = ").append(myState.myName);
        return sb.toString();
    }

    /**
     * 
     * @return a unique id
     */
    public final int getId(){
        return myId;
    }
    
    /**
     * 
     * @return the time that the notice was created
     */
    public final double getCreateTime(){
        return myCreateTime;
    }
    
    /**
     *
     * @return the time that the failure should last
     */
    public final double getInactiveTime() {
        return myInactiveTime;
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
        return myState == myCanceledState;
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
            throw new IllegalStateException("Tried to delay an "
                    + "InactivePeriodNotice that is not delayable.");
        }
        myState.delay();
    }

    final void cancel() {
        myState.cancel();
    }

    final void complete() {
        myState.complete();
    }

    protected class InactivePeriodNoticeState {

        protected final String myName;

        protected InactivePeriodNoticeState(String name) {
            myName = name;
        }

        protected void activate() {
            throw new IllegalStateException("Tried to activate from an illegal state: " + myName);
        }

        protected void delay() {
            throw new IllegalStateException("Tried to delay from an illegal state: " + myName);
        }

        protected void cancel() {
            throw new IllegalStateException("Tried to cancel from an illegal state: " + myName);
        }

        protected void complete() {
            throw new IllegalStateException("Tried to complete from an illegal state: " + myName);
        }
    }

    protected class CreatedState extends InactivePeriodNoticeState {

        public CreatedState() {
            super("Created");
        }

        @Override
        protected void activate() {
            myState = myActiveState;
        }

        @Override
        protected void delay() {
            myState = myDelayedState;
        }

        @Override
        protected void cancel() {
            myState = myCanceledState;
        }
    }

    protected class ActiveState extends InactivePeriodNoticeState {

        public ActiveState() {
            super("Active");
        }

        @Override
        protected void complete() {
            myState = myCompletedState;
        }
        
                @Override
        protected void cancel() {
            myState = myCanceledState;;
        }

    }

    protected class DelayedState extends InactivePeriodNoticeState {

        public DelayedState() {
            super("Delayed");
        }

        @Override
        protected void activate() {
            myState = myActiveState;
        }

        @Override
        protected void cancel() {
            myState = myCanceledState;
        }
    }

    protected class CompletedState extends InactivePeriodNoticeState {

        public CompletedState() {
            super("Completed");
        }

    }

    protected class CanceledState extends InactivePeriodNoticeState {

        public CanceledState() {
            super("Canceled");
        }

    }
}
