import java.util.Stack;

public class ReaderWriterProblem {
    static Stack mainStack = new Stack();
    static final Object monitor = new Object();
    static boolean flag = false;

    public static class Reader implements Runnable {
        @Override
        public void run() {
            while (!mainStack.isEmpty() || !flag) {
                synchronized (monitor) {
                    if (mainStack.isEmpty()) {
                        try {
                            System.out.println("Waiting");
                            monitor.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("Number " + mainStack.pop() + " has read");
                }
            }
        }
    }

    public static class Writer implements Runnable {
        @Override
        public void run() {
            for (int i = 1; i < 20; i++) {
                try {
                    Thread.sleep(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (monitor) {
                    System.out.println("Number " + (mainStack.size() + 1) + " has written");
                    mainStack.push(mainStack.size() + 1);
                    monitor.notifyAll();
                }
            }
            flag = true;
        }
    }

    public static void main(String[] args) {
        Thread writerThread = new Thread(new Writer());
        Thread readerThread = new Thread(new Reader());
        readerThread.start();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        writerThread.start();
    }
}
