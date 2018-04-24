package test.misc;

import jsl.modeling.Simulation;
import jsl.modeling.queue.QObject;
import jsl.modeling.queue.Queue;
import org.junit.*;
import static org.junit.Assert.*;

public class TestQ {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void test1() {
        Simulation s = new Simulation();

        Queue<QObject> q = new Queue(s.getModel());
        
//        Queue q1 = new Queue(s.getModel());
//        q1.enqueue(queueingObject);
//        QObject remove = q1.remove(0);
        QObject r1 = new QObject(q.getTime(), "A");
        q.enqueue(r1);
        QObject r2 = new QObject(q.getTime(), "B");
        q.enqueue(r2);
        QObject r3 = new QObject(q.getTime(), "A");
        q.enqueue(r3);
        QObject r4 = new QObject(q.getTime(), "C");
        q.enqueue(r4);
        QObject r5 = new QObject(q.getTime(), "D");
        q.enqueue(r5);

        System.out.println("Before");
        for (QObject qo : q) {
            System.out.println(qo);
        }

        for (int i = 0; i < q.size(); i++) {
            QObject v = q.peekAt(i);
            if (v.getName().equals("A")) {
                q.remove(v);
            }
        }

        System.out.println("After");
        boolean t = true;
        int i = 1;
        for (QObject qo : q) {
            System.out.println(qo);
            if (i == 1 && !qo.getName().equals("B")) {
                t = false;
                break;
            }
            if (i == 2 && !qo.getName().equals("C")) {
                t = false;
                break;
            }
            if (i == 3 && !qo.getName().equals("D")) {
                t = false;
                break;
            }
            i++;
        }
        assertTrue(t);
    }

}
