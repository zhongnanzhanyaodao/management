package com.zydcompany.management.domain.dto;

import lombok.Data;

@Data
public class WechatAccessTokenDto {

    private String access_token;
    private Integer expires_in;

}
