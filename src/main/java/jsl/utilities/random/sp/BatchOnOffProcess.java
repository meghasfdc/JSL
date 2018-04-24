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
package jsl.utilities.random.sp;

import jsl.utilities.random.AbstractRandom;
import jsl.utilities.random.RandomIfc;
import jsl.utilities.random.distributions.Constant;
import jsl.utilities.random.distributions.Geometric;
import jsl.utilities.random.distributions.ShiftedGeometric;
import jsl.utilities.random.rng.RngIfc;
import jsl.utilities.reporting.JSL;
import jsl.utilities.statistic.Statistic;

/**
 * @author rossetti
 *
 */
public class BatchOnOffProcess extends AbstractRandom {

    /** BUSY means that the process can be non-zero for a slot
     *  IDLE means that the process is zero for a slot
     *
     */
    public static enum State {

        BUSY, IDLE
    };

    /** Governs the number of busy slots in a row, when
     *  the process goes busy
     *
     */
    protected RandomIfc myDistNumBusySlots;

    /** Governs the number of idle slots in a row, when
     *  the process goes idle
     *
     */
    protected RandomIfc myDistNumIdleSlots;

    /** Governs the amount returned when a slot is busy
     *
     */
    protected RandomIfc myDistAmtNZ;

    /** The default starting state is the idle state
     *
     */
    protected State myCurrentState;

    /** The number of slots for the current period
     *
     */
    protected int myNumSlotsInPeriod = 1;

    /** Counts the number of slots in the period
     *  up to the required number for the period
     *
     */
    protected int myPeriodSlotCounter = 0;

    /** The value returned previous to the current value
     *
     */
    protected double myPreviousValue = 0.0;

    /** Specifies the initial state, assumed idle
     *
     */
    protected State myInitialState = State.IDLE;

    /** Indicates whether the process has been initialized
     *  using the initial state
     *
     */
    protected boolean myInitFlag = false;

    /**
     *
     */
    protected double myCurrentValue = 0.0;

    /** Creates a default BatchOnOffProcess with:
     *  number of busy slots = Constant.ONE
     *  number of idle slots = Constant.ONE
     *  busy amount = Constant.ONE
     *
     */
    public BatchOnOffProcess() {
        this(Constant.ONE, Constant.ONE, Constant.ONE, null);
    }

    /** Creates a default BatchOnOffProcess with:
     *  number of busy slots = Constant.ONE
     *  number of idle slots = Constant.ONE
     *  busy amount = Constant.ONE
     *
     */
    public BatchOnOffProcess(String name) {
        this(Constant.ONE, Constant.ONE, Constant.ONE, name);
    }

    /**
     *
     * @param numBusySlots
     * @param numIdleSlots
     * @param busyAmt
     */
    public BatchOnOffProcess(RandomIfc numBusySlots, RandomIfc numIdleSlots, RandomIfc busyAmt) {
        this(numBusySlots, numIdleSlots, busyAmt, null);
    }

    /**
     *
     * @param numBusySlots
     * @param numIdleSlots
     * @param busyAmt
     */
    public BatchOnOffProcess(RandomIfc numBusySlots, RandomIfc numIdleSlots, RandomIfc busyAmt, String name) {
        super(name);
        setNumberOfBusySlotsDistribution(numBusySlots);
        setNumberOfIdleSlotsDistribution(numIdleSlots);
        setBusyAmountDistribution(busyAmt);
    }

    /** Gets the RandomIfc governing the number of busy slots in a busy period
     * @return the number of busy slots distribution
     */
    public RandomIfc getNumberOfBusySlotsDistribution() {
        return myDistNumBusySlots;
    }

    /** Governs the number of busy slots in a busy period
     *
     * @param numBusySlots the numBusySlots to set, must not be null
     */
    public void setNumberOfBusySlotsDistribution(RandomIfc numBusySlots) {
        if (numBusySlots == null) {
            throw new IllegalArgumentException("The distribution of the number of busy slots must not be null");
        }
        myDistNumBusySlots = numBusySlots;
    }

