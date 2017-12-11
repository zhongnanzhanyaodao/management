package com.zydcompany.management.manager.lock.zookeeper;

/**
 * 执行任务
 */
@FunctionalInterface
public interface Work {

    void doWork();
}
