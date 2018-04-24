/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsl.modeling.elements.station;

import jsl.modeling.ModelElement;
import jsl.modeling.elements.RandomElement;
import jsl.modeling.queue.QObject;

/** This model element randomly selects a instance that implements 
 *  the ReceiveQObjectIfc and sends the QObject to the receiver
 *
 * @author rossetti
 */
public class NWayByChanceQObjectSender extends RandomElement<ReceiveQObjectIfc> 
        implements SendQObjectIfc {

    public NWayByChanceQObjectSender(ModelElement parent) {
        this(parent, null);
    }
        
    public NWayByChanceQObjectSender(ModelElement parent, String name) {
        super(parent, name);
    }
    
    @Override
    public void send(QObject qObj) {
        getRandomElement().receive(qObj);
    }
    
}
