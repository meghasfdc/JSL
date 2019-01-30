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
public class BankAccountFL extends SchedulingElement {

    private final TimeWeighted myBalance;

    private final RandomVariable myTBFreqPayments;

    private final RandomVariable myTBOccasionalPayments;

    private final RandomVariable myTBWithdrawals;

    private final RandomVariable myFreqPaymentSize;

    private final RandomVariable myOccasionalPaymentSize;

    private final RandomVariable myWithdrawalSize;

    public BankAccountFL(ModelElement parent) {
        this(parent, null);
    }

    public BankAccountFL(ModelElement parent, String name) {
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
        schedule(this::frequentPayment).in(myTBFreqPayments).units();
        schedule(this::occasionalPayment).in(myTBOccasionalPayments).units();
        schedule(this::withdraw).in(myTBWithdrawals).units();
    }

    private void frequentPayment(JSLEvent evt){
        myBalance.increment(myFreqPaymentSize.getValue());
        schedule(this::frequentPayment).in(myTBFreqPayments).units();
    }

    private void occasionalPayment(JSLEvent evt){
        myBalance.increment(myOccasionalPaymentSize.getValue());
        schedule(this::occasionalPayment).in(myTBOccasionalPayments).units();
    }

    private void withdraw(JSLEvent evt){
        myBalance.decrement(myWithdrawalSize.getValue());
        schedule(this::withdraw).in(myTBWithdrawals).units();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Simulation sim = new Simulation("Bank Account Example");

        new BankAccountFL(sim.getModel());

        sim.setNumberOfReplications(20);
        sim.setLengthOfReplication(400.0);

        SimulationReporter r = sim.makeSimulationReporter();

        // tell the simulaton to run
        sim.run();

        r.printAcrossReplicationSummaryStatistics();

        System.out.println("Done!");
    }
}
