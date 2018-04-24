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
import jsl.observers.Mover2DObserver;

public abstract class AnimationObserverAbstract extends Mover2DObserver {

    protected AnimationMessageHandlerIfc myAnimationMessageHandler;

    protected double myTimeOfPreviousUpdate;

    public AnimationObserverAbstract(AnimationMessageHandlerIfc handler) {
        this(null, handler);
    }

    public AnimationObserverAbstract(String name, AnimationMessageHandlerIfc handler) {
        super(name);
        setAnimationMessageHandler(handler);
    }

    public void update(Object observable, Object arg) {

        ModelElement m = (ModelElement) observable;

        // tell the message handler that a new message is beginning
        myAnimationMessageHandler.beginMessage();

        // build standard model element message
//		buildStandardModelElementMessage(m);

        // handle the model element updates
        super.update(observable, arg);

        // post the message and record the time of this update
        if (myAnimationMessageHandler.isStarted()) {
            myAnimationMessageHandler.commitMessage();
            myTimeOfPreviousUpdate = m.getTime();
        }

    }

    /**
     * @return Returns the animation message handler.
     */
    protected final AnimationMessageHandlerIfc getAnimationMessageHandler() {
        return myAnimationMessageHandler;
    }

    /**
     * @param handler The AnimationMessageHandlerIfc to set.
     */
    protected final void setAnimationMessageHandler(AnimationMessageHandlerIfc handler) {
        if (handler == null) {
            throw new IllegalArgumentException("The AnimationMessageHandlerIfc was null");
        }
        myAnimationMessageHandler = handler;
    }

    protected void buildStandardModelElementMessage(ModelElement element) {

        myAnimationMessageHandler.append(getClass().getName());
        myAnimationMessageHandler.append("\t");
        myAnimationMessageHandler.append(element.getClass().getName());
        myAnimationMessageHandler.append("\t");
        myAnimationMessageHandler.append(element.getTime());
        myAnimationMessageHandler.append("\t");
        myAnimationMessageHandler.append(element.getId());
        myAnimationMessageHandler.append("\t");
        myAnimationMessageHandler.append(element.getName());
        myAnimationMessageHandler.append("\t");
        myAnimationMessageHandler.append(myTimeOfPreviousUpdate);
        myAnimationMessageHandler.append("\t");

    }
}
