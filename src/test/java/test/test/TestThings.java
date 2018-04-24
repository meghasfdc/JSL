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
public class TestThings {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Thing<String> thing = new Thing<>();
        thing.setThing("stuff");
        String thing1 = thing.getThing();
        Thing2<Integer> thing2 = new Thing2<>();
        thing2.setThing(0);
        Integer thing3 = thing2.getThing();
        
        Something s = new Something();
        Thing something = s.getSomething();
    }
    
}
