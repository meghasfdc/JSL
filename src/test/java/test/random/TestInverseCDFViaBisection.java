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
import jsl.utilities.random.distributions.Distribution;
import jsl.utilities.random.distributions.Normal;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author rossetti
 */
public class TestInverseCDFViaBisection {
    
    public TestInverseCDFViaBisection() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void test1() {
        
        Normal n = new Normal();
        
        double p = 0.95;
        double x1 = n.invCDF(p);
        System.out.println("invCDF("+ p + ") = " + x1);

        System.out.println("CDF("+ -5 + ") = " + n.cdf(-5));
        System.out.println("CDF("+ 5 + ") = " + n.cdf(5));
        
        double x2 = Distribution.inverseContinuousCDFViaBisection(n, p, -5, 5);
        
        System.out.println("invCDF("+ p + ") = " + x2);
        
        assertTrue(JSLMath.within(x1, x2, 0.000001));
    }
}
