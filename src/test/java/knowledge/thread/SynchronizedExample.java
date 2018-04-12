package knowledge.thread;


public class SynchronizedExample {

    private static Object object = new Object();

    public static void methodA(String name) throws InterruptedException {
        System.out.println(name + "等待执行methodA");
        Thread.sleep(1 * 1000);
        synchronized (object) {
            System.out.println(name + "开始执行methodA");
            Thread.sleep(10 * 1000);
            System.out.println(name + "执行methodA完成");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            try {
                methodA("子线程");
            } catch (InterruptedException e) {
            }
        }).start();
        methodA("主线程");
    }
}
