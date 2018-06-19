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

package jsl.modeling.resource;

import jsl.modeling.ModelElement;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.utilities.random.RandomIfc;

import java.util.LinkedHashSet;
import java.util.Set;

public class MultipleResourceUnitFailureEvent extends ModelElement {

    private final RandomVariable myTimeToEvent;
    private final RandomVariable myEventDuration;
    private final Set<FailureEvent> myFailureEvents;

    public MultipleResourceUnitFailureEvent(ModelElement parent, RandomIfc timeToEvent, RandomIfc eventDuration) {
        this(parent, timeToEvent, eventDuration, null);
    }

    public MultipleResourceUnitFailureEvent(ModelElement parent, RandomIfc timeToEvent, RandomIfc eventDuration, String name) {
        super(parent, name);
        myTimeToEvent = new RandomVariable(this, timeToEvent, getName() + ":TimeToEvent");
        myEventDuration = new RandomVariable(this, eventDuration, getName() + ":EventDuration");
        myFailureEvents = new LinkedHashSet<>();
    }
}
