/*
 * Copyright (c) 2018. Manuel D. Rossetti, manuelrossetti@gmail.com
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

package ex.javaprog;

public class FlowOfControl {

    public static void main(String[] args) {

        // examples of primitive types and arrays
        // declare and create an array of doubles
        double[] x = {2.1, 3.2, 4.3, 5.4, 6.5};
        // declare another array and assign x to it, they refer to the same array
        double[] y = x;
        // declare an array z of size x.length
        double[] z = new double[x.length];
        // copy x to z
        System.arraycopy(x, 0, z, 0, x.length);
        //change  some elements
        y[1] = 99.0;
        z[4] = -1.0;
        // print out arrays
        for(int i=0; i < x.length; i++)
            System.out.println("x["+i+"] = " + x[i] + "\t y["+i+"] = " + y[i] +
                    "\t z["+i+"] = " + z[i]);

        // skip a line in output
        System.out.println();
        
        //while loop
        int k = 0;
        while(k < x.length){
            if (k == 2){
                System.out.println("Hey, k is 2");
            } else {
                System.out.println("Hey, k is not 2");
            }
            System.out.println("x["+k+"] = " + x[k]);
            k = k + 1;
        }

    }

}
