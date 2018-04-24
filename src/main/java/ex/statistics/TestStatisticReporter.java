/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ex.statistics;

import java.util.ArrayList;
import java.util.List;
import jsl.utilities.random.distributions.Normal;
import jsl.utilities.random.rvariable.NormalRV;
import jsl.utilities.random.rvariable.RVariableIfc;
import jsl.utilities.reporting.StatisticReporter;
import jsl.utilities.statistic.Statistic;
import jsl.utilities.statistic.StatisticAccessorIfc;

/**
 *
 * @author rossetti
 */
public class TestStatisticReporter {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        RVariableIfc n = new NormalRV();
        Statistic s1 = new Statistic("s1");
        Statistic s2 = new Statistic("s2 blah");
        s1.collect(n.sample(100));
        s2.collect(n.sample(200));
        List<StatisticAccessorIfc> list = new ArrayList<>();
        list.add(s1);
        list.add(s2);
        StatisticReporter r = new StatisticReporter(list);
        System.out.println(r.getHalfWidthSummaryReport(0.95));
        System.out.println(r.getSummaryReport());
        //r.setDecimalPlaces(2);
        r.setNameFieldSize(r.findSizeOfLongestName() + 5);
        System.out.println(r.getHalfWidthSummaryReport());
        System.out.println(r.findSizeOfLongestName());
        System.out.println(r.getSummaryReportAsLaTeXTabular(5));
        System.out.println(r.getCSVStatistics());
//        StringBuilder sb = r.myRowFormat;
//        for (int i=0; i< sb.length(); i++){
//            System.out.println("i = " + i + " char = " + sb.charAt(i));
//        }
    }
    
}
