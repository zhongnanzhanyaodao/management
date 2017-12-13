package knowledge.thread;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 有一个需求：有几个同学约好一起去食堂吃饭，各自都从各自的宿舍出发，然后到宿舍楼下集合。当所有的人都到了宿舍楼下之后，再一起从宿舍楼下出发前往食堂吃饭。
 */
public class CyclicBarrierExample {

    private static final int THREAD_NUMBER = 3;
    private static CyclicBarrier sCyclicBarrier = new CyclicBarrier(
            THREAD_NUMBER, new Runnable() {
        @Override
        public void run() {
            System.out.println("大家都到达了宿舍楼下，一起出发前往食堂吃饭吧。。。");
        }
    });

    public static void main(String[] args) {
        ExecutorService executorService = Executors
                .newFixedThreadPool(THREAD_NUMBER);
        for (int i = 0; i < THREAD_NUMBER; i++) {
            executorService.execute(new WalkFromDomitoryToCanteenRunnable(
                    sCyclicBarrier, "同学" + i));
        }
        try {
            Thread.sleep(10000);//主线程睡眠
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("CyclicBarrier重用");
        for (int i = THREAD_NUMBER; i < THREAD_NUMBER * 2; i++) {
            executorService.execute(new WalkFromDomitoryToCanteenRunnable(
                    sCyclicBarrier, "同学" + i));
        }
    }

    /**
     * 从宿舍到食堂线程
     *
     * @author LiuYi
     */
    public static class WalkFromDomitoryToCanteenRunnable implements Runnable {
        private CyclicBarrier mCyclicBarrier;
        private String mName;

        public WalkFromDomitoryToCanteenRunnable(CyclicBarrier cyclicBarrier,
                                                 String name) {
            this.mCyclicBarrier = cyclicBarrier;
            this.mName = name;
        }

        @Override
        public void run() {
            System.out.println(mName + "开始从宿舍出发。。。");
            try {
                Thread.sleep(1000);
                mCyclicBarrier.await();// 等待别同学
                // 前往食堂
                System.out.println(mName + "开始从宿舍楼下出发去食堂。。。");
                Thread.sleep(1000);
                System.out.println(mName + "达到食堂。。。");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }
}
