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

package jsl.calendar;

import jsl.modeling.JSLEvent;
import java.util.SortedSet;
import java.util.TreeSet;

/** This class provides an event calendar by using a tree set to hold the underlying events.
*/
public class TreeSetEventCalendar implements CalendarIfc {
    
    private final SortedSet<JSLEvent> myEventSet;
    
    /** Creates new Calendar */
    public TreeSetEventCalendar(){
        myEventSet = new TreeSet<>();
    }
         
    @Override
    public void add(JSLEvent e){
        myEventSet.add(e);
    }
     
    @Override
    public JSLEvent nextEvent(){
        if (!isEmpty()){
            JSLEvent e = (JSLEvent)myEventSet.first();
            myEventSet.remove(e);
            return (e);
        }
        else
            return(null);
    }
   
    @Override
    public JSLEvent peekNext(){
        if (!isEmpty())
            return ((JSLEvent)myEventSet.first());
        else
            return(null);
    }
       
    @Override
    public boolean isEmpty(){
        return myEventSet.isEmpty();
    }
       
    @Override
    public void clear(){
        myEventSet.clear();
    }
    
    @Override
    public void cancel(JSLEvent e){
        e.setCanceledFlag(true);
    }
      
    @Override
    public int size(){
        return(myEventSet.size());
    }
      
    @Override
    public String toString(){
        return(myEventSet.toString());
    }    
}
