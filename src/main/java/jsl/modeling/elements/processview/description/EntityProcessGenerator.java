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
package jsl.modeling.elements.processview.description;

import jsl.modeling.JSLEvent;
import jsl.modeling.ModelElement;
import jsl.modeling.elements.resource.EntityGenerator;
import jsl.modeling.elements.EventGenerator;
import jsl.modeling.elements.resource.Entity;
import jsl.modeling.elements.resource.EntityType;
import jsl.modeling.elements.resource.NoEntityTypeSpecifiedException;
import jsl.utilities.random.RandomIfc;

/**
 *
 */
public class EntityProcessGenerator extends EntityGenerator {

    /** A reference to the process description for this generator
     *
     */
    protected ProcessDescription myProcessDescription;

    /**
     * @param parent the parent model element
     * @param processDescription the ProcessDescription
     */
    public EntityProcessGenerator(ModelElement parent, ProcessDescription processDescription) {
        this(parent, processDescription, null, null, Long.MAX_VALUE, Double.POSITIVE_INFINITY, null);
    }

    /**
     * @param parent the parent model element
     * @param processDescription the ProcessDescription
     * @param name the name of the model element
     */
    public EntityProcessGenerator(ModelElement parent, ProcessDescription processDescription, String name) {
        this(parent, processDescription, null, null, Long.MAX_VALUE, Double.POSITIVE_INFINITY, name);
    }

    /**
     * @param parent the parent model element
     * @param processDescription the ProcessDescription
     * @param timeUntilFirst the time until the first generation
     */
    public EntityProcessGenerator(ModelElement parent, ProcessDescription processDescription,
            RandomIfc timeUntilFirst) {
        this(parent, processDescription, timeUntilFirst, null, Long.MAX_VALUE, Double.POSITIVE_INFINITY, null);
    }

    /**
     * @param parent the parent model element
     * @param processDescription the ProcessDescription
     * @param name the name of the model element
     * @param timeUntilFirst the time until the first generation
     */
    public EntityProcessGenerator(ModelElement parent, ProcessDescription processDescription,
            RandomIfc timeUntilFirst, String name) {
        this(parent, processDescription, timeUntilFirst, null, Long.MAX_VALUE, Double.POSITIVE_INFINITY, name);
    }

    /**
     * @param parent the parent model element
     * @param processDescription the ProcessDescription
     * @param name the name of the model element
     * @param timeUntilFirst the time until the first generation
     * @param timeUntilNext the time between generations
     */
    public EntityProcessGenerator(ModelElement parent, ProcessDescription processDescription,
            RandomIfc timeUntilFirst, RandomIfc timeUntilNext, String name) {
        this(parent, processDescription, timeUntilFirst, timeUntilNext, Long.MAX_VALUE, Double.POSITIVE_INFINITY, name);
    }

    /**
     * @param parent the parent model element
     * @param processDescription the ProcessDescription
     * @param timeUntilFirst the time until the first generation
     * @param timeUntilNext the time between generations
     */
    public EntityProcessGenerator(ModelElement parent, ProcessDescription processDescription,
            RandomIfc timeUntilFirst, RandomIfc timeUntilNext) {
        this(parent, processDescription, timeUntilFirst, timeUntilNext, Long.MAX_VALUE, Double.POSITIVE_INFINITY, null);
    }

    /**
     * @param parent the parent model element
     * @param processDescription the ProcessDescription
     * @param name the name of the model element
     * @param timeUntilFirst the time until the first generation
     * @param timeUntilNext the time between generations
     * @param maxNum the maximum number of generations
     */
    public EntityProcessGenerator(ModelElement parent, ProcessDescription processDescription,
            RandomIfc timeUntilFirst, RandomIfc timeUntilNext, long maxNum, String name) {
        this(parent, processDescription, timeUntilFirst, timeUntilNext, maxNum, Double.POSITIVE_INFINITY, name);
    }

    /**
     * @param parent the parent model element
     * @param processDescription the ProcessDescription
     * @param timeUntilFirst the time until the first generation
     * @param timeUntilNext the time between generations
     * @param maxNum the maximum number of generations
     */
    public EntityProcessGenerator(ModelElement parent, ProcessDescription processDescription,
            RandomIfc timeUntilFirst, RandomIfc timeUntilNext, long maxNum) {
        this(parent, processDescription, timeUntilFirst, timeUntilNext, maxNum, Double.POSITIVE_INFINITY, null);
    }

    /**
     * @param parent the parent model element
     * @param processDescription the ProcessDescription
     * @param timeUntilFirst the time until the first generation
     * @param timeUntilNext the time between generations
     * @param maxNum the maximum number of generations
     * @param timeUntilLast the time until the last event
     */
    public EntityProcessGenerator(ModelElement parent, ProcessDescription processDescription,
            RandomIfc timeUntilFirst, RandomIfc timeUntilNext, long maxNum, double timeUntilLast) {
        this(parent, processDescription, timeUntilFirst, timeUntilNext, maxNum, timeUntilLast, null);
    }

    /**
     * @param parent the parent model element
     * @param processDescription the ProcessDescription
     * @param timeUntilFirst the time until the first generation
     * @param timeUntilNext the time between generations
     * @param maxNum the maximum number of generations
     * @param timeUntilLast the time until the last event
     * @param name the name of the generator
     */
    public EntityProcessGenerator(ModelElement parent, ProcessDescription processDescription,
            RandomIfc timeUntilFirst, RandomIfc timeUntilNext,
            long maxNum, double timeUntilLast, String name) {
        super(parent, timeUntilFirst, timeUntilNext, maxNum, timeUntilLast, name);
        setProcessDescription(processDescription);
    }

    /** Returns a reference to the process description for this generator
     * @return A reference to the process description for this generator
     */
    protected final ProcessDescription getProcessDescription() {
        return (myProcessDescription);
    }

    /** Sets the process description for this generator
     *
     * @param processDescription The ProcessDescription
     */
    protected void setProcessDescription(ProcessDescription processDescription) {
        if (processDescription == null) {
            throw new IllegalArgumentException("ProcessDescription must be non-null!");
        }
        myProcessDescription = processDescription;
    }

    @Override
    protected void generate(JSLEvent event) {
//TODO this will not work
        EntityType et = getEntityType();

        if (et == null) {
            throw new NoEntityTypeSpecifiedException("No entity type was " +
                    "provided for the generator");
        }

        // create the entity
        Entity e = et.createEntity();
        ProcessDescription pd = getProcessDescription();
        ProcessExecutor pe = pd.createProcessExecutor(e);
        pe.initialize();
        pe.start();

    }
}
