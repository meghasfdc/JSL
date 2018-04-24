/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.random;

import java.util.Arrays;
import jsl.utilities.math.JSLMath;
import jsl.utilities.random.distributions.DEmpiricalCDF;
import jsl.utilities.random.rvariable.JSLRandom;
import jsl.utilities.random.rng.RNStreamFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author rossetti
 */
public class TestDEmpirical {

    public TestDEmpirical() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
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
    public void test() {
        double[] p = {0.7, 0.8, 0.9, 1.0};
        double[] x = {1.0, 2.0, 3.0, 4.0};

        assertTrue(JSLRandom.isValidCDF(p) == true);
        double[] makePairs = DEmpiricalCDF.makePairs(1, p);

        System.out.println(Arrays.toString(makePairs));
        double[] pp = {1.0, 0.7, 2.0, 0.8, 3.0, 0.9, 4.0, 1.0};

        assertTrue(JSLMath.compareArrays(makePairs, pp) == true);

        RNStreamFactory.RNStream defaultStream = RNStreamFactory.getDefaultStream();

        DEmpiricalCDF d = new DEmpiricalCDF(pp, defaultStream);

        int n = 100;
        double[] x1 = new double[n];

        for (int i = 0; i < n; i++) {
            x1[i] = JSLRandom.randomlySelect(x, p, defaultStream);
        }
        double[] x2 = new double[n];
        defaultStream.resetStartStream();
        for (int i = 0; i < n; i++) {
            x2[i] = JSLRandom.randomlySelect(x, p, defaultStream);
        }
        System.out.println(Arrays.toString(x1));
        System.out.println(Arrays.toString(x2));
        assertTrue(JSLMath.compareArrays(x1, x2) == true);
    }

}
