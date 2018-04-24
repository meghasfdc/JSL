/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsl.utilities.dbutil;

/**
 * @author rossetti
 */
public class DatabaseAlreadyExistsException extends RuntimeException {


    public DatabaseAlreadyExistsException() {
        super("DatabaseAlreadyExistsException: The database already exists!");
    }

    public DatabaseAlreadyExistsException(String m) {
        super(m);
    }

}
