package com.zydcompany.management.web;

import com.zydcompany.management.domain.model.SystemUserDo;
import com.zydcompany.management.service.SystemUserService;
import com.zydcompany.management.util.FastJSONHelper;
import com.zydcompany.management.util.ManagementLogUtil;
import com.zydcompany.management.util.ManagementPropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;

@RestController
public class TestAction {

    private static final ManagementLogUtil log = ManagementLogUtil.getLogger();

    @Autowired
    SystemUserService systemUserService;

    @RequestMapping("/test")
    public String test() {
        log.info("阳子阳，亲亲小阳阳！");
        return "阳子阳，亲亲小阳阳！";
    }

    @RequestMapping("/testDb")
    public String testDb() {
        log.info("testDb...");
        String testValue = ManagementPropertiesUtil.getManagementBasicPropertiesValue("testKey");
        String environment = ManagementPropertiesUtil.getManagementBasicPropertiesValue("environment");
        log.info("environment={} testValue={}", environment, testValue);
        SystemUserDo systemUserDo = systemUserService.getSystemUserDoById(BigInteger.valueOf(1));
        log.info(FastJSONHelper.serialize(systemUserDo));
        return environment + FastJSONHelper.serialize(systemUserDo);
    }
}
