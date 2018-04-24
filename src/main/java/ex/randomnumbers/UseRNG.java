/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ex.randomnumbers;

import jsl.utilities.random.distributions.Normal;
import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RNStreamFactory.RNStream;
import jsl.utilities.statistic.Statistic;
import jsl.utilities.statistic.StatisticXY;

/**
 *
 * @author rossetti
 */
public class UseRNG {

    public static void main(String[] args) {
        test1();
        //crn();
        //antithetic();
    }

    public static void crn() {
        
        // make a factory for creating streams
        RNStreamFactory f1 = new RNStreamFactory();

        // get the first stream from the factory
        RNStream f1s1 = f1.getStream();
        
        RNStream f1s2 = f1.getStream();

        // make another factory, the factories are identical
        RNStreamFactory f2 = new RNStreamFactory();

        // thus the first streams returned are identical
        RNStream f2s1 = f2.getStream();

        for (int i = 0; i < 10; i++) {
            System.out.println("f1s1 = " + f1s1.randU01() + " f2s1 = " + f2s1.randU01());
        }

        Statistic d = new Statistic("X - Y");
        StatisticXY statXY = new StatisticXY("Stat X Y");
//        Normal n1 = new Normal(2, 1, f1s1);
//        Normal n2 = new Normal(2.1, 1, f2s1);
                Normal n1 = new Normal(2, 1);
        Normal n2 = new Normal(2.1, 1);
        for (int i = 0; i < 10; i++) {
            double x = n1.getValue();
            double y = n2.getValue();
            d.collect(x-y);
            statXY.collectXY(x, y);
            System.out.println("x = " + x + " y = " + y);
        }
        
        System.out.println("CRN");
        System.out.println(d);
        System.out.println("=============================");
        System.out.println(statXY);
        System.out.println("=============================");
    }

    /**
     *
     */
    public static void antithetic() {
        // make a factory for creating streams
        RNStreamFactory f1 = new RNStreamFactory();

        // get the first stream from the factory
        RNStream f1s1 = f1.getStream();

        // make another factory, the factories are identical
        RNStreamFactory f2 = new RNStreamFactory();

        // thus the first streams returned are identical
        RNStream f2s1 = f2.getStream();
        
        // now tell the stream to produce antithetic random numbers
        // f2s1 and f1s1 are now antithetic to each other
        f2s1.setAntitheticOption(true);

        for (int i = 0; i < 10; i++) {
            System.out.println("f1s1 = " + f1s1.randU01() + " f2s1 = " + f2s1.randU01());
        }

        Statistic d = new Statistic("(X + Y)/2");
        StatisticXY statXY = new StatisticXY("Stat X Y");
        Normal n1 = new Normal(2, 1, f1s1);
        Normal n2 = new Normal(2, 1, f2s1);
        for (int i = 0; i < 10; i++) {
            double x = n1.getValue();
            double y = n2.getValue();
            d.collect((x+y)/2.0);
            statXY.collectXY(x, y);
            System.out.println("x = " + x + " y = " + y);
        }
        
        System.out.println("Antithetic");
        System.out.println(d);
        System.out.println("=============================");
        System.out.println(statXY);
        System.out.println("=============================");

    }

    public static void test1() {
        // TODO code application logic here
        System.out.println("My first Java print satement");
        RNStream defaultStream = RNStreamFactory.getDefaultStream();

        for (int i = 1; i <= 3; i++) {
            System.out.println("u = " + defaultStream.randU01());
        }

        RNStreamFactory f = new RNStreamFactory();
        RNStream s1 = f.getStream();
        System.out.println("Stream 1");
        for (int i = 1; i <= 3; i++) {
            System.out.println("u = " + s1.randU01());
        }

        s1.advanceToNextSubstream();
        System.out.println("advanced");
        for (int i = 1; i <= 3; i++) {
            System.out.println("u = " + s1.randU01());
        }

        s1.resetStartStream();
        System.out.println("reset");
        for (int i = 1; i <= 3; i++) {
            System.out.println("u = " + s1.randU01());
        }
        RNStream s2 = f.getStream();
        System.out.println("Sream 2");
        for (int i = 1; i <= 3; i++) {
            System.out.println("u = " + s2.randU01());
        }
    }
}
