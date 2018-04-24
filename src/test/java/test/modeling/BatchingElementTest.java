/*
 *  Copyright (C) 2010 rossetti
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
 *  of Java classes that permit the development and execution of discrete event
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
package test.modeling;

import jsl.modeling.elements.variable.ResponseVariable;
import jsl.modeling.elements.variable.TimeWeighted;
import jsl.utilities.statistic.StatisticAccessorIfc;
import jsl.utilities.math.JSLMath;
import ex.queueing.DriverLicenseBureauWithQ;
import jsl.utilities.random.distributions.Exponential;
import jsl.modeling.ExperimentGetIfc;
import jsl.modeling.Model;
import jsl.modeling.Simulation;
import jsl.modeling.StatisticalBatchingElement;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author rossetti
 */
public class BatchingElementTest {

    private Simulation mySim;

    private ExperimentGetIfc myExp;

    private Model myModel;

    DriverLicenseBureauWithQ myDLB;

    StatisticalBatchingElement myBE;

    @Before
    public void setUp() {

        mySim = new Simulation();
        myExp = mySim.getExperiment();
        myModel = mySim.getModel();

        mySim.turnOnStatisticalBatching();
        StatisticalBatchingElement be = mySim.getStatisticalBatchingElement().get();

        // create the model element and attach it to the main model
        myDLB = new DriverLicenseBureauWithQ(myModel);

        // set the parameters of the experiment

        mySim.setLengthOfReplication(200000.0 * 30.0);
        mySim.setLengthOfWarmUp(50000.0);
        System.out.println(myModel.getResponseVariableNames());
    }

    @Test
    public void test1() {

        int k = 0;
        double p = 0.0;

        Exponential d = new Exponential(0.8);
        myDLB.setServiceDistributionInitialRandomSource(d);

        // run the simulation
        mySim.run();

        System.out.println(myBE);

        TimeWeighted tw = (TimeWeighted) myModel.getResponseVariable("DriverLicenseQ:NumInQ");
        System.out.println("tw = " + tw);

        StatisticAccessorIfc sNQ = myBE.getBatchStatistic(tw);

        tw = (TimeWeighted) myModel.getResponseVariable("NS");
        StatisticAccessorIfc sNS = myBE.getBatchStatistic(tw);

        tw = (TimeWeighted) myModel.getResponseVariable("NumBusy");
        StatisticAccessorIfc sNB = myBE.getBatchStatistic(tw);

        ResponseVariable rv = myModel.getResponseVariable("DriverLicenseQ:TimeInQ");
        StatisticAccessorIfc sTQ = myBE.getBatchStatistic(rv);

        System.out.println(sTQ);

        k = sNB.getLeadingDigitRule(1.0) + 1;
        p = Math.pow(10.0, k);
        assertTrue(JSLMath.within(sNB.getAverage(), 0.8, p));

        k = sNS.getLeadingDigitRule(1.0) + 1;
        p = Math.pow(10.0, k);
        assertTrue(JSLMath.within(sNS.getAverage(), 4.0, p));

        k = sNQ.getLeadingDigitRule(1.0) + 1;
        p = Math.pow(10.0, k);
        assertTrue(JSLMath.within(sNQ.getAverage(), 3.2, p));

        k = sTQ.getLeadingDigitRule(1.0) + 1;
        p = Math.pow(10.0, k);
        assertTrue(JSLMath.within(sTQ.getAverage(), 3.2, p));
    }
}
