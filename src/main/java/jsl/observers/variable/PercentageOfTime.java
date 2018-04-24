/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsl.observers.variable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import jsl.modeling.ModelElement;
import jsl.modeling.elements.variable.TimeWeighted;
import jsl.observers.ModelElementObserver;
import jsl.utilities.statistic.Statistic;
import jsl.utilities.statistic.WeightedProportion;


/**
 *
 * @author rossetti
 */
public class PercentageOfTime extends ModelElementObserver {

    private TimeWeighted myTW;

    private WeightedProportion myWP;

    private Map<Integer, Statistic> myAcrossRepStats;

    public PercentageOfTime(TimeWeighted tw) {
        this(tw, null);
    }

    public PercentageOfTime(TimeWeighted tw, String name) {
        super(name);
        if (tw == null){
            throw new IllegalArgumentException("TimeWeighted variable was null");
        }
        myTW = tw;
        myWP = new WeightedProportion(name);
        myTW.addObserver(this);
        myAcrossRepStats = new HashMap<Integer,Statistic>();
    }

    @Override
    protected void removedFromModel(ModelElement m, Object arg) {
        myWP.reset();
        myWP = null;
        myTW = null;
    }

    @Override
    protected void update(ModelElement m, Object arg) {
        myWP.collect(myTW.getPreviousValue(), myTW.getWeight());
    }

    @Override
    protected void warmUp(ModelElement m, Object arg) {
        myWP.reset();
    }

    @Override
    protected void beforeExperiment(ModelElement m, Object arg) {
        myWP.reset();
        myAcrossRepStats.clear();
    }

    @Override
    protected void beforeReplication(ModelElement m, Object arg) {
        myWP.reset();
    }

    @Override
    protected void afterReplication(ModelElement m, Object arg) {
        int[] values = myWP.getValues();
        for(int i: values){
            Statistic s = myAcrossRepStats.get(i);
            if (s == null){
                s = new Statistic(myTW.getName() + " P(" + i +") ");
                myAcrossRepStats.put(i, s);
            }
            s.collect(myWP.getProportion(i));
        }
    }

    @Override
    public String toString(){
        StringBuilder s = new StringBuilder();
        for(Statistic stat: myAcrossRepStats.values()){
            s.append(stat.toString());
        }
        return s.toString();
    }

    public WeightedProportion getWeightedProportion(){
        return myWP;
    }

    public Map<Integer, Statistic> getAcrossReplicationStatistics(){
        return Collections.unmodifiableMap(myAcrossRepStats);
    }

}
