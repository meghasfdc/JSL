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
package jsl.modeling.elements.queue;

import java.util.List;
import jsl.modeling.ModelElement;
import jsl.modeling.elements.RandomElementIfc;

import jsl.utilities.random.distributions.DUniform;

/** The RandomDiscipline provides a mechanism to the Queue object
 * to provide a random selection of objects from the queue
 * rule.
 */
@Deprecated
public class RandomDiscipline extends QueueDiscipline implements RandomElementIfc {

    /** indicates whether or not the random picking
     *  distribution has it stream reset to the default
     *  stream, or not prior to each experiment.  Resetting
     *  allows each experiment to use the same underlying random numbers
     *  i.e. common random numbers, this is the default
     * 
     *  Setting it to true indicates that it does reset
     */
    protected boolean myResetStartStreamOption;

    /** indicates whether or not the random picking
     *  distribution has it stream reset to the next substream
     *  stream, or not, prior to each replication.  Resetting
     *  allows each replication to better ensure that each
     *  replication will be start at the same place in the
     *  substreams, thereby, improving sychronization when using
     *  common random numbers.
     *
     *  Setting it to true indicates that it does jump to
     *  the next substream, true is the default
     */
    protected boolean myResetNextSubStreamOption;

    private DUniform myDistribution;

    private int myNext;

    public RandomDiscipline(ModelElement parent) {
        this(parent, null);
    }

    public RandomDiscipline(ModelElement parent, String name) {
        super(parent, name);
        setResetStartStreamOption(true);
        setResetNextSubStreamOption(true);
        myDistribution = new DUniform();
    }

    @Override
    protected void add(List<QObject> list, QObject qObject) {
        list.add(qObject);
    }

    @Override
    protected QObject peekNext(List<QObject> list) {

        if (list.isEmpty()) {
            return (null);
        }

        if (list.size() == 1) {
            myNext = 0;
        } else {
            myDistribution.setRange(0, list.size() - 1);
            myNext = (int) myDistribution.getValue();
        }
        return ((QObject) list.get(myNext));// randomly pick it from the range available
    }

    @Override
    protected QObject removeNext(List<QObject> list) {

        if (list.isEmpty()) {
            return (null);
        }

        peekNext(list); // sets the next randomly

        return ((QObject) list.remove(myNext)); // now returns the next
    }

    @Override
    protected void switchFrom(List<QObject> list, QueueDiscipline currentDiscipline) {
    }

    @Override
    public void advanceToNextSubstream() {
        myDistribution.advanceToNextSubstream();
    }

    @Override
    public void resetStartStream() {
        myDistribution.resetStartStream();
    }

    @Override
    public void resetStartSubstream() {
        myDistribution.resetStartSubstream();
    }

    @Override
    public void setAntitheticOption(boolean flag) {
        myDistribution.setAntitheticOption(flag);
    }

    @Override
    public boolean getAntitheticOption() {
        return myDistribution.getAntitheticOption();
    }

    @Override
    public final boolean getResetStartStreamOption() {
        return myResetStartStreamOption;
    }

    @Override
    public final void setResetStartStreamOption(boolean b) {
        myResetStartStreamOption = b;
    }

    @Override
    public final boolean getResetNextSubStreamOption() {
        return myResetNextSubStreamOption;
    }

    @Override
    public final void setResetNextSubStreamOption(boolean b) {
        myResetNextSubStreamOption = b;
    }

    @Override
    protected void beforeExperiment() {
        if (getResetStartStreamOption()) {
            myDistribution.resetStartStream();
        }
    }

    /** after each replication reset the underlying random number generator to the next
     *  substream
     */
    @Override
    protected void afterReplication() {
        if (getResetNextSubStreamOption()) {
            myDistribution.advanceToNextSubstream();
        }
    }
}
