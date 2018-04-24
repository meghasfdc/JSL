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

import java.util.Observer;

import jsl.modeling.ModelElement;
import jsl.utilities.IdentityIfc;

public interface SpatialElementIfc extends IdentityIfc, PositionIfc {

    /**
     * @return Returns the currentPosition.
     */
    @Override
    public abstract CoordinateIfc getPosition();

    /**
     * @return Returns the initialPosition.
     */
    public abstract CoordinateIfc getInitialPosition();

    /** Sets the initial position of the element.  This position should
     *  be used when initializeSpatialElement() is called, typically at the
     *  beginning of a replication.
     *
     * @param coordinate
     */
    public abstract void setInitialPosition(CoordinateIfc coordinate);

    /**
     * @return Returns the previousPosition.
     */
    public abstract CoordinateIfc getPreviousPosition();

    /** Observers can call this to get an integer representing the
     *  state of the element after the observers have been notified
     *
     * @return
     */
    public abstract int getObserverState();

    /** This is a "convenience" method for getting the distance
     *  from this element to the supplied coordinate within
     *  the underlying spatial model
     *
     * @param coordinate
     * @return
     */
    public abstract double distanceTo(CoordinateIfc coordinate);

    /** Returns true if the position of this element is the
     *  same as supplied coordinate within the underlying spatial
     *  model.  This is not necessarily object reference equality, but rather
     *  whether or not the positions within the underlying spatial model
     *  can be considered the same (equivalent).
     *
     *
     * @param coordinate
     * @return
     */
    public abstract boolean isPositionEqualTo(CoordinateIfc coordinate);

    /** This is a "convenience" method for getting the distance
     *  from this element to the supplied element within
     *  the underlying spatial model
     *
     *  Requirement: The elements must be in the same spatial model.
     *  The distance should be calculated by the spatial model.
     *  If they are not in the same spatial model this method will
     *  throw and IllegalArgumentException
     *
     * @param element
     * @return
     */
    public abstract double distanceTo(SpatialElementIfc element);

    /** Returns true if the position of this element is the
     *  same as the position of the supplied element within the underlying spatial
     *  model.  This is not necessarily object reference equality, but rather
     *  whether or not the positions within the underlying spatial model
     *  can be considered the same (equivalent).
     *
     *  Requirement: The elements must be in the same spatial model. If
     *  they are not in the same spatial model, then this method should
     *  return false.
     *
     * @param element
     * @return
     */
    public abstract boolean isPositionEqualTo(SpatialElementIfc element);

    /** Returns the current spatial model that contains this
     *  element
     *
     * @return
     */
    public abstract SpatialModel getSpatialModel();

    /** Returns the spatial model that should hold this element
     *  at the beginning of each replication of a simulation
     *
     * @return
     */
    public abstract SpatialModel getInitialSpatialModel();

    /** Changes the spatial model for this element and places the element at the supplied
     *  coordinate within the new spatial model.
     *
     *  Throws IllegalArgumentException if the coordinate is not valid for the
     *  supplied spatial model.
     *
     *  This spatial element becomes a child element of the new spatial model.
     *
     * @param spatialModel
     * @param coordinate
     */
    public abstract void changeSpatialModel(SpatialModel spatialModel,
            CoordinateIfc coordinate);

    /** Gets the ModelElement associated with this spatial element
     *  May be null
     *
     * @return
     */
    public abstract ModelElement getModelElement();

//    /** Sets the model element associated with this spatial element if available
//     *
//     * @param modelElement
//     */
////	public abstract void setModelElement(ModelElement modelElement);
    /** This method should be called to initialize the spatial element prior
     *  to running a simulation
     *
     *
     */
    public abstract void initializeSpatialElement();

    /** Implementor of this interface should allow Observers to
     *  be attached.  For example, the observers should be notified
     *  when the position changes.  It is the responsibility of implementers
     *  to properly notify the observers.
     *
     * @param observer
     */
    public abstract void attachPositionObserver(Observer observer);

    /** Remove the observer from this PositionIfc
     *
     * @param observer
     */
    public abstract void removePositionObserver(Observer observer);
}
