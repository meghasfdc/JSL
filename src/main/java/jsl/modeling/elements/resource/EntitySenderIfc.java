/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsl.modeling.elements.resource;

/** This interface is used by EntityReceiver if one of the
 *  default options is not specified.  A client can supply an instance of 
 *  a class that implements this interface in order to provide 
 *  a general method to send the entity to its next receiver
 *
 * @author rossetti
 */
public interface EntitySenderIfc {
    
    /** Generic method for sending an entity to a receiver
     * 
     * @param e
     */
    void sendEntity(Entity e);
}
