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

public class PrimitiveTypesAndOperators {

    public static void main(String[] args) {

        // call another method
        someTypes();
        someMath();
    }

    public static void someTypes(){
        
        double x = 10.0/3.0; 
        System.out.println("Sets x = " + x);
 
        int n = (int)x; 
        System.out.println("Sets n = " + n);

        float f = (float)x; 
        System.out.println("Sets f = " + f);

        long l = Long.MAX_VALUE;
        System.out.println("Biggest long = " + l);
 
        System.out.println("Biggest int = " + Integer.MAX_VALUE);
        System.out.println("Smallest int = " + Integer.MIN_VALUE);

        boolean t = true;
        System.out.println("t = " + t);
        System.out.println("not t = " + !t);
    }
    
    public static void someMath(){
        double x = 9.0;
        System.out.println("x = " + x);
        double y = Math.sqrt(x);
        System.out.println("y = " + y);
        double z = Math.pow(x, 2.0);
        System.out.println("z = " + z );
        
    }
    
}
