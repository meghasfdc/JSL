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

import java.util.Iterator;
import java.util.List;

/**
 *
 * @author rossetti
 */
public class CyclicResourceSelectionRule implements ResourceSelectionRuleIfc {

    /** Returns the next available resource to be used for allocating
     *  to requests, null if none are found that can satisfy the request.
     *
     *  The default selection method is to cycle through the resources in the
     *  order in which they were RELEASED.
     *
     *  The idle resource set starts in the order the resources were added to the
     *  resource set and assumes that the first resource listed was the resource that
     *  was allocated the longest time in the past, and so on.  For example,
     *  if the set of idle resources has the following list:
     *
     *  Resource B
     *  Resource A
     *  Resource C
     *
     *  Then C was the most recently used, resource A the next most recently used, and
     *  resource B the oldest used.  Resource B will be next allocated, if it can
     *  satisfy the amount of the request. If not, Resource A will be checked, and so forth
     *  until a resource that can satisfy the request is found. Thus, it is entirely
     *  possible that B will not be the next recommended resource. The selected resource
     *  is added to the end of the list after it is released.
     *
     *  Note that the the order can vary with this list depending on the order in which the resources are
     *  released.  To provide alternative behavior either override
     *  this method, or provide a ResourceSelectionRuleIfc
     *
     *  The default behavior is to return the first available resource
     *  that can fully supply the amount needed
     *
     * @param amtNeeded
     * @return
     */
    public Resource selectAvailableResource(List<Resource> list, int amtNeeded) {
        Resource found = null;

        if (!list.isEmpty()) {
            Iterator<Resource> iter = list.iterator();
            Resource r = null;
            int amt = amtNeeded;
            while (iter.hasNext()) {
                r = iter.next();
                if (amt <= r.getNumberAvailable()) {
                    found = r;
                    break;
                }
            }
        }
        return (found);
    }

    public void addAvailableResource(List<Resource> list, Resource resource) {
        if (!list.contains(resource)) {
            list.add(resource);
        }
    }

    /** Selects an available resource or null if none are available
     *  The default is to find the first available resource
     *  that has the maximum available units. To change this
     *  either override this method or supply a ResourceSelectionRuleIfc
     *
     * @return
     */
    public Resource selectAvailableResource(List<Resource> list) {
        // the default is the resource in the idle resource list
        // that has the max available units
        int max = Integer.MIN_VALUE;
        Resource rmax = null;
        for (Resource r : list) {
            if (r.getNumberAvailable() > max) {
                rmax = r;
            }
        }
        return (rmax);
    }
}
