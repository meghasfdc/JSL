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
package jsl.modeling.elements.station;

import jsl.modeling.ModelElement;
import jsl.modeling.SchedulingElement;
import jsl.modeling.queue.QObject;

/** A Station represents a location that can receive QObjects for
 *  processing. Sub-classes of Station must supply an implementation of the 
 *  ReceiveQObjectIfc interface.
 * 
 *  A Station may or may not have a helper object that implements the 
 *  SendQObjectIfc interface.  If this helper object is supplied it will
 *  be used to send the processed QObject to its next location for
 *  processing.
 * 
 *  A Station may or may not have a helper object that implements the 
 *  ReceiveQObjectIfc interface.  If this helper object is supplied and
 *  the SendQObjectIfc helper is not supplied, then the object that implements
 *  the ReceiveQObjectIfc will be the next receiver for the QObject
 * 
 *  If neither helper object is supplied then a runtime exception will
 *  occur when trying to use the send() method
 *
 * @author rossetti
 */
public abstract class Station extends SchedulingElement implements ReceiveQObjectIfc {

    /**
     * Can be supplied in order to provide logic
     *  to send the QObject to its next receiver
     */
    private SendQObjectIfc mySender;

    /** Can be used to directly tell the receiver to receive the departing
     *  QObject
     * 
     */
    private ReceiveQObjectIfc myNextReceiver;

    public Station(ModelElement parent) {
        this(parent, null, null);
    }

    public Station(ModelElement parent, String name) {
        this(parent, null, name);
    }

    /**
     * 
     * @param parent
     * @param sender can be null
     * @param name 
     */
    public Station(ModelElement parent, SendQObjectIfc sender, String name) {
        super(parent, name);
        setSender(sender);
    }

    /**
     * A Station may or may not have a helper object that implements the 
     *  SendQObjectIfc interface.  If this helper object is supplied it will
     *  be used to send the processed QObject to its next location for
     *  processing.
     * @return 
     */
    public final SendQObjectIfc getSender() {
        return mySender;
    }

    /**
     * A Station may or may not have a helper object that implements the 
     *  SendQObjectIfc interface.  If this helper object is supplied it will
     *  be used to send the processed QObject to its next location for
     *  processing.
     * @param sender 
     */
    public final void setSender(SendQObjectIfc sender) {
        mySender = sender;
    }

    /**
     *  A Station may or may not have a helper object that implements the 
     *  ReceiveQObjectIfc interface.  If this helper object is supplied and
     *  the SendQObjectIfc helper is not supplied, then the object that implements
     *  the ReceiveQObjectIfc will be the next receiver for the QObject when using 
     *  default send() method.
     * @return 
     */
    public final ReceiveQObjectIfc getNextReceiver() {
        return myNextReceiver;
    }

    /**
     *  A Station may or may not have a helper object that implements the 
     *  ReceiveQObjectIfc interface.  If this helper object is supplied and
     *  the SendQObjectIfc helper is not supplied, then the object that implements
     *  the ReceiveQObjectIfc will be the next receiver for the QObject when using 
     *  default send() method.
     * @param receiver 
     */
    public final void setNextReceiver(ReceiveQObjectIfc receiver) {
        myNextReceiver = receiver;
    }

    /**
     *  A Station may or may not have a helper object that implements the 
     *  SendQObjectIfc interface.  If this helper object is supplied it will
     *  be used to send the processed QObject to its next location for
     *  processing.
     * 
     *  A Station may or may not have a helper object that implements the 
     *  ReceiveQObjectIfc interface.  If this helper object is supplied and
     *  the SendQObjectIfc helper is not supplied, then the object that implements
     *  the ReceiveQObjectIfc will be the next receiver for the QObject
     * 
     *  If neither helper object is supplied then a runtime exception will
     *  occur when trying to use the send() method     
     * @param qObj 
     */
    protected void send(QObject qObj) {
        if (getSender() != null) {
            getSender().send(qObj);
        } else if (getNextReceiver() != null) {
            getNextReceiver().receive(qObj);
        } else {
            throw new RuntimeException("No valid sender or receiver");
        }
    }

}
