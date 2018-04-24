/*
 * Created on Feb 21, 2007
 * Copyright (c) 2007, Manuel D. Rossetti (rossetti@uark.edu)
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
package ex.statistics;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import jsl.utilities.random.distributions.DistributionIfc;
import jsl.utilities.random.distributions.Gamma;
import jsl.utilities.random.distributions.Normal;
import jsl.utilities.random.rvariable.GammaRV;
import jsl.utilities.random.rvariable.NormalRV;
import jsl.utilities.random.rvariable.RVariableIfc;
import jsl.utilities.statistic.BatchStatistic;
import jsl.utilities.statistic.Statistic;
import jsl.utilities.statistic.WeightedStatistic;

/**
 * @author rossetti
 *
 */
public class TestStatistic {

    /**
     * @param args
     */
    public static void main(String[] args) {
        test1();
        //test2();
        //test3();
        //test4();
        //test5();
        //test6();
        //test7();
        test8();
    }

    public static void test1() {
        RVariableIfc r = new GammaRV(23.0, 9.0);

        double[] data = r.sample(10);
        for (double d : data) {
            System.out.println(d);
        }
        System.out.println();

        Statistic stat = new Statistic("Gamma data");
        stat.collect(data);
        System.out.println(stat);
        System.out.println(stat.getConfidenceInterval(0.95));

        System.out.println(stat.getCSVStatisticHeader());
        System.out.println(stat.getCSVStatistic());

        RVariableIfc normal = new NormalRV();

        Statistic stat2 = new Statistic("Normal data", normal.sample(10));

        List<Statistic> list = new ArrayList<Statistic>();

        list.add(stat);
        list.add(stat2);

        Statistic.writeSummaryStatistics(new PrintWriter(System.out, true), list);

    }

    public static void test2() {
        RVariableIfc r = new GammaRV(23.0, 9.0);

        double[] data = r.sample(10);
        for (double d : data) {
            System.out.println(d);
        }

        System.out.println("Median = " + Statistic.getMedian(data));
        for (double d : data) {
            System.out.println(d);
        }
        System.out.println();

        Statistic stat = new Statistic();
        stat.setSaveDataOption(true);
        stat.collect(data);
        System.out.println(stat);
        System.out.println();
        StringBuilder sb = new StringBuilder();
        sb.append(stat.getCSVHeader());
        sb.append(stat.getCSVValues());
        System.out.println(sb);
        System.out.println();
        double[] x = stat.getSavedData();
        for (double d : x) {
            System.out.println(d);
        }

        //double[] y = {9,6,9,6,9,6,9,6,9,6,9,6,9,6,9,6,9,6,9,6,9,6,9,6,9,6,9,6,9,6,9,6,9,6,9,6};
        double[] y = {9, 6, 9};
        //double[] y = {47,64,23,71,38,64,55,41,59,48,71,35,57,40,58,44,80,55,37,74,51,57,50,60,45,57,50,45,25,59,50,71,56,74,50,58,45,54,36,54,48,55,45,57,50,62,44,64,43,52,38,59,55,41,53,49,34,35,54,45,68,38,50,60,39,59,40,57,54,23};
        Statistic yStat = new Statistic(y);
        System.out.println(yStat);

//        
//        double[] x = stat.getStatistics();
//        stat.getStatistics(x);
//        StringBuilder sb = new StringBuilder();
//        for(int i=0; i< x.length; i++){
//        if (Double.isNaN(x[i]) || Double.isInfinite(x[i]))
//        sb.append("");
//        else
//        sb.append(x[i]);
//        if (i < x.length - 1)
//        sb.append(",");
//        }
//
//        System.out.println(sb.toString());
    }

    public static void test3() {
        Statistic stat = new Statistic("test 3");
        WeightedStatistic ws = new WeightedStatistic("ws test 3");
        System.out.println(stat);
        System.out.println(ws);
        Normal n = new Normal();
        double x;
        for (int i = 1; i <= 10; i++) {
            if (i == 2) {
                x = ws.getMax();
            } else {
                x = n.getValue();
            }
            stat.collect(x);
            System.out.println("x = " + x);
        }
        System.out.println(stat);
    }

    public static void test4() {
        // test half-width checking
        Normal n = new Normal(10, 2);
        Statistic s = new Statistic("test hw");
        s.setCollectionRule(Statistic.CollectionRule.HALF_WIDTH);
        s.setDesiredHalfWidth(0.1);
        boolean b = true;
        while (b) {
            b = s.collect(n);
        }
        System.out.println(s);
        s.reset();
        while (s.collect(n));
        System.out.println(s);
    }

    public static void test5() {
        // test relative precision
        Normal n = new Normal(10, 2);
        Statistic s = new Statistic("test rp");
        s.setCollectionRule(Statistic.CollectionRule.REL_PRECISION);
        s.setRelativePrecision(0.01);
        while (s.collect(n));
        System.out.println(s);
        double rp = s.getRelativeWidth() / 2.0;
        System.out.println("rp = " + rp);
    }

    public static void test6() {
        // test relative precision
        Normal n = new Normal(10, 2);
        BatchStatistic s = new BatchStatistic("test rp");
        s.setCollectionRule(Statistic.CollectionRule.HALF_WIDTH);
        s.setDesiredHalfWidth(0.1);
        while (s.collect(n));
        System.out.println(s);
    }

    public static void test7() {
        // test relative precision
        Normal n = new Normal(10, 2);
        BatchStatistic s = new BatchStatistic("test rp");
        s.setCollectionRule(Statistic.CollectionRule.REL_PRECISION);
        s.setRelativePrecision(0.01);
        while (s.collect(n));
        System.out.println(s);
        double rp = s.getRelativeWidth() / 2.0;
        System.out.println("rp = " + rp);
    }
    
    public static void test8(){
          Normal n = new Normal(10, 2);     
          WeightedStatistic ws = new WeightedStatistic("ws test 8");
          for(int i=1; i<=100;i++){
              ws.collect(n.getValue(), 1.0);
          }
          System.out.println(ws);
    }
}
