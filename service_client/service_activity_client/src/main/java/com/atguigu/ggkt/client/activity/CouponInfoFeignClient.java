package com.atguigu.ggkt.client.activity;

import com.atguigu.ggkt.model.activity.CouponInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("service-activity")
public interface CouponInfoFeignClient {
    @GetMapping("/api/activity/couponInfo/inner/getById/{couponId}")
    CouponInfo getById(@PathVariable Long couponId);

    @GetMapping("/api/activity/couponInfo/inner/updateCouponInfoUseStatus/{couponUseId}/{orderId}")
    Boolean updateCouponInfoUseStatus(@PathVariable("couponUseId") Long couponUseId,
                                             @PathVariable("orderId") Long orderId);

}
