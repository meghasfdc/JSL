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

package jsl.modeling.elements.variable;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.Table;
import jsl.modeling.ModelElement;
import jsl.modeling.ModelElementState;
import jsl.observers.ModelElementObserver;
import jsl.utilities.Interval;

import java.util.Arrays;

public class LevelResponse extends ModelElement {

    private final Variable myVariable;
    private final double myLevel;
    private final ModelElementState myAbove;
    private final ModelElementState myBelow;
    private final ModelElementObserver myObserver = new TheObserver();
    private final Counter myTotalTransitionCounter;
    private ModelElementState myCurrentState;
    private final Table<ModelElementState, ModelElementState, Counter> myTransitionCounters;

    public LevelResponse(Variable variable, double level){
        this(variable, level, null);
    }

    public LevelResponse(Variable variable, double level, String name) {
        super(variable, name);
        myVariable = variable;
        if ((level < myVariable.getLowerLimit()) || (myVariable.getUpperLimit() < level)){
            Interval i = new Interval(myVariable.getLowerLimit(), myVariable.getUpperLimit());
            throw new IllegalArgumentException("The supplied level " + level + " was outside the range of the variable " + i);
        }
        myLevel = level;
        myAbove = new ModelElementState(this,getName() + ":+");
        myBelow = new ModelElementState(this, getName() + ":-");
        myAbove.turnOnSojournTimeCollection();
        myBelow.turnOnSojournTimeCollection();
        myVariable.addObserver(myObserver);
        myTransitionCounters = ArrayTable.create(Arrays.asList(myAbove, myBelow), Arrays.asList(myAbove, myBelow));
        Counter caa = new Counter(this, getName()+": +To+");
        Counter cab = new Counter(this, getName()+": +To-");
        Counter cbb = new Counter(this, getName()+": -To-");
        Counter cba = new Counter(this, getName()+": -To+");
        myTransitionCounters.put(myAbove, myAbove, caa);
        myTransitionCounters.put(myAbove, myBelow, cab);
        myTransitionCounters.put(myBelow, myBelow, cbb);
        myTransitionCounters.put(myBelow, myAbove, cba);
        myTotalTransitionCounter = new Counter(this, myVariable.getName()+":Transition:Count");
    }

    private class TheObserver extends ModelElementObserver {
        @Override
        protected void update(ModelElement m, Object arg) {
            stateUpdate();
        }
    }

    @Override
    protected void initialize() {
        if (myVariable.getValue() >= myLevel){
            myCurrentState = myAbove;
        } else {
            myCurrentState = myBelow;
        }
        myCurrentState.enter();
    }

    protected void stateUpdate(){
        ModelElementState nextState;
        if (myVariable.getValue() >= myLevel){
            nextState = myAbove;
        } else {
            nextState = myBelow;
        }
        myTransitionCounters.get(myCurrentState, nextState).increment();
        myTotalTransitionCounter.increment();
        if (myCurrentState != nextState){
            myCurrentState.exit();
            nextState.enter();
            myCurrentState = nextState;
        }
    }
}
