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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ex.models;

import jsl.modeling.EventActionIfc;
import jsl.modeling.JSLEvent;
import jsl.modeling.ModelElement;
import jsl.modeling.SchedulingElement;
import jsl.modeling.Simulation;

/**
 *
 * @author rossetti
 */
public class SchedulingEventExamples extends SchedulingElement {

    private EventAction myEventAction;

    public SchedulingEventExamples(ModelElement parent) {
        this(parent, null);
    }

    public SchedulingEventExamples(ModelElement parent, String name) {
        super(parent, name);
        myEventAction = new EventAction();
    }

    @Override
    protected void initialize() {
        // schedule a type 1 event at time 10.0
        scheduleEvent(10.0, 1);
        // schedule an event that uses myEventAction for time 20.0
        scheduleEvent(myEventAction, 20.0);
    }

    @Override
    protected void handleEvent(JSLEvent event) {
        int type = event.getType();
        if (type == 1) {
            System.out.println("Type 1 event at time : " + getTime());
            // schedule a type 2 event for time t + 5
            scheduleEvent(5.0, 2);
        }

        if (type == 2) {
            System.out.println("Type 2 event at time : " + getTime());
        }
        
    }

    private class EventAction implements EventActionIfc {

        @Override
        public void action(JSLEvent jsle) {
            System.out.println("EventAction event at time : " + getTime());
            // schedule a type 2 event for time t + 15
            scheduleEvent(15.0, 2);
            // reschedule the EventAction event for t + 2
            rescheduleEvent(jsle, 20.0);
        }
    }

    public static void main(String[] args) {

        Simulation s = new Simulation("Scheduling Example");
        new SchedulingEventExamples(s.getModel());
        s.setLengthOfReplication(100.0);
        s.run();
    }
}
