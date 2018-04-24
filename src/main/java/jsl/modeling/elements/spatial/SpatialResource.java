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
import jsl.modeling.elements.resource.Resource;
import jsl.utilities.reporting.JSL;

/**
 * A SpatialResource is a resource that can be placed/positioned within a
 * SpatialModel
 *
 * It is not "self-moving" but can be positioned by clients at various locations
 * (coordinates, etc) within the spatial model. A SpatialResource can be
 * assigned a "home location" via the ResourceLocation class
 *
 * In all other respects a SpatialResource acts like a Resource.
 *
 */
public class SpatialResource extends Resource implements SpatialElementIfc {

    /**
     * Indicates that the transporter has changed state to its observers
     */
    public static final int MOVED = JSL.getNextEnumConstant();

    /**
     * Used to respresent the resources in a SpatialModel2D
     *
     */
    private SpatialElement mySpatialElement;

    /**
     * If a SpatialResource is assigned to a ResourceLocation then this field is
     * used to refer to the location. The default is null (not assigned to a
     * location).
     */
    private ResourceLocation myResourceLocation;

    /**
     * If a SpatialResource is assigned to a ResourceLocation then this field is
     * used to remember the location for when the SpatialResource is initialized
     * prior to a replication. The default is null (not assigned to a location).
     */
    private ResourceLocation myInitialResourceLocation;

    /**
     * Creates a SpatialResource with the default position within its spatial
     * model. The spatial model of the parent is used as the spatial model of
     * this object. If the parent does not have a spatial model (i.e.
     * getSpatialModel() == null), then an IllegalArgumentException is thrown
     *
     * @param parent
     */
    public SpatialResource(ModelElement parent) {
        this(parent, 1, null, null, null);
    }

    /**
     * Creates a SpatialResource with the given capacity at the default position
     * within its spatial model. The spatial model of the parent is used as the
     * spatial model of this object. If the parent does not have a spatial model
     * (i.e. getSpatialModel() == null), then an IllegalArgumentException is
     * thrown
     *
     * @param parent
     * @param capacity
     */
    public SpatialResource(ModelElement parent, int capacity) {
        this(parent, capacity, null, null, null);
    }

    /**
     * Creates a SpatialResource with the given capacity at the default position
     * within its spatial model. The spatial model of the parent is used as the
     * spatial model of this object. If the parent does not have a spatial model
     * (i.e. getSpatialModel() == null), then an IllegalArgumentException is
     * thrown
     *
     * @param parent
     * @param capacity
     * @param name
     */
    public SpatialResource(ModelElement parent, int capacity, String name) {
        this(parent, capacity, name, null, null);
    }

    /**
     * Creates a SpatialResource with the given capacity at (x,y) within its
     * spatial model. The spatial model of the parent is used as the spatial
     * model of this object. If the parent does not have a spatial model (i.e.
     * getSpatialModel() == null), then an IllegalArgumentException is thrown
     *
     * @param parent
     * @param name
     * @param x
     * @param y
     */
    public SpatialResource(ModelElement parent, String name, double x, double y) {
        this(parent, 1, name, null, x, y, 0.0);
    }

    /**
     * Creates a SpatialResource with the given capacity at (x,y) within its
     * spatial model. The spatial model of the parent is used as the spatial
     * model of this object. If the parent does not have a spatial model (i.e.
     * getSpatialModel() == null), then an IllegalArgumentException is thrown
     *
     * @param parent
     * @param capacity
     * @param name
     * @param x
     * @param y
     */
    public SpatialResource(ModelElement parent, int capacity, String name, double x, double y) {
        this(parent, capacity, name, null, x, y, 0.0);
    }

    /**
     * Creates a SpatialResource with capacity 1 at the given coordinate within
     * its spatial model. The spatial model of the parent is used as the spatial
     * model of this object. If the parent does not have a spatial model (i.e.
     * getSpatialModel() == null), then an IllegalArgumentException is thrown
     *
     * @param parent
     * @param name
     * @param position
     */
    public SpatialResource(ModelElement parent, String name, CoordinateIfc position) {
        this(parent, 1, name, null, position);
    }

