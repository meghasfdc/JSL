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
