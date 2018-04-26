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

package ex.variables;

import jsl.modeling.JSLEvent;
import jsl.modeling.ModelElement;
import jsl.modeling.Simulation;
import jsl.modeling.elements.variable.LevelResponse;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.modeling.elements.variable.ResponseVariable;
import jsl.utilities.random.distributions.Normal;


public class TestLevelResponse extends ModelElement {

    private RandomVariable myRV;

    private LevelResponse myLR;

    private ResponseVariable myR;

    public TestLevelResponse(ModelElement parent) {
        this(parent, null);
    }

    public TestLevelResponse(ModelElement parent, String name) {
        super(parent, name);
        myRV = new RandomVariable(this, new Normal());
        myLR = new LevelResponse(myRV, 0.0);
        myR = new ResponseVariable(this, "Observations");
    }

    @Override
    protected void initialize() {
        schedule(this::variableUpdate).in(1.0).units();
    }

    protected void variableUpdate(JSLEvent evnt){

        double x = myRV.getValue();
        myR.setValue(x);
        schedule(this::variableUpdate).in(1.0).units();
        //System.out.println("in variable update");
    }

    public static void main(String[] args) {
        Simulation s = new Simulation("Temp");

        new TestLevelResponse(s.getModel());

        s.setNumberOfReplications(10);
        s.setLengthOfReplication(10000.0);
        s.run();

        s.printHalfWidthSummaryReport();
    }

}