    /**
     * Creates a SpatialResource with capacity 1 at the coordinates of the
     * supplied spatial element within its spatial model. The spatial model of
     * the parent is used as the spatial model of this object. If the parent
     * does not have a spatial model (i.e. getSpatialModel() == null), then an
     * IllegalArgumentException is thrown
     *
     * @param parent
     * @param name
     * @param position
     */
    public SpatialResource(ModelElement parent, String name, SpatialElementIfc position) {
        this(parent, 1, name, null, position.getPosition());
    }

    /**
     * Creates a SpatialResource with the given capacity at the given coordinate
     * within its spatial model. The spatial model of the parent is used as the
     * spatial model of this object. If the parent does not have a spatial model
     * (i.e. getSpatialModel() == null), then an IllegalArgumentException is
     * thrown
     *
     * @param parent
     * @param capacity
     * @param name
     * @param position
     */
    public SpatialResource(ModelElement parent, int capacity, String name, CoordinateIfc position) {
        this(parent, capacity, name, null, position);
    }

    /**
     * Creates a SpatialResource with capacity 1 at (x,y) within its spatial
     * model. The spatial model of the parent is used as the spatial model of
     * this object. If the parent does not have a spatial model (i.e.
     * getSpatialModel() == null), then an IllegalArgumentException is thrown
     *
     * @param parent
     * @param x
     * @param y
     */
    public SpatialResource(ModelElement parent, double x, double y) {
        this(parent, 1, null, null, x, y, 0.0);
    }

    /**
     * Creates a SpatialResource with capacity 1 at the given coordinate within
     * its spatial model. The spatial model of the parent is used as the spatial
     * model of this object. If the parent does not have a spatial model (i.e.
     * getSpatialModel() == null), then an IllegalArgumentException is thrown
     *
     * @param parent
     * @param position
     */
    public SpatialResource(ModelElement parent, CoordinateIfc position) {
        this(parent, 1, null, null, position);
    }

    /**
     * Creates a SpatialResource with the given capacity at the given coordinate
     * within its spatial model. The spatial model of the parent is used as the
     * spatial model of this object. If the parent does not have a spatial model
     * (i.e. getSpatialModel() == null), then an IllegalArgumentException is
     * thrown
     *
     * @param parent
     * @param capacity
     * @param position
     */
    public SpatialResource(ModelElement parent, int capacity, CoordinateIfc position) {
        this(parent, capacity, null, null, position);
    }

    /**
     * Creates a SpatialResource with the given capacity at (x,y) within its
     * spatial model. The spatial model of the parent is used as the spatial
     * model of this object. If the parent does not have a spatial model (i.e.
     * getSpatialModel() == null), then an IllegalArgumentException is thrown
     *
     * @param parent
     * @param capacity
     * @param x
     * @param y
     */
    public SpatialResource(ModelElement parent, int capacity, double x, double y) {
        this(parent, capacity, null, null, x, y, 0.0);
    }

    /**
     * Creates a SpatialResource with the given capacity at the default position
     * within the spatial model. If the spatial model is null then the spatial
     * model of the parent is used as the spatial model of this object. If the
     * parent does not have a spatial model (i.e. getSpatialModel() == null),
     * then an IllegalArgumentException is thrown
     *
     * @param parent
     * @param capacity
     * @param name
     * @param spatialModel
     */
    public SpatialResource(ModelElement parent, int capacity, String name, SpatialModel spatialModel) {
        this(parent, capacity, name, spatialModel, null);
    }

    /**
     * Creates a SpatialResource with the given capacity at the default position
     * within the spatial model. If the spatial model is null then the spatial
     * model of the parent is used as the spatial model of this object. If the
     * parent does not have a spatial model (i.e. getSpatialModel() == null),
     * then an IllegalArgumentException is thrown
     *
     * @param parent
     * @param capacity
     * @param spatialModel
     */
    public SpatialResource(ModelElement parent, int capacity, SpatialModel spatialModel) {
        this(parent, capacity, null, spatialModel, null);
    }

