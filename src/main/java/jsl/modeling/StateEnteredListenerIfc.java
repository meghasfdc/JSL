/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jsl.modeling;

/**
 *
 * @author rossetti
 */
public interface StateEnteredListenerIfc extends Comparable<StateEnteredListenerIfc> {
    
    void update(ModelElementState state);
    
}
