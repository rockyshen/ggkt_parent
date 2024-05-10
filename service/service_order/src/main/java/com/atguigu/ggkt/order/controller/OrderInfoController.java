package com.atguigu.ggkt.order.controller;


import com.atguigu.ggkt.model.order.OrderInfo;
import com.atguigu.ggkt.order.service.OrderInfoService;
import com.atguigu.ggkt.result.Result;
import com.atguigu.ggkt.vo.order.OrderInfoQueryVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 * 订单表 订单表 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2024-05-10
 */

@Api(tags = "订单管理接口")
@RestController
@RequestMapping("/admin/order/orderInfo")
public class OrderInfoController {
    @Autowired
    private OrderInfoService orderInfoService;

    @GetMapping("{page}/{limit}")
    public Result listOrder(@PathVariable Long page,
                            @PathVariable Long limit,
                            @RequestBody OrderInfoQueryVo orderInfoQueryVo) {

        Page<OrderInfo> pageParam = new Page<>(page,limit);
        Map<String, Object> map = orderInfoService.selectOrderInfoPage(pageParam,orderInfoQueryVo);
        return null;
    }
}

