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
package test.misc;

import jsl.utilities.math.JSLMath;

/**
 *
 * @author rossetti
 */
public class TestInfinity {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        testing1();
    }

    public static void testing1() {
        JSLMath.printParameters(System.out);
        System.out.println("This is a test");
        if (Double.isInfinite(Double.NEGATIVE_INFINITY)) {
            System.out.println("Double.NEGATIVE_INFINITY is infinite");
        }

        if (Double.isInfinite(Double.POSITIVE_INFINITY)) {
            System.out.println("Double.POSITIVE_INFINITY is infinite");
        }

        if (Double.POSITIVE_INFINITY < Double.POSITIVE_INFINITY) {
            System.out.println("Double.POSITIVE_INFINITY < Double.POSITIVE_INFINITY");
        }

        if (Double.POSITIVE_INFINITY == Double.POSITIVE_INFINITY) {
            System.out.println("Double.POSITIVE_INFINITY == Double.POSITIVE_INFINITY");
        }

        if (0 < Double.POSITIVE_INFINITY) {
            System.out.println("0< Double.POSITIVE_INFINITY");
        }

        if (2 < Double.POSITIVE_INFINITY) {
            System.out.println("2< Double.POSITIVE_INFINITY");
        }

        if (0 > Double.NEGATIVE_INFINITY) {
            System.out.println("0> Double.NEGATIVE_INFINITY");
        }

        if (Double.NEGATIVE_INFINITY < Double.POSITIVE_INFINITY) {
            System.out.println("Double.NEGATIVE_INFINITY < Double.POSITIVE_INFINITY");
        }

        if (Double.NaN < Double.NEGATIVE_INFINITY) {
            System.out.println("Double.NaN < Double.NEGATIVE_INFINITY");
        } else if (Double.NaN > Double.NEGATIVE_INFINITY) {
            System.out.println("Double.NaN > Double.NEGATIVE_INFINITY");
        } else {
            System.out.println("Double.NaN = Double.NEGATIVE_INFINITY");
        }


        if (Double.NaN < Double.POSITIVE_INFINITY) {
            System.out.println("Double.NaN < Double.Double.POSITIVE_INFINITY");
        } else if (Double.NaN > Double.POSITIVE_INFINITY) {
            System.out.println("Double.NaN > Double.POSITIVE_INFINITY");
        } else {
            System.out.println("Double.NaN = Double.POSITIVE_INFINITY");
        }

        if (Double.NaN < 0) {
            System.out.println("Double.NaN < 0");
        } else if (Double.NaN > 0) {
            System.out.println("Double.NaN > 0");
        } else {
            System.out.println("Double.NaN = 0");
        }

        double value = Double.NaN;//Double.POSITIVE_INFINITY;
        double myLowerLimit = 0;//Double.NEGATIVE_INFINITY;
        double myUpperLimit = 2;//Double.POSITIVE_INFINITY;

        if ((value < myLowerLimit) || (value > myUpperLimit)) {
            throw new IllegalArgumentException("Invalid argument. supplied value was not in range, [" + myLowerLimit + "," + myUpperLimit + "]");
        }
    }
}
