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
package jsl.modeling.elements.station;

import jsl.modeling.ModelElement;
import jsl.modeling.queue.QObject;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.utilities.random.distributions.Bernoulli;

/** This model element randomly selects between two receivers 
 *  (objects that implement ReceiveQObjectIfc) and sends the
 *  QObject to the chosen receiver.  The first receiver is 
 *  chosen with probability p and the second receiver is chosen
 *  with probability 1-p
 *
 * @author rossetti
 */
public class TwoWayByChanceQObjectSender extends ModelElement implements SendQObjectIfc {

    private RandomVariable myRV;

    private ReceiveQObjectIfc myR1;

    private ReceiveQObjectIfc myR2;

    public TwoWayByChanceQObjectSender(ModelElement parent, double p,
            ReceiveQObjectIfc r1, ReceiveQObjectIfc r2) {
        this(parent, null, p, r1, r2);
    }

    public TwoWayByChanceQObjectSender(ModelElement parent, String name, double p,
            ReceiveQObjectIfc r1, ReceiveQObjectIfc r2) {
        super(parent, name);
        setFirstReceiver(r1);
        setSecondReceiver(r2);
        myRV = new RandomVariable(this, new Bernoulli(p));
    }

    public final void setFirstReceiver(ReceiveQObjectIfc r1) {
        if (r1 == null) {
            throw new IllegalArgumentException("Receiver 1 was null");
        }
        myR1 = r1;
    }

    public final void setSecondReceiver(ReceiveQObjectIfc r2) {
        if (r2 == null) {
            throw new IllegalArgumentException("Receiver 2 was null");
        }
        myR2 = r2;
    }

    @Override
    public void send(QObject qObj) {
        if (myRV.getValue() == 1.0) {
            myR1.receive(qObj);
        } else {
            myR2.receive(qObj);
        }
    }
}