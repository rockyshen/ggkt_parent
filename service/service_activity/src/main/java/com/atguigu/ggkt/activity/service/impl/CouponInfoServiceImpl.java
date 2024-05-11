package com.atguigu.ggkt.activity.service.impl;

import com.atguigu.ggkt.activity.mapper.CouponInfoMapper;
import com.atguigu.ggkt.activity.service.CouponInfoService;
import com.atguigu.ggkt.activity.service.CouponUseService;
import com.atguigu.ggkt.client.user.UserInfoFeignClient;
import com.atguigu.ggkt.model.activity.CouponInfo;
import com.atguigu.ggkt.model.activity.CouponUse;
import com.atguigu.ggkt.model.user.UserInfo;
import com.atguigu.ggkt.vo.activity.CouponUseQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import org.apache.commons.lang.StringUtils;
import org.springframework.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 优惠券信息 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2024-05-11
 */
@Service
public class CouponInfoServiceImpl extends ServiceImpl<CouponInfoMapper, CouponInfo> implements CouponInfoService {

    @Autowired
    private CouponUseService couponUseService;

    @Autowired
    private UserInfoFeignClient userInfoFeignClient;

    @Override
    public IPage<CouponUse> selectCouponUsePage(Page<CouponUse> pageParam, CouponUseQueryVo couponUseQueryVo) {
        // 利用OpenFeign远程调用service_user，查询到user相关信息
        //1 获取条件
        Long couponId = couponUseQueryVo.getCouponId();
        String couponStatus = couponUseQueryVo.getCouponStatus();
        String getTimeBegin = couponUseQueryVo.getGetTimeBegin();
        String getTimeEnd = couponUseQueryVo.getGetTimeEnd();

        //2 判空，封装wrapper
        QueryWrapper<CouponUse> wrapper = new QueryWrapper<>();

        if(!StringUtils.isEmpty(couponId)) {
            wrapper.eq("coupon_id",couponId);
        }
        if(!StringUtils.isEmpty(couponStatus)) {
            wrapper.eq("coupon_status",couponStatus);
        }
        if(!StringUtils.isEmpty(getTimeBegin)) {
            wrapper.ge("get_time",getTimeBegin);
        }
        if(!StringUtils.isEmpty(getTimeEnd)) {
            wrapper.le("get_time",getTimeEnd);
        }

        //3 调用方法查询
        IPage<CouponUse> pageModel = couponUseService.page(pageParam, wrapper);

        //4 基于pageModel，远程调用，获取User
        pageModel.getRecords().stream().forEach(item -> {
            this.getUserInfoById(item);
        });

        return pageModel;
    }

    // 通过远程调用，根据id获取用户信息
    private CouponUse getUserInfoById(CouponUse couponUse) {
        Long userId = couponUse.getUserId();
        if(!StringUtils.isEmpty(userId)){
            UserInfo userInfo = userInfoFeignClient.getById(userId);
            if (userInfo != null){
                couponUse.getParam().put("nickName",userInfo.getNickName());
                couponUse.getParam().put("phone",userInfo.getPhone());
            }
        }
        return couponUse;
    }
}
