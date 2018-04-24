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
package jsl.utilities.random.robj;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jsl.utilities.random.distributions.DEmpiricalPMF;
import jsl.utilities.random.rng.RNStreamFactory;
import jsl.utilities.random.rng.RngIfc;

public class DEmpiricalList<T> implements RElementIfc<T> {

    protected List<T> myElements;

    protected DEmpiricalPMF myPDF;

    public DEmpiricalList() {
        this(RNStreamFactory.getDefault().getStream());
    }

    public DEmpiricalList(RngIfc rng) {
        super();
        myElements = new ArrayList<T>();
        myPDF = new DEmpiricalPMF(rng);
    }

    public DEmpiricalList(List<T> elements, double[] prob, RngIfc rng) {
        this(rng);
        if (elements == null) {
            throw new IllegalArgumentException("The list of elements was null");
        }
        if (prob == null) {
            throw new IllegalArgumentException("The list of probabilities was null");
        }
        if (elements.size() < prob.length) {
            throw new IllegalArgumentException("The number of objects was less than the number of probabilities.");
        }

        // the array has n=length elements
        // with index from 0 to n-1
        for (int i = 0; i <= prob.length - 2; i++) {
            T obj = elements.get(i);
            add(obj, prob[i]);
        }

        T obj = elements.get(prob.length - 1);
        addLast(obj);
    }

    public void add(T obj, double p) {

        if (myElements.contains(obj)) {
            throw new IllegalArgumentException("Already in the set");
        }

        myElements.add(obj);
        myPDF.addProbabilityPoint(myElements.indexOf(obj), p);
    }

    public void addLast(T obj) {
        if (myElements.contains(obj)) {
            throw new IllegalArgumentException("Already in the set");
        }

        myElements.add(obj);
        myPDF.addLastProbabilityPoint(myElements.indexOf(obj));

    }

    @Override
    public T getRandomElement() {
        int i = (int) myPDF.getValue();
        return (myElements.get(i));
    }

    /* (non-Javadoc)
     * @see jsl.utilities.random.RandomListIfc#resetNextSubstream()
     */
    @Override
    public void advanceToNextSubstream() {
        myPDF.advanceToNextSubstream();
    }

    /* (non-Javadoc)
     * @see jsl.utilities.random.RandomListIfc#resetStartStream()
     */
    @Override
    public void resetStartStream() {
        myPDF.resetStartStream();
    }

    /* (non-Javadoc)
     * @see jsl.utilities.random.RandomListIfc#resetStartSubstream()
     */
    @Override
    public void resetStartSubstream() {
        myPDF.resetStartSubstream();
    }

    /* (non-Javadoc)
     * @see jsl.utilities.random.RandomListIfc#setAntithetic(boolean)
     */
    @Override
    public void setAntitheticOption(boolean flag) {
        myPDF.setAntitheticOption(flag);
    }

    @Override
    public final boolean getAntitheticOption() {
        return myPDF.getAntitheticOption();
    }

    /* (non-Javadoc)
     * @see jsl.utilities.random.RandomListIfc#contains(java.lang.Object)
     */
    public final boolean contains(Object arg0) {
        return myElements.contains(arg0);
    }

    /* (non-Javadoc)
     * @see jsl.utilities.random.RandomListIfc#containsAll(java.util.Collection)
     */
    public final boolean containsAll(Collection<?> arg0) {
        return myElements.containsAll(arg0);
    }

    /* (non-Javadoc)
     * @see jsl.utilities.random.RandomListIfc#indexOf(java.lang.Object)
     */
    public final int indexOf(Object arg0) {
        return myElements.indexOf(arg0);
    }

    /* (non-Javadoc)
     * @see jsl.utilities.random.RandomListIfc#isEmpty()
     */
    public final boolean isEmpty() {
        return myElements.isEmpty();
    }

    /* (non-Javadoc)
     * @see jsl.utilities.random.RandomListIfc#size()
     */
    public final int size() {
        return myElements.size();
    }

    /* (non-Javadoc)
     * @see jsl.utilities.random.RandomListIfc#getList()
     */
    public final List<T> getList() {
        return (Collections.unmodifiableList(myElements));
    }

    public static void main(String[] args) {

        DEmpiricalList<String> originSet = new DEmpiricalList<String>();

        originSet.add("KC", 0.4);
        originSet.add("CH", 0.4);
        originSet.addLast("NY");

        for (int i = 1; i <= 10; i++) {
            System.out.println(originSet.getRandomElement());
        }

        Map<String, DEmpiricalList<String>> od = new HashMap<String, DEmpiricalList<String>>();

        DEmpiricalList<String> kcdset = new DEmpiricalList<String>();

        kcdset.add("CO", 0.2);
        kcdset.add("AT", 0.4);
        kcdset.addLast("NY");

        DEmpiricalList<String> chdset = new DEmpiricalList<String>();

        chdset.add("AT", 0.2);
        chdset.add("NY", 0.4);
        chdset.addLast("KC");

        DEmpiricalList<String> nydset = new DEmpiricalList<String>();

        nydset.add("AT", 0.2);
        nydset.add("KC", 0.4);
        nydset.addLast("CH");

        od.put("KC", kcdset);
        od.put("CH", chdset);
        od.put("NY", nydset);

        for (int i = 1; i <= 10; i++) {
            String key = originSet.getRandomElement();
            DEmpiricalList<String> rs = od.get(key);
            System.out.println(rs.getRandomElement());
        }
    }
}
