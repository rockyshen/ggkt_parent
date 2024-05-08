package com.atguigu.ggkt.vod.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConstantPropertiesUtil implements InitializingBean {

//    @Value("${alicloud.oss.endpoint}")
    @Value("${spring.cloud.alicloud.oss.endpoint}")
    private String endpoint;

//    @Value("${alicloud.oss.access-key}")
    @Value("${spring.cloud.alicloud.access-key}")
    private String accessKey;

//    @Value("${alicloud.oss.secret-key}")
    @Value("${spring.cloud.alicloud.secret-key}")
    private String secretKey;

//    @Value("${alicloud.oss.bucket}")
    @Value("${spring.cloud.alicloud.oss.bucket}")
    private String bucketName;

    public static String END_POINT;
    public static String ACCESS_KEY;
    public static String SECRET_KEY;
    public static String BUCKET_NAME;

    @Override
    public void afterPropertiesSet() throws Exception {
        END_POINT = endpoint;
        ACCESS_KEY = accessKey;
        SECRET_KEY = secretKey;
        BUCKET_NAME = bucketName;
    }
}
