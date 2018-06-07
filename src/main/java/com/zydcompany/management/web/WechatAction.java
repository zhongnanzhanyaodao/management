package com.zydcompany.management.web;

import com.google.common.base.Strings;
import com.zydcompany.management.util.ManagementLogUtil;
import com.zydcompany.management.util.ManagementPropertiesUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@RestController
@RequestMapping("/wechat")
public class WechatAction {

    private static final ManagementLogUtil log = ManagementLogUtil.getLogger();

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