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

package ex.javaprog;

public class FlowOfControl {

    public static void main(String[] args) {

        // examples of primitive types and arrays
        // declare and create an array of doubles
        double[] x = {2.1, 3.2, 4.3, 5.4, 6.5};
        // declare another array and assign x to it, they refer to the same array
        double[] y = x;
        // declare an array z of size x.length
        double[] z = new double[x.length];
        // copy x to z
        System.arraycopy(x, 0, z, 0, x.length);
        //change  some elements
        y[1] = 99.0;
        z[4] = -1.0;
        // print out arrays
        for(int i=0; i < x.length; i++)
            System.out.println("x["+i+"] = " + x[i] + "\t y["+i+"] = " + y[i] +
                    "\t z["+i+"] = " + z[i]);

        // skip a line in output
        System.out.println();
        
        //while loop
        int k = 0;
        while(k < x.length){
            if (k == 2){
                System.out.println("Hey, k is 2");
            } else {
                System.out.println("Hey, k is not 2");
            }
            System.out.println("x["+k+"] = " + x[k]);
            k = k + 1;
        }

    }

}
