/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ex.javaprog;

import java.io.PrintStream;

/**
 *
 * @author rossetti
 */
public class GreeterTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Greeter worldGreeter = new Greeter("World");
        String greeting = worldGreeter.sayHello();
        System.out.println(greeting);
    }
}
