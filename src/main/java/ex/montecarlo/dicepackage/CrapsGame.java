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
package ex.montecarlo.dicepackage;

public class CrapsGame {

    protected boolean winner = false;

    protected int numberoftosses = 0;

    protected RollIfc dice = new TwoSixSidedDice();

    public boolean play() {
        winner = false;
        int point = dice.roll();
        numberoftosses = 1;
        if (point == 7 || point == 11) {
            winner = true; // automatic winner
        } else if (point == 2 || point == 3 || point == 12) {
            winner = false; // automatic loser
        } else { // now must roll to get point
            boolean continueRolling = true;
            while (continueRolling == true) {
                // increment number of tosses
                numberoftosses++;
                // make next roll
                int nextRoll = dice.roll();
                // if next roll == point then winner = 1, contineRolling = false
                // else if next roll = 7 then winner = 0, continueRolling = false
                if (nextRoll == point) {
                    winner = true;
                    continueRolling = false;
                } else if (nextRoll == 7) {
                    winner = false;
                    continueRolling = false;
                }
            }
        }

        return winner;

    }

    public boolean getResult() {
        return winner;
    }

    public int getNumberOfTosses() {
        return numberoftosses;
    }

    public static void main(String[] args) {
        CrapsGame g = new CrapsGame();
        g.play();
        System.out.println("result = " + g.getResult());
        System.out.println("# tosses = " + g.getNumberOfTosses());
    }
}