    /**
     * Creates a SpatialResource with the given capacity at (x,y,z) within the
     * spatial model. If the spatial model is null then the spatial model of the
     * parent is used as the spatial model of this object. If the parent does
     * not have a spatial model (i.e. getSpatialModel() == null), then an
     * IllegalArgumentException is thrown
     *
     * @param parent
     * @param capacity
     * @param name
     * @param spatialModel
     * @param x
     * @param y
     * @param z
     */
    public SpatialResource(ModelElement parent, int capacity, String name, SpatialModel spatialModel, double x, double y, double z) {
        this(parent, capacity, name, spatialModel, new Vector3D(x, y, z));
    }

    /**
     * Creates a SpatialResource with the given capacity at the default position
     * within the spatial model. If the spatial model is null then the spatial
     * model of the parent is used as the spatial model of this object. If the
     * parent does not have a spatial model (i.e. getSpatialModel() == null),
     * then an IllegalArgumentException is thrown
     *
     * @param parent
     * @param capacity
     * @param name
     * @param spatialModel
     * @param coordinate
     */
    public SpatialResource(ModelElement parent, int capacity, String name, SpatialModel spatialModel, CoordinateIfc coordinate) {
        super(parent, capacity, name);

        if (spatialModel == null) {
            spatialModel = parent.getSpatialModel();
            if (spatialModel == null) {
                throw new IllegalArgumentException("No spatial model is available!");
            }
        }

        setSpatialModel(spatialModel); // set the model element's spatial model

        setSpatialElement(new SpatialElement(spatialModel, coordinate, getName()));
        myInitialResourceLocation = null;

    }

    // ===========================================
    // PUBLIC METHODS
    // ===========================================
    /* (non-Javadoc)
	 * @see jsl.modeling.elements.spatial.SpatialElementIfc#attachPositionObserver(java.util.Observer)
     */
    public final void attachPositionObserver(Observer observer) {
        mySpatialElement.attachPositionObserver(observer);
    }

    /* (non-Javadoc)
	 * @see jsl.modeling.elements.spatial.SpatialElementIfc#changeSpatialModel(jsl.modeling.elements.spatial.SpatialModel, jsl.modeling.elements.spatial.CoordinateIfc)
     */
    public final void changeSpatialModel(SpatialModel spatialModel, CoordinateIfc coordinate) {
        mySpatialElement.changeSpatialModel(spatialModel, coordinate);
    }

    /* (non-Javadoc)
	 * @see jsl.modeling.elements.spatial.SpatialElementIfc#distanceTo(jsl.modeling.elements.spatial.CoordinateIfc)
     */
    public final double distanceTo(CoordinateIfc coordinate) {
        return mySpatialElement.distanceTo(coordinate);
    }

    /* (non-Javadoc)
	 * @see jsl.modeling.elements.spatial.SpatialElementIfc#distanceTo(jsl.modeling.elements.spatial.SpatialElementIfc)
     */
    public final double distanceTo(SpatialElementIfc element) {
        return mySpatialElement.distanceTo(element);
    }

    /* (non-Javadoc)
	 * @see jsl.modeling.elements.spatial.SpatialElementIfc#getCurrentPosition()
     */
    public final CoordinateIfc getPosition() {
        return mySpatialElement.getPosition();
    }

    /* (non-Javadoc)
	 * @see jsl.modeling.elements.spatial.SpatialElementIfc#getInitialPosition()
     */
    public final CoordinateIfc getInitialPosition() {
        return mySpatialElement.getInitialPosition();
    }

    /* (non-Javadoc)
	 * @see jsl.modeling.elements.spatial.SpatialElementIfc#getInitialSpatialModel()
     */
    public final SpatialModel getInitialSpatialModel() {
        return mySpatialElement.getInitialSpatialModel();
    }

    /* (non-Javadoc)
	 * @see jsl.modeling.elements.spatial.SpatialElementIfc#getModelElement()
     */
    public final ModelElement getModelElement() {
        return mySpatialElement.getModelElement();
    }

