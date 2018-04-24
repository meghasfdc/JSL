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

import jsl.modeling.Simulation;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author rossetti
 */
public class SimulationTest {

    private Simulation mySim;

    //private ExperimentParameters myExp;
    //private Model myModel;
    @Before
    public void setUp() {

        mySim = new Simulation();
        //myExp = mySim.getExperiment();
        //myModel = mySim.getModel();

    }

    @Test
    public void runTwoIndivReps() {
        mySim.setNumberOfReplications(2);
        System.out.println("Begin Test 1 ------------------------------------------");
        System.out.println("Run two individual reps using runNext()");
        mySim.initialize();
        mySim.runNext();
        mySim.runNext();
        if (mySim.hasNextReplication()) {
            mySim.runNext();
        }
        System.out.println(mySim);
        System.out.println("End Test 1 ------------------------------------------");
        assertTrue(mySim.getCurrentReplicationNumber() == 2);
    }

    @Test
    public void runTwoThenStopReps() {
        mySim.setNumberOfReplications(4);
        System.out.println("Begin Test 2 ------------------------------------------");
        System.out.println("Run two individual reps using runNext() then stop");
        mySim.initialize();
        System.out.println(mySim);
        mySim.runNext();
        System.out.println(mySim);
        mySim.runNext();
        System.out.println(mySim);
        mySim.end();
        System.out.println(mySim);
        System.out.println("End Test 2 ------------------------------------------");
        assertTrue(mySim.isDone());
        assertTrue(mySim.isEnded());
    }

    @Test
    public void runTwoIndivRepsWithError() {
        mySim.setNumberOfReplications(2);
        System.out.println("Begin Test 3 ------------------------------------------");
        System.out.println("Run two individual reps using runNext()");
        mySim.initialize();
        mySim.runNext();
        mySim.runNext();
        boolean f = false;
        try {
            mySim.runNext();//should cause an error
        } catch (jsl.modeling.NoSuchStepException e) {
             System.out.println("### catch the error");
             System.out.println(e);
             f = true;
        }
        System.out.println(mySim);
        System.out.println("End Test 3 ------------------------------------------");
        assertTrue(f);
    }

    @Test
    public void runAllReps() {
        int r = 5;
        mySim.setNumberOfReplications(r);
        System.out.println("Begin Test 4 ------------------------------------------");
        System.out.println("Run " + r + " reps Via run()");
        //mySim.initialize();
        mySim.run();
        System.out.println(mySim);
        System.out.println("End Test 4 ------------------------------------------");
        assertTrue(mySim.getCurrentReplicationNumber() == r);
    }

    @Test
    public void runOneRep() {
        int r = 1;
        mySim.setNumberOfReplications(r);
        System.out.println("Begin Test 5 ------------------------------------------");
        System.out.println("Run " + r + " reps Via run()");
       // mySim.initialize();
        mySim.run();
        System.out.println(mySim);
        System.out.println("End Test 5 ------------------------------------------");
        assertTrue(mySim.getCurrentReplicationNumber() == r);
    }
}
