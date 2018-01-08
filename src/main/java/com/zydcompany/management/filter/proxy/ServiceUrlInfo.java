package com.zydcompany.management.filter.proxy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceUrlInfo {
    private String serviceName;
    private String serviceSuffixUrl;
}
