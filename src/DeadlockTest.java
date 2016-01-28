import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

public class DeadlockTest {
    private final static Object object1 = new Object();
    private final static Object object2 = new Object();

    public static class Runnable_A implements Runnable {
        @Override
        public void run() {
            System.out.println("First Synchronization");
            synchronized (object1) {
                System.out.println("Second Synchronization");
                synchronized (object2) {
                    System.out.println("Done");
                }
            }
        }
    }

    public static class Runnable_B implements Runnable {
        @Override
        public void run() {
            System.out.println("First Synchronization");
            synchronized (object2) {
                System.out.println("Second Synchronization");
                synchronized (object1) {
                    System.out.println("Done");
                }
            }
        }
    }

    private static void detectDeadlock() {
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        long[] threadIds = threadBean.findMonitorDeadlockedThreads();
        int deadlockedThreads = threadIds != null ? threadIds.length : 0;
        System.out.println("Number of deadlocked threads: " + deadlockedThreads);
    }

    public static void main(String[] args) {
        Runnable run1 = new Runnable_A();
        Runnable run2 = new Runnable_B();

        Thread thread1 = new Thread(run1);
        Thread thread2 = new Thread(run2);

        thread1.start();
        thread2.start();
        DeadlockTest.detectDeadlock();


    }
}