    /** Gets the RandomIfc governing the number of idle slots in a idle period
     *
     * @return the number of idle slots distribution
     */
    public RandomIfc getNumberOfIdleSlotsDistribution() {
        return myDistNumIdleSlots;
    }

    /** Governs the number of busy slots in a idle period
     *
     * @param numIdleSlots the numIdleSlots to set, must not be null
     */
    public void setNumberOfIdleSlotsDistribution(RandomIfc numIdleSlots) {
        if (numIdleSlots == null) {
            throw new IllegalArgumentException("The distribution of the number of idle slots must not be null");
        }
        myDistNumIdleSlots = numIdleSlots;
    }

    /** Governs the amount for a busy slot
     *
     * @return the amount distribution
     */
    public RandomIfc getBusyAmountDistribution() {
        return myDistAmtNZ;
    }

    /** Sets the amount for a busy slot
     *
     * @param busyAmt the amount for a busy slot, must not be null
     */
    public void setBusyAmountDistribution(RandomIfc busyAmt) {
        if (busyAmt == null) {
            throw new IllegalArgumentException("The distribution of the amount for busy slot must not be null");
        }
        myDistAmtNZ = busyAmt;
    }

    /* (non-Javadoc)
     * @see jsl.utilities.random.RandomIfc#newInstance()
     */
    public RandomIfc newInstance() {
        BatchOnOffProcess b = new BatchOnOffProcess();
        b.myInitialState = myInitialState;
        b.myInitFlag = myInitFlag;
        b.myCurrentState = myCurrentState;
        b.myCurrentValue = myCurrentValue;
        b.myPreviousValue = myPreviousValue;
        b.myDistAmtNZ = myDistAmtNZ.newInstance();
        b.myDistNumBusySlots = myDistNumBusySlots.newInstance();
        b.myDistNumIdleSlots = myDistNumIdleSlots.newInstance();
        b.myNumSlotsInPeriod = myNumSlotsInPeriod;
        b.myPeriodSlotCounter = myPeriodSlotCounter;
        return b;
    }

       /* (non-Javadoc)
     * @see jsl.utilities.random.RandomIfc#newInstance()
     */
    public RandomIfc newInstance(RngIfc rng) {
        BatchOnOffProcess b = new BatchOnOffProcess();
        b.myInitialState = myInitialState;
        b.myInitFlag = myInitFlag;
        b.myCurrentState = myCurrentState;
        b.myCurrentValue = myCurrentValue;
        b.myPreviousValue = myPreviousValue;
        b.myDistAmtNZ = myDistAmtNZ.newInstance(rng);
        b.myDistNumBusySlots = myDistNumBusySlots.newInstance(rng);
        b.myDistNumIdleSlots = myDistNumIdleSlots.newInstance(rng);
        b.myNumSlotsInPeriod = myNumSlotsInPeriod;
        b.myPeriodSlotCounter = myPeriodSlotCounter;
        return b;
    }

    /** The parameters are ordered in the array by:
     *  1) amount distribution parameters
     *  2) Number of busy slots distribution parameters
     *  3) Number of idle slots distribution parameters
     *  4) Initial state 1.0 means busy, 0.0 means idle
     *
     * @return the parameters as an array
     */
    public double[] getParameters() {
        double[] p1 = myDistAmtNZ.getParameters();
        double[] p2 = myDistNumBusySlots.getParameters();
        double[] p3 = myDistNumIdleSlots.getParameters();
        int n = p1.length + p2.length + p3.length;
        double[] p = new double[n + 1];
        System.arraycopy(p1, 0, p, 0, p1.length);
        System.arraycopy(p2, 0, p, p1.length, p2.length);
        System.arraycopy(p3, 0, p, p1.length + p2.length, p3.length);
        if (myInitialState == State.BUSY) {
            p[n] = 1.0;
        }
        return p;
    }

