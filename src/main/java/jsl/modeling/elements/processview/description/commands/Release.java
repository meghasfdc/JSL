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

import jsl.modeling.elements.processview.description.ProcessCommand;
import jsl.modeling.queue.Queue;
import jsl.modeling.elements.resource.*;
import jsl.modeling.ModelElement;

/**
 *
 */
public class Release extends ProcessCommand {

    private Resource myResource;

    private Queue myQueue;

    public Release(ModelElement parent, Resource resource, Queue queue) {
        this(parent, resource, queue, null);
    }

    public Release(ModelElement parent, Resource resource, Queue queue, String name) {
        super(parent, name);
        setResource(resource);
        setQueue(queue);
    }

    protected final void setResource(Resource resource) {
        if (resource == null) {
            throw new IllegalArgumentException("Resource was equal to null!");
        }
        myResource = resource;
    }

    protected final void setQueue(Queue queue) {
        if (queue == null) {
            throw new IllegalArgumentException("Queue was equal to null!");
        }
        myQueue = queue;
    }

    @Override
    public void execute() {

        // get the entity that is releasing the resource
        Entity entity = getProcessExecutor().getCurrentEntity();

        // release the resource
        entity.release(myResource);

    }
}
