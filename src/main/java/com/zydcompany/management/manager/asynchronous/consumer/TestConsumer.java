package com.zydcompany.management.manager.asynchronous.consumer;

import com.zydcompany.management.config.disconf.DemoConfig;
import com.zydcompany.management.manager.asynchronous.producer.TestProducer;
import com.zydcompany.management.util.ManagementLogUtil;
import com.zydcompany.management.util.ManagementPropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class TestConsumer {

    private static final ManagementLogUtil log = ManagementLogUtil.getLogger();
    private ExecutorService executor;

    @Autowired
    DemoConfig demoConfig;
    @Autowired
    TestProducer testProducer;


    @PostConstruct
    public void init() {
        log.info("TestConsumer init...");
        int corePoolSize = Integer.parseInt(ManagementPropertiesUtil.getThreadPropertiesValue("thread.TestConsumer.corePoolSize"));
        int maximumPoolSize = Integer.parseInt(ManagementPropertiesUtil.getThreadPropertiesValue("thread.TestConsumer.maximumPoolSize"));
        long keepAliveTime = Long.parseLong(ManagementPropertiesUtil.getThreadPropertiesValue("thread.TestConsumer.keepAliveTime"));
        executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        //线程池中固定一个线程不断的从生产者队列里获取数据,将获取的数据交给线程池中的其它线程去处理
        executor.submit(() -> {
            retrieveTraceId();
        });

    }

    private void retrieveTraceId() {
        log.info("TestConsumer retrieveTraceId...");
        while (true) {
            try {
                String traceId = testProducer.take();
                log.info("traceId={}", traceId);
                //将获取的数据交给线程池中的其它线程去处理
                executor.submit(() -> {
                    handleTraceId(traceId);
                });

            } catch (Throwable e) {
                log.error("TestConsumer retrieveTraceId Throwable", e);
            }
        }
    }

    /**
     * traceId用来记录流程，解决多线程环境追踪不到流程id的问题
     *
     * @param traceId
     */
    private void handleTraceId(String traceId) {
        try {
            log.info("traceId={} handleTraceId...", traceId);
            log.info("traceId={} id={}", traceId, demoConfig.getId());
        } catch (Exception e) {
            log.error("traceId={} TestConsumer handleTraceId exception", traceId, e);
        }
    }


}
