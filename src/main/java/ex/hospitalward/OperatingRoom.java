/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 *
 * Copyright (c) Manuel D. Rossetti (rossetti@uark.edu)
 *
 * Contact:
 *	Manuel D. Rossetti, Ph.D., P.E.
 *	Department of Industrial Engineering
 *	University of Arkansas
 *	4207 Bell Engineering Center
 *	Fayetteville, AR 72701
 *	Phone: (479) 575-6756
 *	Email: rossetti@uark.edu
 *	Web: www.uark.edu/~rossetti
 *
 * This file is part of the JSL (a Java Simulation Library). The JSL is a framework
 * of Java classes that permit the easy development and execution of discrete event
 * simulation programs.
 *
 * The JSL is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * The JSL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the JSL (see file COPYING in the distribution);
 * if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor,
 * Boston, MA  02110-1301  USA, or see www.fsf.org
 *
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
