/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ex.statistics;

import jsl.utilities.random.distributions.Exponential;
import jsl.utilities.statistic.BatchStatistic;
import jsl.utilities.statistic.Statistic;

/**
 *
 * @author rossetti
 */
public class BatchStatisticExample {

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        Exponential d = new Exponential(2);

        // number of observations
        int n = 1000; 
        
        // minimum number of batches permitted
        // there will not be less than this number of batches
        int minNumBatches = 40;
        
        // minimum batch size permitted
        // the batch size can be no smaller than this amount
        int minBatchSize = 25; 
        
        // maximum number of batch multiple
        //  The multiple of the minimum number of batches
        //  that determines the maximum number of batches
        //  e.g. if the min. number of batches is 20
        //  and the max number batches multiple is 2,
        //  then we can have at most 40 batches
        int maxNBMultiple = 2; 

        // In this example, since 40*25 = 1000, the batch multiple does not matter
        
        BatchStatistic bm = new BatchStatistic(minNumBatches, minBatchSize, maxNBMultiple);

        for (int i = 1; i <= n; ++i) {
            bm.collect(d.getValue());
        }
        System.out.println(bm);

        double[] bma = bm.getBatchMeanArrayCopy();
        int i=0;
        for(double x: bma){
            System.out.println("bm(" + i + ") = " + x);
            i++;
        }
        // this rebatches the 40 down to 10
        //Statistic s = bm.rebatchToNumberOfBatches(10);
        //System.out.println(s);

    }
}
