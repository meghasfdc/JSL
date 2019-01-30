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
package ineg5823sp19.hw3;

import jsl.modeling.JSLEvent;
import jsl.modeling.ModelElement;
import jsl.modeling.SchedulingElement;
import jsl.modeling.Simulation;
import jsl.modeling.SimulationReporter;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.modeling.elements.variable.TimeWeighted;
import jsl.utilities.random.rvariable.ConstantRV;
import jsl.utilities.random.rvariable.ExponentialRV;
import jsl.utilities.random.rvariable.LognormalRV;
import jsl.utilities.random.rvariable.UniformRV;

/**
 * @author rossetti
 */
public class BankAccountHE extends SchedulingElement {

    public static final int FrequentPayment = 1;

    public static final int OccasionalPayment = 2;

    public static final int Withdraw = 3;

    private TimeWeighted myBalance;

    private RandomVariable myTBFreqPayments;

    private RandomVariable myTBOccasionalPayments;

    private RandomVariable myTBWithdrawals;

    private RandomVariable myFreqPaymentSize;

    private RandomVariable myOccasionalPaymentSize;

    private RandomVariable myWithdrawalSize;

    public BankAccountHE(ModelElement parent) {
        this(parent, null);
    }

    public BankAccountHE(ModelElement parent, String name) {
        super(parent, name);

        // create an initialize the balance
        myBalance = new TimeWeighted(this, 150.0, "Balance");
        //myBalance.turnOnTrace();
        myTBFreqPayments = new RandomVariable(this, new UniformRV(7.0, 10.0));
        myTBOccasionalPayments = new RandomVariable(this, new UniformRV(25.0, 35.0));
        myTBWithdrawals = new RandomVariable(this, new ExponentialRV(1.0));
        myFreqPaymentSize = new RandomVariable(this, new ExponentialRV(16.0));
        myOccasionalPaymentSize = new RandomVariable(this, new ConstantRV(100.0));
        // used lognormal to prevent negative withdrawals
        myWithdrawalSize = new RandomVariable(this, new LognormalRV(5.0, 1.0));
    }

    @Override
    protected void initialize() {
        // schedule initial events
        scheduleEvent(myTBFreqPayments.getValue(), FrequentPayment);
        scheduleEvent(myTBOccasionalPayments.getValue(), OccasionalPayment);
        scheduleEvent(myTBWithdrawals.getValue(), Withdraw);
    }

    @Override
    protected void handleEvent(JSLEvent event) {
        // handle events
        switch (event.getType()) {
            case FrequentPayment:
                myBalance.increment(myFreqPaymentSize.getValue());
                scheduleEvent(myTBFreqPayments.getValue(), FrequentPayment);
                //rescheduleEvent(event, myTBFreqPayments.getValue());
                break;
            case OccasionalPayment:
                myBalance.increment(myOccasionalPaymentSize.getValue());
                scheduleEvent(myTBOccasionalPayments.getValue(), OccasionalPayment);
                break;
            case Withdraw:
                myBalance.decrement(myWithdrawalSize.getValue());
                scheduleEvent(myTBWithdrawals.getValue(), Withdraw);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Simulation sim = new Simulation("Bank Account Example");

        new BankAccountHE(sim.getModel());

        sim.setNumberOfReplications(20);
        sim.setLengthOfReplication(400.0);

        SimulationReporter r = sim.makeSimulationReporter();

        // tell the simulaton to run
        sim.run();

        r.printAcrossReplicationSummaryStatistics();

        System.out.println("Done!");
    }
}
