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
package jsl.modeling.elements.processview.description.commands;

import jsl.modeling.ModelElement;
import jsl.modeling.elements.processview.description.ProcessCommand;
import jsl.modeling.elements.resource.Entity;
import jsl.modeling.elements.variable.ResponseVariable;

/**
 */
public class Terminate extends ProcessCommand {

	protected ResponseVariable myProcessTime;
	
	/**
	 * @param parent
	 */
	public Terminate(ModelElement parent) {
		this(parent, false, null);
	}
	
	/**
	 * @param parent
	 * @param name
	 */
	public Terminate(ModelElement parent, String name) {
		this(parent, false, name);
	}

	/**
	 * @param parent
	 * @param name
	 */
	public Terminate(ModelElement parent, boolean collectEntityTimeFlag, String name) {
		super(parent, name);
		
		if (collectEntityTimeFlag == true)
			myProcessTime = new ResponseVariable(this, getName() + " Entity Flow Time");
	}
	
	/* (non-Javadoc)
	 * @see jsl.modeling.elements.processview.description.ProcessCommand#execute(jsl.modeling.elements.processview.description.ProcessExecutor)
	 */
	public final void execute() {
		if (myProcessTime != null){
			// get the entity that is terminating
			Entity entity = getProcessExecutor().getCurrentEntity();
			myProcessTime.setValue(getTime() - entity.getCreateTime());
		}
		getProcessExecutor().terminate();
	}

}
