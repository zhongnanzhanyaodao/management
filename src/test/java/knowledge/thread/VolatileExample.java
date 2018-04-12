package knowledge.thread;


public class VolatileExample {

    private static volatile boolean isRunning;

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            while (!isRunning) {
                System.out.println("子线程 is running");
            }
        }).start();

        try {
            Thread.sleep(2 * 1000);
        } catch (InterruptedException e) {
        }
        isRunning = true;
    }
}
