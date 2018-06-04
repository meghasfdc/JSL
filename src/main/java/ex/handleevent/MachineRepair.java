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
package ex.handleevent;

import jsl.modeling.JSLEvent;
import jsl.modeling.Model;
import jsl.modeling.ModelElement;
import jsl.modeling.SchedulingElement;
import jsl.modeling.Simulation;
import jsl.modeling.queue.QObject;
import jsl.modeling.queue.Queue;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.modeling.elements.variable.TimeWeighted;
import jsl.utilities.random.RandomIfc;
import jsl.utilities.random.distributions.Exponential;
import jsl.modeling.SimulationReporter;

/**
 *
 * @author rossetti
 */
public class MachineRepair extends SchedulingElement {

    public static final int FAILURE = 0;

    public static final int REPAIR = 1;

    protected Queue myRepairQ;

    protected RandomVariable myTBFailure;

    protected RandomVariable myRepairTime;

    protected TimeWeighted myNumFailedMachines;

    protected TimeWeighted myNumAvailableOperators;

    protected TimeWeighted myNumBusyOperators;

    protected TimeWeighted myProbAllBroken;

    protected int myNumMachines;

    protected int myNumOperators;

    /**
     * 
     * @param parent
     * @param numOperators
     * @param numMachines
     * @param tbFailure
     * @param repTime 
     */
    public MachineRepair(ModelElement parent, int numOperators, int numMachines,
            RandomIfc tbFailure, RandomIfc repTime) {
        super(parent);
        if (numMachines <= numOperators) {
            throw new IllegalArgumentException("The number of machines must be > number operators");
        }
        myRepairQ = new Queue(this, "RepairQ");
        myNumMachines = numMachines;
        myNumOperators = numOperators;
        myNumFailedMachines = new TimeWeighted(this, 0.0, "Num Failed Machines");
        myProbAllBroken = new TimeWeighted(this, 0.0, "Prob all broken");
        myNumAvailableOperators = new TimeWeighted(this, myNumOperators, "Num Available Operators");
        myNumBusyOperators = new TimeWeighted(this, 0.0, "Num Busy Operators");
        myTBFailure = new RandomVariable(this, tbFailure);
        myRepairTime = new RandomVariable(this, repTime);
    }

    @Override
    protected void initialize() {
        super.initialize();
        for (int i = 1; i <= myNumMachines; i++) {
            scheduleEvent(myTBFailure, FAILURE);
        }
    }

    @Override
    protected void handleEvent(JSLEvent event) {
        switch (event.getType()) {
            case FAILURE:
                failure(event);
                break;
            case REPAIR:
                repair(event);
                break;
            default:
                System.out.println("Invalid event type");
        }
    }

    private void failure(JSLEvent event) {
        myNumFailedMachines.increment();
        myProbAllBroken.setValue(myNumFailedMachines.getValue() == myNumMachines);
        QObject arrival = createQObject();
        myRepairQ.enqueue(arrival);
        if (myNumAvailableOperators.getValue() > 0) {
            myNumAvailableOperators.decrement();
            myNumBusyOperators.increment();
            QObject nc = myRepairQ.removeNext();
            scheduleEvent(myRepairTime, REPAIR, nc);
        }
    }

    private void repair(JSLEvent event) {
        myNumFailedMachines.decrement();
        myProbAllBroken.setValue(myNumFailedMachines.getValue() == myNumMachines);
        scheduleEvent(myTBFailure, FAILURE);
        if (myRepairQ.isNotEmpty()) {
            QObject nc = myRepairQ.removeNext();
            scheduleEvent(myRepairTime, REPAIR, nc);
        } else {
            myNumAvailableOperators.increment();
            myNumBusyOperators.decrement();
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Simulation s = new Simulation("Machine Repair");
        s.setLengthOfReplication(11000.0);
        s.setLengthOfWarmUp(1000.0);
        s.setNumberOfReplications(20);

        Model m = s.getModel();

        int numMachines = 5;
        int numOperators = 1;
        RandomIfc tbf = new Exponential(10.0);
        RandomIfc rt = new Exponential(4.0);
        MachineRepair machineRepair = new MachineRepair(m, numOperators, numMachines, tbf, rt);

        s.run();
        
        SimulationReporter r = s.makeSimulationReporter();
        r.printAcrossReplicationStatistics();
    }
}
