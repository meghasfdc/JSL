/*
 * Created on Feb 9, 2007
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
package ex.jobshop;

import java.util.Iterator;

import jsl.modeling.JSLEvent;
import jsl.modeling.ModelElement;
import jsl.modeling.elements.EventGenerator;
import jsl.modeling.elements.RandomElement;
import jsl.modeling.queue.QObject;
import jsl.modeling.elements.variable.ResponseVariable;
import jsl.utilities.random.RandomIfc;

/**
 * @author rossetti
 *
 */
public class JobGenerator extends EventGenerator {

    protected RandomElement<JobType> myJobTypes;
    
    //protected DEmpiricalList<JobType> myJobTypes;

    /**
     * @param parent
     */
    public JobGenerator(ModelElement parent) {
        this(parent, null, null, Long.MAX_VALUE, Double.POSITIVE_INFINITY, null);
    }

    /**
     * @param parent
     * @param name
     */
    public JobGenerator(ModelElement parent, String name) {
        this(parent, null, null, Long.MAX_VALUE, Double.POSITIVE_INFINITY, name);
    }

    /**
     * @param parent
     * @param timeUntilFirst
     */
    public JobGenerator(ModelElement parent, RandomIfc timeUntilFirst) {
        this(parent, timeUntilFirst, null, Long.MAX_VALUE, Double.POSITIVE_INFINITY, null);
    }

    /**
     * @param parent
     * @param timeUntilFirst
     * @param timeUntilNext
     */
    public JobGenerator(ModelElement parent, RandomIfc timeUntilFirst,
            RandomIfc timeUntilNext) {
        this(parent, timeUntilFirst, timeUntilNext, Long.MAX_VALUE, Double.POSITIVE_INFINITY, null);
    }

    /**
     * @param parent
     * @param timeUntilFirst
     * @param timeUntilNext
     * @param name
     */
    public JobGenerator(ModelElement parent, RandomIfc timeUntilFirst,
            RandomIfc timeUntilNext, String name) {
        this(parent, timeUntilFirst, timeUntilNext, Long.MAX_VALUE, Double.POSITIVE_INFINITY, name);
    }

    /**
     * @param parent
     * @param timeUntilFirst
     * @param timeUntilNext
     * @param maxNum
     */
    public JobGenerator(ModelElement parent, RandomIfc timeUntilFirst,
            RandomIfc timeUntilNext, Long maxNum) {
        this(parent, timeUntilFirst, timeUntilNext, maxNum, Double.POSITIVE_INFINITY, null);
    }

    /**
     * @param parent
     * @param timeUntilFirst
     * @param timeUntilNext
     * @param maxNum
     * @param timeUntilLast
     */
    public JobGenerator(ModelElement parent, RandomIfc timeUntilFirst,
            RandomIfc timeUntilNext, Long maxNum,
            double timeUntilLast) {
        this(parent, timeUntilFirst, timeUntilNext, maxNum, timeUntilLast, null);
    }

    /**
     * @param parent
     * @param timeUntilFirst
     * @param timeUntilNext
     * @param maxNum
     * @param timeUntilLast
     * @param name
     */
    public JobGenerator(ModelElement parent, RandomIfc timeUntilFirst,
            RandomIfc timeUntilNext, Long maxNum,
            double timeUntilLast, String name) {
        super(parent, null, timeUntilFirst, timeUntilNext, maxNum, timeUntilLast, name);
        //myJobTypes = new DEmpiricalList<JobType>();
        myJobTypes = new RandomElement<JobType>(this);
    }

    public void addJobType(String name, Sequence sequence, double prob) {
        ResponseVariable v = new ResponseVariable(this, name + "SystemTime");
        JobType type = new JobType();
        type.myName = name;
        type.mySequence = sequence;
        type.mySystemTime = v;
        myJobTypes.add(type, prob);
    }

    public void addLastJobType(String name, Sequence sequence) {
        ResponseVariable v = new ResponseVariable(this, name + "SystemTime");
        JobType type = new JobType();
        type.myName = name;
        type.mySequence = sequence;
        type.mySystemTime = v;
        myJobTypes.addLast(type);
    }

    @Override
    protected void generate(JSLEvent event) {
        if (!myJobTypes.isEmpty()) {
            // create the job
            Job job = new Job(getTime());
            // tell it to start its sequence
            job.doNextJobStep();
        }
    }

    public class JobType {

        String myName;

        Sequence mySequence;

        ResponseVariable mySystemTime;
    }

    public class Job extends QObject {

        JobType myType;

        Iterator<JobStep> myProcessPlan;

        double myServiceTime;

        Job(double time) {
            super(time);
            myType = myJobTypes.getRandomElement();
            myProcessPlan = myType.mySequence.getIterator();
            setName(myType.myName);
        }

        public void doNextJobStep() {

            if (myProcessPlan.hasNext()) {
                JobStep step = myProcessPlan.next();
                myServiceTime = step.getProcessingTime();
                WorkStation w = step.getWorkStation();
                w.arrive(this);
            } else {
                myType.mySystemTime.setValue(getTime() - getCreateTime());
            }
        }

        public double getServiceTime() {
            return (myServiceTime);
        }
    }
}
