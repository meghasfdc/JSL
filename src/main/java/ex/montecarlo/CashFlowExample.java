/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ex.montecarlo;

import jsl.utilities.random.distributions.Beta;
import jsl.utilities.random.distributions.DEmpiricalCDF;
import jsl.utilities.random.distributions.Normal;
import jsl.utilities.random.distributions.Uniform;
import jsl.utilities.statistic.Statistic;

/**
 *
 * @author rossetti
 */
public class CashFlowExample {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Uniform S = new Uniform(90.0,100.0);
        Normal C = new Normal(100.0, 10.0*10.0);
        Normal Y = new Normal(300.0, 50.0*50.0);
        double[] p = {4.0, 0.3, 5.0, 0.7, 6.0, 0.8, 7.0, 1.0};
        DEmpiricalCDF N = new DEmpiricalCDF(p);
        Beta b = new Beta(5.0, 1.5);
        Statistic npvStat = new Statistic("NPV");
        Statistic npvLT0Stat = new Statistic("P(NPV < 0)");
        int r = 1910;
        for(int j=1; j<=r; j++){
            double s = S.getValue();
            double c = C.getValue();
            double y = Y.getValue();
            int n = (int)N.getValue();
            double i = 0.06 + (0.09 - 0.06)*b.getValue();
            double p1 = getPGivenA(i, n, c);
            double p2 = getPGivenA(i, n, y);
            double p3 = getPGivenF(i, n, s);
            double npv = -800.0 - p1 + p2 + p3;
            npvStat.collect(npv);
            npvLT0Stat.collect(npv < 0);
        }
        System.out.println(npvStat);
        System.out.println(npvLT0Stat);
    }

    public static double getPGivenF(double i, int n, double f) {
        if (i <= -1.0) {
            throw new IllegalArgumentException("interest rate was <= -1");
        }

        if (n < 0) {
            throw new IllegalArgumentException("number of periods was < 0");
        }

        double d = Math.pow((1.0 + i), n);
        return f / d;
    }

    public static double getPGivenA(double i, int n, double a) {
        if (i <= -1.0) {
            throw new IllegalArgumentException("interest rate was <= -1");
        }

        if (n < 0) {
            throw new IllegalArgumentException("number of periods was < 0");
        }

        double d = Math.pow((1.0 + i), n);
        return a * ((d - 1.0) / (i * (d)));
    }
}
