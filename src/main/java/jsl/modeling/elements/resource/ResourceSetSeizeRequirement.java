/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 *
 * Copyright (c) Manuel D. Rossetti (rossetti@uark.edu)
 *
 * Contact:
 *	Manuel D. Rossetti, Ph.D., P.E.
 *	Department of Industrial Engineering
 *	University of Arkansas
 *	4207 Bell Engineering Center
 *	Fayetteville, AR 72701
 *	Phone: (479) 575-6756
 *	Email: rossetti@uark.edu
 *	Web: www.uark.edu/~rossetti
 *
 * This file is part of the JSL (a Java Simulation Library). The JSL is a framework
 * of Java classes that permit the easy development and execution of discrete event
 * simulation programs.
 *
 * The JSL is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * The JSL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the JSL (see file COPYING in the distribution);
 * if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor,
 * Boston, MA  02110-1301  USA, or see www.fsf.org
 *
 */
package jsl.modeling.elements.resource;

/**
 *
 * @author rossetti
 */
public class ResourceSetSeizeRequirement extends SeizeRequirement {
    
    protected ResourceSet myResourceSet;

    protected ResourceSelectionRuleIfc myRule;

    protected String mySaveResourceKey;

    public ResourceSetSeizeRequirement(ResourceSet resource) {
        this(resource, 1, Request.DEFAULT_PRIORITY, false, 
                ResourceSet.CYCLICAL, null);
    }

    public ResourceSetSeizeRequirement(ResourceSet resource, int amt) {
        this(resource, amt, Request.DEFAULT_PRIORITY, false, 
                ResourceSet.CYCLICAL, null);
    }

    public ResourceSetSeizeRequirement(ResourceSet resource, int amt, int priority) {
        this(resource, amt, priority, false, ResourceSet.CYCLICAL,
                null);
    }

    public ResourceSetSeizeRequirement(ResourceSet resource, int amt, int priority,
            boolean partialFillFlag, ResourceSelectionRuleIfc rule, String saveKey) {
        super(amt, priority, partialFillFlag);
        if (resource == null) {
            throw new IllegalArgumentException("The SeizeIfc was null");
        }

        myResourceSet = resource;
        setResourceSelectionRule(rule);
        setSaveResourceKey(saveKey);
    }

    public ResourceSelectionRuleIfc getResourceSelectionRule() {
        return myRule;
    }

    public void setResourceSelectionRule(ResourceSelectionRuleIfc rule) {
        if (rule == null) {
            throw new IllegalArgumentException("The ResourceSelectionRuleIfc was null");
        }
        myRule = rule;
    }

    @Override
    public SeizeIfc getResource() {
        return myResourceSet;
    }

    @Override
    public Request createRequest(Entity entity, AllocationListenerIfc listener) {
        Request r = new Request(myAmtNeeded, myPriority, myPartialFillFlag);
        r.setEntity(entity);
        r.setResourceAllocationListener(listener);
        r.setResourceSelectionRule(myRule);
//        r.setSeizeRequirement(this);
        r.setResourceSaveKey(mySaveResourceKey);
        return r;
    }

    public String getSaveResourceKey() {
        return mySaveResourceKey;
    }

    public void setSaveResourceKey(String resourceKey) {
        mySaveResourceKey = resourceKey;
    }


}
