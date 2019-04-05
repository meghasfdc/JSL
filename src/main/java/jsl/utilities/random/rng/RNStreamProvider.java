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

package jsl.utilities.random.rng;

import java.util.ArrayList;
import java.util.List;

public final class RNStreamProvider implements RNStreamProviderIfc {

    private final RNStreamFactory myStreamFactory;

    private final List<RNStreamIfc> myStreams;

    private final int myDefaultStreamNum;

    public RNStreamProvider(){
        this(1);
    }

    public RNStreamProvider(int defaultStreamNum) {
        if (defaultStreamNum <= 0){
            throw new IllegalArgumentException("The default stream number must be > 0");
        }
        myDefaultStreamNum = defaultStreamNum;
        myStreamFactory = new RNStreamFactory();
        myStreams = new ArrayList<>();
    }

    @Override
    public int defaultRNStreamNumber() {
        return myDefaultStreamNum;
    }

    @Override
    public RNStreamIfc nextRNStream() {
        RNStreamIfc stream = myStreamFactory.getStream();
        myStreams.add(stream);
        return stream;
    }

    @Override
    public int lastRNStreamNumber() {
        return myStreams.size();
    }

    @Override
    public RNStreamIfc rnStream(int i) {
        if (i > lastRNStreamNumber()){
            RNStreamIfc stream = null;
            for(int j=lastRNStreamNumber();j<=i;j++){
                 stream = nextRNStream();
            }
            return stream;
        }
        return myStreams.get(i-1);
    }

    @Override
    public void advanceStreams(int n) {
        myStreamFactory.advanceSeeds(n);
    }

    @Override
    public void resetRNStreamSequence() {
        myStreams.clear();
        myStreamFactory.resetFactorySeed();
    }
}
