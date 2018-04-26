/*
 * Copyright (c) 2018. Manuel D. Rossetti, manuelrossetti@gmail.com
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
package ex.hospitalward;

import jsl.modeling.JSLEvent;
import jsl.modeling.Model;
import jsl.modeling.ModelElement;
import jsl.modeling.Simulation;
import jsl.modeling.elements.EventGenerator;
import jsl.modeling.queue.QObject;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.modeling.elements.variable.ResponseVariable;
import jsl.utilities.GetValueIfc;
import jsl.utilities.random.distributions.Exponential;
import jsl.utilities.random.distributions.Lognormal;
import jsl.modeling.SimulationReporter;
import jsl.modeling.elements.EventGeneratorActionIfc;

/**
 *
 * @author rossetti
 */
public class HospitalWard extends ModelElement {

    private EventGenerator myNoOpPatientGenerator;

    private NoOpPatientArrivalListener myNoOpPatientArrivalListener;

    private EventGenerator myOpPatientGenerator;

    private OpPatientArrivalListener myOpPatientArrivalListener;

    private RandomVariable myNonOpPatientStayTime;

    private RandomVariable myPreOpStayTime;

    private RandomVariable myOperationTime;

    private RandomVariable myPostOpStayTime;

    private ResponseVariable mySystemTime;

    private BedWard myBedWard;

    private OperatingRoom myOR;

    public HospitalWard(ModelElement parent) {
        this(parent, null);
    }

    public HospitalWard(ModelElement parent, String name) {
        super(parent, name);

        myBedWard = new BedWard(this);
        myOR = new OperatingRoom(this);

        myNoOpPatientArrivalListener = new NoOpPatientArrivalListener();
        Exponential d1 = new Exponential(12.0);
        myNoOpPatientGenerator = new EventGenerator(this, myNoOpPatientArrivalListener, d1, d1);

        myOpPatientArrivalListener = new OpPatientArrivalListener();
        Exponential d2 = new Exponential(6.0);
        myOpPatientGenerator = new EventGenerator(this, myOpPatientArrivalListener, d2, d2);

        myNonOpPatientStayTime = new RandomVariable(this, new Exponential(60.0));
        myPreOpStayTime = new RandomVariable(this, new Exponential(24.0));
        myOperationTime = new RandomVariable(this, new Lognormal(0.75, 0.25 * 0.25));
        myPostOpStayTime = new RandomVariable(this, new Exponential(72.0));

        mySystemTime = new ResponseVariable(this, "System Time");
    }

    void departingPatient(QObject p) {
        mySystemTime.setValue(getTime() - p.getCreateTime());
    }

    void sendToOperatingRoom(OpPatient p) {
        myOR.receivePatient(p);
    }

    void endOfOperation(OpPatient p) {
        myBedWard.receivePostOperationPatient(p);
    }

    protected class NoOpPatient extends QObject {

        public NoOpPatient(double creationTime, String name) {
            super(creationTime, name);
        }

        public NoOpPatient(double creationTime) {
            super(creationTime);
        }

        public GetValueIfc getHospitalStayTime() {
            return myNonOpPatientStayTime;
        }
    }

    protected class OpPatient extends QObject {

        public OpPatient(double creationTime, String name) {
            super(creationTime, name);
        }

        public OpPatient(double creationTime) {
            super(creationTime);
        }

        public GetValueIfc getPreOperationTime() {
            return myPreOpStayTime;
        }

        public GetValueIfc getOperationTime() {
            return myOperationTime;
        }

        public GetValueIfc getPostOperationTime() {
            return myPostOpStayTime;
        }
    }

    private class NoOpPatientArrivalListener implements EventGeneratorActionIfc {

        @Override
        public void generate(EventGenerator generator, JSLEvent event) {
            myBedWard.receiveNewPatient(new NoOpPatient(getTime()));
        }
    }

    private class OpPatientArrivalListener implements EventGeneratorActionIfc {

        @Override
        public void generate(EventGenerator generator, JSLEvent event) {
            myBedWard.receiveNewPatient(new OpPatient(getTime()));
        }
    }

    public static void main(String[] args) {
        
        Simulation s = new Simulation("Hospital Ward Simulation");
        
        // create the containing model
        Model m = s.getModel();

        // create the model element and attach it to the model
        new HospitalWard(m, "HospitalWard");

        // set the parameters of the experiment
        s.setNumberOfReplications(30);
        s.setLengthOfWarmUp(5000.0);
        s.setLengthOfReplication(25000.0);
        //s.setMaximumExecutionTime(4);
        //s.setMaximumExecutionTimePerReplication(1);

        // tell the experiment to run
        s.run();
        
        System.out.println(s);
        SimulationReporter r = s.makeSimulationReporter();
        r.printAcrossReplicationSummaryStatistics();
    }
}