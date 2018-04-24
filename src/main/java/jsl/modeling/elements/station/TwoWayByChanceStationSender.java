/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsl.modeling.elements.station;

import jsl.modeling.ModelElement;
import jsl.modeling.queue.QObject;

/** This station will receive a QObject and immediately
 *  send it out to one of two randomly selected receivers
 * 
 * @author rossetti
 */
public class TwoWayByChanceStationSender extends Station {

    protected TwoWayByChanceQObjectSender myTwoWaySender;
    
    public TwoWayByChanceStationSender(ModelElement parent, double p,
            ReceiveQObjectIfc r1, ReceiveQObjectIfc r2) {
        this(parent, null, p, r1, r2);
    }

    public TwoWayByChanceStationSender(ModelElement parent, String name, double p,
            ReceiveQObjectIfc r1, ReceiveQObjectIfc r2) {
        super(parent, name);
        myTwoWaySender = new TwoWayByChanceQObjectSender(this, p, r1, r2);
        setSender(myTwoWaySender);
    }

    public final void setSecondReceiver(ReceiveQObjectIfc r2) {
        myTwoWaySender.setSecondReceiver(r2);
    }

    public final void setFirstReceiver(ReceiveQObjectIfc r1) {
        myTwoWaySender.setFirstReceiver(r1);
    }

    @Override
    public void receive(QObject qObj) {
        send(qObj);
    }
}
