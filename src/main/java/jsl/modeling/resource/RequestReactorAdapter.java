/*
 *  Copyright (C) 2017 rossetti
 * 
 *  Contact:
 * 	Manuel D. Rossetti, Ph.D., P.E.
 * 	Department of Industrial Engineering
 * 	University of Arkansas
 * 	4207 Bell Engineering Center
 * 	Fayetteville, AR 72701
 * 	Phone: (479) 575-6756
 * 	Email: rossetti@uark.edu
 * 	Web: www.uark.edu/~rossetti
 * 
 *  This file is part of the JSL (a Java Simulation Library). The JSL is a framework
 *  of Java classes that permit the easy development and execution of discrete event
 *  simulation programs.
 * 
 *  The JSL is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 * 
 *  The JSL is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jsl.modeling.resource;

import jsl.modeling.queue.Queue;

/**
 * A convenience class that implements the RequestReactorIfc.
 * Each implemented method, does nothing. Clients can sub-class this
 base class and override only those methods that they are interested in.
 It would be very common to just override the completed() method
 *
 * @author rossetti
 */
public class RequestReactorAdapter implements RequestReactorIfc {

    @Override
    public void enqueued(Request request, Queue<Request> queue) {
    }

    @Override
    public void dequeued(Request request, Queue<Request> queue) {
    }

    @Override
    public void rejected(Request request) {
    }

    @Override
    public void canceled(Request request) {
    }

    @Override
    public void preempted(Request request) {
    }

    @Override
    public void allocated(Request request) {
    }

    @Override
    public void completed(Request request) {
    }

    @Override
    public void prepared(Request request) {
    }

    @Override
    public void resumed(Request request) {
    }

}
