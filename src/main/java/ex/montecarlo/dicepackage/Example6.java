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

import jsl.utilities.statistic.IntegerFrequency;
import jsl.utilities.statistic.Statistic;

public class Example6 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        TwoSixSidedDice d2 = new TwoSixSidedDice();

        IntegerFrequency h = new IntegerFrequency("Distribution across points ");
        int n = 100000;
        for (int i=1;i<=n;i++){
            h.collect(d2.roll());
        }

        System.out.println(h);

        int point = 2;
        Statistic s = new Statistic("Rolls to reach " + point);

        int k = 100000;
        System.out.println();
        System.out.println("Estimating number of rolls to reach " + point);

        for(int i=1;i<=k;i++){
            int rolls = d2.countRolls(point);
            s.collect(rolls);
        }

        System.out.println(s);


    }

}
