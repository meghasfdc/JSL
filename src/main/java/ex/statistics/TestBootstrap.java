package ex.statistics;

import jsl.utilities.random.SampleIfc;
import jsl.utilities.random.distributions.Lognormal;
import jsl.utilities.random.distributions.Normal;
import jsl.utilities.random.rvariable.RVariableIfc;
import jsl.utilities.statistic.Bootstrap;
import jsl.utilities.statistic.EstimatorIfc;
import jsl.utilities.statistic.MultiBootstrap;
import jsl.utilities.statistic.Statistic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestBootstrap {

    public static void main(String[] args) {
        //example1();
        //example2();
        example3();
    }

    public static void example1(){
        Normal n = new Normal(10, 3);
        RVariableIfc rv = n.getRandomVariable();

        Bootstrap bs = new Bootstrap("Test Normal", rv.sample(50));

        bs.generateSamples(10, true);
        System.out.println(bs);
        List<Statistic> list = bs.getStatisticForEachBootstrapSample();

        for(Statistic s: list){
            System.out.println(s.asString());
        }

        bs.generateSamples(10, true);
        System.out.println(bs);
        List<Statistic> list2 = bs.getStatisticForEachBootstrapSample();

        for(Statistic s: list2){
            System.out.println(s.asString());
        }
    }

    public static void example2(){
        Lognormal n = new Lognormal(10, 3);
        RVariableIfc rv = n.getRandomVariable();

        Bootstrap bs = new Bootstrap("Test Lognormal", rv.sample(50));
        bs.generateSamples(1000, new EstimatorIfc.Minimum());

        System.out.println(bs);
    }

    public static void example3(){
        Normal n1 = new Normal(10, 3);
        Normal n2 = new Normal(5, 1.5);

        Map<String, SampleIfc> smap = new HashMap<>();
        smap.put("n1", n1.getRandomVariable());
        smap.put("n2", n2.getRandomVariable());

        MultiBootstrap multiBootstrap = MultiBootstrap.create(100, smap);

        multiBootstrap.generateSamples(20, true);

        String data = multiBootstrap.getTablesawTable().print();

        System.out.println(multiBootstrap);
        System.out.println(data);

        System.out.println(multiBootstrap.getTablesawTable().summary());

    }
}
