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
public class Allocation {

    /**
     * The entity holding the allocation
     */
    private Entity myEntity;

    /**
     * The current resource that the units of the allocation are
     * associated with
     */
    private Resource myAllocatedResource;

    /**
     * The amount of resource allocated
     */
    private int myAmountAllocated = 0;

    protected Allocation(Entity entity, Resource resource) {
        setEntity(entity);
        setAllocatedResource(resource);
        myEntity.addAllocation(this);
    }

    public final void nullify() {
        myAllocatedResource = null;
        myEntity = null;
    }

    protected final void setEntity(Entity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("The supplied entity was null");
        }
        myEntity = entity;
    }

    protected final void setAllocatedResource(Resource resource) {
        if (resource == null) {
            throw new IllegalArgumentException("The supplied resource was null");
        }
        myAllocatedResource = resource;
    }

    protected final void increaseAllocation(int amountAllocated) {
        if (amountAllocated <= 0) {
            throw new IllegalArgumentException("Amount of increase in allocation was less or equal to zero!");
        }

        myAmountAllocated = myAmountAllocated + amountAllocated;
    }

    protected final void decreaseAllocation(int amountOfDecrease) {
        if (amountOfDecrease <= 0) {
            throw new IllegalArgumentException("Amount of decrease in allocation was less or equal to zero!");
        }

        myAmountAllocated = myAmountAllocated - amountOfDecrease;
    }

//    public final void releaseResource(){
//        myAllocatedResource.release(this);
//    }

    /** The current amount allocated
     * 
     * @return
     */
    public final int getAmountAllocated() {
        return myAmountAllocated;
    }

    /** Returns true if there are units allocated
     *
     * @return
     */
    public final boolean isAllocated() {
        return (myAmountAllocated > 0);
    }

    /** Returns true if there are no units allocated
     *
     * @return
     */
    public final boolean isDeallocated() {
        return (myAmountAllocated == 0);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Entity ");
        sb.append(myEntity.getId());
        sb.append(" holds ");
        sb.append(getAmountAllocated());
        sb.append(" units of resource ");
        sb.append(myAllocatedResource.getName());
        return sb.toString();
    }

    /** The Entity associated with the allocation
     *
     * @return
     */
    public final Entity getEntity() {
        return myEntity;
    }

    /** Gets the resource that is associated with the allocation
     *
     * @return The resource
     */
    public final Resource getAllocatedResource() {
        return (myAllocatedResource);
    }
}
