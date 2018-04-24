/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsl.modeling.elements.station;

import jsl.modeling.queue.QObject;

/** A generic interface to facilitate the sending of
 *  QObjects
 *
 * @author rossetti
 */
public interface SendQObjectIfc {
    
    void send(QObject qObj);
}
