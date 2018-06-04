/*
 * Copyright (c) 2018. Manuel D. Rossetti, rossetti@uark.edu
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
