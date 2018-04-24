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
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/** LinkedListEventCalendar is a concrete implementation of the CalendarIfc for use with the Scheduler
 *  This class provides an event calendar by using a java.util.LinkedList to hold the underlying events.
 *
*/
public class LinkedListEventCalendar implements CalendarIfc {
    
    private final List<JSLEvent> myEventSet;
    
    /** Creates new Calendar */
    public LinkedListEventCalendar(){
        myEventSet = new LinkedList<>();
    }
           
    @Override
    public void add(JSLEvent e){
        
        // nothing in calendar, just add it, and return
        if (myEventSet.isEmpty()){ 
            myEventSet.add(e);
            return;
        }
        
        // might as well check for worse case, if larger than the largest then put it at the end and return
        if (e.compareTo(myEventSet.get(myEventSet.size()-1)) >= 0){
            myEventSet.add(e);
            return;
        }
        
         // now iterate through the list
        for (ListIterator<JSLEvent> i=myEventSet.listIterator(); i.hasNext(); ){
            if ( e.compareTo(i.next()) < 0 ){
                // next() move the iterator forward, if it is < what was returned by next(), then it
                // must be inserted at the previous index
                myEventSet.add(i.previousIndex(),e);
                return;
            }
        }
    }
       
    @Override
    public JSLEvent nextEvent(){
        if (!isEmpty())
            return ((JSLEvent)myEventSet.remove(0));
        else
            return(null);
    }
      
    @Override
    public JSLEvent peekNext(){
        if (!isEmpty())
            return ((JSLEvent)myEventSet.get(0));
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