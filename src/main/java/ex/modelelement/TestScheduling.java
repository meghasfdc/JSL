/*
 * Copyright (c) 2019. Manuel D. Rossetti, rossetti@uark.edu
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

package ex.modelelement;

import jsl.modeling.JSLEvent;
import jsl.modeling.ModelElement;
import jsl.modeling.SchedulingElement;
import jsl.modeling.Simulation;

/**
 *  An example of a bad idea.  Something not to do.
 */
public class TestScheduling extends SchedulingElement {

    public TestScheduling(ModelElement parent) {
        this(parent, null);
    }

    public TestScheduling(ModelElement parent, String name) {
        super(parent, name);
    }

    // this is a bad idea, don't do it
    public void scheduleSomething(double time){
        scheduleEvent(time);
    }

    @Override
    protected void handleEvent(JSLEvent event) {
        // This never executes
        System.out.println(event);
    }

    public static void main(String[] args) {
        Simulation s = new Simulation();
        TestScheduling t = new TestScheduling(s.getModel());

        // An example of what not to do, this event will not execute
        t.scheduleSomething(10.0);

        s.setNumberOfReplications(2);
        s.setLengthOfReplication(25.0);

        s.run();
    }
}
