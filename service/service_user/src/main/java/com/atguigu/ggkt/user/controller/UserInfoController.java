package com.atguigu.ggkt.user.controller;


import com.atguigu.ggkt.model.user.UserInfo;
import com.atguigu.ggkt.user.service.UserInfoService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2024-05-11
 */

@ApiOperation("根据id获取用户信息")
@RestController
@RequestMapping("/admin/user/userInfo")
public class UserInfoController {
    @Autowired
    private UserInfoService userInfoService;

    @GetMapping("inner/getById/{id}")
    public UserInfo getById(@PathVariable Long id) {
        UserInfo userInfo = userInfoService.getById(id);
        return userInfo;   //不封装Result.ok，因为这是微服务模块内部调用，直接返回对象更简单
    }

}

