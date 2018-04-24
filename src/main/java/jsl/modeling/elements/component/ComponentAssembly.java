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

import java.util.LinkedList;
import java.util.List;

import jsl.modeling.ModelElement;

public class ComponentAssembly extends ComponentStateChangeListener {

    protected List<Component> myComponents;

    public ComponentAssembly(ModelElement parent) {
        this(parent, null);
    }

    public ComponentAssembly(ModelElement parent, String name) {
        super(parent, name);
        myComponents = new LinkedList<Component>();
    }

    /**
     * Adds the component to the assembly
     *
     * @param component, must not be null and must not be already part of
     * another assembly
     */
    public void addComponent(Component component) {

        if (component == null) {
            throw new IllegalArgumentException("The component was null!");
        }

        if (component.getAssembly() != null) {
            throw new IllegalArgumentException("The component is already part of an assembly.");
        }

        // add the component
        myComponents.add(component);
        component.setAssembly(this);
    }

    /**
     * Checks if the assembly contains the supplied component
     *
     * @param component
     * @return
     */
    public boolean contains(Component component) {
        return myComponents.contains(component);
    }

    /**
     * Removes the component from the assembly. It must be part of the assembly.
     *
     * @param component, must not be null
     */
    public void removeComponent(Component component) {
        if (component == null) {
            throw new IllegalArgumentException("The component was null!");
        }

        if (component.getAssembly() != this) {
            throw new IllegalArgumentException("The component is not part of this assembly.");
        }

        myComponents.remove(component);
        component.setAssembly(null);
    }

}
