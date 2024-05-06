package com.atguigu.ggkt.vod.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.atguigu.ggkt.result.Result;
import com.atguigu.ggkt.vod.service.FileService;
import com.atguigu.ggkt.vod.utils.ConstantPropertiesUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
//    private String endpoint = "https://oss-cn-shanghai.aliyuncs.com";
//    private String bucketName = "ggkt-rockyshen";
//    private String accessKey = "LTAI5tGNmsHDpCCL81aDse4s";
//    private String secretKey = "FETzGymAxTLrKWDRcMupPAKaeBCTFm";

    //文件上传
    @Override
    public String upload(MultipartFile file) {
        /*
        OSS,签名直传服务
        思考：下面示例代码，是服务器签名直传的示例代码；利用controller中的URL触发
        硅谷课堂用的是上传文件到服务器，然后再提交给aliyun oss，实现代码步骤不同，不能参考
        */

        String endpoint = ConstantPropertiesUtil.END_POINT;
        String bucketName = ConstantPropertiesUtil.BUCKET_NAME;
        String accessKey = ConstantPropertiesUtil.ACCESS_KEY;
        String secretKey = ConstantPropertiesUtil.SECRET_KEY;

        OSS ossClient = new OSSClientBuilder().build(endpoint,accessKey,secretKey);

        try {
            InputStream inputStream = file.getInputStream();

            String originalFileName = file.getOriginalFilename();
            String fileName = UUID.randomUUID().toString() + originalFileName.substring(originalFileName.lastIndexOf("."));

            ossClient.putObject(bucketName,fileName,inputStream);

            String url = endpoint.split("//")[0] + bucketName + "." + endpoint.split("//")[1] + "/" + fileName;
            return url;

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            ossClient.shutdown();
        }
        return null;
    }
}
