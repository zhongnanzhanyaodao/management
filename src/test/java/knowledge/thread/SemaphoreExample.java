package knowledge.thread;

import java.util.concurrent.Semaphore;

public class SemaphoreExample {
    private static Semaphore semaphoreA = new Semaphore(1);

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            try {
                Thread.sleep(5*1000);
                semaphoreA.release();
                System.out.println("子线程："+semaphoreA.availablePermits());
            } catch (InterruptedException e) {

            }
        }).start();
        System.out.println("主线程一："+semaphoreA.availablePermits());
        semaphoreA.release();
        System.out.println("主线程二："+semaphoreA.availablePermits());
        semaphoreA.acquire();
        System.out.println("主线程三："+semaphoreA.availablePermits());
        semaphoreA.acquire();
        System.out.println("主线程四："+semaphoreA.availablePermits());
        semaphoreA.acquire();
        System.out.println("主线程五："+semaphoreA.availablePermits());
    }
}
