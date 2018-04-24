/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jsl.modeling.elements.resource;

/**
 *
 * @author rossetti
 */
public class ReleaseResourceSetRequirement extends ReleaseRequirement {

    public enum ReleaseOption {

        LAST_MEMBER_SEIZED, FIRST_MEMBER_SEIZED//, SPECIFIC_MEMBER
    }

    protected ReleaseOption myReleaseOption;

    protected ResourceSet myResourceSet;

    protected String myResourceSaveKey;

    public ReleaseResourceSetRequirement(int amt) {
        super(amt);
    }

    public ResourceSet getResourceSet() {
        return myResourceSet;
    }

    public void setResourceSet(ResourceSet set) {
        myResourceSet = set;
    }

    public ReleaseOption getReleaseOption() {
        return myReleaseOption;
    }

    public void setReleaseOption(ReleaseOption option) {
        myReleaseOption = option;
    }

    public void setResourceSaveKey(String saveKey) {
        myResourceSaveKey = saveKey;
    }

    public String getResourceSaveKey(){
        return myResourceSaveKey;
    }

        @Override
    public void release(Entity e) {

        if (myReleaseOption == ReleaseOption.FIRST_MEMBER_SEIZED) {
            e.releaseFirstMemberSeized(myResourceSet, myReleaseAmount);
        } else if (myReleaseOption  == ReleaseOption.LAST_MEMBER_SEIZED) {
            e.releaseLastMemberSeized(myResourceSet, myReleaseAmount);
        } else if (myResourceSaveKey != null) {
            e.releaseSpecificMember(myResourceSet, myResourceSaveKey, myReleaseAmount);
        } else {
            throw new IllegalArgumentException("The ReleaseRequirment's option was not defined");
        }
    }

}
