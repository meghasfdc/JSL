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

/** This class provides an event calendar by using a skew heap to hold the underlying events.
*/
public class SkewHeapEventCalendar implements CalendarIfc {
    
    private BinaryNode myRoot;
    private int myNumEvents;
    
    /** Creates new Calendar */
    public SkewHeapEventCalendar(){
        myNumEvents = 0;
        myRoot = null;
    }
    
    @Override
    public void add(JSLEvent e){
        myRoot = merge(myRoot, new BinaryNode(e));
        myNumEvents++;
    }
       
    @Override
    public JSLEvent nextEvent(){
        if (!isEmpty()){
            JSLEvent e = (JSLEvent)myRoot.value;
            myRoot = merge(myRoot.leftChild, myRoot.rightChild);
            myNumEvents--;
            return (e);
        }
        else
            return(null);
    }
    
    @Override
    public JSLEvent peekNext(){
        if (!isEmpty()){
            JSLEvent e = (JSLEvent)myRoot.value;
            return (e);
        }
        else
            return(null);   	
    }
    
    @Override
    public boolean isEmpty(){
        return (myRoot == null);
    }
       
    @Override
    public void clear(){
        while (nextEvent() != null){
        }
    }
       
    @Override
    public void cancel(JSLEvent e){
        e.setCanceledFlag(true);
    }
       
    @Override
    public int size(){
        return(myNumEvents);
    }
       
    @Override
    public String toString(){
        return("Number of events = " + myNumEvents);
    }
    
    private BinaryNode merge(BinaryNode left, BinaryNode right) {
        if (left == null) return right;
        if (right == null) return left;
        
        JSLEvent leftValue = (JSLEvent)left.value;
        JSLEvent rightValue = (JSLEvent)right.value;
        
        if (leftValue.compareTo(rightValue) < 0) {
            BinaryNode swap = left.leftChild;
            left.leftChild = merge(left.rightChild, right);
            left.rightChild = swap;
            return left;
        } else {
            BinaryNode swap = right.rightChild;
            right.rightChild = merge(right.leftChild, left);
            right.leftChild = swap;
            return right;
        }
    }    
    
    private class BinaryNode {
        /**
         * value being held by node
         */
        public Object value;
        
        /**
         * left child of node
         */
        public BinaryNode leftChild = null;
        
        /**
         * right child of node
         */
        public BinaryNode rightChild = null;
        
        /**
         * initialize a newly created binary node
         */
        public BinaryNode(){
            value = null;
        }
        
        /**
         * initialize a newly created binary node
         *
         * @param v value to be associated with new node
         */
        public BinaryNode(Object v){
            value = v;
        }
        
        /** return true if we are not a sentinel node
         * @return true if not a sentinal node
         */
        public boolean isEmpty() {
            return false;
        }
    }
}
