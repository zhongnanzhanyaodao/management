package com.zydcompany.management.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestAction {

    private static final Logger log = LoggerFactory.getLogger(TestAction.class);

    @RequestMapping("/test")
    public String test() {
        log.info("阳子阳，亲亲小阳阳！");
        return "阳子阳，亲亲小阳阳！";
    }
}
