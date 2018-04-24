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
package jsl.observers;

import java.util.ArrayList;
import java.util.List;

/**  The Java observer/observable pattern has a number of flaws.  This class
 *  provides a base implementation of the observer/observable pattern that
 *  mitigates those flaws.  This allows observers to be added and called
 *  in the order added to the component.  The basic usage of this class is to
 *  have a class have an instance of ObservableComponent while implementing
 *  the ObservableIfc.  The notifyObservers() method can be used to notify
 *  attached observers whenever necessary.
 *
 * @author rossetti
 */
public class ObservableComponent implements ObservableIfc {

    /** The list of observers
     *
     */
    private final List<ObserverIfc> myObservers = new ArrayList<ObserverIfc>();

    @Override
    public void addObserver(ObserverIfc observer) {
        if (observer == null) {
            throw new IllegalArgumentException("Attempted to attach a null observer");
        }
        if (myObservers.contains(observer)) {
            throw new IllegalArgumentException("The supplied observer is already attached");
        }

        myObservers.add(observer);
    }

    @Override
    public void deleteObserver(ObserverIfc observer) {
        if (observer == null) {
            throw new IllegalArgumentException("Attempted to delete a null observer");
        }
        myObservers.remove(observer);
    }

    @Override
    public void deleteObservers() {
        myObservers.clear();
    }

    @Override
    public boolean contains(ObserverIfc observer) {
        return myObservers.contains(observer);
    }

    @Override
    public int countObservers() {
        return myObservers.size();
    }

    /** Notify the observers
     * 
     * @param theObserved
     * @param arg 
     */
    public void notifyObservers(Object theObserved, Object arg){
        for(ObserverIfc o: myObservers){
            o.update(theObserved, arg);
        }
    }
}
