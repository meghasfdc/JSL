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

/**
 *
 */
public class Mover extends AbstractMover {

    /**
     * Creates a Mover with the default position within its spatial model. The
     * spatial model of the parent is used as the spatial model of this object.
     * If the parent does not have a spatial model (i.e. getSpatialModel() ==
     * null), then an IllegalArgumentException is thrown
     *
     * @param parent
     */
    public Mover(ModelElement parent) {
        this(parent, null, null, null);
    }

    /**
     * Creates a Mover with the default position within its spatial model. The
     * spatial model of the parent is used as the spatial model of this object.
     * If the parent does not have a spatial model (i.e. getSpatialModel() ==
     * null), then an IllegalArgumentException is thrown
     *
     * @param parent
     * @param name
     */
    public Mover(ModelElement parent, String name) {
        this(parent, name, null, null);
    }

    /**
     * Creates a Mover at the given coordinate within its spatial model. The
     * spatial model of the parent is used as the spatial model of this object.
     * If the parent does not have a spatial model (i.e. getSpatialModel() ==
     * null), then an IllegalArgumentException is thrown
     *
     * @param parent
     * @param coordinate
     */
    public Mover(ModelElement parent, CoordinateIfc coordinate) {
        this(parent, null, null, coordinate);
    }

    /**
     * Creates a Mover with the default position within the given spatial model.
     * If the supplied spatial model is null the spatial model of the parent is
     * used as the spatial model of this object. If the parent does not have a
     * spatial model (i.e. getSpatialModel() == null), then an
     * IllegalArgumentException is thrown
     *
     * @param parent
     * @param name
     * @param spatialModel
     */
    public Mover(ModelElement parent, String name, SpatialModel spatialModel) {
        this(parent, name, spatialModel, null);
    }

    /**
     * Creates a Mover with the default position within the given spatial model.
     * If the supplied spatial model is null the spatial model of the parent is
     * used as the spatial model of this object. If the parent does not have a
     * spatial model (i.e. getSpatialModel() == null), then an
     * IllegalArgumentException is thrown
     *
     * @param parent
     * @param spatialModel
     */
    public Mover(ModelElement parent, SpatialModel spatialModel) {
        this(parent, null, spatialModel, null);
    }

    /**
     * Creates a Mover with the given coordinates within the given spatial
     * model. If the supplied spatial model is null the spatial model of the
     * parent is used as the spatial model of this object. If the parent does
     * not have a spatial model (i.e. getSpatialModel() == null), then an
     * IllegalArgumentException is thrown
     *
     * @param parent
     * @param name
     * @param spatialModel
     * @param coordinate
     */
    public Mover(ModelElement parent, String name, SpatialModel spatialModel, CoordinateIfc coordinate) {
        super(parent, name, spatialModel, coordinate);
    }

    /**
     * Causes the element to travel from its current position to the coordinates
     * specified. This starts a trip. A trip is a series of movements to move
     * from the current position to the specified coordinates. At the beginning
     * of a trip, trip start observers are notified. If the trip is broken down
     * into movements, then each movement can have its own velocity, distance,
     * and direction (as long as the final movement ends at the destination). If
     * the destination is the same as the current position of the element then
     * no trip is started and no movement occurs, i.e. nothing occurs
     *
     * If the specified coordinates are not in the element's associated spatial
     * model then outsideSpatialModelHandler() is called. The default action is
     * to throw an exception but this can be overridden in
     * outsideSpatialModelHandler().
     *
     * @param destination Must not be null
     */
    public final void travelTo(CoordinateIfc destination) {
        moveTo(destination);
    }

}
