/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ex.randomnumbers;

import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RNStreamFactory.RNStream;

/**
 *
 * @author rossetti
 */
public class FirstFiveRN {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        RNStreamFactory f = RNStreamFactory.getDefault();
        RNStream g1 = f.getStream();
        double u1;
        for (int i = 1; i <= 5; i++) {
            u1 = g1.randU01();
            System.out.println("u("+i+") = " + u1);
        }

    }
}
