/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ex.models;

import jsl.modeling.EventActionIfc;
import jsl.modeling.JSLEvent;
import jsl.modeling.ModelElement;
import jsl.modeling.SchedulingElement;
import jsl.modeling.Simulation;

/**
 *
 * @author rossetti
 */
public class SchedulingEventExamples extends SchedulingElement {

    private EventAction myEventAction;

    public SchedulingEventExamples(ModelElement parent) {
        this(parent, null);
    }

    public SchedulingEventExamples(ModelElement parent, String name) {
        super(parent, name);
        myEventAction = new EventAction();
    }

    @Override
    protected void initialize() {
        // schedule a type 1 event at time 10.0
        scheduleEvent(10.0, 1);
        // schedule an event that uses myEventAction for time 20.0
        scheduleEvent(myEventAction, 20.0);
    }

    @Override
    protected void handleEvent(JSLEvent event) {
        int type = event.getType();
        if (type == 1) {
            System.out.println("Type 1 event at time : " + getTime());
            // schedule a type 2 event for time t + 5
            scheduleEvent(5.0, 2);
        }

        if (type == 2) {
            System.out.println("Type 2 event at time : " + getTime());
        }
        
    }

    private class EventAction implements EventActionIfc {

        @Override
        public void action(JSLEvent jsle) {
            System.out.println("EventAction event at time : " + getTime());
            // schedule a type 2 event for time t + 15
            scheduleEvent(15.0, 2);
            // reschedule the EventAction event for t + 2
            rescheduleEvent(jsle, 20.0);
        }
    }

    public static void main(String[] args) {

        Simulation s = new Simulation("Scheduling Example");
        new SchedulingEventExamples(s.getModel());
        s.setLengthOfReplication(100.0);
        s.run();
    }
}
