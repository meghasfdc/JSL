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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jsl.modeling.ModelElement;

/**
 *
 * @author rossetti
 */
abstract public class SeizeResources extends EntityReceiver {

    /** NONE = no requirement specified, will result in an exception
     *  DIRECT = uses the requirements specified directly for the activity
     *  BY_TYPE = asks the EntityType to provide the requirements for this activity
     *  ENTITY = uses the entity's getRequirements() method
     *
     */
    public enum RequirementOption {

        NONE, DIRECT, ENTITY, BY_TYPE

    };
    /** The requirement option for the seize
     *
     */
    protected RequirementOption myReqOption = RequirementOption.NONE;

    /** A Map of requirements for this seize
     *
     */
    protected List<SeizeRequirement> myRequirements;

    /** Default allocation listener for single resource
     *  or single resource set requirements
     *
     */
    protected AllocationListener myAllocationListener;

    public SeizeResources(ModelElement parent) {
        this(parent, null);
    }

    public SeizeResources(ModelElement parent, String name) {
        super(parent, name);
        myAllocationListener = new AllocationListener();
    }

    /** Subclasses need to implement a mechanism
     *  to hold entities that are waiting for resources
     *
     * @param entity
     */
    abstract protected void queueEntity(Entity entity);

    /** Subclasses need to implement a mechanism to select
     *  the next entity from whatever mechanism is being used
     *  to hold the entities waiting for resources. The
     *  entity should not be removed from the underlying holding
     *  mechanism. If null is returned, then no entities
     *  can be selected
     *
     * @return
     */
    abstract protected Entity selectNextEntity();

    /** Subclasses need to implement a mechanism to remove
     *  the an entity (e.g. selectNextEntity())
     *  from whatever mechanism being used
     *  to hold the entities waiting for resources.
     *
     * @param entity the entity to be removed
     */
    abstract protected void removeEntity(Entity entity);

    @Override
    protected void receive(Entity entity) {
        queueEntity(entity);
        seizeResources(entity);
    }

    protected void seizeResources(Entity entity) {
        List<SeizeRequirement> reqList = getSeizeRequirements(entity);
        if (reqList.size() > 1) {
            AllocationSetListener ral = new AllocationSetListener();
            // create a map to hold the request, resource pairs
            Map<Request, SeizeIfc> seizes = new LinkedHashMap<Request,SeizeIfc>();
            // create all the requests, and save the seizes
            for (SeizeRequirement req : reqList) {
                Request r = req.createRequest(entity, ral);
                ral.add(r);
                SeizeIfc s = req.getResource();
                seizes.put(r, s);
            }

            // make all the seizes
            for(Request r: seizes.keySet()){
                  SeizeIfc s = seizes.get(r);
                s.seize(r);
            }
            seizes.clear();
            seizes = null;
        } else {
            SeizeRequirement req = reqList.get(0);
            Request r = req.createRequest(entity, myAllocationListener);
            SeizeIfc s = req.getResource();
            s.seize(r);
        }

    }

    /** Implements the start of using
     *  the resources by removing the entity
     *  and starting the activity
     *
     * @param entity
     */
    protected void startUsingResources(Entity entity) {
        removeEntity(entity);
        sendEntity(entity);
    }

    protected List<SeizeRequirement> getSeizeRequirements(Entity e) {
        List<SeizeRequirement> requirements = null;
        if (myReqOption == RequirementOption.DIRECT) {
            if (myRequirements == null) {
                throw new NoRequirementsSpecifiedException();
            } else {
                requirements = myRequirements;
            }
        } else if (myReqOption == RequirementOption.ENTITY) {
            requirements = e.getSeizeRequirements();
        } else if (myReqOption == RequirementOption.BY_TYPE) {
            EntityType et = e.getType();
            requirements = et.getSeizeRequirements(this);//TODO
        } else if (myReqOption == RequirementOption.NONE) {
            throw new NoRequirementsSpecifiedException();
        }
        if (requirements == null) {
            throw new NoRequirementsSpecifiedException();
        }
        if (requirements.isEmpty()) {
            throw new NoRequirementsSpecifiedException();
        }
        return requirements;
    }

