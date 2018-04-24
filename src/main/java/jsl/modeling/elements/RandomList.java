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
package jsl.modeling.elements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import jsl.modeling.ModelElement;
import jsl.utilities.random.robj.DUniformList;
import jsl.utilities.random.robj.RList;
import jsl.utilities.random.robj.RListIfc;

/** Allows for random selection from a list of objects as supplied by the {@literal RList<T>}
 *  
 *  The list is created empty unless a {@literal List<T>} is supplied upon construction. Thus,
 *  if the user does not supply an initial list then there will not be any elements to
 *  select from during the replication.
 *
 *  By default the list is cleared prior to each replication.  If a {@literal List<T>} is
 *  supplied to initialize the list at the beginning of each replication, then
 *  it will be used to set up the elements in the list
 * 
 *  Note: The underlying streams for the random selection process are controlled 
 *   by the replication, see RandomVariable
 *   
 *  Note: If the user calls setRandomList() or calls any mutator methods on the list, 
 *  during the replication the list will be changed; however, the supplied initial list
 *  will not have been changed and will be used to reinitialize the list at the
 *  beginning of each replication. If the user does not want this behavior,
 *  then overwrite the initialize() method or turn
 *  off automatic initialization via the ModelElement interface,
 *  setInitializationOption(false).
 *
 *  Warning: If the user uses ANY mutator methods (e.g. add() etc) prior to
 *  the start of a replication, then the list will be CLEARED prior to the
 *  the replication (or set to the supplied initialization list).  If the user
 *  does not want the list cleared or initialize use setInitializationOption(false).
 *  
 *  Warning: If the user changes the supplied initial list during or between replications, then
 *  each replication may not start under the same conditions.
 *  
 *  Warning: There may not be elements in the list, thus a call to getRandomElement() may return null
 * 
 *  The default RList is DUniformList, equally likely selection over the elements in the list
 * 
 * @param <T>
 */
public class RandomList<T> extends ModelElement implements RListIfc<T>, RandomElementIfc {

    protected RList<T> myRList;

    protected List<T> myInitialList;

    private boolean myResetStartStreamOption;

    private boolean myResetNextSubStreamOption;

    /** Creates an empty list.  Elements must be added to it
     *  for random selection to be possible.
     * @param parent
     */
    public RandomList(ModelElement parent) {
        this(parent, null, null);
    }

    /** The initial list is used to initialize the list prior to
     *  each replication.  The supplied list is copied.
     * 
     * @param parent
     */
    public RandomList(ModelElement parent, List<T> initialList) {
        this(parent, initialList, null);
    }

    /** The initial list is used to initialize the list prior to
     *  each replication. The supplied list is copied.
     *
     * @param parent
     * @param initialList
     * @param name
     */
    public RandomList(ModelElement parent, List<T> initialList, String name) {
        super(parent, name);

        setWarmUpOption(false); // do not need to respond to warm events
        setResetStartStreamOption(true);
        setResetNextSubStreamOption(true);
        myRList = new DUniformList<T>();
        setInitialList(initialList);
    }

    protected void initializeList(List<T> list) {
        myRList.clear();
        if (list != null) {
            myRList.addAll(list);
        }
    }

    @Override
    protected void initialize() {
        initializeList(myInitialList);
    }

    @Override
    protected void removedFromModel() {
        super.removedFromModel();
        if (myInitialList != null) {
            myInitialList.clear();
        }
        myInitialList = null;
        myRList.clear();
        myRList = null;
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
     *  sub-stream
     */
    @Override
    protected void afterReplication() {
        super.afterReplication();

        if (getResetNextSubStreamOption()) {
            advanceToNextSubstream();
        }

    }

    /** Allows the user to change the random list after it has
     *  been initialized by the initial list.  The supplied
     *  list will be used directly instead of the current list
     * @param list
     */
    public void setRandomList(RList<T> list) {
        if (list == null) {
            throw new IllegalArgumentException("The list was null");
        }
        myRList = list.newInstance();
    }

    /** Sets the initial list to be used to initialize
     *  the list at the beginning of each replication
     *
     * @param list
     */
    public void setInitialList(List<T> list) {
        myInitialList = new ArrayList<T>(list);
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
    public void add(int index, T element) {
        myRList.add(index, element);
    }

    @Override
    public boolean add(T obj) {
        return myRList.add(obj);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return myRList.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends T> c) {
        return myRList.addAll(index, c);
    }

    @Override
    public void clear() {
        myRList.clear();
    }

    @Override
    public boolean contains(Object arg0) {
        return myRList.contains(arg0);
    }

    @Override
    public boolean containsAll(Collection<?> arg0) {
        return myRList.containsAll(arg0);
    }

    @Override
    public T get(int index) {
        return myRList.get(index);
    }

    /**
     * @return
     * @see jsl.utilities.random.robj.RList#getList()
     */
    public List<T> getList() {
        return myRList.getList();
    }

    @Override
    public T getRandomElement() {
        return myRList.getRandomElement();
    }

    @Override
    public int indexOf(Object arg0) {
        return myRList.indexOf(arg0);
    }

    @Override
    public boolean isEmpty() {
        return myRList.isEmpty();
    }

    @Override
    public Iterator<T> iterator() {
        return myRList.iterator();
    }

    @Override
    public int lastIndexOf(Object o) {
        return myRList.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return myRList.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(int index) {
        return myRList.listIterator(index);
    }

    @Override
    public T remove(int index) {
        return myRList.remove(index);
    }

    @Override
    public boolean remove(Object obj) {
        return myRList.remove(obj);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return myRList.removeAll(c);
    }

    @Override
    public void advanceToNextSubstream() {
        myRList.advanceToNextSubstream();
    }

    @Override
    public void resetStartStream() {
        myRList.resetStartStream();
    }

    @Override
    public void resetStartSubstream() {
        myRList.resetStartSubstream();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return myRList.retainAll(c);
    }

    @Override
    public T set(int index, T element) {
        return myRList.set(index, element);
    }

    @Override
    public void setAntitheticOption(boolean flag) {
        myRList.setAntitheticOption(flag);
    }

    @Override
    public final boolean getAntitheticOption() {
        return myRList.getAntitheticOption();
    }

    @Override
    public int size() {
        return myRList.size();
    }

    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return myRList.subList(fromIndex, toIndex);
    }

    @Override
    public Object[] toArray() {
        return myRList.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return myRList.toArray(a);
    }
}
