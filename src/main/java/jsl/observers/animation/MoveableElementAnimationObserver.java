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
package jsl.observers.animation;

import jsl.modeling.ModelElement;
import jsl.modeling.elements.spatial.CoordinateIfc;
import jsl.modeling.elements.spatial.SpatialElementIfc;

public class MoveableElementAnimationObserver extends AnimationObserverAbstract {

    public MoveableElementAnimationObserver(AnimationMessageHandlerIfc generator) {
        this(null, generator);
    }

    public MoveableElementAnimationObserver(String name, AnimationMessageHandlerIfc generator) {
        super(name, generator);

    }

    @Override
    protected void initialize(ModelElement m, Object arg) {
        buildSpatialElementMessage(m, "INITIALIZE");
    }

    /**
     * @param m
     * @param arg
     */
    @Override
    protected void moveEnded(ModelElement m, Object arg) {
        buildSpatialElementMessage(m, "MOVE_ENDED");
    }

    /**
     * @param m
     * @param arg
     */
    @Override
    protected void moveStarted(ModelElement m, Object arg) {
//		buildSpatialElementMessage(m, "MOVE_STARTED");	
    }

    @Override
    protected void tripEnded(ModelElement m, Object arg) {
//		buildSpatialElementMessage(m, "TRIP_ENDED");	
    }

    @Override
    protected void tripStarted(ModelElement m, Object arg) {
//		buildSpatialElementMessage(m, "TRIP_STARTED");			
    }

//	protected void update(ModelElement m, Object arg) {
//			buildSpatialElementMessage(m, "MOVE_ENDED");	
//	}
    protected void buildSpatialElementMessage(ModelElement element, String message) {

        buildStandardModelElementMessage(element);

        SpatialElementIfc se = (SpatialElementIfc) element;

        CoordinateIfc p = se.getPreviousPosition();
        CoordinateIfc c = se.getPosition();

        myAnimationMessageHandler.append(message);
        myAnimationMessageHandler.append("\t");
        myAnimationMessageHandler.append(p.getX1());
        myAnimationMessageHandler.append("\t");
        myAnimationMessageHandler.append(p.getX2());
        myAnimationMessageHandler.append("\t");
        myAnimationMessageHandler.append(c.getX1());
        myAnimationMessageHandler.append("\t");
        myAnimationMessageHandler.append(c.getX2());
    }

}
