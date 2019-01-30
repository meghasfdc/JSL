/*
 * Copyright (c) 2019. Manuel D. Rossetti, rossetti@uark.edu
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
package ineg5823sp19.hw3;

import jsl.modeling.*;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.modeling.elements.variable.TimeWeighted;
import jsl.utilities.random.rvariable.ConstantRV;
import jsl.utilities.random.rvariable.ExponentialRV;
import jsl.utilities.random.rvariable.LognormalRV;
import jsl.utilities.random.rvariable.UniformRV;

/**
 * @author rossetti
 */
public class BankAccountEA extends SchedulingElement {

    private final TimeWeighted myBalance;

    private final RandomVariable myTBFreqPayments;

    private final RandomVariable myTBOccasionalPayments;

    private final RandomVariable myTBWithdrawals;

    private final RandomVariable myFreqPaymentSize;

    private final RandomVariable myOccasionalPaymentSize;

    private final RandomVariable myWithdrawalSize;

    private final WithdrawAction myWithdrawAction;
    private final FrequentPaymentAction myFrequentPaymentAction;
    private final OccasionalPaymentAction myOccasionalPaymentAction;

    public BankAccountEA(ModelElement parent) {
        this(parent, null);
    }

    public BankAccountEA(ModelElement parent, String name) {
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
        myWithdrawAction = new WithdrawAction();
        myFrequentPaymentAction = new FrequentPaymentAction();
        myOccasionalPaymentAction = new OccasionalPaymentAction();
    }

    @Override
    protected void initialize() {
        // schedule initial events
        scheduleEvent(myFrequentPaymentAction, myTBFreqPayments.getValue());
        scheduleEvent(myOccasionalPaymentAction, myTBOccasionalPayments.getValue());
        scheduleEvent(myWithdrawAction, myTBWithdrawals.getValue());
    }

    protected class FrequentPaymentAction implements EventActionIfc {

        @Override
        public void action(JSLEvent evt) {
            myBalance.increment(myFreqPaymentSize.getValue());
            scheduleEvent(myFrequentPaymentAction, myTBFreqPayments.getValue());
        }
    }

    protected class OccasionalPaymentAction implements EventActionIfc {

        @Override
        public void action(JSLEvent evt) {
            myBalance.increment(myOccasionalPaymentSize.getValue());
            rescheduleEvent(evt, myTBOccasionalPayments.getValue());
        }
    }

    protected class WithdrawAction implements EventActionIfc {

        @Override
        public void action(JSLEvent evt) {
            myBalance.decrement(myWithdrawalSize.getValue());
            rescheduleEvent(evt, myTBWithdrawals.getValue());
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Simulation sim = new Simulation("Bank Account Example");

        new BankAccountEA(sim.getModel());

        sim.setNumberOfReplications(20);
        sim.setLengthOfReplication(400.0);

        SimulationReporter r = sim.makeSimulationReporter();

        // tell the simulaton to run
        sim.run();

        r.printAcrossReplicationSummaryStatistics();

        System.out.println("Done!");
    }
}
