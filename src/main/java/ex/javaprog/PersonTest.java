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

import java.util.ArrayList;

public class PersonTest {

    public static void main(String[] args) {
        
        Person p1 = new Person("Manuel", 72, "black");
        Person p2 = new Person("Joseph", 72);
        Person p3 = new Person("Maria", 66);
        Person p4 = new Person("Amy", 66);

        // prints out a Person using the toString() method
        System.out.println(p1);
        
        //print out a name
        System.out.println("p1's name = " + p1.getName());
        // change a hair color
        p1.changeHairColor("blonde");

        System.out.println("After the hair color change");
        System.out.println(p1);
        
        // create an array that can hold instances of Person
        Person[] p = new Person[4];
        p[0] = p1;
        p[1] = p2;
        p[2] = p3;
        p[3] = p4;

        for(int i=0; i<p.length;i++){
            p[i].printAttributes();
            System.out.println();
        }

        // create an ArrayList that can hold the people
        ArrayList<Person> b = new ArrayList<Person>();
        b.add(p1);
        b.add(p2);
        b.add(p3);
        b.add(p4);
        b.add(new Person("Al", 72, "black"));
        for(Person y: b){
            System.out.println(y);
        }
        //print out a name
        System.out.println("A1's name = " +  b.get(4).getName());
       
    }

}