    /* (non-Javadoc)
	 * @see jsl.modeling.elements.spatial.SpatialElementIfc#getPreviousPosition()
     */
    public final CoordinateIfc getPreviousPosition() {
        return mySpatialElement.getPreviousPosition();
    }

    /* (non-Javadoc)
	 * @see jsl.modeling.elements.spatial.SpatialElementIfc#initializeSpatialElement()
     */
    public final void initializeSpatialElement() {
        mySpatialElement.initializeSpatialElement();
    }

    /* (non-Javadoc)
	 * @see jsl.modeling.elements.spatial.SpatialElementIfc#isPositionEqualTo(jsl.modeling.elements.spatial.CoordinateIfc)
     */
    public final boolean isPositionEqualTo(CoordinateIfc coordinate) {
        return mySpatialElement.isPositionEqualTo(coordinate);
    }

    /* (non-Javadoc)
	 * @see jsl.modeling.elements.spatial.SpatialElementIfc#isPositionEqualTo(jsl.modeling.elements.spatial.SpatialElementIfc)
     */
    public final boolean isPositionEqualTo(SpatialElementIfc element) {
        return mySpatialElement.isPositionEqualTo(element);
    }

    /* (non-Javadoc)
	 * @see jsl.modeling.elements.spatial.SpatialElementIfc#removePositionObserver(java.util.Observer)
     */
    public final void removePositionObserver(Observer observer) {
        mySpatialElement.removePositionObserver(observer);
    }

    /* (non-Javadoc)
	 * @see jsl.modeling.elements.spatial.SpatialElementIfc#setInitialPosition(jsl.modeling.elements.spatial.CoordinateIfc)
     */
    public final void setInitialPosition(CoordinateIfc c) {
        mySpatialElement.setInitialPosition(c);
    }

    /**
     * Sets the position to the coordinates of the supplied location
     *
     * @param location
     */
    public final void setPosition(CoordinateIfc location) {
        mySpatialElement.setCurrentPosition(location);
        notifyObservers(MOVED);
    }

    /**
     * Sets the position to the coordinates of the supplied location
     *
     * @param element
     */
    public final void setPosition(SpatialElementIfc element) {
        setPosition(element.getPosition());
    }

    /**
     * Gets the initial resource location. This location is used when the
     * element is initialized, prior to a replication.
     *
     * @return Returns the myInitialResourceLocation.
     */
    public final ResourceLocation getInitialResourceLocation() {
        return myInitialResourceLocation;
    }

    /**
     * Returns the associated ResourceLocation if one exists. May be null
     *
     * @return Returns the ResourceLocation.
     */
    public final ResourceLocation getResourceLocation() {
        return myResourceLocation;
    }

    // ===========================================
    // PROTECTED METHODS
    // ===========================================
    protected void initialize() {
        super.initialize();
        mySpatialElement.initializeSpatialElement();
        setResourceLocation(getInitialResourceLocation());
    }

    protected final SpatialElement getSpatialElement() {
        return mySpatialElement;
    }

    /**
     * Sets the initial resource location. This location is used when the
     * element is initialized prior to a replication. This may be null.
     *
     * @param location The initial resource locaiton to set for the element
     */
    protected final void setInitialResourceLocation(ResourceLocation location) {
        myInitialResourceLocation = location;
    }

    /**
     * Sets the ResourceLocation for this SpatialResource. It can be null
     *
     * @param resourceLocation The resourceLocation to set.
     */
    protected final void setResourceLocation(ResourceLocation resourceLocation) {
        myResourceLocation = resourceLocation;
    }

    /**
     * Sets the underlying SpatialElement
     *
     * @param spatialElement
     */
    protected final void setSpatialElement(SpatialElement spatialElement) {
        if (spatialElement == null) {
            throw new IllegalArgumentException("The supplied spatial element was null!");
        }
        mySpatialElement = spatialElement;
        mySpatialElement.setModelElement(this);
    }
}
