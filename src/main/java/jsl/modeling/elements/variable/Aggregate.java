/*
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
package jsl.modeling.elements.variable;

import java.util.LinkedList;
import java.util.List;

import jsl.modeling.ModelElement;

/**
 * This class serves as a base class for the concept of aggregate variables.
 * That is, something that consists of many variables. It knows about the
 * variables that it aggregates. Any variable can be associated with any number
 * of aggregates and any aggregate can be associated with any number of
 * variables
 *
 */
public abstract class Aggregate extends Aggregatable implements DefaultReportingOptionIfc {

    /**
     * counts the number of observed variables that have been initialized
     */
    protected int myNumInitialized;

    /**
     * counts the number of observed variables that have been warmed up
     */
    protected int myNumWarmedUp;

    /**
     * Holds the variables that this aggregate is observing for the purposes of
     * aggregation
     *
     */
    protected List<Aggregatable> myVariables;

    /**
     * @param parent
     */
    public Aggregate(ModelElement parent) {
        this(parent, null);

    }

    /**
     * @param parent
     * @param name
     */
    public Aggregate(ModelElement parent, String name) {
        super(parent, name);
        // the aggregate variable does not get initialized by the model
        // it must be initialized after all its observed variables are initialized
        setInitializationOption(false);
        // the aggregate variable does not get warmed up by the model
        // it must be warmed up after all its observed counters are warmed up
        setWarmUpOption(false);

        myVariables = new LinkedList<Aggregatable>();
        myNumInitialized = 0;
        myNumWarmedUp = 0;
//		System.out.println("Aggregate: " + getName() + " constructed at time " + getTime());

    }

    /**
     * Tells the Aggregate to observe the provided Variable for the purpose of
     * aggregating
     *
     * @param variable
     */
    public final void subscribeTo(Aggregatable variable) {
        if (variable == null) {
            throw new IllegalArgumentException("The supplied variable was null");
        }

        if (myVariables.contains(variable)) {
            throw new IllegalArgumentException("The supplied variable is already being observed");
        }

        // add the variable to this aggregate's list to observe
        myVariables.add(variable);

        // tell the added variable that this aggregate has been attached
        variable.attachAggregate(this);

        if (getModel().isRunning()) {
            variableAddedDuringReplication(variable);
        } else {
            variableAddedBeforeReplication(variable);
        }

        //TODO
        /*        Replication r = getCurrentReplication();
         if (r != null) {
         if (r.isRunning()) {
         variableAddedDuringReplication(variable);
         } else {
         variableAddedBeforeReplication(variable);
         }
         } else {
         variableAddedBeforeReplication(variable);
         }
         */
    }

    /**
     * Tells the aggregate to stop observing the provided Variable
     *
     * @param variable
     */
    public final void unsubscribeFrom(Aggregatable variable) {
        if (variable == null) {
            throw new IllegalArgumentException("The supplied variable was null");
        }

        if (!myVariables.contains(variable)) {
            throw new IllegalArgumentException("Attempted to unsubscribe a variable that is not being aggregated");
        }

        myVariables.remove(variable);

        variable.detachAggregate(this);

        if (getModel().isRunning()) {
            variableRemovedDuringReplication(variable);
        } else {
            variableRemovedBeforeReplication(variable);
        }
        //TODO
/*
         Replication r = getCurrentReplication();
         if (r != null) {
         if (r.isRunning()) {
         variableRemovedDuringReplication(variable);
         } else {
         variableRemovedBeforeReplication(variable);
         }
         } else {
         variableRemovedBeforeReplication(variable);
         }
         */
    }

    /**
     * Computes and returns the sum of the aggregated variables
     *
     * @return
     */
    public final double sumValues() {
        double sum = 0.0;
        for (Aggregatable x : myVariables) {
            sum = sum + x.getValue();
        }
        return sum;
    }

    /**
     * Computes and returns the average of the aggregated variables
     *
     * @return
     */
    public final double avgValues() {
        double sum = 0.0;
        for (Aggregatable x : myVariables) {
            sum = sum + x.getValue();
        }
        double n = myVariables.size();
        return sum / n;
    }

    /**
     * Counts and returns the number of aggregated variables that have their
     * initialization option on
     *
     * @return
     */
    public final int countNumToInitialize() {
        int i = 0;
        for (Aggregatable x : myVariables) {
//            if (!x.getRemoveFromModelPriorToRepFlag()) {
                if (x.getInitializationOption()) {
                    i++;
                }
//            }
        }
        return i;
    }

    /**
     * Counts and returns the number of aggregated variables that have their
     * warm up option on
     *
     * @return
     */
    public final int countNumToWarmUp() {
        int i = 0;
        for (Aggregatable x : myVariables) {
//            if (!x.getRemoveFromModelPriorToRepFlag()) {
                if (x.getWarmUpOption()) {
                    i++;
                }
//            }
        }
        return i;
    }

    /**
     * Sub-classes must implement this method to properly react to the
     * subscription of a new variable to the aggregate. Note that the variable
     * is already in the aggregate's list of variables when this method is
     * invoked
     *
     * @param variable The variable that has been added to the aggregate
     */
    abstract protected void variableAddedBeforeReplication(Aggregatable variable);

