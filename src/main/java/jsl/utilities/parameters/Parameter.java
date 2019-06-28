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

import com.google.common.collect.Range;

import java.util.Objects;

public class Parameter<T extends Comparable> implements ParameterIfc<T> {

    private final String myName;
    private final Range<T> myRange;
    private T myInitialValue;
    private T myValue;

    public Parameter(String name, T initialValue, Range<T> range) {
        Objects.requireNonNull(name, "The name cannot be null");
        Objects.requireNonNull(initialValue, "The initial value cannot be null");
        Objects.requireNonNull(range, "The range cannot be null");
        myName = name;
        myRange = range;
        setInitialValue(initialValue);
        setValue(initialValue);
    }

    @Override
    public final String getName() {
        return myName;
    }

    @Override
    public final Range<T> getRange() {
        return myRange;
    }

    @Override
    public final T getValue() {
        return myValue;
    }

    @Override
    public final void setValue(T value) {
        if (!isValid(value)) {
            String s = "The value " + value.toString() + " is not valid for range: " + myRange.toString();
            throw new IllegalArgumentException(s);
        }
        myValue = value;
    }

    @Override
    public final void setInitialValue(T value) {
        if (!isValid(value)) {
            String s = "The value " + value.toString() + " is not valid for range: " + myRange.toString();
            throw new IllegalArgumentException(s);
        }
        myInitialValue = value;
    }

    @Override
    public final T getInitialValue() {
        return myInitialValue;
    }
}
