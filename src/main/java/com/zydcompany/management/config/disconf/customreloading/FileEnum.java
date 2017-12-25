package com.zydcompany.management.config.disconf.customreloading;

public enum FileEnum {

    MANAGEMENTBASIC("managementBasic.properties", "基础配置文件"),;

    String fileName;
    String fileDesc;

    private FileEnum(String fileName, String fileDesc) {
        this.fileName = fileName;
        this.fileDesc = fileDesc;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileDesc() {
        return fileDesc;
    }
}
