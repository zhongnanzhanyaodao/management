package com.zydcompany.management.manager.lock.zookeeper;

/**
 * 分布式锁 接口
 */
public interface DistributeLock {
    /**
     * 获取锁，超时则返回false
     *
     * @param key
     * @param timeoutMS
     */
    boolean lock(String key, long timeoutMS);

    /**
     * 获取锁，并执行任务，并释放锁
     *
     * @param key
     * @param timeoutMS
     * @param work      执行任务
     */
    void lockAnDoWork(String key, long timeoutMS, Work work);

    /**
     * 释放锁
     */
    void unlock();
}
