/*
 * Copyright (c) 2018. Manuel D. Rossetti, rossetti@uark.edu
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

import jsl.utilities.random.distributions.DUniform;
import jsl.utilities.statistic.Statistic;

/**
 */
public class CrapsGame {

    public static void main(String[] args) {

        DUniform d1 = new DUniform(1, 6);
        DUniform d2 = new DUniform(1, 6);

        Statistic probOfWinning = new Statistic("Prob of winning");
        Statistic numTosses = new Statistic("Number of Toss Statistics");

        int numGames = 5000;
        for (int k = 1; k <= numGames; k++) {
            boolean winner = false;
            int point = (int) d1.getValue() + (int) d2.getValue();
            int numberoftoss = 1;

            if (point == 7 || point == 11) {
                // automatic winner
                winner = true;
            } else if (point == 2 || point == 3 || point == 12) {
                // automatic loser
                winner = false;
            } else { // now must roll to get point
                boolean continueRolling = true;
                while (continueRolling == true) {
                    // increment number of tosses
                    numberoftoss++; 
                    // make next roll
                    int nextRoll = (int) d1.getValue() + (int) d2.getValue();
                    if (nextRoll == point) {
                        // hit the point, stop rolling
                        winner = true;
                        continueRolling = false;
                    } else if (nextRoll == 7) {
                        // crapped out, stop rolling
                        winner = false;
                        continueRolling = false;
                    }
                }
            }
            probOfWinning.collect(winner);
            numTosses.collect(numberoftoss);
        }

        System.out.printf("Estimate of P(win) = %10.3f\n", probOfWinning.getAverage());
        System.out.printf("Estimate of E[#tosses] = %10.3f\n", numTosses.getAverage());

        System.out.println(probOfWinning);
        System.out.println(numTosses);
    }
}
