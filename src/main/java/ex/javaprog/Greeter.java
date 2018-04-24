/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ex.javaprog;

/**
 *
 * @author rossetti
 */
public class Greeter {

    private String myName;

    /**
    Constructs a Greeter object that can greet a person or 
    entity.
    @param aName the name of the person or entity who should
    be addressed in the greetings.
     */
    public Greeter(String aName) {
        myName = aName;
    }

    /**
    Greet with a "Hello" message.
    @return a message containing "Hello" and the name of
    the greeted person or entity.
     */
    public String sayHello() {
        return "Hello, " + myName + "!";
    }
}
