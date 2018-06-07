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
package ex.hospitalward;

import ex.hospitalward.HospitalWard.OpPatient;
import jsl.modeling.EventActionIfc;
import jsl.modeling.JSLEvent;
import jsl.modeling.SchedulingElement;
import jsl.modeling.queue.Queue;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.modeling.elements.variable.TimeWeighted;
import jsl.utilities.random.distributions.Constant;

/**
 *
 * @author rossetti
 */
public class OperatingRoom extends SchedulingElement {

    public static final double IDLE = 0.0;

    public static final double BUSY = 1.0;

    public static final double OPEN = 1.0;

    public static final double CLOSED = 0.0;

    private HospitalWard myHospitalWard;

    private Queue myORQ;

    private RandomVariable myOpRoomOpenTime;

    private RandomVariable myOpRoomCloseTime;

    private TimeWeighted myORRoomOpenStatus;

    private TimeWeighted myORRoomIdleStatus;

    private OpenOperatingRoomAction myOpenOperatingRoomAction;

    private CloseOperatingRoomAction myCloseOperatingRoomAction;

    private EndOfOperationAction myEndOfOperationAction;

    public OperatingRoom(HospitalWard ward) {
        this(ward, null);
    }

    public OperatingRoom(HospitalWard ward, String name) {
        super(ward, name);
        myHospitalWard = ward;
        myORQ = new Queue(this, "OR Q");
        myOpRoomOpenTime = new RandomVariable(this, new Constant(24.0));
        myOpRoomCloseTime = new RandomVariable(this, new Constant(4.0));
        myORRoomOpenStatus = new TimeWeighted(this, OPEN, "OR-Open-Status");
        myORRoomIdleStatus = new TimeWeighted(this, IDLE, "OR-Idle-Status");
        myOpenOperatingRoomAction = new OpenOperatingRoomAction();
        myCloseOperatingRoomAction = new CloseOperatingRoomAction();
        myEndOfOperationAction = new EndOfOperationAction();
    }

    @Override
    protected void initialize() {
        scheduleEvent(myCloseOperatingRoomAction, myOpRoomOpenTime);
    }

    protected void receivePatient(OpPatient p) {
        myORQ.enqueue(p);
        if (isIdle() && isOpen()) {
            if (p == myORQ.peekNext()) {
                myORRoomIdleStatus.setValue(BUSY);
                myORQ.removeNext();
                scheduleEvent(myEndOfOperationAction, p.getOperationTime(), p);
            }
        }
    }

    public boolean isIdle() {
        return myORRoomIdleStatus.getValue() == IDLE;
    }

    public boolean isOpen() {
        return myORRoomOpenStatus.getValue() == OPEN;
    }

    protected class OpenOperatingRoomAction implements EventActionIfc {

        @Override
        public void action(JSLEvent evt) {

            myORRoomOpenStatus.setValue(OPEN);
            if (isIdle() && myORQ.isNotEmpty()) {
                myORRoomIdleStatus.setValue(BUSY);
                OpPatient p = (OpPatient) myORQ.removeNext();
                scheduleEvent(myEndOfOperationAction, p.getOperationTime(), p);
            }
            scheduleEvent(myCloseOperatingRoomAction, myOpRoomOpenTime);
        }
    }

    protected class CloseOperatingRoomAction implements EventActionIfc {

        @Override
        public void action(JSLEvent evt) {
            myORRoomOpenStatus.setValue(CLOSED);
            scheduleEvent(myOpenOperatingRoomAction, myOpRoomCloseTime);
        }
    }

    protected class EndOfOperationAction implements EventActionIfc {

        @Override
        public void action(JSLEvent evt) {
            if (myORQ.isNotEmpty() && isOpen()) {
                OpPatient nextP = (OpPatient) myORQ.removeNext();
                scheduleEvent(myEndOfOperationAction, nextP.getOperationTime(), nextP);
            } else {
                myORRoomIdleStatus.setValue(IDLE);
            }
            OpPatient currentP = (OpPatient) evt.getMessage();
            myHospitalWard.endOfOperation(currentP);
        }
    }
}
