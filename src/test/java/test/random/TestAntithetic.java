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
