/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsl.modeling.elements.station;

import jsl.modeling.queue.QObject;

/** A generic interface that can be implemented to allow
 *  facilitate the receiving of QObjects
 *
 * @author rossetti
 */
public interface ReceiveQObjectIfc {
    
    void receive(QObject qObj);
}
