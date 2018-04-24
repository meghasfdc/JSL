/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsl.modeling.elements.resource;

import jsl.modeling.ModelElement;
import jsl.modeling.elements.resource.Delay.DelayOption;
import jsl.modeling.elements.resource.ReleaseResourceSetRequirement.ReleaseOption;
import jsl.modeling.elements.resource.SeizeResources.RequirementOption;
import jsl.utilities.random.RandomIfc;

/**
 *
 * @author rossetti
 */
public class ResourcedActivity extends CompositeEntityReceiver {

    protected SQSeize mySeize;
    
    protected Delay myDelay;
    
    protected ReleaseResources myRelease;
    
    public ResourcedActivity(ModelElement parent) {
        this(parent, null);
    }

    public ResourcedActivity(ModelElement parent, String name) {
        super(parent, name);
        mySeize = new SQSeize(this, getName() + "_Seize");
        myDelay = new Delay(this, getName() + "_Delay");
        myRelease = new ReleaseResources(this, getName() + "_Release");
        addInternalReceiver(mySeize);
        addInternalReceiver(myDelay);
        addInternalReceiver(myRelease);
    }

    public void setSeizeRequirementOption(RequirementOption option) {
        mySeize.setSeizeRequirementOption(option);
    }

    public RequirementOption getSeizeRequirementOption() {
        return mySeize.getSeizeRequirementOption();
    }

    public void addSeizeRequirement(ResourceSet set, int amt, int priority,
            boolean partialFillFlag, ResourceSelectionRuleIfc rule, String saveKey) {
        mySeize.addSeizeRequirement(set, amt, priority, partialFillFlag, rule, saveKey);
    }

    public void addSeizeRequirement(Resource r, int amt, int priority,
            boolean partialFillFlag) {
        mySeize.addSeizeRequirement(r, amt, priority, partialFillFlag);
    }

    public final void setDelayTime(RandomIfc distribution) {
        myDelay.setDelayTime(distribution);
    }

    public final void setDelayOption(DelayOption option) {
        myDelay.setDelayOption(option);
    }

    public void setReleaseRequirementOption(ReleaseResources.RequirementOption option) {
        myRelease.setReleaseRequirementOption(option);
    }

    public ReleaseResources.RequirementOption getReleaseRequirementOption() {
        return myRelease.getReleaseRequirementOption();
    }

    public void addReleaseRequirement(ResourceSet set, int amt,
            ReleaseOption option, String saveKey) {
        myRelease.addReleaseRequirement(set, amt, option, saveKey);
    }

    public void addReleaseRequirement(Resource r, int amt) {
        myRelease.addReleaseRequirement(r, amt);
    }

    public void addSeizeRequirement(ResourceSet set, int amt, int priority) {
        mySeize.addSeizeRequirement(set, amt, priority);
    }

    public void addSeizeRequirement(ResourceSet set, int amt, String saveKey) {
        mySeize.addSeizeRequirement(set, amt, saveKey);
    }

    public void addSeizeRequirement(ResourceSet set, int amt, ResourceSelectionRuleIfc rule) {
        mySeize.addSeizeRequirement(set, amt, rule);
    }

    public void addSeizeRequirement(ResourceSet set, int amt) {
        mySeize.addSeizeRequirement(set, amt);
    }

    public void addSeizeRequirement(ResourceSet set) {
        mySeize.addSeizeRequirement(set);
    }

    public void addSeizeRequirement(Resource r, int amt, int priority) {
        mySeize.addSeizeRequirement(r, amt, priority);
    }

    public void addSeizeRequirement(Resource r, int amt, boolean partialFillFlag) {
        mySeize.addSeizeRequirement(r, amt, partialFillFlag);
    }

    public void addSeizeRequirement(Resource r, int amt) {
        mySeize.addSeizeRequirement(r, amt);
    }

    public void addSeizeRequirement(Resource r) {
        mySeize.addSeizeRequirement(r);
    }

    public void addReleaseRequirement(ResourceSet set, int amt, String saveKey) {
        myRelease.addReleaseRequirement(set, amt, saveKey);
    }

    public void addReleaseRequirement(ResourceSet set, String saveKey) {
        myRelease.addReleaseRequirement(set, saveKey);
    }

    public void addReleaseRequirement(ResourceSet set, int amt, ReleaseOption option) {
        myRelease.addReleaseRequirement(set, amt, option);
    }

    public void addReleaseRequirement(ResourceSet set, ReleaseOption option) {
        myRelease.addReleaseRequirement(set, option);
    }

    public void addReleaseRequirement(Resource r) {
        myRelease.addReleaseRequirement(r);
    }

}
