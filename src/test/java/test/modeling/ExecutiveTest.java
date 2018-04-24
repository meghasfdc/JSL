/*
 *  Copyright (C) 2010 rossetti
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
 *  of Java classes that permit the development and execution of discrete event
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
package test.modeling;

import jsl.observers.ObserverIfc;
import jsl.modeling.EventActionIfc;
import jsl.modeling.Executive;
import jsl.modeling.JSLEvent;
import jsl.modeling.Model;
import jsl.modeling.Simulation;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author rossetti
 */
public class ExecutiveTest {

    Executive e;
    Simulation s;
    ActionListener a;
    Model m;

    @Before
    public void setUp() {
        s = new Simulation();
        m = s.getModel();
        e = new Executive();
        a = new ActionListener();

        ExecutiveTrace trace = new ExecutiveTrace();
        e.addObserver(trace);

    }
    
    @Test
    public void test1() {
        System.out.println("Test 1");
        System.out.println("Event 6 & 7 should execute along with end event");
        System.out.println("because other events are scheduled after end event.");
        System.out.println();
        e.initialize();
        JSLEvent evt = e.scheduleEndEvent(0.9, m);
        e.scheduleEvent(a, 1.0, "event 1", 1, null, m);
        e.scheduleEvent(a, 1.1, "event 2", 1, null, m);
        e.scheduleEvent(a, 1.2, "event 3", 1, null, m);
        e.scheduleEvent(a, 1.3, "event 4", 1, null, m);
        e.scheduleEvent(a, 1.3, "event 5", 1, null, m);
        e.scheduleEvent(a, 0.5, "event 6", 1, null, m);
        e.scheduleEvent(a, 0.75, "event 7", 1, null, m);
        e.executeAllEvents();
        System.out.println();
        System.out.println("Done executing all events");
        System.out.println("Number of events executed = " + e.getTotalNumberEventsExecuted());
        System.out.println("Time executive actually ended = " + e.getActualEndingTime());
        System.out.println("End Event time = " + evt.getTime());
        System.out.println("*****************************");
        System.out.println();
        assertTrue(e.getTotalNumberEventsExecuted() == 3);
        assertTrue(evt.getTime() == e.getActualEndingTime());
    }

    @Test
    public void test2() {
        System.out.println("Test 2");
        System.out.println("All events should execute");
        e.initialize();
        e.scheduleEvent(a, 1.0, "event 1", 1, null, m);
        e.scheduleEvent(a, 1.1, "event 2", 1, null, m);
        e.scheduleEvent(a, 1.2, "event 3", 1, null, m);
        e.scheduleEvent(a, 1.3, "event 4", 1, null, m);
        e.scheduleEvent(a, 1.3, "event 5", 1, null, m);
        e.scheduleEvent(a, 0.5, "event 6", 1, null, m);
        e.scheduleEvent(a, 0.75, "event 7", 1, null, m);
        
        e.executeAllEvents();
        
        System.out.println();
        System.out.println("Done executing all events");
        System.out.println("Number of events executed = " + e.getTotalNumberEventsExecuted());
        System.out.println("Time executive actually ended = " + e.getActualEndingTime());
        System.out.println("*****************************");
        System.out.println();

        assertTrue(e.getTotalNumberEventsExecuted() == 7);
        assertTrue(1.3 == e.getActualEndingTime());
    }

    @Test
    public void test3() {
        System.out.println("Test 3");
        System.out.println("Only first 2 events should execute");
        //e.scheduleEndEvent(Double.POSITIVE_INFINITY);
        e.initialize();
        JSLEvent evt = e.getEndEvent();
        e.scheduleEvent(a, 1.0, "event 1", 1, null, m);
        e.scheduleEvent(a, 1.1, "event 2", 1, null, m);
        e.scheduleEvent(a, 1.2, "event 3", 1, null, m);
        e.scheduleEvent(a, 1.3, "event 4", 1, null, m);
        e.scheduleEvent(a, 1.3, "event 5", 1, null, m);
        e.scheduleEvent(a, 0.5, "event 6", 1, null, m);
        e.scheduleEvent(a, 0.75, "event 7", 1, null, m);

        e.executeNextEvent();
        e.executeNextEvent();

        System.out.println(e);
        System.out.println();
        System.out.println("Number of events executed = " + e.getTotalNumberEventsExecuted());
        System.out.println("Executive actual ending time = " + e.getActualEndingTime());
        System.out.println("getTime() = " + e.getTime());
        System.out.println("*****************************");
        System.out.println();

        assertTrue(e.getTotalNumberEventsExecuted() == 2);
        assertTrue(e.getTime() == 0.75);
    }

    @Test
    public void test4() {
        System.out.println("Test 4");
        System.out.println("Only first 2 events should execute");

        e.initialize();

        e.scheduleEvent(a, 1.0, "event 1", 1, null, m);
        e.scheduleEvent(a, 1.1, "event 2", 1, null, m);

        e.executeNextEvent();
        System.out.println(e);
        e.executeNextEvent();
        System.out.println(e);
        
        boolean f = false;
        try {
            System.out.println("### Try to execute event that is not there");
            e.executeNextEvent();//should cause an error
        } catch (jsl.modeling.NoSuchStepException e) {
             System.out.println("### catch the error");
             System.out.println(e);
            f = true;
        }

        System.out.println();
        System.out.println("Done executing all events");
        System.out.println("Number of events executed = " + e.getTotalNumberEventsExecuted());
        System.out.println("Time executive actually ended = " + e.getActualEndingTime());
        System.out.println("getTime() = " + e.getTime());
        System.out.println("*****************************");
        System.out.println();

        assertTrue(e.getTotalNumberEventsExecuted() == 2);
        assertTrue(e.getTime() == 1.1);
        assertTrue(f);
        
        
        e.end("Ended in Test 4");
        assertTrue(e.getActualEndingTime() == 1.1);
                
    }

     @Test
    public void test5() {
        System.out.println("Test 5");
        //System.out.println("Only first 2 events should execute");

        e.initialize();
        e.executeAllEvents();
        assertTrue(e.noStepsExecuted());
     }
     
    public class ActionListener implements EventActionIfc {

        @Override
        public void action(JSLEvent evt) {
            System.out.println(evt);
        }

        @Override
        public String toString() {
            return "ActionListener";
        }
    }

    public class ExecutiveTrace implements ObserverIfc {

        @Override
        public void update(Object subject, Object arg) {
            Executive exec = (Executive) subject;

            if (exec.getObserverState() == Executive.INITIALIZED) {
                System.out.println("Executive: Initialized before running.");
                System.out.println(exec);
            }

            if (exec.getObserverState() == Executive.AFTER_EXECUTION) {
                System.out.println("Executive: After execution");
                System.out.println(exec);
            }

            if (exec.getObserverState() == Executive.BEFORE_EVENT) {
                JSLEvent event = (JSLEvent) arg;
                System.out.println("Executive: Before event " + event.getName());
            }

            if (exec.getObserverState() == Executive.AFTER_EVENT) {
                JSLEvent event = (JSLEvent) arg;
                System.out.println("Executive: After event " + event.getName());
            }

        }
    }
}
