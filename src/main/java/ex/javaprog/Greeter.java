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
