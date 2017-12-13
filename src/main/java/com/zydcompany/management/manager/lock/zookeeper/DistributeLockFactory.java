package com.zydcompany.management.manager.lock.zookeeper;

/**
 * 分布式锁 工厂类
 */
public class DistributeLockFactory {

    /**
     * 获取zk锁
     * @param zkOpt
     * @return
     */
    public static DistributeLock getZkLock(ZKClientOperation zkOpt){
        return new ZKDistributeLock(zkOpt);
    }
}
