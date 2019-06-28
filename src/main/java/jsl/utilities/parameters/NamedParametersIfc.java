/*
 * Copyright (c) 2019. Manuel D. Rossetti, rossetti@uark.edu
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

package jsl.utilities.parameters;

import jsl.utilities.random.ParametersIfc;

import java.util.List;
import java.util.Objects;

/**
 *  An interface for working with named parameters.  Each parameter is given a unique
 *  string name and a value.  Parameters can be set/get via name or index, where index
 *  is based on the ordering of names returned by getParameterNames()
 */
public interface NamedParametersIfc extends ParametersIfc {

    List<String> getParameterNames();

    void setParameter(String name, double value);

    double getParameter(String name);

    /**
     *
     * @param name the name of the parameter to look up
     * @return the index of the name or -1 if not found
     */
    default int getParameterIndex(String name){
        List<String> names = getParameterNames();
        return names.indexOf(name);
    }

    /**
     *
     * @return an array of the parameters
     */
    default double[] getParameters(){
        int n = getNumberOfParameters();
        double[] values = new double[n];
        for(int i=0; i<n;i++){
            values[i] = getParameter(i);
        }
        return values;
    }

    /** Sets all the parameter values assuming the ordering of the names returned by
     *  getParameterNames()
     *
     * @param values must not be null
     */
    default void setParameters(double[] values){
        Objects.requireNonNull(values, "The array was null");
        for(int i=0; i< values.length; i++){
            setParameter(i, values[i]);
        }
    }

    /**
     *
     * @return the count of the number of parameters
     */
    default int getNumberOfParameters(){
        return getParameterNames().size();
    }

    /**
     *
     * @param index the index of the name to look up, must be valid index
     * @return the name associated with the supplied index
     */
    default String getParameterName(int index){
        return getParameterNames().get(index);
    }

    /**
     *
     * @param i the index of the parameter to set, must be 0 to (getNumberOfParameters() - 1)
     * @param value the value to set
     */
    default void setParameter(int i, double value){
        String name = getParameterName(i);
        setParameter(name, value);
    }

    /**
     *
     * @param i the index of the parameter to get, must be 0 to (getNumberOfParameters() - 1)
     * @return the value of the ith parameter
     */
    default double getParameter(int i){
        String name = getParameterName(i);
        return getParameter(name);
    }
}
