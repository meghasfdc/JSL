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

package ex.variables;

import jsl.modeling.Model;
import jsl.modeling.ModelElement;
import jsl.modeling.Simulation;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.modeling.elements.variable.ResponseVariable;
import jsl.utilities.random.rvariable.NormalRV;

public class TestMC extends ModelElement {

    private RandomVariable myRV;
    private ResponseVariable myResponse;

    public TestMC(ModelElement parent) {
        this(parent, null);
    }

    public TestMC(ModelElement parent, String name) {
        super(parent, name);
        myRV = new RandomVariable(this, new NormalRV());
        myResponse = new ResponseVariable(this, "MC stat");
    }

    @Override
    protected void initialize() {
        super.initialize();
        System.out.println("in initialize");
    }

    @Override
    protected void montecarlo() {
        super.montecarlo();
        System.out.println("in montecarlo");
        myResponse.setValue(myRV.getValue());
    }

    public static void main(String[] args) {
        System.out.println("MC Method Test");

        // Create the simulation
        Simulation sim = new Simulation("MC Method Sim");

        // get the containing model
        Model m = sim.getModel();
        m.setMonteCarloOption(true);

        new TestMC(m);

        // set the parameters of the experiment
        sim.setNumberOfReplications(10);

        // run the simulation
        sim.run();

        sim.printHalfWidthSummaryReport();
    }
}
