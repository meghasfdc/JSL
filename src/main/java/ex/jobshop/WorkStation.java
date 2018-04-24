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


package ex.jobshop;

import ex.jobshop.JobGenerator.Job;
import jsl.modeling.queue.*;
import jsl.modeling.elements.variable.*;
import jsl.modeling.*;

/**
 *
 */
public class WorkStation extends SchedulingElement {
    
    private final Queue myQueue;
    private final int myNumServers;
    private final TimeWeighted myNumBusy;
    
    private final EndServiceListener myEndServiceListener;
    
    /** Creates a new instance of WorkStation
     * @param parent
     * @param numServers 
     * @param name 
     */
    public WorkStation(ModelElement parent, int numServers, String name) {
        super(parent, name);
        myNumServers = numServers;
        myQueue = new Queue(this, name + "Q");

        myNumBusy = new TimeWeighted(this, 0.0, name + "NB");

        myEndServiceListener = new EndServiceListener();
    }
    
    public void arrive(Job job){
        myQueue.enqueue(job);
        if (myNumBusy.getValue() < myNumServers){
            myNumBusy.increment();
            Job c = (Job)myQueue.removeNext();
            scheduleEndService(c);
        }
    }
    
    private void scheduleEndService(Job job) {
        double t = job.getServiceTime();
        scheduleEvent(myEndServiceListener, t,
              "Job " + job.getId() + " End Service at " + this.getName(), job);
    }
    
    class EndServiceListener implements EventActionIfc {
        @Override
        public void action(JSLEvent event) {
            myNumBusy.decrement();
            
            Job job = (Job)event.getMessage();
            job.doNextJobStep();
            
            if (myQueue.size() > 0 ) {
                myNumBusy.increment();
                Job nextJob = (Job)myQueue.removeNext();
                scheduleEndService(nextJob);
            }
            
            
        }
    }
    
}
