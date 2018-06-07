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
