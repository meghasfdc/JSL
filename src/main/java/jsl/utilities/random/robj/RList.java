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
package jsl.utilities.random.robj;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import jsl.utilities.random.rng.RNStreamFactory;

import jsl.utilities.random.rng.RngIfc;

abstract public class RList<T> implements RListIfc<T> {

    protected List<T> myElements;

    protected RngIfc myRNG;

    public RList() {
        myElements = new ArrayList<T>();
        myRNG = RNStreamFactory.getDefault().getStream();
    }

    /** The object cannot be null, but it can be added more than once
     *  See how List handles multiple instances of the same object
     *
     * @param obj
     */
    @Override
    public boolean add(T obj) {
        if (obj == null) {
            throw new IllegalArgumentException("The object was null");
        }
        return myElements.add(obj);
    }

    @Override
    public boolean remove(Object obj) {
        return myElements.remove(obj);
    }

    @Override
    abstract public T getRandomElement();

    abstract public RList<T> newInstance();

    @Override
    public boolean contains(Object arg0) {
        return myElements.contains(arg0);
    }

    @Override
    public boolean containsAll(Collection<?> arg0) {
        return myElements.containsAll(arg0);
    }

    @Override
    public int indexOf(Object arg0) {
        return myElements.indexOf(arg0);
    }

    @Override
    public boolean isEmpty() {
        return myElements.isEmpty();
    }

    @Override
    public int size() {
        return myElements.size();
    }

    public List<T> getList() {
        return (Collections.unmodifiableList(myElements));
    }

    @Override
    public void add(int index, T element) {
        if (element == null) {
            throw new IllegalArgumentException("The object was null");
        }
        myElements.add(index, element);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        if (c == null) {
            throw new IllegalArgumentException("The collection was null");
        }
        if (c.contains(null)) {
            throw new IllegalArgumentException("The an object in the collection was null");
        }
        return myElements.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        if (c == null) {
            throw new IllegalArgumentException("The collection was null");
        }
        if (c.contains(null)) {
            throw new IllegalArgumentException("The an object in the collection was null");
        }
        return myElements.addAll(index, c);
    }

    @Override
    public void clear() {
        myElements.clear();
    }

    @Override
    public T get(int index) {
        return myElements.get(index);
    }

    @Override
    public Iterator<T> iterator() {
        return myElements.iterator();
    }

    @Override
    public int lastIndexOf(Object o) {
        return myElements.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return myElements.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return myElements.listIterator(index);
    }

    @Override
    public T remove(int index) {
        return myElements.remove(index);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return myElements.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return myElements.retainAll(c);
    }

    @Override
    public T set(int index, T element) {
        if (element == null) {
            throw new IllegalArgumentException("The object was null");
        }
        return myElements.set(index, element);
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return myElements.subList(fromIndex, toIndex);
    }

    @Override
    public Object[] toArray() {
        return myElements.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return myElements.toArray(a);
    }

    @Override
    public void advanceToNextSubstream() {
        myRNG.advanceToNextSubstream();
    }

    @Override
    public void resetStartStream() {
        myRNG.resetStartStream();
    }

    @Override
    public void resetStartSubstream() {
        myRNG.resetStartSubstream();
    }

    @Override
    public void setAntitheticOption(boolean flag) {
        myRNG.setAntitheticOption(flag);
    }

    @Override
    public final boolean getAntitheticOption() {
        return myRNG.getAntitheticOption();
    }
}
