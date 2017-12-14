package knowledge.thread;

/**
 * =======  问题：Java里一个线程调用了Thread.interrupt()到底意味着什么？         =======
 * =======  还有就是Thread.interrupted()会清除interrupted status意味着什么呢？  =======
 * <p>
 * 首先，一个线程不应该由其他线程来强制中断或停止，而是应该由线程自己自行停止。
 * 所以，Thread.stop, Thread.suspend, Thread.resume 都已经被废弃了。
 * 而 Thread.interrupt 的作用其实也不是中断线程，而是「通知线程应该中断了」，
 * 具体到底中断还是继续运行，应该由被通知的线程自己处理。
 * <p>
 * 具体来说，当对一个线程，调用 interrupt() 时，
 * ① 如果线程处于被阻塞状态（例如处于sleep, wait, join 等状态），那么线程将立即退出被阻塞状态，
 * 并抛出一个InterruptedException异常。仅此而已。
 * ② 如果线程处于正常活动状态，那么会将该线程的中断标志设置为 true，仅此而已。
 * 被设置中断标志的线程将继续正常运行，不受影响。
 * <p>
 * interrupt() 并不能真正的中断线程，需要被调用的线程自己进行配合才行。
 * 也就是说，一个线程如果有被中断的需求，那么就可以这样做。
 * ① 在正常运行任务时，经常检查本线程的中断标志位，如果被设置了中断标志就自行停止线程。
 * ② 在调用阻塞方法时正确处理InterruptedException异常。（例如，catch异常后就结束线程。）
 * <p>
 * Thread thread = new Thread(() -> {
 * while (!Thread.interrupted()) {
 * // do more work.
 * }
 * });
 * thread.start();
 * // 一段时间以后
 * thread.interrupt();
 * <p>
 * 具体到你的问题，Thread.interrupted()清除标志位是为了下次继续检测标志位。
 * 如果一个线程被设置中断标志后，选择结束线程那么自然不存在下次的问题，
 * 而如果一个线程被设置中断标识后，进行了一些处理后选择继续进行任务，
 * 而且这个任务也是需要被中断的，那么当然需要清除标志位了。
 */
public class InterruptedExample {

    public static void main(String[] args) {
        testBlockJoin();
//        testBlock();
//        testNoBlock();
    }

    /**
     * 线程处于非阻塞状态，也就是（不处于sleep, wait, join 等状态）时对中断的处理
     */
    public static void testNoBlock() {
        Thread childThread = new Thread(() -> {
            //Thread.interrupted(),获取线程中断状态后会清除中断状态，也就是说之后再调用isInterrupted()为false，因为中断状态被清除了。
            while (!Thread.interrupted()) {
                // doing work...
                System.out.println("doing work...");
            }
            System.out.println("after while...");
        });
        childThread.start();
        try {
            //主线程休眠2秒
            Thread.currentThread().sleep(2 * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 一段时间以后,设置childThread中断标志
        childThread.interrupt();
    }

    /**
     * 线程处于阻塞状态，也就是（处于sleep, wait, join 等状态）时对中断的处理
     */
    public static void testBlock() {
        Thread childThread = new Thread(() -> {
            //isInterrupted()只会获取线程中断状态，不会清除中断状态。
            while (!Thread.currentThread().isInterrupted()) {
                // doing work...
                System.out.println("doing work...");
                try {
                    //使childThread处于阻塞状态
                    Thread.currentThread().sleep(5 * 1000L);
                } catch (InterruptedException e) {
                    System.out.println("childThread catch InterruptedException...");
                    e.printStackTrace();
                    System.out.println("childThread after catch InterruptedException isInterrupted: " + Thread.currentThread().isInterrupted());
                    //重新设置childThread的中断状态,即能退出循环
                    Thread.currentThread().interrupt();
                }
            }
            System.out.println("after while...");
        });
        childThread.start();
        try {
            //主线程休眠2秒，保证childThread也执行到了sleep，即childThread处于阻塞状态
            Thread.currentThread().sleep(2 * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 一段时间以后,设置childThread中断标志，childThread此时处于阻塞状态
        childThread.interrupt();
    }

    /**
     * 线程处于阻塞状态(join)时对中断的处理
     */
    public static void testBlockJoin() {

        Thread joinThread = new Thread(() -> {
            try {
                //使joinThread没那么早结束
                System.out.println("joinThread begin...");
                Thread.currentThread().sleep(30 * 1000L);
                System.out.println("joinThread end...");
            } catch (InterruptedException e) {

            }
        });
        joinThread.start();

        Thread childThread = new Thread(() -> {
            //isInterrupted()只会获取线程中断状态，不会清除中断状态。
            while (!Thread.currentThread().isInterrupted()) {
                // doing work...
                System.out.println("doing work...");
                try {
                    //使childThread等待joinThread完成后再执行，即childThread处于阻塞状态
                    joinThread.join();
                } catch (InterruptedException e) {
                    System.out.println("childThread catch InterruptedException...");
                    e.printStackTrace();
                    System.out.println("childThread after catch InterruptedException isInterrupted: " + Thread.currentThread().isInterrupted());
                    //重新设置子线程的中断状态,即能退出循环
                    Thread.currentThread().interrupt();
                }
            }
            System.out.println("after while...");
        });
        childThread.start();
        try {
            //主线程休眠2秒，保证childThread执行到了joinThread.join()，即childThread处于阻塞状态
            Thread.currentThread().sleep(2 * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 一段时间以后,设置childThread中断标志，childThread此时处于阻塞状态
        childThread.interrupt();
    }
}
