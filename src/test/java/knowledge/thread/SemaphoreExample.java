package knowledge.thread;

import java.util.concurrent.Semaphore;

public class SemaphoreExample {
    private static Semaphore semaphore = new Semaphore(1);

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            try {
                Thread.sleep(5*1000);
                semaphore.release();
                System.out.println("子线程："+semaphore.availablePermits());
            } catch (InterruptedException e) {

            }
        }).start();
        System.out.println("主线程一："+semaphore.availablePermits());
        semaphore.release();
        System.out.println("主线程二："+semaphore.availablePermits());
        semaphore.acquire();
        System.out.println("主线程三："+semaphore.availablePermits());
        semaphore.acquire();
        System.out.println("主线程四："+semaphore.availablePermits());
        semaphore.acquire();
        System.out.println("主线程五："+semaphore.availablePermits());
    }
}
