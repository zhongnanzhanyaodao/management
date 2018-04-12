package knowledge.thread;

import java.util.concurrent.CountDownLatch;

public class CountDownLatchExample {
    /**
     * 计数器，用来控制线程
     * 传入参数2，表示计数器计数为2
     */
    private final static CountDownLatch mCountDownLatch = new CountDownLatch(2);

    public static void main(String[] args) throws Exception {

        // 运行两个子线程
        // 子线程1运行10秒
        new Thread(() -> {
            try {
                Thread.sleep(10 * 1000);
                mCountDownLatch.countDown();
                System.out.println("子线程1 执行10秒后完成");
            } catch (InterruptedException e) {
            }
        }).start();
        // 子线程2运行5秒
        new Thread(() -> {
            try {
                Thread.sleep(5 * 1000);
                mCountDownLatch.countDown();
                System.out.println("子线程2 执行5秒后完成");
            } catch (InterruptedException e) {
            }
        }).start();

        //主线程等待子线程执行完成
        mCountDownLatch.await();

        System.out.println("主线程在所有子线程完成后继续向下执行其他...");
    }
}