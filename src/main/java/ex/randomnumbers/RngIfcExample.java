/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ex.randomnumbers;

import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RngIfc;

/**
 *
 * @author rossetti
 */
public class RngIfcExample {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        RNStreamFactory f = RNStreamFactory.getDefault();
        RngIfc g1 = f.getStream();
        RngIfc g2 = f.getStream();
        System.out.println("Two different streams from the same factory.");
        System.out.println("Note that they produce different random numbers");
        double u1;
        double u2;
        for (int i = 0; i < 5; i++) {
            u1 = g1.randU01();
            u2 = g2.randU01();
            System.out.println("u1 = " + u1 + "\t u2 = " + u2);
        }

        System.out.println();

        g1.resetStartStream();
        g2.resetStartStream();
        System.out.println("Resetting to the start of each stream simply");
        System.out.println("causes them to repeat the above.");

        for (int i = 0; i < 5; i++) {
            u1 = g1.randU01();
            u2 = g2.randU01();
            System.out.println("u1 = " + u1 + "\t u2 = " + u2);
        }

        g1.advanceToNextSubstream();
        g1.advanceToNextSubstream();
        System.out.println("Advancing to the start of the next substream ");
        System.out.println("causes them to advance to the beginning of the next substream.");

        for (int i = 0; i < 5; i++) {
            u1 = g1.randU01();
            u2 = g2.randU01();
            System.out.println("u1 = " + u1 + "\t u2 = " + u2);
        }

        g1.resetStartStream();
        g2.resetStartStream();
        g1.setAntitheticOption(true);
        g2.setAntitheticOption(true);
        System.out.println("Resetting to the start of the stream and turning on antithetic");
        System.out.println("causes them to produce the antithetics for the original starting stream.");

        for (int i = 0; i < 5; i++) {
            u1 = g1.randU01();
            u2 = g2.randU01();
            System.out.println("u1 = " + u1 + "\t u2 = " + u2);
        }

    }
}
