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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.modeling;

import ex.models.DTPQueueModel;
import jsl.modeling.elements.variable.AcrossReplicationStatisticIfc;
import jsl.utilities.math.JSLMath;
import jsl.utilities.random.distributions.Exponential;
import ex.models.DTPFunctionalTest;
import jsl.modeling.Model;
import jsl.modeling.Simulation;
import jsl.modeling.SimulationReporter;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author rossetti
 */
public class DTPQueueTest {

    double aNB, aNS, aNQ, aTQ, aST;
    double bNB, bNS, bNQ, bTQ, bST;

    public DTPQueueTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @After
    public void tearDown() {
    }

    @Before
    public void setUp() {
        Simulation sim = new Simulation("Drive Through Pharmacy");
        // get the model
        Model m = sim.getModel();
        // add DriveThroughPharmacy to the main model
        DTPFunctionalTest dtp = new DTPFunctionalTest(m);
        dtp.setArrivalRS(new Exponential(6.0));
        dtp.setServiceRS(new Exponential(3.0));
        // set the parameters of the experiment
        sim.setNumberOfReplications(30);
        sim.setLengthOfReplication(20000.0);
        sim.setLengthOfWarmUp(5000.0);
        SimulationReporter r = sim.makeSimulationReporter();
        System.out.println("Simulation started.");
        sim.run();
        r.printAcrossReplicationSummaryStatistics();
        AcrossReplicationStatisticIfc sNB = m.getAcrossReplicationResponseVariable("NumBusy");
        AcrossReplicationStatisticIfc sNS = m.getAcrossReplicationResponseVariable("# in System");
        AcrossReplicationStatisticIfc sNQ = m.getAcrossReplicationResponseVariable("PharmacyQ:NumInQ");
        AcrossReplicationStatisticIfc sTQ = m.getAcrossReplicationResponseVariable("PharmacyQ:TimeInQ");
        AcrossReplicationStatisticIfc sST = m.getAcrossReplicationResponseVariable("System Time");
        aNB = sNB.getAcrossReplicationAverage();
        aNS = sNS.getAcrossReplicationAverage();
        aNQ = sNQ.getAcrossReplicationAverage();
        aTQ = sTQ.getAcrossReplicationAverage();
        aST = sST.getAcrossReplicationAverage();
    }

    @Test
    public void test1() {
        Simulation sim = new Simulation("New Queue Testing");
        // get the model
        Model m = sim.getModel();
        // add DriveThroughPharmacy to the main model
        DTPQueueModel driveThroughPharmacy = new DTPQueueModel(m);
        driveThroughPharmacy.setArrivalRS(new Exponential(6.0));
        driveThroughPharmacy.setServiceRS(new Exponential(3.0));

        // set the parameters of the experiment
        sim.setNumberOfReplications(30);
        sim.setLengthOfReplication(20000.0);
        sim.setLengthOfWarmUp(5000.0);
        SimulationReporter r = sim.makeSimulationReporter();
        System.out.println("Simulation started.");
        sim.run();
        r.printAcrossReplicationSummaryStatistics();
        AcrossReplicationStatisticIfc sNB = m.getAcrossReplicationResponseVariable("NumBusy");
        AcrossReplicationStatisticIfc sNS = m.getAcrossReplicationResponseVariable("# in System");
        AcrossReplicationStatisticIfc sNQ = m.getAcrossReplicationResponseVariable("PharmacyQ:NumInQ");
        AcrossReplicationStatisticIfc sTQ = m.getAcrossReplicationResponseVariable("PharmacyQ:TimeInQ");
        AcrossReplicationStatisticIfc sST = m.getAcrossReplicationResponseVariable("System Time");
        bNB = sNB.getAcrossReplicationAverage();
        bNS = sNS.getAcrossReplicationAverage();
        bNQ = sNQ.getAcrossReplicationAverage();
        bTQ = sTQ.getAcrossReplicationAverage();
        bST = sST.getAcrossReplicationAverage();
        int k;
        double p;

        k = sNB.getAcrossReplicationStatistic().getLeadingDigitRule(1.0) + 1;
        p = Math.pow(10.0, k);
        assertTrue(JSLMath.within(aNB, bNB, p));
        System.out.printf("k = %d, p = %f, aNB = %f, bNB = %f, aNB - bNB = %f%n", k, p, aNB, bNB, (aNB - bNB));
        k = sNS.getAcrossReplicationStatistic().getLeadingDigitRule(1.0) + 1;
        p = Math.pow(10.0, k);
        assertTrue(JSLMath.within(aNS, bNS, p));
        System.out.printf("k = %d, p = %f, aNS = %f, bNS = %f, aNS - bNS = %f%n", k, p, aNS, bNS, (aNS - bNS));
        k = sNQ.getAcrossReplicationStatistic().getLeadingDigitRule(1.0) + 1;
        p = Math.pow(10.0, k);
        assertTrue(JSLMath.within(aNQ, bNQ, p));
        System.out.printf("k = %d, p = %f, aNQ = %f, bNQ = %f, aNQ - bNQ = %f%n", k, p, aNQ, bNQ, (aNQ - bNQ));
        k = sTQ.getAcrossReplicationStatistic().getLeadingDigitRule(1.0) + 1;
        p = Math.pow(10.0, k);
        assertTrue(JSLMath.within(aTQ, bTQ, p));
        System.out.printf("k = %d, p = %f, aTQ = %f, bTQ = %f, aTQ - bTQ = %f%n", k, p, aTQ, bTQ, (aTQ - bTQ));
        k = sST.getAcrossReplicationStatistic().getLeadingDigitRule(1.0) + 1;
        p = Math.pow(10.0, k);
        assertTrue(JSLMath.within(aST, bST, p));
        System.out.printf("k = %d, p = %f, aTST = %f, bTST = %f, aST - bST = %f%n", k, p, aST, bST, (aST - bST));
    }
}
