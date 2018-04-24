package ex.statistics;

import jsl.utilities.random.distributions.Lognormal;
import jsl.utilities.random.distributions.Normal;
import jsl.utilities.statistic.EstimatorIfc;
import jsl.utilities.statistic.JackKnifeEstimator;

public class TestJackKnife {

    public static void main(String[] args) {
        example1();
        example2();
    }

    public static void example1(){
        Normal n = new Normal(10, 3);

        JackKnifeEstimator bs = new JackKnifeEstimator(n.sample(50), new EstimatorIfc.Average());

        System.out.println(bs);
    }

    public static void example2(){
        Lognormal n = new Lognormal(10, 3);

        JackKnifeEstimator bs = new JackKnifeEstimator(
                n.sample(50), new EstimatorIfc.Minimum());

        System.out.println(bs);
    }
}
