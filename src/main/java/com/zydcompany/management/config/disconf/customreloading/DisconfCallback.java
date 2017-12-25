package com.zydcompany.management.config.disconf.customreloading;

import com.baidu.disconf.client.common.update.IDisconfUpdatePipeline;
import com.zydcompany.management.util.ManagementLogUtil;
import com.zydcompany.management.util.ManagementPropertiesUtil;
import org.springframework.stereotype.Service;

@Service
public class DisconfCallback implements IDisconfUpdatePipeline {

    private static final ManagementLogUtil log = ManagementLogUtil.getLogger();

    @Override
    public void reloadDisconfFile(String arg0, String arg1) throws Exception {
        log.info("DisconfCallback reloadDisconfFile fileName={}", arg0);
        if (FileEnum.MANAGEMENTBASIC.getFileName().equals(arg0)) {
            ManagementPropertiesUtil.managementBasicPropReload();
        }
    }

    @Override
    public void reloadDisconfItem(String arg0, Object arg1) throws Exception {
    }

}
