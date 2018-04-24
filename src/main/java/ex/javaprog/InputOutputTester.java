/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ex.javaprog;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rossetti
 */
public class InputOutputTester {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        inputTest();
        
        outputTest();
        
    }

    public static void inputTest() {
        Scanner in = new Scanner(System.in);
        System.out.print("How old are you? ");
        int age = in.nextInt();
        age++;
        System.out.println("Next year, you'll be " + age);
    }

    public static void outputTest() {                
        String dn = "directory";
        File d = new File(dn);
        d.mkdir();
        String fn = "filename.txt";
        File f = new File(d, fn);
        System.out.println();
        System.out.println(f);
        try {
            //create the PrintWriter
            PrintWriter pw = new PrintWriter(new FileWriter(f), true);
            pw.println("Write a line to the file");
        } catch (IOException ex) {
            Logger.getLogger(InputOutputTester.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
