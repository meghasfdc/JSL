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
import jsl.utilities.random.*;
import jsl.utilities.random.distributions.DistributionIfc;
import jsl.utilities.random.distributions.Triangular;

/**
 *
 */
public class EuclideanStepBasedMovementController extends AbstractMovementController {

    private Vector3D myPosition;

    private Vector3D myDestination;

    private Vector3D myFuturePosition;

    private Vector3D myDirection;

    /* The following are based on human walking speed, in meters/min
    * This assumes a standard walking speed of about 1.3 meters/sec
    * 2 meters/sec is considered very fast, humans begin running at 3m/seco
     */
    private final double myVelocityMin = 53.6448;

    private final double myVelocityMode = 80.4672;

    private final double myVelocityMax = 107.2896;

    /* The following is based on the typical length of a stride
	 * 30 inches or .762 meters of a human
     */
    private final double myStepSizeMin = 0.66;

    private final double myStepSizeMode = 0.762;

    private final double myStepSizeMax = 0.79;

    private RandomVariable myStepSize;

    /**
     * @param parent
     */
    public EuclideanStepBasedMovementController(ModelElement parent) {
        this(parent, null);
    }

    /**
     * @param parent
     * @param name
     */
    public EuclideanStepBasedMovementController(ModelElement parent, String name) {
        super(parent, name);
        DistributionIfc velocityCDF = new Triangular(myVelocityMin, myVelocityMode, myVelocityMax);
        setVelocityInitialRandomSource(velocityCDF);
        DistributionIfc stepSizeCDF = new Triangular(myStepSizeMin, myStepSizeMode, myStepSizeMax);
        myStepSize = new RandomVariable(this, stepSizeCDF);
        myPosition = new Vector3D();
        myDestination = new Vector3D();
        myFuturePosition = new Vector3D();
        myDirection = new Vector3D();

    }

    @Override
    public void controlMovement(AbstractMover movingElement) {
        // get current position
        myPosition.setCoordinates(movingElement.getPosition());

        // get destination
        myDestination.setCoordinates(movingElement.getDestination());

        // the vector from A to B can be obtained by
        // substracting the position vector a of point A from
        // the position vector b of point B
        // set direction vector equal to next position's coordinates
        myDirection.setCoordinates(myDestination);

        // subtract the current position, resulting in a vector from A to B
        myDirection.subtract(myPosition);

        // normalize the vector for direction
        myDirection.normalize();

        // get the spatial model
        SpatialModel sm = movingElement.getSpatialModel();

        // get remaining distance to destination
        double d = sm.distance(myPosition, myDestination);

        // get the current size of the step
        double x = myStepSize.getValue();

        double step = Math.min(x, d);

        // clear the future position
        myFuturePosition.setCoordinates(0.0, 0.0, 0.0);
        // set the direction of travel, direction is a unit vector
        myFuturePosition.setCoordinates(myDirection);
        // set the distance, in the direction of travel
        myFuturePosition.multiply(step);
        // add the current position to get the future position
        myFuturePosition.add(myPosition);

        // get the velocity
        double velocity = getVelocity();
        // set up the movement on the moving element
        movingElement.setMovement(velocity, myFuturePosition);

    }

    /**
     * @return Returns the stepSize.
     */
    public final RandomIfc getStepSizeInitialRandomSource() {
        return myStepSize.getInitialRandomSource();
    }

    /**
     * @param stepSize The stepSize to set.
     */
    public final void setStepSizeInitialRandomSource(RandomIfc stepSize) {
        myStepSize.setInitialRandomSource(stepSize);
    }

    /**
     * @return Returns the stepSize.
     */
    public final RandomIfc getStepSizeRandomSource() {
        return myStepSize.getRandomSource();
    }

    /**
     * @param stepSize The stepSize to set.
     */
    public final void setStepSizeRandomSource(RandomIfc stepSize) {
        myStepSize.setRandomSource(stepSize);
    }

}
