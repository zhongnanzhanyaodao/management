package com.zydcompany.management.manager.asynchronous.producer;

import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
public class TestProducer {

    private static final BlockingQueue<String> traceIdQueue = new LinkedBlockingQueue<>(50000);

    public boolean offer(String e) {
        return traceIdQueue.offer(e);
    }

    public String take() throws InterruptedException {
        return traceIdQueue.take();
    }

    public int size() {
        return traceIdQueue.size();
    }
}
