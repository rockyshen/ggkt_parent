package com.atguigu.ggkt.vod.utils;

import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class ConstantPropertiesUtil implements InitializingBean {

//    @Value("${alicloud.oss.endpoint}")
    @Value("${spring.cloud.alicloud.oss.endpoint}")
    private String aliyunOssEndpoint;

//    @Value("${alicloud.oss.access-key}")
    @Value("${spring.cloud.alicloud.access-key}")
    private String aliyunOssAccessKey;

//    @Value("${alicloud.oss.secret-key}")
    @Value("${spring.cloud.alicloud.secret-key}")
    private String aliyunOssSecretKey;

//    @Value("${alicloud.oss.bucket}")
    @Value("${spring.cloud.alicloud.oss.bucket}")
    private String aliyunOssBucketName;

    @Value("${tencent.cloud.access-key}")
    private String tencentVodAccessKey;

    @Value("${tencent.cloud.secret-key}")
    private String tencentVodSecretKey;

    public static String END_POINT;
    public static String ACCESS_KEY;
    public static String SECRET_KEY;
    public static String BUCKET_NAME;

    public static String TENCENT_ACCESS_KEY;

    public static String TENCENT_SECRET_KEY;

    @Override
    public void afterPropertiesSet() throws Exception {
        END_POINT = aliyunOssEndpoint;
        ACCESS_KEY = aliyunOssAccessKey;
        SECRET_KEY = aliyunOssSecretKey;
        BUCKET_NAME = aliyunOssBucketName;

        TENCENT_ACCESS_KEY = tencentVodAccessKey;
        TENCENT_SECRET_KEY = tencentVodSecretKey;
    }
}
