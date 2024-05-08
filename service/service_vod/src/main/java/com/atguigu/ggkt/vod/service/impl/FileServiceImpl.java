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
    //文件上传
    @Override
    public String upload(MultipartFile file) {
        /*
        硅谷课堂用的是上传文件到服务器，然后再提交给aliyun oss
        不同于谷粒商城的服务端签名直传
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
