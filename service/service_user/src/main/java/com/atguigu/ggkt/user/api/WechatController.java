package com.atguigu.ggkt.user.api;

import com.alibaba.fastjson.JSON;
import com.atguigu.ggkt.model.user.UserInfo;
import com.atguigu.ggkt.user.service.UserInfoService;
import com.atguigu.ggkt.utils.JwtHelper;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.exception.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;

@Controller   // 不用RestController，因为本方法需要跳转，RestController只会返回JSON
@RequestMapping("/api/user/wechat")
public class WechatController {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private WxMpService wxMpService;

    @Value("${wechat.userInfoUrl}")
    private String userInfoUrl;

    //1 授权跳转
    @GetMapping("authorize")
    public String authorize(@RequestParam("returnUrl") String returnUrl,
                            HttpServletRequest request) {
        //参数1 用户信息去哪里取  参数2:授权范围   参数3:取到数据后，重定向到哪个URL

//        System.out.println(returnUrl);
//
//        System.out.println(userInfoUrl);

        String url = wxMpService.oauth2buildAuthorizationUrl(userInfoUrl, WxConsts.OAUTH2_SCOPE_USER_INFO, URLEncoder.encode(returnUrl.replace("guiguketan","#")));
        return "redirect:"+url;
    }

    //2 微信用户信息获取
    @GetMapping("userInfo")
    public String userInfo(@RequestParam("code") String code,
                           @RequestParam("state") String returnUrl) {
        try {
            WxMpOAuth2AccessToken wxMpOAuth2AccessToken =  wxMpService.oauth2getAccessToken(code);
            // 获取微信id  openid
            String openId = wxMpOAuth2AccessToken.getOpenId();
            System.out.println(openId);

            // 获取微信其他信息（昵称、头像等）
            WxMpUser wxMpUser = wxMpService.oauth2getUserInfo(wxMpOAuth2AccessToken,null);
            System.out.println("wxMpUser = " + JSON.toJSONString(wxMpUser));

            // 将微信用户信息添加到数据库（如果openid已存在，就不加了）
            UserInfo userInfo = userInfoService.getUserInfoOpenId(openId);
            if(userInfo == null) {
                //openid 不存在，写入
                userInfo = new UserInfo();
                userInfo.setOpenId(openId);
                userInfo.setNickName(wxMpUser.getNickname());
                userInfo.setUnionId(wxMpUser.getUnionId());
                userInfo.setAvatar(wxMpUser.getHeadImgUrl());
                userInfo.setSex(wxMpUser.getSexId());
                userInfo.setProvince(wxMpUser.getProvince());
                userInfoService.save(userInfo);
            }
            //授权完成后，跳转到业务页面
            String token = JwtHelper.createToken(userInfo.getId(),userInfo.getNickName());
            if(returnUrl.indexOf("?") == -1) {
                return "redirect:" + returnUrl + "?token=" + token;
            } else {
                return "redirect:" + returnUrl + "&token=" + token;
            }

        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
    }
}
