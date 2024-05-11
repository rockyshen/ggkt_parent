package com.atguigu.ggkt.user.service.impl;

import com.atguigu.ggkt.model.user.UserInfo;
import com.atguigu.ggkt.user.mapper.UserInfoMapper;
import com.atguigu.ggkt.user.service.UserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2024-05-11
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

}
