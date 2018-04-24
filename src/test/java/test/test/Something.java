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
public class Something<T extends Thing> {
    private T myT;
    
    public T getSomething(){return myT;}
    public void setSomething(T theThing) {myT = theThing;}
}
