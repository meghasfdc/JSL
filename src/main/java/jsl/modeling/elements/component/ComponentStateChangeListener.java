/*
* Copyright (c) 2007, Manuel D. Rossetti (rossetti@uark.edu)
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
package jsl.modeling.elements.component;

import jsl.modeling.ModelElement;
import jsl.modeling.SchedulingElement;

/**
 * A component state change listener can be attached to a component and will
 * have it's stateChange() method called at each state change of the component.
 *
 *
 */
abstract public class ComponentStateChangeListener extends SchedulingElement implements ComponentStateChangeListenerIfc {

    /**
     * Creates a component state change listener
     *
     * @param parent
     */
    public ComponentStateChangeListener(ModelElement parent) {
        this(parent, null);
    }

    /**
     * Creates a component state change listener
     *
     * @param parent
     * @param name
     */
    public ComponentStateChangeListener(ModelElement parent, String name) {
        super(parent, name);
    }

    @Override
    public void stateChange(Component c) {

        if (c.isAvailable()) {
            componentAvailable(c);
            if (c.isPreviousState(c.getCreatedState())) {
                // available after being created
                componentAvailableAfterCreation(c);
            } else if (c.isPreviousState(c.getDeactivatedState())) {
                // available after being deactivated
                componentAvailableAfterUnavailable(c);
            } else if (c.isPreviousState(c.getRepairingState())) {
                // available after being repaired
                componentAvailableAfterRepair(c);
            } else if (c.isPreviousState(c.getOperatingState())) {
                componentAvailableAfterOperating(c);
            }
        } else if (c.isOperating()) {
            componentStartedOperating(c);
        } else if (c.isFailed()) {
            componentFailed(c);
        } else if (c.isInRepair()) {
            componentStartedRepair(c);
        } else if (c.isUnavailable()) {
            componentUnavailable(c);
        }

    }

    /**
     * Called when the component transitions into the available state from any
     * other legal state
     *
     * @param c
     */
    protected void componentAvailable(Component c) {

    }

    /**
     * Called after componentAvailable() but only when the component enters from
     * the created state
     *
     * @param c
     */
    protected void componentAvailableAfterCreation(Component c) {

    }

    /**
     * Called after componentAvailable() but only when the component enters from
     * the repairing state
     *
     * @param c
     */
    protected void componentAvailableAfterRepair(Component c) {

    }

    /**
     * Called after componentAvailable() but only when the component enters from
     * the unavailable state
     *
     * @param c
     */
    protected void componentAvailableAfterUnavailable(Component c) {

    }

    /**
     * Called after componentAvailable() but only when the component enters from
     * the operating state
     *
     * @param c
     */
    protected void componentAvailableAfterOperating(Component c) {

    }

    /**
     * Called when the component transitions into the operating state
     *
     * @param c
     */
    protected void componentStartedOperating(Component c) {

    }

    /**
     * Called when the component transitions into the unavailable state
     *
     * @param c
     */
    protected void componentUnavailable(Component c) {

    }

    /**
     * Called when the component transitions into the failed state
     *
     * @param c
     */
    protected void componentFailed(Component c) {

    }

    /**
     * Called when the component transitions into the repairing state
     *
     * @param c
     */
    protected void componentStartedRepair(Component c) {

    }

}
