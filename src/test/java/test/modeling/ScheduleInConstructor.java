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
