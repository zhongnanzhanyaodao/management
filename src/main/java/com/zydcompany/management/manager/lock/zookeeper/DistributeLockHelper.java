package com.zydcompany.management.manager.lock.zookeeper;

/**
 * 分布式锁 辅助类
 */
public class DistributeLockHelper {

    /**
     * 获取zk锁
     * @param zkOpt
     * @return
     */
    public static DistributeLock getZkLock(ZKClientOperation zkOpt){
        return new ZKDistributeLock(zkOpt);
    }
}
