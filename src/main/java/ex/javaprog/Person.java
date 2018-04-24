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

public class Person {

    private static int numPeopleCreated = 0;

    private static String defaultHairColor = "Brown";

    private int id;

    private String name;

    private float height; // inches

    private String haircolor;

    public Person(String name, int height) {
        this(name, height, defaultHairColor);
    }

    public Person(String name, int height, String haircolor) {
        numPeopleCreated = numPeopleCreated + 1;
        this.id = numPeopleCreated;
        this.name = name;
        this.height = height;
        this.haircolor = haircolor;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public float getHeight() {
        return height;
    }

    public String getHairColor() {
        return haircolor;
    }

    public void changeHairColor(String color) {
        this.haircolor = color;
    }

    public void printAttributes() {
        System.out.println("id = " + id);
        System.out.println("name = " + name);
        System.out.println("height = " + height);
        System.out.println("haircolor = " + haircolor);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("id = ");
        sb.append(id);
        sb.append("\n");
        sb.append("name = ");
        sb.append(name);
        sb.append("\n");
        sb.append("height = ");
        sb.append(height);
        sb.append("\n");
        sb.append("haircolor = ");
        sb.append(haircolor);
        sb.append("\n");

        return sb.toString();
    }

    public static void setDefaultHairColor(String color) {
        defaultHairColor = color;
    }

    public static int getTotalNumberPeopleCreated() {
        return numPeopleCreated;
    }
}
