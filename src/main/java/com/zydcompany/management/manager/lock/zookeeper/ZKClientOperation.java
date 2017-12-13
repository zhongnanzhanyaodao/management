package com.zydcompany.management.manager.lock.zookeeper;

import com.zydcompany.management.util.ManagementLogUtil;
import com.zydcompany.management.util.ManagementPropertiesUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * zookeeper 客户端工具类
 */
@Component
public class ZKClientOperation {
    private static final ManagementLogUtil log = ManagementLogUtil.getLogger();

    private static CuratorFramework zkClient;
    public static final String NAME_SPACE = "management";
    public static final String CONNECTION = ManagementPropertiesUtil.getManagementBasicPropertiesValue("zookeeper.address");
    public static final Integer RETRY_SLEEP_MS = 100; // 连接失败重试时间间隔
    public static final Integer MAX_RETRIES = 2; // 最多重试次数
    public static final Integer CONNECTION_TIME_OUT_MS = 3 * 1000; // 连接超时时间
    public static final Integer SESSION_TIME_OUT_MS = 10 * 1000; // 会话超时时间

    @PostConstruct
    public void init() {
        try {
            zkClient = CuratorFrameworkFactory.builder()
                    .namespace(NAME_SPACE)
                    .connectString(CONNECTION)
                    .retryPolicy(new ExponentialBackoffRetry(RETRY_SLEEP_MS, MAX_RETRIES))
                    .connectionTimeoutMs(CONNECTION_TIME_OUT_MS)
                    .sessionTimeoutMs(SESSION_TIME_OUT_MS)
                    .build();
           /* zkClient.getConnectionStateListenable().addListener(new ConnectionStateListener() {
                @Override
                public void stateChanged(CuratorFramework client, ConnectionState state) {
                    if (state == ConnectionState.LOST) {
                        log.error("ZKClientOperation zkClient lost with zookeeper");
                    } else if (state == ConnectionState.CONNECTED) {
                        log.info("ZKClientOperation zkClient connected with zookeeper");
                    } else if (state == ConnectionState.RECONNECTED) {
                        log.info("ZKClientOperation zkClient reconnected with zookeeper");
                    } else if (state == ConnectionState.SUSPENDED) {
                        log.error("ZKClientOperation zkClient suspended with zookeeper");
                    } else if (state == ConnectionState.READ_ONLY) {
                        log.error("ZKClientOperation zkClient read-only with zookeeper");
                    }
                }
            });*/
            zkClient.start();
            log.info("ZKClientOperation init success ...");
        } catch (Exception e) {
            log.error("ZKClientOperation init Exception", e);
        }
    }

    /**
     * 获取客户端
     *
     * @return
     */
    public CuratorFramework getZkClient() {
        try {
            if (zkClient.getZookeeperClient().isConnected()) {
                return zkClient;
            }
        } catch (Exception e) {
            log.error("ZKClientUtils getZkClient Exception", e);
        }
        return null;
    }

    @PreDestroy
    public void destroy() {
        try {
            CloseableUtils.closeQuietly(zkClient);
            log.info("ZKClientOperation destroy success ...");
        } catch (Exception e) {
            log.error("ZKClientOperation destroy Exception", e);
        }
    }


}