    /** The parameters should be ordered in the array by:
     *  1) amount distribution parameters
     *  2) Number of busy slots distribution parameters
     *  3) Number of idle slots distribution parameters
     *  4) Initial state 1.0 means busy, 0.0 means idle
     *
     * @param parameters the parameters as an array
     */
    @Override
    public void setParameters(double[] parameters) {
        if (parameters == null) {
            throw new IllegalArgumentException("The parameters array was null");
        }

        double[] p1 = myDistAmtNZ.getParameters();
        double[] p2 = myDistNumBusySlots.getParameters();
        double[] p3 = myDistNumIdleSlots.getParameters();
        int n = p1.length + p2.length + p3.length + 1;
        if (parameters.length != n) {
            throw new IllegalArgumentException("The length of the array is not sufficient.");
        }

        System.arraycopy(parameters, 0, p1, 0, p1.length);
        System.arraycopy(parameters, p1.length, p2, 0, p2.length);
        System.arraycopy(parameters, p1.length + p2.length, p3, 0, p3.length);

        myDistAmtNZ.setParameters(p1);
        myDistNumBusySlots.setParameters(p2);
        myDistNumIdleSlots.setParameters(p3);
        if (parameters[n] == 0.0) {
            myInitialState = State.IDLE;
        } else {
            myInitialState = State.BUSY;
        }

    }

    /** Sets the initial state to the provided state
     *  Does not change values from getValue() unless
     *  resetInitialization() is used
     *
     * @param state
     */
    public void setInitialState(State state) {
        myInitialState = state;
    }

    /** Causes the process to think that it has not been
     *  initialized.  It will use the initial state upon
     *  the next call to getValue()
     *
     */
    public void resetInitialization() {
        myInitFlag = false;
    }

    /* (non-Javadoc)
     * @see jsl.utilities.random.GetValueIfc#getValue()
     */
    public double getValue() {

        if (myInitFlag == false) {
            myInitFlag = true;
            myCurrentState = myInitialState;
            if (myCurrentState == State.BUSY) {
                myNumSlotsInPeriod = (int) myDistNumBusySlots.getValue();
            } else {
                myNumSlotsInPeriod = (int) myDistNumIdleSlots.getValue();
            }
        }

        // determine if reached the number of slots for the period
        if (myPeriodSlotCounter == myNumSlotsInPeriod) {
            // need to change state
            if (myCurrentState == State.BUSY) {
                // transition to OFF
                myNumSlotsInPeriod = (int) myDistNumIdleSlots.getValue();
                if (myNumSlotsInPeriod < 1) {
                    throw new IllegalStateException("The number of slots in a period must be >= 1");
                }
                myCurrentState = State.IDLE;
            } else {
                // transition to ON
                myNumSlotsInPeriod = (int) myDistNumBusySlots.getValue();
                if (myNumSlotsInPeriod < 1) {
                    throw new IllegalStateException("The number of slots in a period must be >= 1");
                }
                myCurrentState = State.BUSY;
            }
            // reset slot counter
            myPeriodSlotCounter = 0;
        }

        // remember the previous value
        myPreviousValue = myCurrentValue;

        // determine the amount based on state
        if (myCurrentState == State.BUSY) {
            myCurrentValue = myDistAmtNZ.getValue();
        } else {
            myCurrentValue = 0.0;
        }

        // count the slot
        myPeriodSlotCounter++;

        return myCurrentValue;
    }

    /* (non-Javadoc)
     * @see jsl.utilities.random.rng.RandomStreamIfc#resetNextSubstream()
     */
    public void advanceToNextSubstream() {
        myDistAmtNZ.advanceToNextSubstream();
        myDistNumBusySlots.advanceToNextSubstream();
        myDistNumIdleSlots.advanceToNextSubstream();
    }

