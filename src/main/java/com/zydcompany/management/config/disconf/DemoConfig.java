package com.zydcompany.management.config.disconf;

import com.baidu.disconf.client.common.annotations.DisconfFile;
import com.baidu.disconf.client.common.annotations.DisconfFileItem;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("singleton")
@DisconfFile(filename = "demo.properties")
public class DemoConfig {
    private String id;

    @DisconfFileItem(name = "demo.id", associateField = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}