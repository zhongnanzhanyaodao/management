package com.zydcompany.management.web;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.zydcompany.management.common.PlatformResponse;
import com.zydcompany.management.config.redis.RedisServerFactory;
import com.zydcompany.management.domain.dto.WechatAccessTokenDto;
import com.zydcompany.management.exception.message.BaseExceptionMsg;
import com.zydcompany.management.util.FastJSONHelper;
import com.zydcompany.management.util.ManagementLogUtil;
import com.zydcompany.management.util.ManagementPropertiesUtil;
import com.zydcompany.management.util.RestTemplateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/wechat")
public class WechatAction {

    private static final ManagementLogUtil log = ManagementLogUtil.getLogger();

    @Autowired
    RestTemplateUtil restTemplateUtil;


    /**
     * 验证微信后台配置的服务器地址的有效性
     * <p>
     * 接收并校验四个请求参数
     *
     * @param signature 微信加密签名
     * @param timestamp 时间戳
     * @param nonce     随机数
     * @param echostr   随机字符串
     * @return echostr
     */
    @GetMapping("/validToken")
    public String validToken(@RequestParam(name = "signature") String signature,
                             @RequestParam(name = "timestamp") String timestamp,
                             @RequestParam(name = "nonce") String nonce,
                             @RequestParam(name = "echostr") String echostr) {

        log.info("WechatAction validToken input signature={} timestamp={} nonce={} echostr={}",
                signature, timestamp, nonce, echostr);

      /*
        加密/校验流程如下:
        1. 将token、timestamp、nonce三个参数进行字典序排序
        2. 将三个参数字符串拼接成一个字符串进行sha1加密
        3. 开发者获得加密后的字符串可与signature对比，标识该请求来源于微信
      */

        // 1.排序
        // token: 为微信公众平台后台配置的Token
        String token = ManagementPropertiesUtil.getManagementBasicPropertiesValue("wechat.token");
        String sortStr = sort(token, timestamp, nonce);
        // 2.sha1加密
        String sha1Str = sha1(sortStr);
        // 3.字符串校验
        if (!Strings.isNullOrEmpty(sha1Str) && sha1Str.equals(signature)) {
            log.info("WechatAction validToken success echostr={}", echostr);
            //如果检验成功原样返回echostr，微信服务器接收到此输出，才会确认检验完成。
            return echostr;
        } else {
            log.error("WechatAction validToken fail");
            return null;
        }
    }

    /**
     * 获取access_token
     *
     * @return
     */
    @RequestMapping("/getAccessToken")
    public PlatformResponse getAccessToken() {
        log.info("getAccessToken...");
        String accessTokenStr = RedisServerFactory.getRedisServer().getString("accessTokenStr", "getAccessToken");
        if (!Strings.isNullOrEmpty(accessTokenStr)) {
            WechatAccessTokenDto wechatAccessTokenDto = FastJSONHelper.deserialize(accessTokenStr, WechatAccessTokenDto.class);
            return PlatformResponse.builder().data(wechatAccessTokenDto).build();
        }
        String accessTokenUrl = ManagementPropertiesUtil.getManagementBasicPropertiesValue("wechat.accessToken.url");
        String appid = ManagementPropertiesUtil.getManagementBasicPropertiesValue("wechat.appid");
        String secret = ManagementPropertiesUtil.getManagementBasicPropertiesValue("wechat.secret");
        Map<String, String> requestParams = Maps.newHashMap();
        requestParams.put("grant_type", "client_credential");
        requestParams.put("appid", appid);
        requestParams.put("secret", secret);
        String respStr = restTemplateUtil.getByMap(accessTokenUrl, requestParams);
        WechatAccessTokenDto wechatAccessTokenDto = FastJSONHelper.deserialize(respStr, WechatAccessTokenDto.class);
        if (!Strings.isNullOrEmpty(wechatAccessTokenDto.getAccess_token())) {
            //超时时间比外部系统小20分钟，保证从本系统获取的accessToken是有效的。外部系统为7200秒
            RedisServerFactory.getRedisServer().setString("accessTokenStr", respStr, 6000, "getAccessToken");
            return PlatformResponse.builder().data(wechatAccessTokenDto).build();
        }
        return PlatformResponse.builder().code(BaseExceptionMsg.FAIL_CODE).msg(BaseExceptionMsg.FAIL_MSG).build();
    }


    /**
     * 排序方法
     *
     * @param token     Token
     * @param timestamp 时间戳
     * @param nonce     随机数
     * @return
     */
    private String sort(String token, String timestamp, String nonce) {
        String[] strArray = {token, timestamp, nonce};
        Arrays.sort(strArray);
        StringBuffer sb = new StringBuffer();
        for (String str : strArray) {
            sb.append(str);
        }

        return sb.toString();
    }

    /**
     * 将字符串进行sha1加密
     *
     * @param sortStr 需要加密的字符串
     * @return 加密后的内容
     */
    private String sha1(String sortStr) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(sortStr.getBytes());
            byte messageDigest[] = digest.digest();
            // 创建 16进制字符串
            StringBuffer hexString = new StringBuffer();
            // 字节数组转换为 十六进制 数
            for (int i = 0; i < messageDigest.length; i++) {
                String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexString.append(0);
                }
                hexString.append(shaHex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            log.error("sha1 NoSuchAlgorithmException ", e);
        }
        return null;
    }
}