/*
 * Copyright (c) 2007, Manuel D. Rossetti (rossetti@uark.edu)
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
package jsl.utilities.random.robj;

import java.util.HashMap;
import java.util.Map;

public class DUniformList<T> extends RList<T> {

    public DUniformList() {
        super();
    }

    @Override
    public T getRandomElement() {
        if (myElements.isEmpty()) {
            return null;
        }

        return (myElements.get(myRNG.randInt(0, myElements.size() - 1)));
    }

    @Override
    public RList<T> newInstance() {
        DUniformList<T> l = new DUniformList<T>();
        l.addAll(this);
        return l;
    }

    public static void main(String[] args) {

        DUniformList<String> originSet = new DUniformList<String>();

        originSet.add("KC");
        originSet.add("CH");
        originSet.add("NY");

        for (int i = 1; i <= 10; i++) {
            System.out.println(originSet.getRandomElement());
        }

        Map<String, DUniformList<String>> od = new HashMap<String, DUniformList<String>>();

        DUniformList<String> kcdset = new DUniformList<String>();

        kcdset.add("CO");
        kcdset.add("AT");
        kcdset.add("NY");

        DUniformList<String> chdset = new DUniformList<String>();

        chdset.add("AT");
        chdset.add("NY");
        chdset.add("KC");

        DUniformList<String> nydset = new DUniformList<String>();

        nydset.add("AT");
        nydset.add("KC");
        nydset.add("CH");

        od.put("KC", kcdset);
        od.put("CH", chdset);
        od.put("NY", nydset);

        for (int i = 1; i <= 10; i++) {
            String key = originSet.getRandomElement();
            DUniformList<String> rs = od.get(key);
            System.out.println(rs.getRandomElement());
        }
    }
}
