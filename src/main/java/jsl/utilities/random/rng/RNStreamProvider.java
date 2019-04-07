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

import jsl.utilities.reporting.JSL;

import java.util.ArrayList;
import java.util.List;

/**
 * A concrete implementation of RNStreamProviderIfc.  If more than getStreamNumberWarningLimit()
 * streams are made a warning message is logged.  Generally, unless you know what you are doing
 * you should not need an immense number of streams.  Instead, use a small number of
 * streams many times. Conceptually this provider could have a possibly infinite number of streams,
 * which would have bad memory implications.  Thus, the reason for the warning.
 * The default stream if not set is the first stream.
 */
public final class RNStreamProvider implements RNStreamProviderIfc {

    private int myStreamNumberWarningLimit = 5000;

    private final RNStreamFactory myStreamFactory;

    private final List<RNStreamIfc> myStreams;

    private final int myDefaultStreamNum;

    /**
     * Assumes stream 1 is the default
     */
    public RNStreamProvider(){
        this(1);
    }

    /**
     *
     * @param defaultStreamNum the stream number to use as the default
     */
    public RNStreamProvider(int defaultStreamNum) {
        if (defaultStreamNum <= 0){
            throw new IllegalArgumentException("The default stream number must be > 0");
        }
        myDefaultStreamNum = defaultStreamNum;
        myStreamFactory = new RNStreamFactory();
        myStreams = new ArrayList<>();
        // get the default stream number, this makes the intermediate streams also
        defaultRNStream();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("RNStreamProvider{");
        sb.append("StreamNumberWarningLimit=").append(getStreamNumberWarningLimit());
        sb.append(", DefaultStreamNum=").append(defaultRNStreamNumber());
        sb.append(", Number of Streams Provided =").append(lastRNStreamNumber());
        sb.append('}');
        return sb.toString();
    }

    /**
     *
     * @return the limit associated with the warning message concerning the number of streams created
     */
    public final int getStreamNumberWarningLimit(){
        return myStreamNumberWarningLimit;
    }

    /**
     *
     * @param limit the limit associated with the warning message concerning the number of streams created
     */
    public final void setStreamNumberWarningLimit(int limit){
        myStreamNumberWarningLimit = limit;
    }

    @Override
    public int defaultRNStreamNumber() {
        return myDefaultStreamNum;
    }

    @Override
    public RNStreamIfc nextRNStream() {
        RNStreamIfc stream = myStreamFactory.getStream();
        myStreams.add(stream);
        if (myStreams.size() > myStreamNumberWarningLimit){
            JSL.LOGGER.warn("The number of streams made is now = {}", myStreams.size());
            JSL.LOGGER.warn("Increase the stream warning limit if you don't want to see this message");
        }
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
    public int getStreamNumber(RNStreamIfc stream) {
        return myStreams.indexOf(stream) + 1;
    }

    @Override
    public void advanceStreamMechanism(int n) {
        myStreamFactory.advanceSeeds(n);
    }

    @Override
    public void resetRNStreamSequence() {
        myStreams.clear();
        myStreamFactory.resetFactorySeed();
    }
}
