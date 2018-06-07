/*
 * Copyright (c) 2018. Manuel D. Rossetti, rossetti@uark.edu
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
package test.modeling;

import java.util.Optional;
import jsl.modeling.Model;
import jsl.modeling.Simulation;
import jsl.modeling.queue.QObject;
import jsl.modeling.queue.Queue;
import jsl.modeling.queue.QueueResponse;

/**
 *
 * @author rossetti
 */
public class Test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Simulation sim = new Simulation("test");
        
        Model m = sim.getModel();
        Queue<Tarzan> queue = new Queue<>(m);
        Optional<QueueResponse> queueResponses = queue.getQueueResponses();
        System.out.println(queueResponses.get().getName());
        
        Tarzan t = new Tarzan(0.6, "him");
       // new Tarzan(0.4, "him");
        queue.enqueue(t);
        queue.removeNext();
        
//        Queue.<Tarzan>newQueue().addTo(m);
//        new Queue.Builder<Tarzan>().addTo(m);
          //new Queue<Tarzan>.Builder().addTo(m);
//        
//        Queue.newQueue().name("it").withoutStats().discipline(Queue.Discipline.FIFO).addTo(m);
        
//        Test test = new Test();
//        Queue<Tarzan> newQueue = test.newQueue("test", Queue.Discipline.LIFO, true);

    }
    
    public static class Tarzan extends QObject {

        public Tarzan(double creationTime) {
            this(creationTime, null);
        }
        
        public Tarzan(double creationTime, String name) {
            super(creationTime, name);
        }
        
    }
    
//    public <T extends QObject> Queue<T> newQueue(String name, Queue.Discipline discipline, boolean statOption){
//        ModelElement me = null;
//        return new Queue<>(me, name, discipline, statOption);
//    }
//    
}
