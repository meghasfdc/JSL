/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ex.montecarlo;

import jsl.utilities.random.distributions.Uniform;
import jsl.utilities.statistic.Statistic;

/**
 *
 * @author rossetti
 */
public class MCAreaEstimation {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        double a = 1.0;
        double b = 4.0;
        Uniform ucdf = new Uniform(a, b);
        Statistic stat = new Statistic("Area Estimator");
        int n = 100; // sample size
        for(int i=1;i<=n;i++){
            double x = ucdf.getValue();
            double gx = Math.sqrt(x);
            double y = (b-a)*gx;
            stat.collect(y);
        }
        System.out.printf("True Area = %10.3f\n", 14.0/3.0);
        System.out.printf("Area estimate = %10.3f\n", stat.getAverage());
       
    }
    
}
