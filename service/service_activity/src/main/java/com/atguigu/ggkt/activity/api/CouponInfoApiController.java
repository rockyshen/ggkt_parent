package com.atguigu.ggkt.activity.api;

import com.atguigu.ggkt.activity.service.CouponInfoService;
import com.atguigu.ggkt.model.activity.CouponInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "优惠劵接口")
@RestController
@RequestMapping("/api/activity/couponInfo")
public class CouponInfoApiController {

    @Autowired
    private CouponInfoService couponInfoService;

    @ApiOperation("根据优惠劵id查询优惠劵信息")
    @GetMapping("inner/getById/{couponId}")
    public CouponInfo getById(@PathVariable Long couponId) {
        CouponInfo couponInfo = couponInfoService.getById(couponId);
        return couponInfo;
    }

    @ApiOperation("更新优惠劵使用情况")
    @GetMapping("inner/updateCouponInfoUseStatus/{couponUseId}/{orderId}")
    public Boolean updateCouponInfoUseStatus(@PathVariable("couponUseId") Long couponUseId,
                                             @PathVariable("orderId") Long orderId) {
        couponInfoService.updateCouponInfoUseStatus(couponUseId,orderId);
        return true;
    }
}
