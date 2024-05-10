package com.atguigu.ggkt.vod.service.impl;

import com.atguigu.ggkt.exception.GgktException;
import com.atguigu.ggkt.vod.service.VodService;
import com.atguigu.ggkt.vod.utils.ConstantPropertiesUtil;
import com.qcloud.vod.VodUploadClient;
import com.qcloud.vod.model.VodUploadRequest;
import com.qcloud.vod.model.VodUploadResponse;
import com.tencentcloudapi.common.AbstractModel;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.vod.v20180717.VodClient;
import com.tencentcloudapi.vod.v20180717.models.DeleteMediaRequest;
import com.tencentcloudapi.vod.v20180717.models.DeleteMediaResponse;
import org.springframework.stereotype.Service;

@Service
public class VodServiceImpl implements VodService {

    //上传视频
    @Override
    public String uploadVideo() {
        //腾讯云的key
        VodUploadClient client = new VodUploadClient(ConstantPropertiesUtil.TENCENT_ACCESS_KEY, ConstantPropertiesUtil.TENCENT_SECRET_KEY);
        VodUploadRequest request = new VodUploadRequest();

        //Todo 视频本地路径，路径写死，肯定不行啊！改写为“腾讯云客户端上传视频到云平台”
        request.setMediaFilePath("/Users/shen/Desktop/vodUploadTest/009.MOV");


        request.setProcedure("LongVideoPreset");             //任务流名称
        try {
            VodUploadResponse response = client.upload("ap-guangzhou", request);   //注意修改地域节点
            String fileId = response.getFileId();
            return fileId;
            //logger.info("Upload FileId = {}", response.getFileId());
        } catch (Exception e) {
            // 业务方进行异常处理
            //logger.error("Upload Err", e);
            throw new GgktException(20001,"上传视频失败");
        }
    }

    @Override
    public void removeVideo(String fileId) {
        try{
            // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
            // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议采用更安全的方式来使用密钥，请参见：https://cloud.tencent.com/document/product/1278/85305
            // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
            // 腾讯云的key
            Credential cred = new Credential(ConstantPropertiesUtil.TENCENT_ACCESS_KEY, ConstantPropertiesUtil.TENCENT_SECRET_KEY);
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("vod.tencentcloudapi.com");
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            VodClient client = new VodClient(cred, "", clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            DeleteMediaRequest req = new DeleteMediaRequest();
            req.setFileId(fileId);
            // 返回的resp是一个DeleteMediaResponse的实例，与请求对象对应
            DeleteMediaResponse resp = client.DeleteMedia(req);
            // 输出json格式的字符串回包
            System.out.println(AbstractModel.toJsonString(resp));
        } catch (TencentCloudSDKException e) {
            throw new GgktException(20001,"删除视频失败");
        }
    }
}
