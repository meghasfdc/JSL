/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 *
 * Copyright (c) Manuel D. Rossetti (rossetti@uark.edu)
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
package jsl.modeling.elements.resource;

import jsl.modeling.ModelElement;

/**
 *
 * @author rossetti
 */
public class CompositeEntityReceiver extends EntityReceiver {

    protected EntityReceiver myFirstReceiver;

    protected EntityReceiver myPrevReceiver;

    protected Exit myExit;

    public CompositeEntityReceiver(ModelElement parent) {
        this(parent, null);
    }

    public CompositeEntityReceiver(ModelElement parent, String name) {
        super(parent, name);
        myExit = new Exit(this);
        myFirstReceiver = myExit;
    }

    public void addInternalReceiver(EntityReceiver receiver){
        receiver.setDirectEntityReceiver(myExit);
        receiver.setComposite(this);
        
        if (myPrevReceiver == null){
            myPrevReceiver = receiver;
            myFirstReceiver = receiver;
        } else {
            myPrevReceiver.setDirectEntityReceiver(receiver);
            myPrevReceiver = receiver;
        }
    }

    @Override
    protected void receive(Entity entity) {
        myFirstReceiver.receive(entity);
    }

    protected class Exit extends EntityReceiver {

        public Exit(ModelElement parent) {
            super(parent);
        }

        @Override
        protected void receive(Entity entity) {
            entity.setCurrentReceiver(CompositeEntityReceiver.this);
            CompositeEntityReceiver.this.sendEntity(entity);
        }

    }
}
