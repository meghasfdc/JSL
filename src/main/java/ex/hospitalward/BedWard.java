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

import ex.hospitalward.HospitalWard.NoOpPatient;
import ex.hospitalward.HospitalWard.OpPatient;
import jsl.modeling.EventActionIfc;
import jsl.modeling.JSLEvent;
import jsl.modeling.SchedulingElement;
import jsl.modeling.queue.QObject;
import jsl.modeling.queue.Queue;
import jsl.modeling.elements.variable.TimeWeighted;

/**
 *
 * @author rossetti
 */
public class BedWard extends SchedulingElement {

    private Queue myNoOpPatientQ;

    private Queue myOpPatientQ;

    private TimeWeighted myAvailableBeds;

    private TimeWeighted myBusyBeds;

    private EndNoOpPatientStayListener myNoOpPatientStayListener;

    private EndOpPatientPreOpStayListener myOpPatientPreOpStayListener;

    private EndOfPostOperationStayAction myEndOfPostOperationStayAction;

    private HospitalWard myHospitalWard;

    public BedWard(HospitalWard ward) {
        this(ward, null);
    }

    public BedWard(HospitalWard ward, String name) {
        super(ward, name);

        myHospitalWard = ward;
        myNoOpPatientQ = new Queue(this, "No Op Patient Q");
        myOpPatientQ = new Queue(this, "Op Patient Q");
        myAvailableBeds = new TimeWeighted(this, 20.0, "Beds Available");
        myBusyBeds = new TimeWeighted(this, 0.0, "Number Busy Beds");
        myNoOpPatientStayListener = new EndNoOpPatientStayListener();

        myOpPatientPreOpStayListener = new EndOpPatientPreOpStayListener();

        myEndOfPostOperationStayAction = new EndOfPostOperationStayAction();

    }

    protected void receiveNewPatient(NoOpPatient p) {
        myNoOpPatientQ.enqueue(p);
        if (myAvailableBeds.getValue() > 0.0) {
            if (p == myNoOpPatientQ.peekNext()) {
                myNoOpPatientQ.removeNext();
                myAvailableBeds.decrement();
                myBusyBeds.increment();
                scheduleEvent(myNoOpPatientStayListener, p.getHospitalStayTime(), p);
            }
        }
    }

    protected void receiveNewPatient(OpPatient p) {
        myOpPatientQ.enqueue(p);
        if (myAvailableBeds.getValue() > 0.0) {
            if (p == myOpPatientQ.peekNext()) {
                myOpPatientQ.removeNext();
                myAvailableBeds.decrement();
                myBusyBeds.increment();
                scheduleEvent(myOpPatientPreOpStayListener, p.getPreOperationTime(), p);
            }
        }
    }

    protected void receivePostOperationPatient(OpPatient p) {
        scheduleEvent(myEndOfPostOperationStayAction, p.getPostOperationTime(), p);
    }

    private void reallocateBed() {
        // preference by order of checking
        if (myNoOpPatientQ.isNotEmpty()) {
            NoOpPatient p = (NoOpPatient) myNoOpPatientQ.removeNext();
            scheduleEvent(myNoOpPatientStayListener, p.getHospitalStayTime(), p);
        } else if (myOpPatientQ.isNotEmpty()) {
            OpPatient p = (OpPatient) myOpPatientQ.removeNext();
            scheduleEvent(myOpPatientPreOpStayListener, p.getPreOperationTime(), p);
        } else {
            myAvailableBeds.increment();
            myBusyBeds.decrement();
        }
    }

    protected class EndNoOpPatientStayListener implements EventActionIfc {

        @Override
        public void action(JSLEvent evt) {
            reallocateBed();
            myHospitalWard.departingPatient((QObject) evt.getMessage());
        }
    }

    protected class EndOpPatientPreOpStayListener implements EventActionIfc {

        @Override
        public void action(JSLEvent evt) {
            OpPatient p = (OpPatient) evt.getMessage();
            myHospitalWard.sendToOperatingRoom(p);
        }
    }

    protected class EndOfPostOperationStayAction implements EventActionIfc {

        @Override
        public void action(JSLEvent evt) {
            reallocateBed();
            myHospitalWard.departingPatient((QObject) evt.getMessage());
        }
    }
}
