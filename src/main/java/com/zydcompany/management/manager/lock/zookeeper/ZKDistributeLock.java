package com.zydcompany.management.manager.lock.zookeeper;

import com.google.common.base.Strings;
import com.zydcompany.management.util.ManagementLogUtil;
import com.zydcompany.management.util.ManagementPropertiesUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * zk 实现分布式锁 基于 InterProcessMutex 实现
 */
public class ZKDistributeLock implements DistributeLock {

    private static final ManagementLogUtil log = ManagementLogUtil.getLogger();

    InterProcessMutex lock = null;

    private static final String ROOT = "/management/lock/";

    private ZKClientOperation zkOpt;

    public ZKDistributeLock(ZKClientOperation zkOpt) {
        this.zkOpt = zkOpt;
    }

    /**
     * 获取锁
     *
     * @param key
     * @param timeoutMS
     * @return
     */
    @Override
    public boolean lock(String key, long timeoutMS) {
        try {
            log.info("ZKDistributeLock lock key={} timeoutMS={}", key, timeoutMS);
            // 是否开启分布式锁 不开启直接拿到锁
            if (!Boolean.valueOf(ManagementPropertiesUtil.getManagementBasicPropertiesValue("zookeeper.open.distribute.lock")))
                return Boolean.TRUE;
            CuratorFramework zkClient = zkOpt.getZkClient();
            //如果zk 客户端获取为null 则直接返回 拿到锁
            if (Objects.isNull(zkClient)) return Boolean.TRUE;
            lock = new InterProcessMutex(zkClient, ROOT + key);
            return lock.acquire(timeoutMS, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("ZKDistributeLock lock key={} timeoutMS={} Exception", key, timeoutMS, e);
        }
        return false;
    }

    /**
     * 获取锁，并执行任务，并释放锁
     *
     * @param key
     * @param timeoutMS
     * @param work      执行任务
     */
    @Override
    public void lockAnDoWork(String key, long timeoutMS, Work work) {
        try {
            log.info("ZKDistributeLock lockAnDoWork key={} timeoutMS={} work={}", key, timeoutMS, work);
            // 如果为空直接返回 不阻塞
            if (Objects.isNull(work) || Strings.isNullOrEmpty(key)) return;
            // 没拿到锁，不执行任务
            if (!lock(key, timeoutMS)) return;
            try {
                work.doWork();
            } catch (Exception e) {
                log.error("ZKDistributeLock lockAnDoWork doWork key={} timeoutMS={} work={} Exception", key, timeoutMS, work, e);
            } finally {
                log.info("ZKDistributeLock lockAnDoWork isAcquiredInThisProcess key={}", key);
                if (Objects.nonNull(lock) && lock.isAcquiredInThisProcess()) {
                    unlock();
                    log.info("ZKDistributeLock lockAnDoWork isAcquiredInThisProcess unlock done. key={}", key);
                }
            }
        } catch (Exception e) {
            log.error("ZKDistributeLock lockAnDoWork key={} timeoutMS={} work={} error={}", key, timeoutMS, work, e);
        }
    }

    @Override
    public void unlock() {
        try {
            lock.release();
        } catch (Exception e) {
            log.error("ZKDistributeLock unlock lock={} Exception", lock, e);
        }
    }
}
