/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsl.modeling.elements.resource;

import java.util.SortedSet;
import java.util.TreeSet;
import jsl.modeling.ModelElement;

/**
 *
 * @author rossetti
 */
public class ReleaseResources extends EntityReceiver {

    /** NONE = no requirement specified, will result in an exception
     *  DIRECT = uses the requirements specified directly for the activity
     *  BY_TYPE = asks the EntityType to provide the requirements for this release
     *  ENTITY = uses the entity's getReleaseRequirements() method
     *
     */
    public enum RequirementOption {

        NONE, DIRECT, ENTITY, BY_TYPE

    };

    /** The requirement option for the seize
     *
     */
    protected RequirementOption myReqOption = RequirementOption.NONE;

    /** A Map of requirments for this activity for each resource
     *
     */
    protected SortedSet<ReleaseRequirement> myRequirements;

    public ReleaseResources(ModelElement parent) {
        this(parent, null);
    }

    public ReleaseResources(ModelElement parent, String name) {
        super(parent, name);
    }

    @Override
    protected void receive(Entity entity) {//TODO
        SortedSet<ReleaseRequirement> reqSet = getRequirements(entity);
        for(ReleaseRequirement req: reqSet){
            req.release(entity);
        }
        sendEntity(entity);
    }

    protected SortedSet<ReleaseRequirement> getRequirements(Entity e) {
        SortedSet<ReleaseRequirement> requirements = null;
        if (myReqOption == RequirementOption.DIRECT) {
            if (myRequirements == null) {
                throw new NoRequirementsSpecifiedException();
            } else {
                requirements = myRequirements;
            }
        } else if (myReqOption == RequirementOption.ENTITY) {
            requirements = e.getReleaseRequirements();
        } else if (myReqOption == RequirementOption.BY_TYPE) {
            EntityType et = e.getType();
            requirements = et.getReleaseRequirements(this);//TODO
        } else if (myReqOption == RequirementOption.NONE) {
            throw new NoRequirementsSpecifiedException();
        }
        if (requirements == null) {
            throw new NoRequirementsSpecifiedException();
        }
        if (requirements.size() == 0) {
            throw new NoRequirementsSpecifiedException();
        }
        return requirements;
    }

    public void addReleaseRequirement(Resource r){
        addReleaseRequirement(r, 1);
    }

    public void addReleaseRequirement(Resource r, int amt) {
        if (myRequirements == null) {
            myRequirements = new TreeSet<ReleaseRequirement>();
            myReqOption = RequirementOption.DIRECT;
        }

        ReleaseResourceRequirement req = new ReleaseResourceRequirement(amt);
        req.setResource(r);
        myRequirements.add(req);
    }

    public void addReleaseRequirement(ResourceSet set,
            ReleaseResourceSetRequirement.ReleaseOption option){
        addReleaseRequirement(set, 1, option, null);
    }

    public void addReleaseRequirement(ResourceSet set, int amt,
           ReleaseResourceSetRequirement.ReleaseOption option){
        addReleaseRequirement(set, amt, option, null);
    }

    public void addReleaseRequirement(ResourceSet set, String saveKey){
        addReleaseRequirement(set, 1, null, saveKey);
    }
    
    public void addReleaseRequirement(ResourceSet set, int amt, String saveKey){
        addReleaseRequirement(set, amt, null, saveKey);
    }

    public void addReleaseRequirement(ResourceSet set, int amt,
            ReleaseResourceSetRequirement.ReleaseOption option, String saveKey) {
        if (myRequirements == null) {
            myRequirements = new TreeSet<ReleaseRequirement>();
            myReqOption = RequirementOption.DIRECT;
        }

        ReleaseResourceSetRequirement req = new ReleaseResourceSetRequirement(amt);
        req.setResourceSet(set);
        req.setReleaseOption(option);
        req.setResourceSaveKey(saveKey);
        myRequirements.add(req);
    }

    public RequirementOption getReleaseRequirementOption() {
        return myReqOption;
    }

    public void setReleaseRequirementOption(RequirementOption option) {
        myReqOption = option;
    }
}
