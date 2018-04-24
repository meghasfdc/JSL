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