    /**
     * Sub-classes must implement this method to properly react to the
     * subscription of a new variable to the aggregate. Note that the variable
     * is already in the aggregate's list of variables when this method is
     * invoked
     *
     * @param variable The variable that has been added to the aggregate
     */
    abstract protected void variableAddedDuringReplication(Aggregatable variable);

    /**
     * Sub-classes must implement this method to properly react to the removal
     * of a variable from the aggregate. Note that the variable is no longer in
     * the aggregate's list of variables when this method is invoked
     *
     * @param variable The variable that has been removed from the aggregate
     */
    abstract protected void variableRemovedBeforeReplication(Aggregatable variable);

    /**
     * Sub-classes must implement this method to properly react to the removal
     * of a variable from the aggregate. Note that the variable is no longer in
     * the aggregate's list of variables when this method is invoked
     *
     * @param variable The variable that has been removed from the aggregate
     */
    abstract protected void variableRemovedDuringReplication(Aggregatable variable);

    /**
     * Sub-classes must implement this method to properly react to a change in
     * the variable with respect to its form of aggregation
     *
     * @param variable The variable that has just changed
     */
    abstract protected void valueChangedBeforeReplication(Aggregatable variable);

    /**
     * Sub-classes must implement this method to properly react to a change in
     * the variable with respect to its form of aggregation
     *
     * @param variable The variable that has just changed
     */
    abstract protected void valueChangedDuringReplication(Aggregatable variable);

    /**
     * Sub-classes must implement this method to properly initialize the
     * aggregate after all its variables have been initialized
     *
     */
    abstract protected void initializeAggregate();

    /**
     * Sub-classes must implement this method to properly warm up the aggregate
     * after all its variables have been warmed up
     *
     */
    abstract protected void warmUpAggregate();

    /**
     * Sub-classes can implement this method to properly react to the removal
     * from the model of the variable with respect to its form of aggregation.
     *
     * @param variable The variable that has just been removed from the model
     */
    protected void removedFromModel(Aggregatable variable) {
        // the variable is being removed from the model
        // the aggregate should no longer be subscribed
        unsubscribeFrom(variable);
    }

    /**
     * Aggregatables to which the Aggregate is subscribed call this method when
     * they are initialized
     *
     * @param variable The variable that has just been initialized
     */
    protected void initialized(Aggregatable variable) {
//		System.out.println("In Aggregate initialized(Aggregatable variable) " + getName() + " initialized() notified by " + variable.getName());

        if (countNumToInitialize() == 0) {
            // use the model element's initialize to initialize any children that need it
            initialize_();
            // initialize the aggregate itself (since it does not participate in model element initialization)
            initializeAggregate();
            notifyInitializationObservers();
            notifyAggregatesOfInitialization();
            return;
        }

        // an aggregated variable has just been initialized, count it
        myNumInitialized++;
//		System.out.println("Number initialized = " + myNumInitialized);
//		System.out.println("count of number needing initialization = " + countNumToInitialize());
        if (myNumInitialized == countNumToInitialize()) {
            // reached the number to initialize
            // use the model element's initialize to initialize any children that need it
            initialize_();
            // initialize the aggregate itself (since it does not participate in model element initialization)
            initializeAggregate();
            notifyInitializationObservers();
            notifyAggregatesOfInitialization();
        }
    }

    /**
     * Aggregatables to which the Aggregate is subscribed call this method when
     * they are warmed up
     *
     * @param variable The variable that has just been warmed up
     */
    protected void warmedUp(Aggregatable variable) {
//		System.out.println("In Aggregate warmedUp(Aggregatable variable) " + getName() + " warmedUp() notified by " + variable.getName());
        if (countNumToWarmUp() == 0) {
            // warm up the aggregate itself (since it does not participate in model element warm up)
            warmUpAggregate();
            myWarmUpIndicator = true;
            notifyWarmUpObservers();
            notifyAggregatesOfWarmUp();
            // use the model element's initialize to warm up any children that need it
            warmUp_();
            return;
        }

        myNumWarmedUp++;
        if (myNumWarmedUp == countNumToWarmUp()) {
            // reached the number to warm up
            // warm up the aggregate itself (since it does not participate in model element warm up)
            warmUpAggregate();
            myWarmUpIndicator = true;
            notifyWarmUpObservers();
            notifyAggregatesOfWarmUp();
            // use the model element's initialize to warm up any children that need it
            warmUp_();
        }
    }

    @Override
    protected void beforeExperiment() {
        myNumInitialized = 0;
        myNumWarmedUp = 0;
    }

//	protected void beforeReplication(){
//		super.beforeReplication();
//		System.out.println("Aggregate: " + getName() + " beforeReplication(). # observing: " + myVariables.size());	
//	}
//    @Override
//    protected void replicationEnded() {
//        super.replicationEnded();
//        System.out.println("Aggregate: " + getName() + " replicationEnded(). # observing: " + myVariables.size());
//        System.out.println(this);
//    }

    @Override
    protected void afterReplication() {
        myNumInitialized = 0;
        myNumWarmedUp = 0;
    }
}
