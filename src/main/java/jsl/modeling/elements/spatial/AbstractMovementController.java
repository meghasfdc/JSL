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
import jsl.modeling.SchedulingElement;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.utilities.random.RandomIfc;
import jsl.utilities.random.distributions.Constant;

/**
 * An AbstractMovementController can be used to control the movements of an
 * AbstractMover
 *
 *
 */
public abstract class AbstractMovementController extends SchedulingElement implements MovementControllerIfc {

    /**
     * The velocity addFactor, default is 1. If this is changed, it is changed for
     * all replications
     */
    protected double myVelFactor;

    protected RandomVariable myVelocity;

    /**
     * @param parent
     */
    public AbstractMovementController(ModelElement parent) {
        this(parent, null);
    }

    /**
     * @param parent
     * @param name
     */
    public AbstractMovementController(ModelElement parent, String name) {
        super(parent, name);
        myVelFactor = 1.0;
        myVelocity = new RandomVariable(this, Constant.ONE);
    }

    @Override
    abstract public void controlMovement(AbstractMover movingElement);

    @Override
    public final void setVelocityChangeFactor(double factor) {
        if (factor <= 0.0) {
            throw new IllegalArgumentException("The velocity addFactor must be > 0.");
        }
        myVelFactor = factor;
    }

    @Override
    public final double getVelocityChangeFactor() {
        return myVelFactor;
    }

    /**
     * The velocity for an individual movement
     *
     * @return
     */
    @Override
    public double getVelocity() {
        return myVelocity.getValue() * getVelocityChangeFactor();
    }

    /**
     * @return Returns the velocity.
     */
    @Override
    public final RandomIfc getVelocityInitialRandomSource() {
        return myVelocity.getInitialRandomSource();
    }

    /**
     * @param velocity The velocity to set.
     */
    @Override
    public final void setVelocityInitialRandomSource(RandomIfc velocity) {
        myVelocity.setInitialRandomSource(velocity);
    }

    /**
     * @return Returns the velocity.
     */
    @Override
    public final RandomIfc getVelocityRandomSource() {
        return myVelocity.getRandomSource();
    }

    /**
     * @param velocity The velocity to set.
     */
    @Override
    public final void setVelocityRandomSource(RandomIfc velocity) {
        myVelocity.setRandomSource(velocity);
    }

}
