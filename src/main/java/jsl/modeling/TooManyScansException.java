/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsl.modeling;

/**
 *
 * @author rossetti
 */
public class TooManyScansException extends RuntimeException {

    public TooManyScansException() {
        super(new String("Too many scans during the conditional action processing" +
                " check for cycling between events or turn off max scan checking"));
    }

    public TooManyScansException(String m) {
        super(m);
    }
}
