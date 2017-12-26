package com.zydcompany.management.web;

import com.zydcompany.management.common.PlatformResponse;
import com.zydcompany.management.config.disconf.DemoConfig;
import com.zydcompany.management.config.redis.RedisServerFactory;
import com.zydcompany.management.config.ssdb.SsdbFactory;
import com.zydcompany.management.domain.dto.TestDto;
import com.zydcompany.management.domain.model.SystemUserDo;
import com.zydcompany.management.exception.BusinessException;
import com.zydcompany.management.manager.asynchronous.producer.TestProducer;
import com.zydcompany.management.manager.lock.zookeeper.DistributeLock;
import com.zydcompany.management.manager.lock.zookeeper.DistributeLockFactory;
import com.zydcompany.management.manager.lock.zookeeper.ZKClientOperation;
import com.zydcompany.management.service.SystemUserService;
import com.zydcompany.management.util.FastJSONHelper;
import com.zydcompany.management.util.ManagementLogUtil;
import com.zydcompany.management.util.ManagementPropertiesUtil;
import com.zydcompany.management.util.ThreadLocalUtil;
import org.nutz.ssdb4j.spi.Response;
import org.nutz.ssdb4j.spi.SSDB;
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
    @Autowired
    DemoConfig demoConfig;
    @Autowired
    TestProducer testProducer;

    @RequestMapping("/test")
    public PlatformResponse test() {
        log.info("test...");
        return PlatformResponse.builder().data(MSG).build();
    }

    @RequestMapping("/testJson")
    public PlatformResponse testJson(@RequestBody TestDto jsonData) {
        log.info("testJson... jsonData={}", FastJSONHelper.serialize(jsonData));
        return PlatformResponse.builder().data(jsonData).build();
    }

    @RequestMapping("/testDb")
    public PlatformResponse testDb() {
        log.info("testDb...");
        SystemUserDo systemUserDo = systemUserService.getSystemUserDoById(BigInteger.valueOf(1));
        log.info("testDb systemUserDo={}", FastJSONHelper.serialize(systemUserDo));
        return PlatformResponse.builder().data(systemUserDo).build();
    }

    @RequestMapping("/testBaseException")
    public PlatformResponse testBaseException() {
        log.info("testBaseException ...");
        if (true) {
            throw new RuntimeException("testBaseException");
        }
        return PlatformResponse.builder().build();
    }

    @RequestMapping("/testBusinessException")
    public PlatformResponse testBusinessException() {
        log.info("testBusinessException ...");
        if (true) {
            throw BusinessException.createBusinessException("testBusinessException");
        }
        return PlatformResponse.builder().build();
    }

    @RequestMapping("/testZookeeper")
    public PlatformResponse testZookeeper() {
        log.info("testZookeeper ...");
        // 分布式锁
        String inputId = "666";
        String lockKey = "testZookeeper" + inputId;
        DistributeLock lock = DistributeLockFactory.getZkLock(zkOpt);
        lock.lockAnDoWork(lockKey, 0, () -> {
            log.info("testZookeeper is donging work");
          /*  try {
                Thread.sleep(60*60*1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            log.info("testZookeeper work done");
        });
        return PlatformResponse.builder().build();
    }

    @RequestMapping("/testRedis")
    public PlatformResponse testRedis() {
        log.info("testRedis ...");
        RedisServerFactory.getRedisServer().setString("zhazha", "you", "testRedis");
        String value = RedisServerFactory.getRedisServer().getString("zhazha", "testRedis");
        log.info("testRedis value={}", value);
        return PlatformResponse.builder().data(value).build();
    }

    @RequestMapping("/testEnvironment")
    public PlatformResponse testEnvironment() {
        log.info("testEnvironment ...");
        String environment = ManagementPropertiesUtil.getManagementBasicPropertiesValue("environment");
        log.info("testEnvironment environment={}", environment);
        return PlatformResponse.builder().data(environment).build();
    }

    @RequestMapping("/testSSDB")
    public PlatformResponse testSSDB(String inputId) {
        log.info("testSSDB ...");
        SSDB ssdb = SsdbFactory.getSSDB();
        ssdb.set("name", "rencai").check(); // call check() to make sure resp is ok
        Response resp = ssdb.get("name");
        if (!resp.ok()) {
            // ...
        } else {
            log.info("testSSDB value={}", resp.asString());
        }
        return PlatformResponse.builder().data(resp.asString()).build();
    }

    @RequestMapping("/testDisconf")
    public PlatformResponse testDisconf() {
        log.info("testDisconf ...");
        String id = demoConfig.getId();
        log.info("testDisconf id={}", id);
        String environment = ManagementPropertiesUtil.getManagementBasicPropertiesValue("environment");
        log.info("testEnvironment environment={}", environment);
        return PlatformResponse.builder().data(environment + ":" + id).build();
    }

    @RequestMapping("/testAsynchronous")
    public PlatformResponse testAsynchronous() {
        log.info("testAsynchronous ...");
        testProducer.offer(ThreadLocalUtil.getTraceId());
        return PlatformResponse.builder().data(ThreadLocalUtil.getTraceId()).build();
    }


}
