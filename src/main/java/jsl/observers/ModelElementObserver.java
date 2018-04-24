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
package jsl.observers;

import jsl.modeling.ModelElement;
import jsl.utilities.IdentityIfc;

/**
 *
 */
public class ModelElementObserver implements ObserverIfc, IdentityIfc {

    protected String myName;

    private static int myCounter_;

    private int myId;

    private ModelElement myModelElement;

    public ModelElementObserver() {
        this(null);
    }

    public ModelElementObserver(String name) {
        myCounter_ = myCounter_ + 1;
        myId = myCounter_;
        setName(name);
    }

    /**
     * Sets the name of this model element
     *
     * @param str The name as a string.
     */
    public final void setName(String str) {

        if (str == null) { // no name is being passed, construct a default name
            String s = this.getClass().getName();
            int k = s.lastIndexOf(".");
            if (k != -1) {
                s = s.substring(k + 1);
            }
            str = s + "-" + getId();
        }
        myName = str;

    }

    /**
     * Gets this model element observer's name.
     *
     * @return The name of the model element observer
     */
    @Override
    public final String getName() {
        return myName;
    }

    /**
     * Gets a uniquely assigned integer identifier for this model element
     * observer. This identifier is assigned when the model element observer is
     * created. It may vary if the order of creation changes.
     *
     * @return The identifier for the model element observer
     */
    @Override
    public final long getId() {
        return (myId);
    }

    @Override
    public void update(Object observable, Object arg) {
        ModelElement m = (ModelElement) observable;

        int state = m.getObserverState();

        if (state == ModelElement.BEFORE_EXPERIMENT) {
            beforeExperiment(m, arg);
        } else if (state == ModelElement.INITIALIZED) {
            initialize(m, arg);
        } else if (state == ModelElement.BEFORE_REPLICATION) {
            beforeReplication(m, arg);
        } else if (state == ModelElement.MONTE_CARLO) {
            montecarlo(m, arg);
        } else if (state == ModelElement.REPLICATION_ENDED) {
            replicationEnded(m, arg);
        } else if (state == ModelElement.AFTER_REPLICATION) {
            afterReplication(m, arg);
        } else if (state == ModelElement.UPDATE) {
            update(m, arg);
        } else if (state == ModelElement.WARMUP) {
            warmUp(m, arg);
        } else if (state == ModelElement.TIMED_UPDATE) {
            timedUpdate(m, arg);
        } else if (state == ModelElement.AFTER_EXPERIMENT) {
            afterExperiment(m, arg);
        } else if (state == ModelElement.REMOVED_FROM_MODEL) {
            removedFromModel(m, arg);
        }
    }

    @Override
    public String toString() {
        return (getName());
    }

    protected final ModelElement getModelElement() {
        return myModelElement;
    }

    protected final void setModelElement(ModelElement m) {
        if (m == null) {
            throw new IllegalArgumentException("The observed model element was null.");
        }
        myModelElement = m;
    }

    protected void beforeExperiment(ModelElement m, Object arg) {
        setModelElement(m);
    }

    protected void beforeReplication(ModelElement m, Object arg) {
    }

    protected void initialize(ModelElement m, Object arg) {
    }

    protected void montecarlo(ModelElement m, Object arg) {
    }

    protected void replicationEnded(ModelElement m, Object arg) {
    }

    protected void afterReplication(ModelElement m, Object arg) {
    }

    protected void update(ModelElement m, Object arg) {
    }

    protected void warmUp(ModelElement m, Object arg) {
    }

    protected void timedUpdate(ModelElement m, Object arg) {
    }

    protected void afterExperiment(ModelElement m, Object arg) {
    }

    protected void removedFromModel(ModelElement m, Object arg) {
//            System.out.println("ModelElementObserver removedFromModel() " + m.getName());
    }
}
