package knowledge.thread;

import java.util.concurrent.CountDownLatch;

public class CountDownLatchExample {
    /**
     * 计数器，用来控制线程
     * 传入参数2，表示计数器计数为2
     */
    private final static CountDownLatch mCountDownLatch = new CountDownLatch(2);

    /**
     * 工作线程类
     */
    private static class WorkingThread extends Thread {
        private final int mSleepTime;

        public WorkingThread(String threadName, int sleepTime) {
            super.setName(threadName);
            mSleepTime = sleepTime;
        }

        @Override
        public void run() {
            System.out.println("[" + Thread.currentThread().getName() + "] started!");
            try {
                Thread.sleep(mSleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mCountDownLatch.countDown();
            System.out.println("[" + Thread.currentThread().getName() + "] end!");
        }
    }

    /**
     * 需要等待的线程类
     */
    private static class NeedWaitThread extends Thread {

        public NeedWaitThread(String threadName) {
            super.setName(threadName);
        }

        @Override
        public void run() {
            System.out.println("[" + Thread.currentThread().getName() + "] started!");
            try {
                // 会阻塞在这里等待 mCountDownLatch 里的计数变为0；
                // 也就是等待另外的WorkingThread调用countDown()
                mCountDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("[" + Thread.currentThread().getName() + "] end!");
        }
    }

    public static void main(String[] args) throws Exception {
        // 最先run NeedWaitThread
        new NeedWaitThread("NeedWaitThread").start();
        // 运行两个工作线程
        // 工作线程1运行10秒
        new WorkingThread("WorkingThread1", 10 * 1000).start();
        // 工作线程2运行5秒
        new WorkingThread("WorkingThread2", 5 * 1000).start();
    }
}