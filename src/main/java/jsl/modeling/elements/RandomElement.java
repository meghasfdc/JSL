/*
 * Created on Apr 11, 2007
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
package jsl.modeling.elements;

import java.util.Collection;
import java.util.List;

import jsl.modeling.ModelElement;
import jsl.utilities.random.robj.DEmpiricalList;

/** RandomElement allows for randomly selecting objects of type T
 *  according to a DEmpiricalList.  This essentially allows DEmpiricalList to
 *  be a ModelElement
 *
 */
public class RandomElement<T> extends ModelElement implements RandomElementIfc {

    /** indicates whether or not the random variable's
     *  distribution has it stream reset to the default
     *  stream, or not prior to each experiment.  Resetting
     *  allows each experiment to use the same underlying random numbers
     *  i.e. common random numbers, this is the default
     * 
     *  Setting it to true indicates that it does reset
     */
    protected boolean myResetStartStreamOption;

    /** indicates whether or not the random variable's
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

    protected DEmpiricalList<T> myRandomList;

    /**
     * @param parent
     */
    public RandomElement(ModelElement parent) {
        this(parent, null);
    }

    /**
     * @param parent
     * @param name
     */
    public RandomElement(ModelElement parent, String name) {
        super(parent, name);
        myRandomList = new DEmpiricalList<T>();
        setWarmUpOption(false); // do not need to respond to warm events
        setResetStartStreamOption(true);
        setResetNextSubStreamOption(true);
    }

    /** Gets the current Reset Start Stream Option
     * @return
     */
    @Override
    public final boolean getResetStartStreamOption() {
        return myResetStartStreamOption;
    }

    /** Sets the reset start stream option, true
     *  means that it will be reset to the starting stream
     * @param b
     */
    @Override
    public final void setResetStartStreamOption(boolean b) {
        myResetStartStreamOption = b;
    }

    /** Gets the current reset next substream option
     *  true means, that it is set to jump to the next substream after
     *  each replication
     * @return
     */
    @Override
    public final boolean getResetNextSubStreamOption() {
        return myResetNextSubStreamOption;
    }

    /** Sets the current reset next substream option
     *  true means, that it is set to jump to the next substream after
     *  each replication
     * @param b
     */
    @Override
    public final void setResetNextSubStreamOption(boolean b) {
        myResetNextSubStreamOption = b;
    }

    /**
     * @param obj
     * @param p
     * @see jsl.utilities.random.robj.DEmpiricalList#add(java.lang.Object, double)
     */
    public final void add(T obj, double p) {
        myRandomList.add(obj, p);
    }

    /**
     * @param obj
     * @see jsl.utilities.random.robj.DEmpiricalList#addLast(java.lang.Object)
     */
    public final void addLast(T obj) {
        myRandomList.addLast(obj);
    }

    /**
     * @return
     * @see jsl.utilities.random.robj.DEmpiricalList#getRandomElement()
     */
    public final T getRandomElement() {
        return myRandomList.getRandomElement();
    }

    /**
     * @param arg0
     * @return
     * @see java.util.List#contains(java.lang.Object)
     */
    public final boolean contains(Object arg0) {
        return myRandomList.contains(arg0);
    }

    /**
     * @param arg0
     * @return
     * @see java.util.List#containsAll(java.util.Collection)
     */
    public final boolean containsAll(Collection<?> arg0) {
        return myRandomList.containsAll(arg0);
    }

    /**
     * @param arg0
     * @return
     * @see java.util.List#indexOf(java.lang.Object)
     */
    public final int indexOf(Object arg0) {
        return myRandomList.indexOf(arg0);
    }

    /**
     * @return
     * @see java.util.List#isEmpty()
     */
    public final boolean isEmpty() {
        return myRandomList.isEmpty();
    }

    /**
     * @return
     * @see java.util.List#size()
     */
    public final int size() {
        return myRandomList.size();
    }

    /** Returns an unmodifiable view of the list of elements
     * @return
     */
    public final List<T> getList() {
        return (myRandomList.getList());
    }

    @Override
    public final void advanceToNextSubstream() {
        myRandomList.advanceToNextSubstream();
    }

    @Override
    public final void resetStartStream() {
        myRandomList.resetStartStream();
    }

    @Override
    public final void resetStartSubstream() {
        myRandomList.resetStartSubstream();
    }

    @Override
    public final void setAntitheticOption(boolean flag) {
        myRandomList.setAntitheticOption(flag);
    }

    @Override
    public final boolean getAntitheticOption() {
        return myRandomList.getAntitheticOption();
    }

    /** before any replications reset the underlying random number generator to the
     *  starting stream
     *
     */
    @Override
    protected void beforeExperiment() {
        super.beforeExperiment();
        if (getResetStartStreamOption()) {
            resetStartStream();
        }

    }

    /** after each replication reset the underlying random number generator to the next
     *  substream
     */
    @Override
    protected void afterReplication() {
        super.afterReplication();
        if (getResetNextSubStreamOption()) {
            advanceToNextSubstream();
        }

    }
}
