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
package jsl.modeling.elements.spatial;

import jsl.modeling.ModelElement;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.utilities.random.RandomIfc;
import jsl.utilities.random.distributions.Uniform;

/**
 *
 */
public class RandomMover extends AbstractMover {

    private RandomVariable myTripDestinationX;
    private RandomVariable myTripDestinationY;
    private Vector3D myDestination;

    /**
     *
     * @param parent
     * @param smodel
     */
    public RandomMover(ModelElement parent, SpatialModel smodel) {
        this(parent, null, smodel);
    }

    /**
     *
     * @param parent
     * @param name
     * @param smodel
     */
    public RandomMover(ModelElement parent, String name, SpatialModel smodel) {
        super(parent, name, smodel);
        myTripDestinationX = new RandomVariable(this, new Uniform(2, 10));
        myTripDestinationY = new RandomVariable(this, new Uniform(3, 10));
        myDestination = new Vector3D();
        //MovementControllerIfc c = new EuclideanStepBasedMovementController(this);
        // setMovementController(c);
    }

    public final void setXDestinationInitialRandomSource(RandomIfc source) {
        myTripDestinationX.setInitialRandomSource(source);
    }

    public final void setYDestinationInitialRandomSource(RandomIfc source) {
        myTripDestinationY.setInitialRandomSource(source);
    }

    @Override
    protected void initialize() {
        super.initialize();
        double x = myTripDestinationX.getValue();
        double y = myTripDestinationY.getValue();
        myDestination.setCoordinates(x, y);
        moveTo(myDestination);
    }

    @Override
    protected void startNextTrip() {
        double x = myTripDestinationX.getValue();
        double y = myTripDestinationY.getValue();
        myDestination.setCoordinates(x, y);
        moveTo(myDestination);
    }

}
