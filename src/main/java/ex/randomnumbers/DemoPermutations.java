/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ex.randomnumbers;

import java.util.ArrayList;
import java.util.List;
import jsl.utilities.random.robj.DPopulation;
import jsl.utilities.random.rvariable.JSLRandom;

/**
 *
 * @author rossetti
 */
public class DemoPermutations {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        double[] y = new double[10];
        for (int i = 0; i < 10; i++) {
            y[i] = i + 1;
        }

        DPopulation p = new DPopulation(y);
        System.out.println(p);

        p.permute();
        System.out.println(p);

        System.out.println("Permuting y");
        JSLRandom.permutation(y);
        System.out.println(DPopulation.toString(y));

        double[] x = p.sample(5);
        System.out.println("Sampling x from population");
        System.out.println(DPopulation.toString(x));
        
        List<String> strList = new ArrayList<>();
        strList.add("a");
        strList.add("b");
        strList.add("c");
        strList.add("d");
        System.out.println(strList);
        JSLRandom.permutation(strList);
        System.out.println(strList);
    }
    
}
