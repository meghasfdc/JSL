/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 *
 * Copyright (c) Manuel D. Rossetti (rossetti@uark.edu)
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
package jsl.utilities.random.rng;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import jsl.utilities.random.rng.RNStreamFactory.RNStream;

/** A wrapper for holding a list of streams so that
 *  all streams can be managed together
 *
 *  The methods of the RandomStreamIfc are applied
 *  to all contained random number streams
 *
 * @author rossetti
 */
public class RNGStreamManager implements RandomStreamManagerIfc {

    /** Holds the streams
     */
    protected List<RandomStreamIfc> myStreams;

    public RNGStreamManager() {
        myStreams = new ArrayList<>();
    }

    /** Makes a stream manager and fills it with streams from
     *  RNStreamFactory.getDefault()
     * @param numStreams, must be &gt; 0
     * @return
     */
    public static RNGStreamManager makeRngStreams(int numStreams) {
        return makeRngStreams(numStreams, RNStreamFactory.getDefault());
    }

    /** Makes RNStreams and fills a RNGStreamManager
     *
     * @param numStreams, must be &gt; 0
     * @param f the factory
     * @return the manager
     */
    public static RNGStreamManager makeRngStreams(int numStreams, RNStreamFactory f) {
        if (numStreams <= 0) {
            throw new IllegalArgumentException("The supplied number of streams to make was <= 0");
        }
        if (f == null) {
            throw new IllegalArgumentException("The supplied RNStreamFactory was null");
        }
        RNGStreamManager m = new RNGStreamManager();
        for (int i = 1; i <= numStreams; i++) {
            m.add(f.getStream());
        }
        return m;
    }

    @Override
    public void resetStartStream() {
        for (RandomStreamIfc r : myStreams) {
            r.resetStartStream();
        }
    }

    @Override
    public void resetStartSubstream() {
        for (RandomStreamIfc r : myStreams) {
            r.resetStartSubstream();
        }
    }

    @Override
    public void advanceToNextSubstream() {
        for (RandomStreamIfc r : myStreams) {
            r.advanceToNextSubstream();
        }
    }

    /** Causes all managed streams to advance their
     *  to the next nth substream
     *
     * @param n
     */
    public void advanceToNextSubstream(int n) {
        if (n <= 0) {
            return;
        }
        for (int i = 1; i <= n; i++) {
            advanceToNextSubstream();
        }
    }

    @Override
    public void setAntitheticOption(boolean flag) {
        for (RandomStreamIfc r : myStreams) {
            r.setAntitheticOption(flag);
        }
    }

    @Override
    public boolean getAntitheticOption() {
        if(myStreams.isEmpty()){
            throw new IllegalStateException("There were no streams present");
        }

        ListIterator<RandomStreamIfc> listIterator = myStreams.listIterator();
        boolean b = listIterator.next().getAntitheticOption();

        while( listIterator.hasNext()){
            b = b && listIterator.next().getAntitheticOption();
        }
        return b;
    }

    @Override
    public int size() {
        return myStreams.size();
    }

    /** Sets the stream at the index
     *
     * @param index, must be a valid index
     * @param element, must not be null
     * @return the RandomStreamIfc
     */
    public RandomStreamIfc set(int index, RngIfc element) {
        if (element == null) {
            throw new IllegalArgumentException("The supplied RngIfc was null");
        }
        return myStreams.set(index, element);
    }

    /**
     *
     * @param index must be a valid index
     * @return RandomStreamIfc
     */
    public RandomStreamIfc remove(int index) {
        return myStreams.remove(index);
    }

    public boolean remove(RngIfc o) {
        return myStreams.remove(o);
    }

    public Iterator<RandomStreamIfc> iterator() {
        return myStreams.iterator();
    }

    @Override
    public boolean isEmpty() {
        return myStreams.isEmpty();
    }

    @Override
    public int indexOf(RandomStreamIfc o) {
        return myStreams.indexOf(o);
    }

    /**
     *
     * @param index must be a valid index
     * @return RandomStreamIfc
     */
    @Override
    public RandomStreamIfc get(int index) {
        return myStreams.get(index);
    }

    @Override
    public boolean contains(RandomStreamIfc o) {
        return myStreams.contains(o);
    }

    public void clear() {
        myStreams.clear();
    }

    /** Adds the stream to the manager
     * 
     * @param index, must be a valid index
     * @param element, must not be null
     */
    public void add(int index, RandomStreamIfc element) {
        if (element == null) {
            throw new IllegalArgumentException("The supplied RngIfc was null");
        }
        myStreams.add(index, element);
    }

    /** Adds the stream to the manager
     * 
     * @param e must not be null
     * @return true if added
     */
    public boolean add(RandomStreamIfc e) {
        if (e == null) {
            throw new IllegalArgumentException("The supplied RngIfc was null");
        }
        return myStreams.add(e);
    }

    /** Adds a stream from RNStreamFactory.getDefault()
     *
     * @return the added RNStream
     */
    public RNStream addNewRNStream() {
        return addNewRNStream(RNStreamFactory.getDefault());
    }

    /** Creates a new stream from the supplied factory and adds it
     *  to the list of managed streams
     * @param f, must not be null
     * @return the created stream
     */
    public RNStream addNewRNStream(RNStreamFactory f) {
        if (f == null) {
            throw new IllegalArgumentException("The supplied RNStreamFactory was null");
        }
        RNStream s = f.getStream();
        add(s);
        return s;
    }
}
