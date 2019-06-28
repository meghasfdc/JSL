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

package ex.montecarlo;

import jsl.utilities.random.distributions.Gamma;
import jsl.utilities.random.rvariable.*;
import jsl.utilities.reporting.JSL;

public class GenerateSomeData {

    public static void main(String[] args) {

        RVariableIfc precheck = new LognormalRV(15.333, 66.888);

        double[] parameters = Gamma.getParametersFromMoments(108.333, 2293.05);
        RVariableIfc placeItems = new GammaRV(parameters[0], parameters[1]);

        RVariableIfc bagScanning = new TriangularRV(10, 15, 75);
        RVariableIfc peopleScanning = new LognormalRV(2.0, 0.667);
        RVariableIfc pickUpBags = new UniformRV(3, 9);
        RVariableIfc bagSearch = new TriangularRV(15, 90, 120);

        JSL.out.printf("%s, %s, %s, %s, %s, %s %n","PreCheck", "PlaceItems", "BagScanning",
                "PeopleScanning", "BagPickUp", "BagSearch");
        for(int i=1; i<=100; i++){
            JSL.out.printf("%.2f, %.2f, %.2f, %.2f, %.2f, %.2f %n",
                    precheck.getValue()+7.0,
                    placeItems.getValue(),
                    bagScanning.getValue(),
                    peopleScanning.getValue()+3.0,
                    pickUpBags.getValue(),
                    bagSearch.getValue());
        }


    }
}
