import java.io.IOException;
import java.util.Random;

class MainTest {

    public static void main(String[] args) throws InterruptedException,
            IOException {

        SimpleDataQueue queue = new SimpleDataQueue(200);
        Producer producer = new Producer(queue);
        Consumer consumer = new Consumer(queue);

        Thread t1 = new Thread(producer);
        Thread t2 = new Thread(producer);
        Thread t3 = new Thread(producer);

        Thread t4 = new Thread(consumer);
        Thread t5 = new Thread(consumer);
        Thread t6 = new Thread(consumer);
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
        t6.start();
        Thread.sleep(5000);
        producer.shutdown();
        //t1.join();
        //t2.join();
        //t3.join();
        consumer.shutdown();

        //t4.join();
        //t5.join();

        System.out.println("Finish!");
    }
}

class SimpleDataQueue {

    private int head;
    private int tail;
    private volatile int elementsCount;
    private Integer[] myArrayQueue;

    public SimpleDataQueue(int size) {
        myArrayQueue = new Integer[size];
    }

    public synchronized void add(Integer element) {
        //synchronized (this) {
        while (elementsCount >= 100) {
            try {
                System.out.println("before wait add");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        myArrayQueue[head] = element;
        elementsCount++;

        if (head == myArrayQueue.length - 1) {
            head = 0;
        } else {
            head++;
        }
        notifyAll();
        //}
    }

    public synchronized Integer remove() {
        //synchronized (this) {
        while (getElementsCount() == 0) {
            try {
                System.out.println("before wait remove");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Integer value = myArrayQueue[tail];
        myArrayQueue[tail] = null;
        elementsCount--;

        if (tail == myArrayQueue.length - 1) {
            tail = 0;
        } else {
            tail++;
        }

        if (elementsCount <= 80) {
            notifyAll();
        }
        return value;
        //}
    }

    public synchronized int getElementsCount() {
        return elementsCount;
    }
}

class Producer implements Runnable {
    private SimpleDataQueue queue;
    private volatile boolean ready = false;

    public Producer(SimpleDataQueue queue) {
        this.queue = queue;
    }


    public void run() {
        Random rand = new Random();
        while (!ready) {
            try {
                //System.out.println("producer waiting");
                Thread.sleep(rand.nextInt(100));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            queue.add(rand.nextInt(100));
            System.out.println("Producer "+Thread.currentThread().getName()+" Queue elements size is: "
                    + queue.getElementsCount());
        }
        System.out.println(" Ending Producer " + Thread.currentThread().getName());
    }

    public void shutdown() {
        ready = true;

    }
}

class Consumer implements Runnable {

    private volatile SimpleDataQueue queue;
    private volatile boolean ready = false;

    // private SomeUtil someUtil;
    public Consumer(SimpleDataQueue queue) {
        this.queue = queue;
        // someUtil = new SomeUtil();
    }


    public void run() {
        Random rand = new Random();
        int cnt=0;
        do{
            synchronized(queue){
                cnt=queue.getElementsCount();
                if(cnt>0){
                    queue.remove();
                    System.out.println("Consumer " + Thread.currentThread().getName()+" Queue elements size is: "
                            + queue.getElementsCount());
                }
                cnt=queue.getElementsCount();

            }
            try {
                //System.out.println("consumer waiting");
                Thread.sleep(rand.nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }while(!ready || cnt>0);

        System.out.println("End Consumer " + Thread.currentThread().getName());
    }

    public void shutdown() {
        ready = true;
    }
}
