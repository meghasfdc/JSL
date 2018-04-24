/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jsl.modeling.elements.resource;

/**
 *
 * @author rossetti
 */
public interface SeizeIfc {

    /**
     * Seizes the resource using the request.
     * Conditions:
     * 1) request must not be null
     * 2) request.getEntity() must not be null
     * 3) The request must not have been seized with another resource
     * 4) The request must have a ResourceAllocationListener attached.
     *
     * @param request
     */
    void seize(Request request);

}
