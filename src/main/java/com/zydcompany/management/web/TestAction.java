package com.zydcompany.management.web;

import com.zydcompany.management.common.PlatformResponse;
import com.zydcompany.management.config.redis.RedisServerFactory;
import com.zydcompany.management.domain.dto.TestDto;
import com.zydcompany.management.domain.model.SystemUserDo;
import com.zydcompany.management.exception.BusinessException;
import com.zydcompany.management.manager.lock.zookeeper.DistributeLock;
import com.zydcompany.management.manager.lock.zookeeper.DistributeLockHelper;
import com.zydcompany.management.manager.lock.zookeeper.ZKClientOperation;
import com.zydcompany.management.service.SystemUserService;
import com.zydcompany.management.util.FastJSONHelper;
import com.zydcompany.management.util.ManagementLogUtil;
import com.zydcompany.management.util.ManagementPropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;

/**
 * 请求入参统一为json格式，否则不能打印入参信息
 */
@RestController
public class TestAction {

    private static final ManagementLogUtil log = ManagementLogUtil.getLogger();
    private static final String MSG = "阳子阳，亲亲小阳阳！";

    @Autowired
    SystemUserService systemUserService;
    @Autowired
    ZKClientOperation zkOpt;

    @RequestMapping("/test")
    public PlatformResponse test(String input) {
        log.info(input);
        RedisServerFactory.getRedisServer().setString("zhazha", "you", "test");
        log.info(RedisServerFactory.getRedisServer().getString("zhazha", "test"));
        return PlatformResponse.builder().data(MSG).build();
    }

    @RequestMapping("/testJson")
    public PlatformResponse testJson(@RequestBody TestDto jsonData) {
        if (false) {
            throw new RuntimeException("testJsonException");
        }
        return PlatformResponse.builder().data(jsonData).build();
    }

    @RequestMapping("/testDb")
    public PlatformResponse testDb() {
        log.info("testDb...");
        String testValue = ManagementPropertiesUtil.getManagementBasicPropertiesValue("testKey");
        String environment = ManagementPropertiesUtil.getManagementBasicPropertiesValue("environment");
        log.info("environment={} testValue={}", environment, testValue);
        SystemUserDo systemUserDo = systemUserService.getSystemUserDoById(BigInteger.valueOf(1));
        log.info(FastJSONHelper.serialize(systemUserDo));
        return PlatformResponse.builder().data(systemUserDo).build();
    }

    @RequestMapping("/testBaseException")
    public PlatformResponse testBaseException(String input) {
        if (true) {
            throw new RuntimeException("testBaseException");
        }
        return PlatformResponse.builder().build();
    }

    @RequestMapping("/testBusinessException")
    public PlatformResponse testBusinessException(String input) {
        if (true) {
            throw BusinessException.createBusinessException("testException");
        }
        return PlatformResponse.builder().build();
    }

    @RequestMapping("/testZookeeper")
    public PlatformResponse testZookeeper(String inputId) {
        // 分布式锁
        String lockKey = "testZookeeper" + inputId;
        DistributeLock lock = DistributeLockHelper.getZkLock(zkOpt);
        lock.lockAnDoWork(lockKey, 0, () -> {
            log.info("testZookeeper is donging work");
          /*  try {
                Thread.sleep(60*60*1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            log.info("testZookeeper sleep down");
        });
        return PlatformResponse.builder().build();
    }


}
