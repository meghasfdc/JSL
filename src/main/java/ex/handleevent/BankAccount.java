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
package ex.handleevent;

import jsl.modeling.JSLEvent;
import jsl.modeling.ModelElement;
import jsl.modeling.SchedulingElement;
import jsl.modeling.Simulation;
import jsl.modeling.SimulationReporter;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.modeling.elements.variable.TimeWeighted;
import jsl.utilities.welch.WelchDataCollectorTW;
import jsl.utilities.random.distributions.Constant;
import jsl.utilities.random.distributions.Exponential;
import jsl.utilities.random.distributions.Lognormal;
import jsl.utilities.random.distributions.Uniform;

/**
 *
 * @author rossetti
 */
public class BankAccount extends SchedulingElement {

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

    public BankAccount(ModelElement parent) {
        this(parent, null);
    }

    public BankAccount(ModelElement parent, String name) {
        super(parent, name);

        // create an initialize the balance
        myBalance = new TimeWeighted(this, 150.0, "Balance");
        //myBalance.turnOnTrace();
        myTBFreqPayments = new RandomVariable(this, new Uniform(7.0, 10.0));
        myTBOccasionalPayments = new RandomVariable(this, new Uniform(25.0, 35.0));
        myTBWithdrawals = new RandomVariable(this, new Exponential(1.0));
        myFreqPaymentSize = new RandomVariable(this, new Exponential(16.0));
        myOccasionalPaymentSize = new RandomVariable(this, new Constant(100.0));
        // used lognormal to prevent negative withdrawals
        myWithdrawalSize = new RandomVariable(this, new Lognormal(5.0, 1.0));
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

        new BankAccount(sim.getModel());

        sim.setNumberOfReplications(10);
        sim.setLengthOfReplication(400.0);

        SimulationReporter r = sim.makeSimulationReporter();

        // tell the simulaton to run
        sim.run();

        r.printAcrossReplicationSummaryStatistics();

        System.out.println("Done!");
    }
}
