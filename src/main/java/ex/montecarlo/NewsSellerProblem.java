/*
 * Copyright (c) 2018. Manuel D. Rossetti, manuelrossetti@gmail.com
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package ex.montecarlo;

import jsl.utilities.random.distributions.DEmpiricalPMF;
import jsl.utilities.random.distributions.DistributionIfc;
import jsl.utilities.statistic.*;

/**
 *
 */
public class NewsSellerProblem {

    private DEmpiricalPMF typeofday;

    private DEmpiricalPMF gd;

    private DEmpiricalPMF fd;

    private DEmpiricalPMF pd;

    private DistributionIfc[] demand = new DistributionIfc[4];

    private double price = 0.50;

    private double cost = 0.33;

    private double lostRevenueCost = 0.17;

    private double scrapPrice = 0.05;

    private int qMin = 50;

    private int qMax = 100;

    private Statistic myProfitStat;

    private Statistic myProfitGT0Stat;

    /**
     *
     */
    public NewsSellerProblem() {
        //super();
        System.out.println("Constructing NSP");
        typeofday = new DEmpiricalPMF();
        typeofday.addProbabilityPoint(1.0, 0.35);
        typeofday.addProbabilityPoint(2.0, 0.45);
        typeofday.addLastProbabilityPoint(3.0);

        gd = new DEmpiricalPMF();
        gd.addProbabilityPoint(40.0, 0.03);
        gd.addProbabilityPoint(50.0, 0.05);
        gd.addProbabilityPoint(60.0, 0.15);
        gd.addProbabilityPoint(70.0, 0.2);
        gd.addProbabilityPoint(80.0, 0.35);
        gd.addProbabilityPoint(90.0, 0.15);
        gd.addLastProbabilityPoint(100.0);

        fd = new DEmpiricalPMF();
        fd.addProbabilityPoint(40.0, 0.1);
        fd.addProbabilityPoint(50.0, 0.18);
        fd.addProbabilityPoint(60.0, 0.4);
        fd.addProbabilityPoint(70.0, 0.2);
        fd.addProbabilityPoint(80.0, 0.08);
        fd.addLastProbabilityPoint(90.0);

        pd = new DEmpiricalPMF();
        pd.addProbabilityPoint(40.0, 0.44);
        pd.addProbabilityPoint(50.0, 0.22);
        pd.addProbabilityPoint(60.0, 0.16);
        pd.addProbabilityPoint(70.0, 0.12);
        pd.addLastProbabilityPoint(80.0);

        demand = new DistributionIfc[4];

        demand[1] = gd;
        demand[2] = fd;
        demand[3] = pd;

        myProfitStat = new Statistic("Avg Profit");
        myProfitGT0Stat = new Statistic("Profit > 0");

    }

    public void setPrice(double p) {
        price = p;
    }

    public double getPrice() {
        return (price);
    }

    public void runSimulation(int q, int n) {
        myProfitStat.reset();
        myProfitGT0Stat.reset();
        for (int k = 1; k <= n; k++) {
            double d = demand[(int) typeofday.getValue()].getValue();
            double profit = price * Math.min(d, q) - cost * q
                    - lostRevenueCost * Math.max(0, d - q) + scrapPrice
                    * Math.max(0, q - d);
            myProfitStat.collect(profit);
            myProfitGT0Stat.collect(profit > 0);

        }
    }

    public Statistic getProfitStat(){
        return myProfitStat;
    }

    public Statistic getProfitGT0Stat(){
        return myProfitGT0Stat;
    }

    public static void main(String[] args) {

        NewsSellerProblem nsp = new NewsSellerProblem();
        nsp.getProfitStat().setSaveDataOption(true);
        nsp.runSimulation(50, 100);
        System.out.println(nsp.getProfitStat());

        double[] profits = nsp.getProfitStat().getSavedData();

        Bootstrap bs = new Bootstrap("Newseller Bootstrap Example", profits);

        bs.generateSamples(1000);

        System.out.println(bs);
    }
}
