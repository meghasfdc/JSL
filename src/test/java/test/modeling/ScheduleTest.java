/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.modeling;

import jsl.modeling.Model;
import jsl.modeling.Simulation;
import jsl.modeling.elements.Schedule;
import jsl.modeling.elements.ScheduleChangeListenerIfc;

/**
 *
 * @author rossetti
 */
public class ScheduleTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Simulation sim = new Simulation("Test Schedule");
        Model model = sim.getModel();
        Schedule s = new Schedule.Builder(model).startTime(10).length(100).build();
        s.addItem(0.0, 5);
        s.addItem(5, 10, "message 1");
        s.addItem(30, 40, "message 2");
        s.addScheduleChangeListener(new ScheduleListener());
        System.out.println(s);
        sim.setLengthOfReplication(200);
        sim.setNumberOfReplications(2);
        sim.run();
    }
    
    public static class ScheduleListener implements ScheduleChangeListenerIfc {

        @Override
        public void scheduleStarted(Schedule schedule) {
            System.out.println(schedule.getTime() + "> The schedule started");
        }

        @Override
        public void scheduleEnded(Schedule schedule) {
            System.out.println(schedule.getTime() + "> The schedule ended");
        }

        @Override
        public void scheduleItemStarted(Schedule.ScheduleItem item) {
            double t = item.getSchedule().getTime();
            System.out.println(t + " > Item started: " + item.getId());
            System.out.println(item);
         }

        @Override
        public void scheduleItemEnded(Schedule.ScheduleItem item) {
            double t = item.getSchedule().getTime();
            System.out.println(t + " > Item ended: " + item.getId());
            System.out.println(item);
         }
        
    }
    
}
