/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsl.modeling.elements.resource;

/**
 *
 * @author rossetti
 */
public class ReleaseResourceRequirement extends ReleaseRequirement {

    protected Resource myResource;

    public ReleaseResourceRequirement(int amt) {
        super(amt);
    }

    public Resource getResource() {
        return myResource;
    }

    public void setResource(Resource r) {
        myResource = r;
    }

    @Override
    public void release(Entity e) {
        myResource.release(e, myReleaseAmount);
    }
}
