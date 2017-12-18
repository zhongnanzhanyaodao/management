package com.zydcompany.management.web;

import com.zydcompany.management.util.ManagementLogUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * thymeleaf模板引擎
 */
@Controller
public class ThymeleafAction {

    private static final ManagementLogUtil log = ManagementLogUtil.getLogger();

    //访问  http://39.108.209.178/management/
    @RequestMapping("/")
    public String testIndex() {
        log.info("ThymeleafAction testIndex...");
        return "index";
    }
}
