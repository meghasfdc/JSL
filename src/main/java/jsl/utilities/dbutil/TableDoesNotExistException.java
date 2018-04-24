/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jsl.utilities.dbutil;

/**
 * @author rossetti
 */
public class TableDoesNotExistException extends RuntimeException {


    public TableDoesNotExistException() {
        super("DatabaseDoesNotExistException: The database does not exist!");
    }

    public TableDoesNotExistException(String m) {
        super(m);
    }

}
