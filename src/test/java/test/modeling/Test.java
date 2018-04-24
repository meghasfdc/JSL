/*
 *  Copyright (C) 2017 rossetti
 * 
 *  Contact:
 * 	Manuel D. Rossetti, Ph.D., P.E.
 * 	Department of Industrial Engineering
 * 	University of Arkansas
 * 	4207 Bell Engineering Center
 * 	Fayetteville, AR 72701
 * 	Phone: (479) 575-6756
 * 	Email: rossetti@uark.edu
 * 	Web: www.uark.edu/~rossetti
 * 
 *  This file is part of the JSL (a Java Simulation Library). The JSL is a framework
 *  of Java classes that permit the easy development and execution of discrete event
 *  simulation programs.
 * 
 *  The JSL is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 * 
 *  The JSL is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
