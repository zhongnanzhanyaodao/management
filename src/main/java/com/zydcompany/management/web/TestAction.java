package com.zydcompany.management.web;

import com.alibaba.fastjson.JSON;
import com.zydcompany.management.domain.model.SystemUserDo;
import com.zydcompany.management.service.SystemUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;

@RestController
public class TestAction {

    private static final Logger log = LoggerFactory.getLogger(TestAction.class);

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
        SystemUserDo systemUserDo = systemUserService.getSystemUserDoById(BigInteger.valueOf(1));
        log.info(JSON.toJSONString(systemUserDo));
        return JSON.toJSONString(systemUserDo);
    }
}
