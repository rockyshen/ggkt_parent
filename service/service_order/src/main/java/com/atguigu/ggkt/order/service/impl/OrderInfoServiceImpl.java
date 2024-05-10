package com.atguigu.ggkt.order.service.impl;

import com.atguigu.ggkt.model.order.OrderDetail;
import com.atguigu.ggkt.model.order.OrderInfo;
import com.atguigu.ggkt.order.mapper.OrderInfoMapper;
import com.atguigu.ggkt.order.service.OrderDetailService;
import com.atguigu.ggkt.order.service.OrderInfoService;
import com.atguigu.ggkt.vo.order.OrderInfoQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单表 订单表 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2024-05-10
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {
    @Autowired
    private OrderDetailService orderDetailService;

    // 订单列表
    @Override
    public Map<String, Object> selectOrderInfoPage(Page<OrderInfo> pageParam,OrderInfoQueryVo orderInfoQueryVo) {
        // 通过orderInfoQueryVo获取查询条件
        Long userId = orderInfoQueryVo.getUserId();
        String outTradeNo = orderInfoQueryVo.getOutTradeNo();
        String phone = orderInfoQueryVo.getPhone();
        String createTimeEnd = orderInfoQueryVo.getCreateTimeEnd();
        String createTimeBegin = orderInfoQueryVo.getCreateTimeBegin();
        Integer orderStatus = orderInfoQueryVo.getOrderStatus();

        // 判断条件值是否为空，不为空，进行封装
        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(userId)){
            wrapper.eq("user_id",userId);
        }
        if(!StringUtils.isEmpty(orderStatus)) {
            wrapper.eq("order_status",orderStatus);
        }
        if(!StringUtils.isEmpty(outTradeNo)) {
            wrapper.eq("out_trade_no",outTradeNo);
        }
        if(!StringUtils.isEmpty(phone)) {
            wrapper.eq("phone",phone);
        }
        if(!StringUtils.isEmpty(createTimeBegin)) {
            wrapper.ge("create_time",createTimeBegin);
        }
        if(!StringUtils.isEmpty(createTimeEnd)) {
            wrapper.le("create_time",createTimeEnd);
        }

        //调用Mapper方法实现分页查询
        Page<OrderInfo> pageModel = baseMapper.selectPage(pageParam, wrapper);
        long totalCount = pageModel.getTotal();
        long pageCount = pageModel.getPages();
        List<OrderInfo> records = pageModel.getRecords();

        //根据订单id查询出详情，再封装
        records.stream().forEach(item -> {
            this.getOrderDetail(item);

        });
        //所有需要的数据都封装到Map中
        Map<String, Object> map = new HashMap<>();
        map.put("total",totalCount);
        map.put("pageCount",pageCount);
        map.put("records",records);
        return map;
    }

    //传入Order_info对象，查询Order_detail
    private OrderInfo getOrderDetail(OrderInfo orderInfo) {
        Long id = orderInfo.getId();
        OrderDetail orderDetail = orderDetailService.getById(id);//order_detail表中id和order_id相同

        if (orderDetail != null) {
            String courseName = orderDetail.getCourseName();
            orderInfo.getParam().put("courseName", courseName);
        }
        return orderInfo;
    }
}
