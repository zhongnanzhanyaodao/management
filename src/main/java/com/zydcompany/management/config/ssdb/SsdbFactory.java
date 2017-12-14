package com.zydcompany.management.config.ssdb;


import org.nutz.ssdb4j.spi.SSDB;

public class SsdbFactory {

    private static final String DEAFAULT_POOL_NAME = "ssdbPool";

    public static SSDB getSSDB() {
        return SsdbPool.ssdbInstance(DEAFAULT_POOL_NAME);
    }
}
