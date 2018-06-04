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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsl.modeling.elements.station;

import jsl.modeling.ModelElement;
import jsl.modeling.queue.QObject;

/** This station will receive a QObject and immediately
 *  send it out to a randomly selected receiver
 *
 * @author rossetti
 */
public class NWayByChanceStationSender extends Station {

    protected NWayByChanceQObjectSender myNWaySender;
    
    public NWayByChanceStationSender(ModelElement parent) {
        this(parent, null);
    }

    public NWayByChanceStationSender(ModelElement parent, String name) {
        super(parent, name);
        myNWaySender = new NWayByChanceQObjectSender(this);
        setSender(myNWaySender);
    }

    @Override
    public void receive(QObject qObj) {
        send(qObj);
    }

    public final int size() {
        return myNWaySender.size();
    }

    public final boolean isEmpty() {
        return myNWaySender.isEmpty();
    }

    public final int indexOf(Object arg0) {
        return myNWaySender.indexOf(arg0);
    }

    public final boolean contains(Object arg0) {
        return myNWaySender.contains(arg0);
    }

    public final void addLast(ReceiveQObjectIfc obj) {
        myNWaySender.addLast(obj);
    }

    public final void add(ReceiveQObjectIfc obj, double p) {
        myNWaySender.add(obj, p);
    }
    
}
