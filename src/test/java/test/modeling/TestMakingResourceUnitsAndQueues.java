/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.modeling;

import jsl.modeling.Model;
import jsl.modeling.Simulation;
import jsl.modeling.queue.QObject;
import jsl.modeling.queue.Queue;
import jsl.modeling.resource.Request;
import jsl.modeling.resource.RequestReactorAdapter;
import jsl.modeling.resource.ResourceUnit;

/**
 *
 * @author rossetti
 */
public class TestMakingResourceUnitsAndQueues {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Simulation sim = new Simulation("Test Making ResourceUnit");
        Model model = sim.getModel();
        ResourceUnit r1 = new ResourceUnit.Builder(model).build();
        RequestReactor rr = new RequestReactor();
        // directly hold Customer
        //Queue<Customer> q = new Queue.Builder<Customer>(model).build();
        Queue<Customer> q = new Queue<>(model);
        QObject qo = new QObject(0.0);
        Queue<QObject> q1 = new Queue<>(model);
        q1.enqueue(qo);
        QObject remove = q1.remove(0);
    }

    public static class Customer extends QObject {

        public Customer(double creationTime, String name) {
            super(creationTime, name);
        }

    }
    
    

    public static class RequestReactor extends RequestReactorAdapter {

        @Override
        public void rejected(Request request) {
            super.rejected(request); //To change body of generated methods, choose Tools | Templates.
        }

    }
}