    /* (non-Javadoc)
     * @see jsl.utilities.random.rng.RandomStreamIfc#resetStartStream()
     */
    public void resetStartStream() {
        myDistAmtNZ.resetStartStream();
        myDistNumBusySlots.resetStartStream();
        myDistNumIdleSlots.resetStartStream();
    }

    /* (non-Javadoc)
     * @see jsl.utilities.random.rng.RandomStreamIfc#resetStartSubstream()
     */
    public void resetStartSubstream() {
        myDistAmtNZ.resetStartSubstream();
        myDistNumBusySlots.resetStartSubstream();
        myDistNumIdleSlots.resetStartSubstream();
    }

    /* (non-Javadoc)
     * @see jsl.utilities.random.rng.RandomStreamIfc#setAntithetic(boolean)
     */
    public void setAntitheticOption(boolean flag) {
        myDistAmtNZ.setAntitheticOption(flag);
        myDistNumBusySlots.setAntitheticOption(flag);
        myDistNumIdleSlots.setAntitheticOption(flag);
    }

    public boolean getAntitheticOption() {
        boolean b = myDistAmtNZ.getAntitheticOption();
        b = b && myDistNumBusySlots.getAntitheticOption();
        b = b && myDistNumIdleSlots.getAntitheticOption();
        return b;
    }

    public static BatchOnOffProcess makeGeometricBatchOnOffProcess(double meanNumIdleSlots,
            double meanNumBusySlots, double meanNZAmt) {
        if (meanNumIdleSlots <= 0.0) {
            throw new IllegalArgumentException("The mean number of idle slots must be > 0");
        }

        if (meanNumBusySlots <= 0.0) {
            throw new IllegalArgumentException("The mean number of busy slots must be > 0");
        }

        if (meanNZAmt <= 0.0) {
            throw new IllegalArgumentException("The mean non zero amount must be > 0");
        }

        // mean of geometric mu =  (myLowerLimit + ((myProbFailure) / myProbSuccess))
        // myLowerLimit = 1
        // p = 1/mu

        ShiftedGeometric b = new ShiftedGeometric(1.0 / meanNumBusySlots);
        ShiftedGeometric i = new ShiftedGeometric(1.0 / meanNumIdleSlots);
        ShiftedGeometric a = new ShiftedGeometric(1.0 / meanNZAmt);

        return new BatchOnOffProcess(b, i, a);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        Statistic s1 = new Statistic("amounts");
        Statistic s2 = new Statistic("p-zero");
        // IntermittentDemandStatistic s3 = new IntermittentDemandStatistic("intermittent stats");

        BatchOnOffProcess p = new BatchOnOffProcess();
        ShiftedGeometric nb = new ShiftedGeometric(0.4);
        p.setNumberOfBusySlotsDistribution(nb);
        ShiftedGeometric ni = new ShiftedGeometric(0.1);
        p.setNumberOfIdleSlotsDistribution(ni);
        ShiftedGeometric na = new ShiftedGeometric(0.8);
        p.setBusyAmountDistribution(na);

        for (int i = 1; i <= 100000; i++) {
            double x = p.getValue();
            s1.collect(x);
            s2.collect(x <= 0.0);
            // JSL.out.println(x);
            //  s3.collect(x);
            //System.out.println(x);
        }

        double enb = nb.getMean();
        double eni = ni.getMean();
        double ena = na.getMean();
        double pzero = eni / (eni + enb);
        double ec = ena * (1.0 - pzero);
        double ena2 = na.getVariance() + ena * ena;
        double vc = ec * (ena2 / ena) - ec * ec;
        System.out.println("Pzero = " + pzero);
        System.out.println("Expected value = " + ec);
        System.out.println("Variance = " + vc);
        System.out.println();
        System.out.println(s1);
        System.out.println(s2);
        // System.out.println(s3);
    }
}
