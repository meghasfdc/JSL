/*
 * Copyright (c) 2018. Manuel D. Rossetti, rossetti@uark.edu
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
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
