/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsl.modeling.elements.resource;

import jsl.modeling.ModelElement;
import jsl.modeling.elements.RandomElement;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.utilities.random.RandomIfc;
import jsl.utilities.random.distributions.Constant;

/**
 *
 * @author rossetti
 */
public class NWayByChanceEntitySender extends EntityReceiver {

    private RandomElement<GetEntityReceiverIfc> mySelector;

     private RandomVariable myTime;

    public NWayByChanceEntitySender(ModelElement parent) {
        this(parent, null, null);
    }

    public NWayByChanceEntitySender(ModelElement parent, RandomIfc time, String name) {
        super(parent, name);
        mySelector = new RandomElement<GetEntityReceiverIfc>(this);
        setTransferTime(time);
        setEntitySender(new Sender());
    }

    /** Sets the transfer time
     * 
     * @param time 
     */
    public final void setTransferTime(double time) {
        setTransferTime(new Constant(time));
    }

    /** If the supplied value is null, then zero is used for the time
     * 
     * @param time 
     */
    public final void setTransferTime(RandomIfc time) {
        if (time == null){
            time = Constant.ZERO;
        }
        if (myTime == null) {
            myTime = new RandomVariable(this, time);
        } else {
            myTime.setInitialRandomSource(time);
        }
    }

    public final int size() {
        return mySelector.size();
    }

    public final void setResetStartStreamOption(boolean b) {
        mySelector.setResetStartStreamOption(b);
    }

    public final void setResetNextSubStreamOption(boolean b) {
        mySelector.setResetNextSubStreamOption(b);
    }

    public final void setAntitheticOption(boolean flag) {
        mySelector.setAntitheticOption(flag);
    }

    public final void resetStartSubstream() {
        mySelector.resetStartSubstream();
    }

    public final void resetStartStream() {
        mySelector.resetStartStream();
    }

    public final boolean isEmpty() {
        return mySelector.isEmpty();
    }

    public final boolean getResetStartStreamOption() {
        return mySelector.getResetStartStreamOption();
    }

    public final boolean getResetNextSubStreamOption() {
        return mySelector.getResetNextSubStreamOption();
    }

    public final boolean getAntitheticOption() {
        return mySelector.getAntitheticOption();
    }

    public final void advanceToNextSubstream() {
        mySelector.advanceToNextSubstream();
    }

    public final void addLast(GetEntityReceiverIfc obj) {
        mySelector.addLast(obj);
    }

    public final void add(GetEntityReceiverIfc obj, double p) {
        mySelector.add(obj, p);
    }

    @Override
    protected final void receive(Entity entity) {
        sendEntity(entity);
    }

    private class Sender implements EntitySenderIfc {
        @Override
        public void sendEntity(Entity e) {
            e.sendViaReceiver(mySelector.getRandomElement().getEntityReceiver(), myTime);
        }
    }
}
