package knowledge.thread;


import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockExample {

    private static Lock lock = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {
        //子线程
        new Thread(() -> {
            System.out.println("子线程准备获得锁");
            lock.lock();
            System.out.println("子线程获得锁开始处理");
            try {
                Thread.sleep(10*1000);
            } catch (InterruptedException e) {
            }
            lock.unlock();
            System.out.println("子线程处理完成释放锁");
        }).start();

        System.out.println("主线程准备获得锁");
        lock.lock();
        System.out.println("主线程获得锁开始处理");
        Thread.sleep(5*1000);
        lock.unlock();
        System.out.println("主线程处理完成释放锁");

    }
}
