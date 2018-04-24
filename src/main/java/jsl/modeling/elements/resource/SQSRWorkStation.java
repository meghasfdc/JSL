/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsl.modeling.elements.resource;

import jsl.modeling.ModelElement;
import jsl.modeling.queue.Queue;
import jsl.modeling.elements.resource.Delay.DelayOption;
import jsl.utilities.random.RandomIfc;

/**
 *
 * @author rossetti
 */
public class SQSRWorkStation extends EntityReceiver {

    protected Queue myQueue;

    protected Resource myResource;

    protected Delay myDelay;

    /** Default allocation listener for single resource
     *  or single resource set requirements
     *
     */
    protected AllocationListener myAllocationListener;

    public SQSRWorkStation(ModelElement parent) {
        this(parent, 1, null);
    }

    public SQSRWorkStation(ModelElement parent, String name){
        this(parent, 1, name);
    }

    public SQSRWorkStation(ModelElement parent, int numServers, String name) {
        super(parent, name);
        myQueue = new Queue(this, getName() + "_Q");
        myResource = new Resource(this, numServers, getName() + "_R");
        myDelay = new Delay(this, getName() + "_Delay");
        myDelay.setDirectEntityReceiver(new Release());
        myAllocationListener = new AllocationListener();
    }

    @Override
    protected void receive(Entity entity) {
        myQueue.enqueue(entity);
        myResource.seize(entity, myAllocationListener);
    }

    protected void startUsingResource(Entity entity) {
        myQueue.remove(entity);
        myDelay.receive(entity);
    }

    protected void endUsingResource(Entity entity) {
        entity.release(myResource);
        entity.setCurrentReceiver(this);
        sendEntity(entity);
    }

    protected class AllocationListener implements AllocationListenerIfc {

        public void allocated(Request request) {
            if (request.isSatisfied()) {
                Entity e = request.getEntity();
                Resource r = request.getSeizedResource();
                Allocation a = r.allocate(e, request.getAmountAllocated());
                startUsingResource(e);
            }
        }
    }

    protected class Release extends EntityReceiverAbstract {

        protected void receive(Entity entity) {
            endUsingResource(entity);
        }
    }

    public final void setDelayTime(RandomIfc distribution) {
        myDelay.setDelayTime(distribution);
    }

    public final void setDelayOption(DelayOption option) {
        myDelay.setDelayOption(option);
    }

    public final Delay getDelay(){
        return myDelay;
    }
}