    public void addSeizeRequirement(Resource r) {
        addSeizeRequirement(r, 1, Request.DEFAULT_PRIORITY, false);
    }

    public void addSeizeRequirement(Resource r, int amt) {
        addSeizeRequirement(r, amt, Request.DEFAULT_PRIORITY, false);
    }

    public void addSeizeRequirement(Resource r, int amt, boolean partialFillFlag) {
        addSeizeRequirement(r, amt, Request.DEFAULT_PRIORITY, partialFillFlag);
    }

    public void addSeizeRequirement(Resource r, int amt, int priority) {
        addSeizeRequirement(r, amt, priority, false);
    }

    public void addSeizeRequirement(Resource r, int amt, int priority,
            boolean partialFillFlag) {
        if (myRequirements == null) {
            myRequirements = new ArrayList<SeizeRequirement>();
            myReqOption = RequirementOption.DIRECT;
        }

        ResourceSeizeRequirement req = new ResourceSeizeRequirement(r, amt, priority,
                partialFillFlag);
        myRequirements.add(req);
    }

    public void addSeizeRequirement(ResourceSet set) {
        addSeizeRequirement(set, 1, Request.DEFAULT_PRIORITY, false,
                ResourceSet.CYCLICAL, null);
    }

    public void addSeizeRequirement(ResourceSet set, int amt) {
        addSeizeRequirement(set, amt, Request.DEFAULT_PRIORITY, false,
                ResourceSet.CYCLICAL, null);
    }

    public void addSeizeRequirement(ResourceSet set, int amt,
            ResourceSelectionRuleIfc rule) {
        addSeizeRequirement(set, amt, Request.DEFAULT_PRIORITY, false, rule, null);
    }

    public void addSeizeRequirement(ResourceSet set, int amt, String saveKey) {
        addSeizeRequirement(set, amt, Request.DEFAULT_PRIORITY, false,
                ResourceSet.CYCLICAL, saveKey);
    }

    public void addSeizeRequirement(ResourceSet set, int amt, int priority) {
        addSeizeRequirement(set, amt, priority, false, ResourceSet.CYCLICAL, null);
    }

    public void addSeizeRequirement(ResourceSet set, int amt, int priority,
            boolean partialFillFlag, ResourceSelectionRuleIfc rule,
            String saveKey) {
        if (myRequirements == null) {
            myRequirements = new ArrayList<SeizeRequirement>();
            myReqOption = RequirementOption.DIRECT;
        }

        ResourceSetSeizeRequirement req = new ResourceSetSeizeRequirement(set, amt, priority,
                partialFillFlag, rule, saveKey);
        myRequirements.add(req);
    }

    public RequirementOption getSeizeRequirementOption() {
        return myReqOption;
    }

    public void setSeizeRequirementOption(RequirementOption option) {
        myReqOption = option;
    }

    protected class AllocationListener implements AllocationListenerIfc {

        public void allocated(Request request) {
            if (request.isSatisfied()) {
                //System.out.println("in request satisfied");
                Entity e = request.getEntity();
                Resource r = request.getSeizedResource();
                Allocation a = r.allocate(e, request.getAmountAllocated());
                startUsingResources(e);
            }
        }
    }

    protected class AllocationSetListener implements AllocationListenerIfc {

        protected List<Request> myRequests;

        protected List<Allocation> myAllocations;

        public AllocationSetListener() {
            myRequests = new ArrayList<Request>();
            myAllocations = new ArrayList<Allocation>();
        }

        public void add(Request r) {
            if (myRequests.contains(r)){
                throw new IllegalArgumentException("The request is already in the AllocationSetListenr");
            }
            myRequests.add(r);
        }

        public void allocated(Request request) {
            Entity e = request.getEntity();
            //System.out.println("in allocated");
            if (request.isSatisfied()) {
                //System.out.println("in request satisfied");
                Resource r = request.getSeizedResource();
                Allocation a = r.allocate(e, request.getAmountAllocated());
                myRequests.remove(request);
                myAllocations.add(a);
            }

            if (myRequests.isEmpty()) {
                //System.out.println("before startUsingResources");
                startUsingResources(e);
                //myRequests = null;
            }
        }
    }
}
