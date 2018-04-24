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
package test.modeling;

import jsl.modeling.EventActionIfc;
import jsl.modeling.JSLEvent;
import jsl.modeling.ModelElement;
import jsl.modeling.SchedulingElement;

/**
 *
 * @author rossetti
 */
public class ScheduleInConstructor extends SchedulingElement {

    private EventAction a = new EventAction();

    public ScheduleInConstructor(ModelElement parent) {
        this(parent, null);
    }

    public ScheduleInConstructor(ModelElement parent, String name) {
        super(parent, name);


        JSLEvent e = scheduleEvent(a, 1.0);
        System.out.println("Constructor: First event scheduled: ");
        System.out.println(e);
        e = scheduleEvent(a, 5.0);
        System.out.println("Constructor: 2nd event scheduled: ");
        System.out.println(e);
        System.out.println();
    }

    @Override
    protected void initialize() {
        JSLEvent e = scheduleEvent(a, 10.0);
        System.out.println("Initialize: 3rd event scheduled: ");
        System.out.println(e);
        System.out.println();
        e = scheduleEvent(a, 30.0);
        System.out.println("Initialize: 4th event scheduled: ");
        System.out.println(e);
        System.out.println();
    }

    private class EventAction implements EventActionIfc {

        @Override
        public void action(JSLEvent evt) {
            System.out.println("Event Action for event: ");
            System.out.println(evt);
            System.out.println();
        }
    }
}
