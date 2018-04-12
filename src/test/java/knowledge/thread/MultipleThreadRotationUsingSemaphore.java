package knowledge.thread;

import java.util.concurrent.Semaphore;

/**
 * 三个线程轮流执行，第一个线程打印A，第二个线程打印B，第三个线程打印C……循环10次。
 */
public class MultipleThreadRotationUsingSemaphore {
    public static void main(String[] args) {
        PrintABCUsingSemaphore printABC = new PrintABCUsingSemaphore();
        new Thread(() -> printABC.printA()).start();
        new Thread(() -> printABC.printB()).start();
        new Thread(() -> printABC.printC()).start();
    }
}

class PrintABCUsingSemaphore {
    private Semaphore semaphoreA = new Semaphore(1);
    private Semaphore semaphoreB = new Semaphore(0);
    private Semaphore semaphoreC = new Semaphore(0);


    public void printA() {
        print("A", semaphoreA, semaphoreB);
    }

    public void printB() {
        print("B", semaphoreB, semaphoreC);
    }

    public void printC() {
        print("C", semaphoreC, semaphoreA);
    }

    private void print(String name, Semaphore currentSemaphore, Semaphore nextSemaphore) {
        for (int i = 0; i < 10; ) {
            try {
                currentSemaphore.acquire();
                System.out.println(Thread.currentThread().getName() + " print " + name);
                i++;
                nextSemaphore.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
