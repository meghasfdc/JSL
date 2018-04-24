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
package test.random;

import jsl.utilities.math.JSLMath;
import jsl.utilities.random.distributions.Normal;
import jsl.utilities.statistic.StatisticXY;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author rossetti
 */
public class TestAntithetic {

    @Before
    public void setup() {
    }

    @Test
    public void test1() {
        Normal e = new Normal();

        Normal ea = e.newAntitheticInstance();
        StatisticXY sxy = new StatisticXY();

        for (int i = 1; i <= 10; i++) {
            double x = e.getValue();
            double xa = ea.getValue();
            sxy.collectXY(x, xa);
        }
        System.out.println(sxy);
        System.out.println("Test 1");
        System.out.println("Correlation should be = -1.0");
        assertTrue(JSLMath.equal(sxy.getCorrelationXY(), -1.0));
    }

    @Test
    public void test2() {
        Normal e = new Normal();

        Normal ea = e.newAntitheticInstance();
        boolean b = true;
        for (int i = 1; i <= 10; i++) {
            double x = e.getValue();
            double x1 = e.getAntitheticValue();
            double xa = ea.getValue();
            if (!JSLMath.equal(xa, x1)) {
                b = false;
            }
        }
        System.out.println("Test 2");
        System.out.println("Test passes if all are equal");
        System.out.println("b = " + b);
        assertTrue(b);
    }
}
