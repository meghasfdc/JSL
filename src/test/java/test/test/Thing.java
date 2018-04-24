/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.test;

/**
 *
 * @author rossetti
 */
public class Thing <T> {
    
    private T myThing;
    
    public T getThing(){
        return myThing;
    }
    
    public void setThing(T thing){
        myThing = thing;
    }
}
