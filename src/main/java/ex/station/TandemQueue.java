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
package ex.station;

import jsl.modeling.JSLEvent;
import jsl.modeling.ModelElement;
import jsl.modeling.SchedulingElement;
import jsl.modeling.Simulation;
import jsl.modeling.SimulationReporter;
import jsl.modeling.elements.EventGenerator;
import jsl.modeling.queue.QObject;
import jsl.modeling.elements.station.ReceiveQObjectIfc;
import jsl.modeling.elements.station.SingleQueueStation;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.modeling.elements.variable.ResponseVariable;
import jsl.utilities.random.distributions.Exponential;
import jsl.modeling.elements.EventGeneratorActionIfc;

/**
 * Arriving customers choose randomly to two stations.  
 * The arrivals are Poisson with mean rate 1.1. Thus, the time 
 * between arrivals is exponential with mean 1/1.1.
 * After receiving service at the first station the customer moves 
 * directly to the second station.
 * 
 * The service times of the stations are exponential with means 0.8 and 0.7, 
 * respectively. After receiving service at the 2nd station, the
 * customer leaves.
 *
 * @author rossetti
 */
public class TandemQueue extends SchedulingElement {

    protected EventGenerator myArrivalGenerator;

    protected RandomVariable myTBA;

    protected SingleQueueStation myStation1;

    protected SingleQueueStation myStation2;

    protected RandomVariable myST1;

    protected RandomVariable myST2;
    
    protected ResponseVariable mySysTime;

    public TandemQueue(ModelElement parent) {
        this(parent, null);
    }

    public TandemQueue(ModelElement parent, String name) {
        super(parent, name);

        myTBA = new RandomVariable(this, new Exponential(1.0/1.1));

        myST1 = new RandomVariable(this, new Exponential(0.8));

        myST2 = new RandomVariable(this, new Exponential(0.7));

        myArrivalGenerator = new EventGenerator(this, new Arrivals(), myTBA, myTBA);
       
        myStation1 = new SingleQueueStation(this, myST1, "Station1");

        myStation2 = new SingleQueueStation(this, myST2, "Station2");
        
        myStation1.setNextReceiver(myStation2);
        myStation2.setNextReceiver(new Dispose());
        
        mySysTime = new ResponseVariable(this, "System Time");

    }

    protected class Arrivals implements EventGeneratorActionIfc {

        @Override
        public void generate(EventGenerator generator, JSLEvent event) {
            myStation1.receive(new QObject(getTime()));
        }

    }

    protected class Dispose implements ReceiveQObjectIfc {

        @Override
        public void receive(QObject qObj) {
           // collect system time
            mySysTime.setValue(getTime() - qObj.getCreateTime());
        }
        
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Simulation s = new Simulation("Tandem Station Example");
        
        new TandemQueue(s.getModel());
        
        s.setNumberOfReplications(10);
        s.setLengthOfReplication(20000);
        s.setLengthOfWarmUp(5000);
        SimulationReporter r = s.makeSimulationReporter();
        
        s.run();
        
        r.printAcrossReplicationSummaryStatistics();
    }

}
