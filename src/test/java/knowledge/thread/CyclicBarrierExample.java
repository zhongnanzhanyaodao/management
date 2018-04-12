package knowledge.thread;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class CyclicBarrierExample {

    private static CyclicBarrier cyclicBarrier = new CyclicBarrier(3);

    public static void main(String[] args) throws BrokenBarrierException, InterruptedException {
        //子线程1
        new Thread(() -> {
            try {
                Thread.sleep(3 * 1000);
                System.out.println("子线程1 已准备好");
                cyclicBarrier.await();// 等待其它线程
                System.out.println("子线程1 开始处理");
            } catch (Exception e) {
            }
        }).start();
        //子线程2
        new Thread(() -> {
            try {
                Thread.sleep(10 * 1000);
                System.out.println("子线程2 已准备好");
                cyclicBarrier.await();// 等待其它线程
                System.out.println("子线程2 开始处理");
            } catch (Exception e) {
            }
        }).start();

        Thread.sleep(5 * 1000);
        System.out.println("主线程 已准备好");
        cyclicBarrier.await();// 等待其它线程
        System.out.println("主线程 开始处理");
    }


}
