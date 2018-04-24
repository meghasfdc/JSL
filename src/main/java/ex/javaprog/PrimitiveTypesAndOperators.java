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

public class PrimitiveTypesAndOperators {

    public static void main(String[] args) {

        // call another method
        someTypes();
        someMath();
    }

    public static void someTypes(){
        
        double x = 10.0/3.0; 
        System.out.println("Sets x = " + x);
 
        int n = (int)x; 
        System.out.println("Sets n = " + n);

        float f = (float)x; 
        System.out.println("Sets f = " + f);

        long l = Long.MAX_VALUE;
        System.out.println("Biggest long = " + l);
 
        System.out.println("Biggest int = " + Integer.MAX_VALUE);
        System.out.println("Smallest int = " + Integer.MIN_VALUE);

        boolean t = true;
        System.out.println("t = " + t);
        System.out.println("not t = " + !t);
    }
    
    public static void someMath(){
        double x = 9.0;
        System.out.println("x = " + x);
        double y = Math.sqrt(x);
        System.out.println("y = " + y);
        double z = Math.pow(x, 2.0);
        System.out.println("z = " + z );
        
    }
    
}
