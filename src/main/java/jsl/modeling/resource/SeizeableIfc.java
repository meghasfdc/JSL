/*
 *  Copyright (C) 2017 rossetti
 * 
 *  Contact:
 * 	Manuel D. Rossetti, Ph.D., P.E.
 * 	Department of Industrial Engineering
 * 	University of Arkansas
 * 	4207 Bell Engineering Center
 * 	Fayetteville, AR 72701
 * 	Phone: (479) 575-6756
 * 	Email: rossetti@uark.edu
 * 	Web: www.uark.edu/~rossetti
 * 
 *  This file is part of the JSL (a Java Simulation Library). The JSL is a framework
 *  of Java classes that permit the easy development and execution of discrete event
 *  simulation programs.
 * 
 *  The JSL is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 * 
 *  The JSL is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jsl.modeling.resource;

/**
 *
 * @author rossetti
 */
public interface SeizeableIfc {

    /**
     * Causes the request to enter the resource. If the resource is idle, the
     * request will be using the resource. If the resource is not idle the
     * request will wait. A Request will be rejected if its preemption rule is
     * NONE and the
     * ResourceUnit's failure delay option is false. This implies that the
     * Request cannot be processed by the ResourceUnit because the request
     * cannot be preempted and the resource unit does not permit its failures to
     * delay (i.e. they must preempt).
     *
     * @param request a request made by this unit
     * @return the request is returned to emphasize that the user may want to
     * check its state
     */
    Request seize(Request request);
    
}
