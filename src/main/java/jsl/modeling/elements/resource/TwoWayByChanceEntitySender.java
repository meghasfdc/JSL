/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsl.modeling.elements.resource;

import jsl.modeling.ModelElement;
import jsl.modeling.elements.variable.RandomVariable;
import jsl.utilities.random.RandomIfc;
import jsl.utilities.random.distributions.Bernoulli;
import jsl.utilities.random.distributions.Constant;

/** This class will probabilistically route to one of two EntityReceiverAbstracts
 *  with probability p to the first EntityReceiverAbstract. If a time
 *  is supplied it is used for the transfer
 *
 * @author rossetti
 */
public class TwoWayByChanceEntitySender extends EntityReceiver {

    private RandomVariable myRV;

    private GetEntityReceiverIfc myR1;

    private GetEntityReceiverIfc myR2;

    private RandomVariable myTime;

    public TwoWayByChanceEntitySender(ModelElement parent, double p,
            GetEntityReceiverIfc r1, GetEntityReceiverIfc r2) {
        this(parent, null, p, r1, r2, null);
    }

    public TwoWayByChanceEntitySender(ModelElement parent, String name, double p,
            GetEntityReceiverIfc r1, GetEntityReceiverIfc r2, RandomIfc time) {
        super(parent, name);
        setFirstReceiver(r1);
        setSecondReceiver(r2);
        setTransferTime(time);
        setEntitySender(new Sender());
        myRV = new RandomVariable(this, new Bernoulli(p));
    }

    /** Sets the transfer time
     * 
     * @param time 
     */
    public final void setTransferTime(double time){
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

    public final void setFirstReceiver(GetEntityReceiverIfc r1) {
        if (r1 == null) {
            throw new IllegalArgumentException("Receiver 1 was null");
        }
        myR1 = r1;
    }

    public final void setSecondReceiver(GetEntityReceiverIfc r2) {
        if (r2 == null) {
            throw new IllegalArgumentException("Receiver 2 was null");
        }
        myR2 = r2;
    }

    @Override
    protected final void receive(Entity entity) {
        sendEntity(entity);
    }

    private class Sender implements EntitySenderIfc {

        @Override
        public void sendEntity(Entity e) {
            if (myRV.getValue() == 1.0) {
                e.sendViaReceiver(myR1.getEntityReceiver(), myTime);
            } else {
                e.sendViaReceiver(myR2.getEntityReceiver(), myTime);
            }
        }
    }
}
