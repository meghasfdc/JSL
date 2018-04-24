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

package jsl.modeling.elements;

import jsl.modeling.JSLEvent;

/**  This interface defines the action to occur for an EventGenerator.
 *  Implementors place code in the generate() method in order to 
 *  provide actions that can occur when the event that was generated
 *  is executed.
 * 
 * @author rossetti
 */
@FunctionalInterface
public interface EventGeneratorActionIfc {
    
    /** The reference to the generator is available to permit control over the
     *  EventGenerator within the defining code.  The event is also available.
     * 
     * @param generator the generator
     * @param event the event
     */
    public void generate(EventGenerator generator, JSLEvent event);
}

